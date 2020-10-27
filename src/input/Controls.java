package input;

public class Controls {

    private String name;
    private int id;

    public Controls(String name, int id){
        this.name = name;
        this.id = id;
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

    public static Controls forward = new Controls("VK_W", 87);
    public static Controls left = new Controls("VK_A", 65);
    public static Controls right = new Controls("VK_D", 68);
    public static Controls back = new Controls("VK_S", 83);

    public static Controls crouch = new Controls("VK_C", 67);
    public static Controls jump = new Controls("VK_SPACE", 32);
    public static Controls up = new Controls("VK_UP", 38);
    public static Controls down = new Controls("VK_DOWN", 40);

    public static Controls action = new Controls("VK_E", 69);
    public static Controls shift = new Controls("VK_SHIFT", 16);
    public static Controls escape = new Controls("VK_ESCAPE", 27);
    public static Controls debug = new Controls("VK_F3", 114);
}
