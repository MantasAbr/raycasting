package raycasting;
import input.Input;
import sounds.Sounds;

import java.util.ArrayList;

/**
 *
 * @author Mantas Abramavičius
 */
public class Camera{

    /*  
        xPos and yPos are the location of the player on the 2D map.
        xDir and yDir are the x and y components of a vector that points in the
        direction that the player is facing.
        xPlane and yPlane is always perpendicular to the direction vector, 
        and it points to the farthest edge of the camera's field of view on
        one side.
    */
    
    public double xPos, yPos, xDir, yDir, xPlane, yPlane;

    public double MOVE_SPEED = .08;
    public final double PITCH_SPEED = .1;
    public double CROUCH_SPEED = .04;
    public double FASTER_MOVE_SPEED = .12;
    public double ROTATION_SPEED = .045;
    // Camera-height change per map unit travelled (walking .08 gives a change of 2).
    public double BOBBING_SPEED = 25;
    private static final double BOBBING_SETTLE_SPEED = 2;

    public int jumpTimer = 10;
    public int jumpChargeTimer = 3;
    private boolean jumpInProgress;
    private boolean jumpWasPressed;
    private boolean recoveringFromCrouch;

    public ArrayList<Sounds> sounds;
    public Raycasting game;
    public Screen screen;
    public Input input;

    boolean maxBobbingAmplitudeReached = false;
    boolean minBobbingAmplitudeReached = true;

    
    public Camera(double x, double y, double xd, double yd, double xp, double yp, ArrayList<Sounds> sounds, Raycasting game, Screen screen, Input input){
        xPos = x;
        yPos = y;
        xDir = xd;
        yDir = yd;
        xPlane = xp;
        yPlane = yp;
        this.sounds = sounds;
        this.game = game;
        this.screen = screen;
        this.input = input;
    }

    public void setXPos(double x){
        this.xPos = x;
    }

    public void setYPos(double y){
        this.yPos = y;
    }


    public void update(int[][] map) {

        if(!(Raycasting.gameIsInInventory || Raycasting.gameIsInOptions)) {
            boolean jumpPressed = input.jump.isPressed();
            if(jumpPressed && !jumpWasPressed && !jumpInProgress){
                jumpInProgress = true;
                jumpTimer = 10;
                jumpChargeTimer = 3;
            }
            jumpWasPressed = jumpPressed;

            if (input.turningLeft || input.turningRight) {
                ROTATION_SPEED = input.rotationValue;
            } else {
                ROTATION_SPEED = .045;
            }

            if (input.turningRight) {
                double oldxDir = xDir;
                xDir = xDir * Math.cos(-ROTATION_SPEED) - yDir * Math.sin(-ROTATION_SPEED);
                yDir = oldxDir * Math.sin(-ROTATION_SPEED) + yDir * Math.cos(-ROTATION_SPEED);
                double oldxPlane = xPlane;
                xPlane = xPlane * Math.cos(-ROTATION_SPEED) - yPlane * Math.sin(-ROTATION_SPEED);
                yPlane = oldxPlane * Math.sin(-ROTATION_SPEED) + yPlane * Math.cos(-ROTATION_SPEED);
            }
            if (input.turningLeft) {
                double oldxDir = xDir;
                xDir = xDir * Math.cos(ROTATION_SPEED) - yDir * Math.sin(ROTATION_SPEED);
                yDir = oldxDir * Math.sin(ROTATION_SPEED) + yDir * Math.cos(ROTATION_SPEED);
                double oldxPlane = xPlane;
                xPlane = xPlane * Math.cos(ROTATION_SPEED) - yPlane * Math.sin(ROTATION_SPEED);
                yPlane = oldxPlane * Math.sin(ROTATION_SPEED) + yPlane * Math.cos(ROTATION_SPEED);
            }

            updateMovement(map);

            //If the player looks up, increase the pitch until a threshold
            if (input.up.isPressed()) {
                screen.pitch += 200 * PITCH_SPEED;
                if (screen.pitch > 200) screen.pitch = 200;
            }

            //If the player looks down, decrease the pitch until a threshold
            if (input.down.isPressed()) {
                screen.pitch -= 200 * PITCH_SPEED;
                if (screen.pitch < -200) screen.pitch = -200;
            }

            //If the player crouches, decrease the posZ until a certain threshold
            //and slow down the player
            if (input.crouch.isPressed()) {
                if(!jumpInProgress){
                    screen.posZ = Math.max(-170, screen.posZ - 400 * PITCH_SPEED);
                    recoveringFromCrouch = true;
                }
                MOVE_SPEED = CROUCH_SPEED;
            }

            // Only crouching needs the slower posture recovery; a negative bob is not a crouch.
            if (!input.crouch.isPressed()) {
                MOVE_SPEED = .08;
                if (!jumpInProgress && recoveringFromCrouch){
                    screen.posZ = Math.min(0, screen.posZ + 100 * PITCH_SPEED);
                    if(screen.posZ == 0)
                        recoveringFromCrouch = false;
                }
            }

            updateJumpHeight(jumpPressed);
            resetPerspective();

            //pitch smoothing
            if (screen.pitch > 0) screen.pitch = Math.max(0, screen.pitch - 100 * PITCH_SPEED);
            if (screen.pitch < 0) screen.pitch = Math.min(0, screen.pitch + 100 * PITCH_SPEED);

        } else {
            input.updateFootsteps(false);
        }
    }

    private void updateMovement(int[][] map){
        int forwardMovement = input.getForwardMovement();
        int sidewaysMovement = input.getSidewaysMovement();
        double directionLength = Math.hypot(xDir, yDir);
        double planeLength = Math.hypot(xPlane, yPlane);
        double moveX = xDir / directionLength * forwardMovement + xPlane / planeLength * sidewaysMovement;
        double moveY = yDir / directionLength * forwardMovement + yPlane / planeLength * sidewaysMovement;
        double movementLength = Math.hypot(moveX, moveY);
        if(movementLength == 0){
            input.updateFootsteps(false);
            return;
        }

        boolean sprinting = input.isSprinting && forwardMovement > 0 && !input.crouch.isPressed();
        double speed = input.crouch.isPressed() ? CROUCH_SPEED : sprinting ? FASTER_MOVE_SPEED : MOVE_SPEED;
        double stepX = moveX / movementLength * speed;
        double stepY = moveY / movementLength * speed;
        double oldX = xPos;
        double oldY = yPos;

        // Use the actual combined step for collision checks, preserving wall sliding.
        if(isWalkable(map, xPos + stepX, yPos))
            xPos += stepX;
        if(isWalkable(map, xPos, yPos + stepY))
            yPos += stepY;

        double distanceMoved = Math.hypot(xPos - oldX, yPos - oldY);
        boolean moved = distanceMoved > 0;
        if(moved)
            viewBobbing(distanceMoved * BOBBING_SPEED, -20, 20);
        input.updateFootsteps(moved && !jumpInProgress);
    }

    private boolean isWalkable(int[][] map, double x, double y){
        return x >= 0 && x < map.length && y >= 0 && y < map[(int)x].length
                && map[(int)x][(int)y] == 0;
    }

    private void updateJumpHeight(boolean jumpPressed){
        if(!jumpInProgress)
            return;

        // Releasing Jump ends the ascent; movement must not interrupt landing.
        if(!jumpPressed){
            jumpChargeTimer = 0;
            jumpTimer = 0;
        } else if(jumpChargeTimer > 0){
            jumpChargeTimer--;
        }

        if(jumpChargeTimer > 0){
            screen.posZ -= 200 * PITCH_SPEED;
        } else if(jumpTimer > 1){
            jumpTimer--;
            screen.posZ = Math.min(190, screen.posZ + 500 * PITCH_SPEED);
        } else {
            jumpTimer = 0;
            if(screen.posZ > 0)
                screen.posZ = Math.max(0, screen.posZ - 100 * PITCH_SPEED);
            else
                screen.posZ = Math.min(0, screen.posZ + 100 * PITCH_SPEED);

            if(screen.posZ == 0){
                jumpInProgress = false;
                recoveringFromCrouch = false;
                minBobbingAmplitudeReached = true;
                maxBobbingAmplitudeReached = false;
            }
        }
    }

    /**
     * View bobbing logic
     * @param change Height change based on distance travelled this update
     * @param lowestPoint Lowest PosZ point
     * @param highestPoint Highest PosZ point
     */
    public void viewBobbing(double change, int lowestPoint, int highestPoint){
        if(jumpInProgress || input.crouch.isPressed() || recoveringFromCrouch)
            return;

        if(!minBobbingAmplitudeReached && maxBobbingAmplitudeReached){
            screen.posZ = Math.max(lowestPoint, screen.posZ - change);
            if(screen.posZ <= lowestPoint){
                minBobbingAmplitudeReached = true;
                maxBobbingAmplitudeReached = false;
            }
        }
        else if(!maxBobbingAmplitudeReached && minBobbingAmplitudeReached){
            screen.posZ = Math.min(highestPoint, screen.posZ + change);
            if(screen.posZ >= highestPoint){
                maxBobbingAmplitudeReached = true;
                minBobbingAmplitudeReached = false;
            }
        }
    }

    /**
     * Quickly settles the last walking bob when all movement keys are released.
     */
    public void resetPerspective(){
        if(jumpInProgress || input.crouch.isPressed() || recoveringFromCrouch)
            return;

        if(!input.isMovementRequested()){
            minBobbingAmplitudeReached = true;
            maxBobbingAmplitudeReached = false;

            if(screen.posZ > 0) screen.posZ = Math.max(0, screen.posZ - BOBBING_SETTLE_SPEED);
            if(screen.posZ < 0) screen.posZ = Math.min(0, screen.posZ + BOBBING_SETTLE_SPEED);
        }
    }
}
