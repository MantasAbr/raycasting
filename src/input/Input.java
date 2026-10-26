package input;

import gui.GUIElement;
import gui.StyleColors;
import items.InventorySlot;
import items.Inventory;
import raycasting.Pointer;
import raycasting.Raycasting;
import sounds.Sounds;
import raycasting.UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowFocusListener{

    Pointer pointer;
    Raycasting raycasting;
    ArrayList<Sounds> sounds;

    public boolean isSprinting = false;
    public boolean isWalking = false;
    public boolean soundAlreadyPlaying = false;

    private InventorySlot carryFromSlot;
    public static boolean carryMessageAlertOn = false;

    public int mouseX;
    public int mouseY;

    public Point leftClick = new Point(-1, -1);
    public Point rightClick = new Point(-1, -1);
    public Point mouseOver = new Point();
    private Point mouseCaptureCenter;
    private final AtomicInteger pendingScroll = new AtomicInteger();

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
    public static Key inventory = new Key("VP_I", 73, false, false);
    public static Key debug = new Key("VK_F3", 114, false, false);
    public static Key exit = new Key("VK_X", 88, true, false);

    public void toggleKey(int keyCode, boolean isPressed){

        // Releases must still clear movement keys while a menu is open.
        if(!(Raycasting.gameIsInOptions || Raycasting.gameIsInInventory) || !isPressed){
            if(keyCode == forward.getId()) forward.toggle(isPressed);
            if(keyCode == back.getId()) back.toggle(isPressed);
            if(keyCode == left.getId()) left.toggle(isPressed);
            if(keyCode == right.getId()) right.toggle(isPressed);
        }

        /**
         * Keys that work when the game is not paused
         */
        if(!(Raycasting.gameIsInOptions || Raycasting.gameIsInInventory)) {

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
        if(!Raycasting.gameIsInInventory){
            if (keyCode == options.getId()) {
                options.toggle(isPressed);
                Raycasting.gameIsInOptions = options.isPressed();
                resetInventoryInteraction();
                setMousePointerFlag();
                System.out.println(isPressed);
            }
        }

        if(!Raycasting.gameIsInOptions){
            if(keyCode == inventory.getId()){
                inventory.toggle(isPressed);
                Raycasting.gameIsInInventory = inventory.isPressed();
                resetInventoryInteraction();
                setMousePointerFlag();
            }
        }
    }

    public synchronized void mouseMovementHandling(double sensitivity){
        rotationValue = 0;
        turningLeft = false;
        turningRight = false;

        Point center = getMouseCaptureCenter();
        if(Raycasting.gameIsInOptions || Raycasting.gameIsInInventory || center == null){
            mouseCaptureCenter = null;
            return;
        }

        // First capture (also after menus/focus changes) establishes a new baseline.
        if(!center.equals(mouseCaptureCenter)){
            moveMousePointer(center);
            mouseCaptureCenter = center;
            return;
        }

        // Poll screen coordinates rather than MouseEvents: Robot-generated events
        // may arrive later, and must never be interpreted as player movement.
        Point position = getMouseScreenPosition();
        if(position == null){
            mouseCaptureCenter = null;
            return;
        }
        int deltaX = position.x - center.x;
        rotationValue = Math.abs((double)deltaX) / sensitivity;
        turningRight = deltaX > 0;
        turningLeft = deltaX < 0;
        if(!position.equals(center))
            moveMousePointer(center);
    }

    protected Point getMouseCaptureCenter(){
        if(raycasting == null || !raycasting.isShowing() || !raycasting.isFocused())
            return null;
        Point center = raycasting.getLocationOnScreen();
        center.translate(raycasting.getWidth() / 2, raycasting.getHeight() / 2);
        return center;
    }

    protected Point getMouseScreenPosition(){
        PointerInfo info = MouseInfo.getPointerInfo();
        return info == null ? null : info.getLocation();
    }

    protected void moveMousePointer(Point position){
        pointer.robot.mouseMove(position.x, position.y);
    }

    public synchronized void setMousePointerFlag(){
        pendingScroll.set(0);
        mouseCaptureCenter = null;
        rotationValue = 0;
        turningLeft = false;
        turningRight = false;
    }

    @Override
    public void windowLostFocus(WindowEvent e){
        setMousePointerFlag();
    }

    @Override
    public void windowGainedFocus(WindowEvent e){
        setMousePointerFlag();
    }

    public int getForwardMovement(){
        return (forward.isPressed() ? 1 : 0) - (back.isPressed() ? 1 : 0);
    }

    public int getSidewaysMovement(){
        return (right.isPressed() ? 1 : 0) - (left.isPressed() ? 1 : 0);
    }

    public boolean isMovementRequested(){
        return getForwardMovement() != 0 || getSidewaysMovement() != 0;
    }

    public void updateFootsteps(boolean moving){
        if(sounds.size() <= 1 || moving == soundAlreadyPlaying)
            return;
        if(moving)
            sounds.get(1).PlaySound(true);
        else
            sounds.get(1).StopSound();
        soundAlreadyPlaying = moving;
    }

    public void optionsScreenClickHandling(UserInterface userInterface) {
        if(userInterface.getExitButton().contains(leftClick)){
            System.exit(0);
        }
        if(userInterface.getSaveGameButton().contains(leftClick)){
            System.out.println("Save game");
            leftClick.setLocation(0, 0);
        }
        if(userInterface.getLoadGameButton().contains(leftClick)){
            System.out.println("Load game");
            leftClick.setLocation(0, 0);
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

    public void inventoryScreenHoverHandling(Inventory items){
        for (InventorySlot slot : items.getSlots()) {
            slot.setImage(slot.getBounds().contains(mouseOver)
                    ? GUIElement.lighterInventorySlot : GUIElement.inventorySlot);
        }
    }

    public void inventoryMovementHandling(Inventory items){
        swapItems(items);
    }

    public void swapItems(Inventory items){
        InventorySlot source = items.getSlotAt(rightClick);
        InventorySlot target = items.getSlotAt(leftClick);
        if (source != null) {
            carryMessageAlertOn = false;
            carryFromSlot = source;
        }
        if (target != null) {
            if (carryFromSlot == null) {
                carryMessageAlertOn = true;
            }
            else {
                items.swapItems(carryFromSlot, target);
                carryFromSlot = null;
                carryMessageAlertOn = false;
            }
        }
        leftClick.setLocation(-1, -1);
        rightClick.setLocation(-1, -1);
    }

    private void resetInventoryInteraction() {
        carryFromSlot = null;
        carryMessageAlertOn = false;
        leftClick.setLocation(-1, -1);
        rightClick.setLocation(-1, -1);
    }

    public void mouseWheelHandling(Inventory items){
        int steps = pendingScroll.getAndSet(0);
        if (!(Raycasting.gameIsInInventory || Raycasting.gameIsInOptions))
            items.selectRelative(steps);
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

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)
                && (Raycasting.gameIsInInventory || Raycasting.gameIsInOptions)){
            leftClick.setLocation(e.getPoint());
            System.out.println("Left click at: " + e.getX() + " " + e.getY());
        }
        if(SwingUtilities.isRightMouseButton(e) && Raycasting.gameIsInInventory){
            rightClick.setLocation(e.getPoint());
            System.out.println("Right click at: " + e.getX() + " " + e.getY());
        }
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!(Raycasting.gameIsInInventory || Raycasting.gameIsInOptions))
            pendingScroll.addAndGet(-e.getWheelRotation());
    }
}
