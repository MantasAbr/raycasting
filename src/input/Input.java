package input;

import raycasting.Raycasting;

import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener {

    Raycasting raycasting;

    public boolean isSprinting = false;
    public boolean isWalking = false;

    private int mouseX;
    private int mouseY;

    private int oldX, newX = 0;
    private int oldY, newY = 0;
    public double rotationValue = 0;
    public boolean turningLeft, turningRight = false;

    public Input(Raycasting raycasting){
        this.raycasting = raycasting;
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

        if(keyCode == KeyEvent.VK_W){
            forward.toggle(isPressed);
            System.out.println("W pressed");
        }
        if(keyCode == KeyEvent.VK_A){
            left.toggle(isPressed);
            System.out.println("A pressed");
        }
        if(keyCode == KeyEvent.VK_S){
            back.toggle(isPressed);
            System.out.println("S pressed");
        }
        if(keyCode == KeyEvent.VK_D){
            right.toggle(isPressed);
            System.out.println("D pressed");
        }


        if(keyCode == KeyEvent.VK_C){
            crouch.toggle(isPressed);
            System.out.println("C pressed");
        }
        if(keyCode == KeyEvent.VK_SPACE){
            jump.toggle(isPressed);
            System.out.println("Space pressed");
        }
        if(keyCode == KeyEvent.VK_UP){
            up.toggle(isPressed);
            System.out.println("Up pressed");
        }
        if(keyCode == KeyEvent.VK_DOWN){
            down.toggle(isPressed);
            System.out.println("Down pressed");
        }


        if(keyCode == KeyEvent.VK_E){
            action.toggle(isPressed);
            System.out.println("E pressed");
        }
        if(keyCode == KeyEvent.VK_SHIFT){
            shift.toggle(isPressed);
            System.out.println("Shift pressed");
        }
        if(keyCode == KeyEvent.VK_ESCAPE){
            options.toggle(isPressed);
            System.out.println("Escape pressed");
        }
        if(keyCode == KeyEvent.VK_F3){
            debug.toggle(isPressed);
            System.out.println("F3 pressed");
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
