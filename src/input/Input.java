package input;

import raycasting.Raycasting;
import raycasting.Sounds;

import java.awt.event.*;
import java.util.ArrayList;

public class Input implements KeyListener, MouseListener, MouseMotionListener {

    Raycasting raycasting;
    ArrayList<Sounds> sounds;

    public boolean isSprinting = false;
    public boolean isWalking = false;
    public boolean soundAlreadyPlaying = false;

    private int mouseX;
    private int mouseY;

    private int oldX, newX = 0;
    private int oldY, newY = 0;
    public double rotationValue = 0;
    public boolean turningLeft, turningRight = false;

    public Input(Raycasting raycasting, ArrayList<Sounds> sounds){
        this.raycasting = raycasting;
        this.sounds = sounds;
    }

    public Key left = new Key(true, false);
    public Key right = new Key(true, false);
    public Key forward = new Key(true, false);
    public Key back = new Key(true, false);

    public Key crouch = new Key(true, false);
    public Key jump = new Key(true, false);
    public Key up = new Key(true, false);
    public Key down = new Key(true, false);

    public Key action = new Key(true, false);
    public Key shift = new Key(true, false);
    public Key options = new Key(false, false);
    public Key debug = new Key(false, false);

    public void toggleKey(int keyCode, boolean isPressed){

        if(keyCode == Controls.forward.getId()){
            forward.toggle(isPressed);
            playSound(isPressed, 1);
        }
        if(keyCode == Controls.left.getId()){
            left.toggle(isPressed);
        }
        if(keyCode == Controls.back.getId()){
            back.toggle(isPressed);
            playSound(isPressed, 1);
        }
        if(keyCode == Controls.right.getId()){
            right.toggle(isPressed);
        }


        if(keyCode == Controls.crouch.getId()){
            crouch.toggle(isPressed);
        }
        if(keyCode == Controls.jump.getId()){
            jump.toggle(isPressed);
        }
        if(keyCode == Controls.up.getId()){
            up.toggle(isPressed);
        }
        if(keyCode == Controls.down.getId()){
            down.toggle(isPressed);
        }


        if(keyCode == Controls.action.getId()){
            action.toggle(isPressed);
        }
        if(keyCode == Controls.shift.getId()){
            shift.toggle(isPressed);
        }
        if(keyCode == Controls.escape.getId()){
            options.toggle(isPressed);
        }
        if(keyCode == Controls.debug.getId()){
            debug.toggle(isPressed);
        }
    }

    private void playSound(boolean isToggled, int soundID){
        if(isToggled){
            if(!soundAlreadyPlaying){
                sounds.get(soundID).PlaySound(true);
                soundAlreadyPlaying = true;
            }
        }
        else{
            sounds.get(soundID).clip.close();
            soundAlreadyPlaying = false;
        }
    }

    public void mouseMovementHandling(double sensitivity){
        newX = mouseX;
        newY = mouseY;

        rotationValue = (Math.abs(newX - oldX) / sensitivity);

        if(newX > oldX)
            turningRight = true;
        if(newX < oldX)
            turningLeft = true;
        if(newX == oldX){
            turningRight = false;
            turningLeft = false;
        }

        if(newX <= 10 || newY <= 10)
            raycasting.robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);
        if(newX >= Raycasting.WINDOW_WIDTH - 10 || newY >= Raycasting.WINDOW_HEIGHT - 10)
            raycasting.robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);

        oldX = newX;
        oldY = newY;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        toggleKey(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        toggleKey(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }
}
