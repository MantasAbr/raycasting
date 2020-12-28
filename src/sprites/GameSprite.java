package sprites;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

public class GameSprite extends Sprite{

    private double xPos;
    private double yPos;
    private double zPos;
    private double angle;

    private BufferedImage firstSide;
    private BufferedImage secondSide;
    private BufferedImage thirdSide;
    private BufferedImage fourthSide;

    //These two are used to sort overlapping sprites
    private double distance = 0;
    private int order = 0;

    public GameSprite(double xPos, double yPos, double zPos, String fileLocation) {
        super(fileLocation);
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public GameSprite(double xPos, double yPos, double zPos, String folderLocation, String spriteName){

    }

    public static Comparator<GameSprite> renderOrder = new Comparator<GameSprite>() {
        @Override
        public int compare(GameSprite o1, GameSprite o2) {

            double firstDistance = o1.getDistance();
            double secondDistance = o2.getDistance();

            return ((int)firstDistance - (int)secondDistance);
        }
    };

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

    public void setDistance(double newDistance){
        distance = newDistance;
    }

    public void setOrder(int newOrder){
        order = newOrder;
    }

    public void setAngle(double newAngle){angle = newAngle;}

    public static GameSprite ceilingLampGreen = new GameSprite(9, 7, -128,"src/sprites/img/ceiling_lamp.png");
    public static GameSprite ceilingLampBlack = new GameSprite(8, 6, -128, "src/sprites/img/ceiling_lamp_black.png");
    public static GameSprite revolverAmmo = new GameSprite(3,3, 128,"src/sprites/img/357_bullets.png");

    public static GameSprite boxDefault = new GameSprite(3,3, 128, "src/sprites/img/box_dir/box_dir_1.png");
    public static GameSprite boxSecond = new GameSprite(3,3, 128, "src/sprites/img/box_dir/box_dir_2.png");
    public static GameSprite boxThird = new GameSprite(3,3, 128, "src/sprites/img/box_dir/box_dir_3.png");
    public static GameSprite boxFourth = new GameSprite(3,3, 128, "src/sprites/img/box_dir/box_dir_4.png");
    public static ArrayList<GameSprite> box = new ArrayList<>();

    public static ArrayList<GameSprite> firstLevelSprites = new ArrayList<>();
    public static ArrayList<GameSprite> secondLevelSprites = new ArrayList<>();
    public static ArrayList<GameSprite> thirdLevelSprites = new ArrayList<>();

    public static ArrayList<GameSprite> firstLevelEnemies = new ArrayList<>();
}
