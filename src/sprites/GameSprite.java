package sprites;


import java.util.Comparator;

public class GameSprite extends Sprite{

    private double xPos;
    private double yPos;

    //These two are used to sort overlapping sprites
    private double distance = 0;
    private int order = 0;

    public GameSprite(double xPos, double yPos, String fileLocation) {
        super(fileLocation);
        this.xPos = xPos;
        this.yPos = yPos;
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

    public double getDistance(){return distance;}

    public int getOrder(){return order;}

    public void setDistance(double newDistance){
        distance = newDistance;
    }

    public void setOrder(int newOrder){
        order = newOrder;
    }

    public static GameSprite lamp = new GameSprite(9, 7, "src/sprites/lamp.png");
    public static GameSprite redlamp = new GameSprite(8, 6, "src/sprites/lamp_red.png");
}
