package input;

import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener {

    public boolean isSprinting = false;
    public boolean isWalking = false;

    public Input(){

    }

    public Key left = new Key(true);
    public Key right = new Key(true);
    public Key forward = new Key(true);
    public Key back = new Key(true);

    public Key crouch = new Key(true);
    public Key jump = new Key(true);
    public Key up = new Key(true);
    public Key down = new Key(true);

    public Key action = new Key(true);
    public Key shift = new Key(true);
    public Key options = new Key(false);
    public Key debug = new Key(false);

    public void toggleKey(int keyCode, boolean isPressed){

        if(keyCode == KeyEvent.VK_W)
            forward.toggle(isPressed);
        if(keyCode == KeyEvent.VK_A)
            left.toggle(isPressed);
        if(keyCode == KeyEvent.VK_S)
            back.toggle(isPressed);
        if(keyCode == KeyEvent.VK_D)
            right.toggle(isPressed);

        if(keyCode == KeyEvent.VK_C)
            crouch.toggle(isPressed);
        if(keyCode == KeyEvent.VK_SPACE)
            jump.toggle(isPressed);
        if(keyCode == KeyEvent.VK_UP)
            up.toggle(isPressed);
        if(keyCode == KeyEvent.VK_DOWN)
            down.toggle(isPressed);

        if(keyCode == KeyEvent.VK_E)
            action.toggle(isPressed);
        if(keyCode == KeyEvent.VK_SHIFT)
            shift.toggle(isPressed);
        if(keyCode == KeyEvent.VK_ESCAPE)
            options.toggle(isPressed);
        if(keyCode == KeyEvent.VK_F3)
            debug.toggle(isPressed);
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
    public void mouseMoved(MouseEvent e) {}

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
