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
    public double BOBBING_SPEED = 0.3;

    public int jumpTimer = 10;
    public int jumpChargeTimer = 3;

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


            if (input.turningLeft || input.turningRight) {
                ROTATION_SPEED = input.rotationValue;
            } else {
                ROTATION_SPEED = .045;
            }

            if (input.forward.isPressed()) {

                if (input.isSprinting) {

                    //resetPerspective();

                    if (map[(int) (xPos + xDir * MOVE_SPEED)][(int) yPos] == 0) {
                        xPos += xDir * FASTER_MOVE_SPEED;
                    }
                    if (map[(int) xPos][(int) (yPos + yDir * MOVE_SPEED)] == 0)
                        yPos += yDir * FASTER_MOVE_SPEED;

                    //view bobbing for sprinting is turned off, because it's buggy
                    //viewBobbing(5, -20, 20);
                } else {
                    if (map[(int) (xPos + xDir * MOVE_SPEED)][(int) yPos] == 0) {
                        xPos += xDir * MOVE_SPEED;
                    }
                    if (map[(int) xPos][(int) (yPos + yDir * MOVE_SPEED)] == 0)
                        yPos += yDir * MOVE_SPEED;

                    viewBobbing(2, -20, 20);
                }
            }
            if (input.back.isPressed()) {
                if (map[(int) (xPos - xDir * MOVE_SPEED)][(int) yPos] == 0)
                    xPos -= xDir * MOVE_SPEED;
                if (map[(int) xPos][(int) (yPos - yDir * MOVE_SPEED)] == 0)
                    yPos -= yDir * MOVE_SPEED;

                viewBobbing(2, -20, 20);
            }

            if (!input.isWalking)
                resetPerspective();


            if (input.turningRight) {
                double oldxDir = xDir;
                xDir = xDir * Math.cos(-ROTATION_SPEED) - yDir * Math.sin(-ROTATION_SPEED);
                yDir = oldxDir * Math.sin(-ROTATION_SPEED) + yDir * Math.cos(-ROTATION_SPEED);
                double oldxPlane = xPlane;
                xPlane = xPlane * Math.cos(-ROTATION_SPEED) - yPlane * Math.sin(-ROTATION_SPEED);
                yPlane = oldxPlane * Math.sin(-ROTATION_SPEED) + yPlane * Math.cos(-ROTATION_SPEED);
            }
            if(Input.right.isPressed()){
                if (map[(int) (xPos + xPlane * MOVE_SPEED)][(int) yPos] == 0) {
                    xPos += xPlane * MOVE_SPEED;
                }
                if (map[(int) xPos][(int) (yPos + yPlane * MOVE_SPEED)] == 0)
                    yPos += yPlane * MOVE_SPEED;

                viewBobbing(2, -20, 20);
            }
            if (input.turningLeft) {
                double oldxDir = xDir;
                xDir = xDir * Math.cos(ROTATION_SPEED) - yDir * Math.sin(ROTATION_SPEED);
                yDir = oldxDir * Math.sin(ROTATION_SPEED) + yDir * Math.cos(ROTATION_SPEED);
                double oldxPlane = xPlane;
                xPlane = xPlane * Math.cos(ROTATION_SPEED) - yPlane * Math.sin(ROTATION_SPEED);
                yPlane = oldxPlane * Math.sin(ROTATION_SPEED) + yPlane * Math.cos(ROTATION_SPEED);
            }

            if(Input.left.isPressed()){
                if (map[(int) (xPos - xPlane * MOVE_SPEED)][(int) yPos] == 0)
                    xPos -= xPlane * MOVE_SPEED;
                if (map[(int) xPos][(int) (yPos - yPlane * MOVE_SPEED)] == 0)
                    yPos -= yPlane * MOVE_SPEED;

                viewBobbing(2, -20, 20);
            }

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
                screen.posZ -= 400 * PITCH_SPEED;
                if (screen.posZ < -170)
                    screen.posZ = -170;
                MOVE_SPEED = CROUCH_SPEED;
            }

            //If the player stops crouching and isn't moving forwards or backwards, push them back up on their feet smoothly,
            //and put the regular walking speed back
            if (!input.crouch.isPressed() && !input.isWalking) {
                MOVE_SPEED = .08;
                if (screen.posZ < 0)
                    screen.posZ = Math.min(0, screen.posZ + 100 * PITCH_SPEED);
            }

            //If the player holds the jump button, decrease the jumpTimer, so that the player won't levitate
            //If the player reaches a certain threshold, stop the jump
            if (input.jump.isPressed()) {
                jumpChargeTimer--;
                if (jumpChargeTimer > 0) {
                    screen.posZ -= 200 * PITCH_SPEED;
                } else {
                    jumpTimer--;
                    if (jumpTimer > 0) {
                        screen.posZ += 500 * PITCH_SPEED;
                        if (screen.posZ > 190)
                            screen.posZ = 190;
                    } else
                        screen.posZ = Math.max(0, screen.posZ - 100 * PITCH_SPEED);
                }
            }

            //If the player stops the jump and isn't moving forwards or backwards, push them down smoothly,
            //and reset the jumpTimer
            if (!input.jump.isPressed() && !input.isWalking) {
                jumpTimer = 10;
                jumpChargeTimer = 3;
                if (screen.posZ > 0)
                    screen.posZ = Math.max(0, screen.posZ - 100 * PITCH_SPEED);
            }

            //pitch smoothing
            if (screen.pitch > 0) screen.pitch = Math.max(0, screen.pitch - 100 * PITCH_SPEED);
            if (screen.pitch < 0) screen.pitch = Math.min(0, screen.pitch + 100 * PITCH_SPEED);

        }
    }

    /**
     * View bobbing logic
     * @param change How fast the camera should "bop"
     * @param lowestPoint Lowest PosZ point
     * @param highestPoint Highest PosZ point
     */
    public void viewBobbing(int change, int lowestPoint, int highestPoint){
        if(!minBobbingAmplitudeReached && maxBobbingAmplitudeReached){
            screen.posZ -= change;
            if(screen.posZ == lowestPoint){
                minBobbingAmplitudeReached = true;
                maxBobbingAmplitudeReached = false;
            }
        }
        if(!maxBobbingAmplitudeReached && minBobbingAmplitudeReached){
            screen.posZ += change;
            if(screen.posZ == highestPoint){
                maxBobbingAmplitudeReached = true;
                minBobbingAmplitudeReached = false;
            }
        }
    }

    /**
     * Resets the screen's posZ to 0
     */
    public void resetPerspective(){
        if(!input.forward.isPressed() && !input.back.isPressed()){
            minBobbingAmplitudeReached = true;
            maxBobbingAmplitudeReached = false;

            if(screen.posZ > 0) screen.posZ = Math.max(0, screen.posZ - 1 * PITCH_SPEED);
            if(screen.posZ < 0) screen.posZ = Math.min(0, screen.posZ + 1 * PITCH_SPEED);
        }
    }
}
