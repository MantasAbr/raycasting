package sprites;

import java.util.ArrayList;
import java.util.Comparator;

public class GameSprite extends Sprite{

    private double xPos;
    private double yPos;
    private double zPos;
    private double angle;
    private boolean isMultiSided;

    //These two are used to sort overlapping sprites
    private double distance = 0;
    private int order = 0;

    public GameSprite(double xPos, double yPos, double zPos, String fileLocation) {
        super(fileLocation);
        isMultiSided = false;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public GameSprite(double xPos, double yPos, double zPos, String first, String second, String third, String fourth) {
        super(first, second, third, fourth);
        isMultiSided = true;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public static Comparator<GameSprite> renderOrder = new Comparator<GameSprite>() {
        @Override
        public int compare(GameSprite o1, GameSprite o2) {

            double firstDistance = o1.getDistance();
            double secondDistance = o2.getDistance();

            return ((int)firstDistance - (int)secondDistance);
        }
    };

    public void setSpriteAccordingToAngle(){
        if(angle >= 0 && angle < 45 || angle >= 315 && angle < 360){
            spriteImage = spriteImageSides[2];
        }
        if(angle >= 45 && angle < 135){
            spriteImage = spriteImageSides[1];
        }
        if(angle >= 135 && angle < 225){
            spriteImage = spriteImageSides[0];
        }
        if(angle >= 225 && angle < 315){
            spriteImage = spriteImageSides[3];
        }
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

    public static GameSprite ceilingLampGreen = new GameSprite(9, 7, -128,"src/sprites/img/ceiling_lamp.png");
    public static GameSprite ceilingLampBlack = new GameSprite(8, 6, -128, "src/sprites/img/ceiling_lamp_black.png");
    public static GameSprite revolverAmmo = new GameSprite(3,3, 128,"src/sprites/img/357_bullets.png");
    public static GameSprite box = new GameSprite(5, 5, 128, "src/sprites/img/box_dir/box_dir_1.png",
                                                                            "src/sprites/img/box_dir/box_dir_2.png",
                                                                            "src/sprites/img/box_dir/box_dir_3.png",
                                                                            "src/sprites/img/box_dir/box_dir_4.png");

    public static ArrayList<GameSprite> firstLevelSprites = new ArrayList<>();
    public static ArrayList<GameSprite> secondLevelSprites = new ArrayList<>();
    public static ArrayList<GameSprite> thirdLevelSprites = new ArrayList<>();
}
