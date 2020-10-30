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

}
