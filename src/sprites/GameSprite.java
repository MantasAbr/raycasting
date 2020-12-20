package sprites;


import java.util.ArrayList;
import java.util.Comparator;

public class GameSprite extends Sprite{

    private double xPos;
    private double yPos;
    private double zPos;

    //These two are used to sort overlapping sprites
    private double distance = 0;
    private int order = 0;

    public GameSprite(double xPos, double yPos, double zPos, String fileLocation) {
        super(fileLocation);
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

    public double getXLoc(){
        return xPos;
    }

    public double getYLoc(){
        return yPos;
    }

    public double getZPos(){return zPos;}

    public double getDistance(){return distance;}

    public int getOrder(){return order;}

    public void setDistance(double newDistance){
        distance = newDistance;
    }

    public void setOrder(int newOrder){
        order = newOrder;
    }


    public static GameSprite ceilingLampGreen = new GameSprite(9, 7, -128,"src/sprites/img/ceiling_lamp.png");
    public static GameSprite ceilingLampBlack = new GameSprite(8, 6, -128, "src/sprites/img/ceiling_lamp_black.png");

    public static ArrayList<GameSprite> firstLevelSprites = new ArrayList<>();
    public static ArrayList<GameSprite> secondLevelSprites = new ArrayList<>();
    public static ArrayList<GameSprite> thirdLevelSprites = new ArrayList<>();
}
