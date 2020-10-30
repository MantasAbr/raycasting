package input;

public class Key {

    private boolean pressed = false;
    private boolean pressToBeActive; //Used for keys that are activated by a press, NOT by holding the key!
    private boolean isActive; //Used to tell whether or not a key remains 'pressed' even after un-pressing it, i.e debug info

    private String name;
    private int id;

    public Key(String name, int id, boolean pressToBeActive, boolean isActive){
        this.name = name;
        this.id = id;
        this.pressToBeActive = pressToBeActive;
        this.isActive = isActive;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }

    public void setName(String newName){
        name = newName;
    }

    public void setId(int newId){
        id = newId;
    }

    public boolean isPressed(){
        return pressed;
    }

    public void toggle(boolean isPressed){
        if(!pressToBeActive){
            if(!isActive && !isPressed){
                isActive = true;
                pressed = true;
            }
            else if(isActive && !isPressed){
                isActive = false;
                pressed = false;
            }
        }
        else
            pressed = isPressed;
    }
}
