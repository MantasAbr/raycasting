package sprites;

import raycasting.Camera;

import java.util.ArrayList;
import java.util.Comparator;

public class Entity extends Sprite{

    private double xPos;
    private double yPos;
    private double zPos;
    private double angle;
    private boolean isMultiSided;

    //These two are used to sort overlapping sprites
    private double distance = 0;
    private int order = 0;

    private boolean isHostile;

    public Entity(double xPos, double yPos, double zPos, String fileLocation, boolean isHostile) {
        super(fileLocation);
        isMultiSided = false;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.isHostile = isHostile;
    }

    public Entity(double xPos, double yPos, double zPos, boolean isMultiSided, String folderLocation, boolean isHostile) {
        super(isMultiSided, folderLocation);
        this.isMultiSided = isMultiSided;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.isHostile = isHostile;
    }

    public static Comparator<Entity> renderOrder = new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {

            double firstDistance = o1.getDistance();
            double secondDistance = o2.getDistance();

            return ((int)firstDistance - (int)secondDistance);
        }
    };

    /**
     * divides the viewing angle in to 12 equal parts and sets the current sprite image accordingly
     */
    public void setSpriteAccordingToAngle(){
        int currIndex = (int)(angle / 30);
        spriteImage = spriteImageSides[currIndex];
        spriteImage.getRGB(0, 0, spriteWidth, spriteHeight, pixels, 0, spriteWidth);
    }

    public double getXLoc(){
        return xPos;
    }

    public double getYLoc(){
        return yPos;
    }

    public double getZPos(){return zPos;}

    public double getDistance(){return distance;}

    public int getOrder(){return order;}

    public double getAngle(){return angle;}

    public boolean isMultiSided() {
        return isMultiSided;
    }

    public boolean isHostile() { return isHostile; }


    public void setXLoc(double newXPos){ xPos = newXPos;}

    public void setYLoc(double newYPos){ yPos = newYPos;}

    public void setDistance(double newDistance){
        distance = newDistance;
    }

    public void setOrder(int newOrder){
        order = newOrder;
    }

    public void setAngle(double newAngle){angle = newAngle;}

    public void setMultiSided(boolean multiSided) {
        isMultiSided = multiSided;
    }

    public void setHostile(boolean hostile) { isHostile = hostile; }

    public static Entity ceilingLampGreen1 = new Entity(9, 7, -128, false,"src/sprites/img/ceiling_lamp.png", false);
    public static Entity ceilingLampGreen2 = new Entity(2, 7, -128, false,"src/sprites/img/ceiling_lamp.png", false);
    public static Entity ceilingLampGreen3 = new Entity(9, 2, -128, false,"src/sprites/img/ceiling_lamp.png", false);
    public static Entity ceilingLampGreen4 = new Entity(2, 2, -128, false,"src/sprites/img/ceiling_lamp.png", false);
    public static Entity ceilingLampGreen5 = new Entity(9, 4.5, -128, false,"src/sprites/img/ceiling_lamp.png", false);
    public static Entity ceilingLampGreen6 = new Entity(2, 4.5, -128, false,"src/sprites/img/ceiling_lamp.png", false);
    public static Entity joke = new Entity(4,4, 0,false,"src/sprites/img/joke.png", true);

    //public static Entity ceilingLampBlack = new Entity(8, 6, -128, false,"src/sprites/img/ceiling_lamp_black.png");
    //public static Entity revolverAmmo = new Entity(3,3, 128,false,"src/sprites/img/357_bullets.png");
    //public static Entity box = new Entity(5, 5, 128, true, "src/sprites/img/box_dir/");
    //public static Entity table = new Entity(6, 5, 128, true, "src/sprites/img/table_dir/");

    public static ArrayList<Entity> firstLevelEntities = new ArrayList<>();
    public static ArrayList<Entity> secondLevelEntities = new ArrayList<>();
    public static ArrayList<Entity> thirdLevelEntities = new ArrayList<>();
}
