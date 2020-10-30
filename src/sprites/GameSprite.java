package sprites;


public class GameSprite extends Sprite{

    private double xPos;
    private double yPos;

    public GameSprite(double xPos, double yPos, String fileLocation) {
        super(fileLocation);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public double getXLoc(){
        return xPos;
    }

    public double getYLoc(){
        return yPos;
    }
}
