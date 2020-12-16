package input;

import gui.StyleColors;
import items.ItemLinkedList;
import raycasting.Pointer;
import raycasting.Raycasting;
import sounds.Sounds;
import raycasting.UserInterface;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{

    Pointer pointer;
    Raycasting raycasting;
    ArrayList<Sounds> sounds;

    public boolean isSprinting = false;
    public boolean isWalking = false;
    public boolean soundAlreadyPlaying = false;

    public int mouseX;
    public int mouseY;

    public Point click = new Point();
    public Point mouseOver = new Point();
    public int mouseScrollValues;

    public int oldX, newX = 0;
    public int oldY, newY = 0;
    public double rotationValue = 0;
    public boolean turningLeft, turningRight = false;

    public Input(Raycasting raycasting, ArrayList<Sounds> sounds, Pointer pointer){
        this.raycasting = raycasting;
        this.sounds = sounds;
        this.pointer = pointer;
    }

    //Directional keys
    public static Key forward = new Key("VK_W", 87, true, false);
    public static Key left = new Key("VK_A", 65, true, false);
    public static Key right = new Key("VK_D", 68, true, false);
    public static Key back = new Key("VK_S", 83, true, false);

    //Movement keys
    public static Key crouch = new Key("VK_C", 67, true, false);
    public static Key jump = new Key("VK_SPACE", 32, true, false);
    public static Key up = new Key("VK_UP", 38, true, false);
    public static Key down = new Key("VK_DOWN", 40, true, false);

    //Utility keys
    public static Key action = new Key("VK_E", 69, true, false);
    public static Key shift = new Key("VK_SHIFT", 16, true, false);
    public static Key options = new Key("VK_ESCAPE", 27, false, false);
    public static Key pause = new Key("VP_P", 80, false, false);
    public static Key debug = new Key("VK_F3", 114, false, false);
    public static Key exit = new Key("VK_X", 88, true, false);

    public void toggleKey(int keyCode, boolean isPressed){

        /**
         * Keys that work when the game is not paused
         */
        if(!(Raycasting.gameIsInOptions || Raycasting.gameIsPaused)) {

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

        //Options key should work regardless
        //The if statements are to make sure that the game couldn't be in pause and options mode at the same time
        if(!Raycasting.gameIsPaused){
            if (keyCode == options.getId()) {
                options.toggle(isPressed);
                Raycasting.gameIsInOptions = options.isPressed();
                System.out.println(isPressed);
            }
        }

        if(!Raycasting.gameIsInOptions){
            if(keyCode == pause.getId()){
                pause.toggle(isPressed);
                Raycasting.gameIsPaused = pause.isPressed();
            }
        }
    }

    public void mouseMovementHandling(double sensitivity){

        if(newX <= 10 || newY <= 10)
            pointer.robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);
        if(newX >= Raycasting.WINDOW_WIDTH - 10 || newY >= Raycasting.WINDOW_HEIGHT - 10)
            pointer.robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);

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

        oldX = newX;
        oldY = newY;
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

    public void optionsScreenClickHandling(UserInterface userInterface) {
        if(userInterface.getExitButton().contains(click)){
            System.exit(0);
        }
        if(userInterface.getSaveGameButton().contains(click)){
            System.out.println("Save game");
            click.setLocation(0, 0);
        }
        if(userInterface.getLoadGameButton().contains(click)){
            System.out.println("Load game");
            click.setLocation(0, 0);
        }
    }

    public void optionsScreenHoverHandling(UserInterface userInterface){
        if(userInterface.getSaveGameButton().contains(mouseOver)){
            UserInterface.buttons.get(0).setTextColor(StyleColors.black);
        }
        else if(userInterface.getLoadGameButton().contains(mouseOver)){
            UserInterface.buttons.get(1).setTextColor(StyleColors.black);
        }
        else if(userInterface.getExitButton().contains(mouseOver)){
            UserInterface.buttons.get(2).setTextColor(StyleColors.darkred);
        }
        else{
            UserInterface.resetTextColor(UserInterface.buttons);
        }
    }

    public void mouseWheelHandling(UserInterface userInterface, ItemLinkedList items){
        if(mouseScrollValues == 1){
            System.out.println("Current inv item: " + items.goRight().getName());
        }
        else if(mouseScrollValues == -1){
            System.out.println("Current inv item: " + items.goLeft().getName());

        }
        mouseScrollValues = 0;
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
        mouseOver.setLocation(e.getX(), e.getY());
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println(e.getX() + " " + e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        click.setLocation(e.getX(), e.getY());
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();

        if(notches < 0){
            mouseScrollValues = 1;
        }

        if(notches > 0) {
            mouseScrollValues = -1;
        }
    }
}
