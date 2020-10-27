package input;

public class Key {

    private boolean pressed = false;
    private boolean pressToBeActive; //Used for keys that are activated by a press, NOT by holding the key!

    public Key(boolean pressToBeActive){
        this.pressToBeActive = pressToBeActive;
    }

    public boolean isPressed(){
        return pressed;
    }

    public void toggle(boolean isPressed){
        pressed = isPressed;
    }
}
