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

    public int mouseX;
    public int mouseY;

    public int oldX, newX = 0;
    public int oldY, newY = 0;
    public double rotationValue = 0;
    public boolean turningLeft, turningRight = false;

    public Input(Raycasting raycasting, ArrayList<Sounds> sounds){
        this.raycasting = raycasting;
        this.sounds = sounds;
    }

    public static Key forward = new Key("VK_W", 87, true, false);
    public static Key left = new Key("VK_A", 65, true, false);
    public static Key right = new Key("VK_D", 68, true, false);
    public static Key back = new Key("VK_S", 83, true, false);

    public static Key crouch = new Key("VK_C", 67, true, false);
    public static Key jump = new Key("VK_SPACE", 32, true, false);
    public static Key up = new Key("VK_UP", 38, true, false);
    public static Key down = new Key("VK_DOWN", 40, true, false);

    public static Key action = new Key("VK_E", 69, true, false);
    public static Key shift = new Key("VK_SHIFT", 16, true, false);
    public static Key options = new Key("VK_ESCAPE", 27, false, false);
    public static Key debug = new Key("VK_F3", 114, false, false);
    public static Key exit = new Key("VK_X", 88, true, false);

    public void toggleKey(int keyCode, boolean isPressed){

        /**
         * Keys that work when the game is not paused
         */
        if(!Raycasting.gameIsPaused) {

            if (keyCode == forward.getId()) {
                forward.toggle(isPressed);
                playSound(isPressed, 1);
            }
            if (keyCode == left.getId()) {
                left.toggle(isPressed);
            }
            if (keyCode == back.getId()) {
                back.toggle(isPressed);
                playSound(isPressed, 1);
            }
            if (keyCode == right.getId()) {
                right.toggle(isPressed);
            }


            if (keyCode == crouch.getId()) {
                crouch.toggle(isPressed);
            }
            if (keyCode == jump.getId()) {
                jump.toggle(isPressed);
            }
            if (keyCode == up.getId()) {
                up.toggle(isPressed);
            }
            if (keyCode == down.getId()) {
                down.toggle(isPressed);
            }


            if (keyCode == action.getId()) {
                action.toggle(isPressed);
            }
            if (keyCode == shift.getId()) {
                shift.toggle(isPressed);
            }
            if (keyCode == debug.getId()) {
                debug.toggle(isPressed);
            }
            if (keyCode == exit.getId()) {
                exit.toggle(isPressed);
            }
        }
        /**
         * Keys that work when the game is paused
         */
        else{

        }

        //Options key should work regardless
        if (keyCode == options.getId()) {
            options.toggle(isPressed);
            Raycasting.gameIsPaused = options.isPressed();
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
