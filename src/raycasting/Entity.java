package raycasting;

public class Entity {

    private double xLoc;
    private double yLoc;

    public Entity(double xLoc, double yLoc){
        this.xLoc = xLoc;
        this.yLoc = yLoc;
    }

    public double getXLoc(){
        return xLoc;
    }

    public double getYLoc(){
        return yLoc;
    }

    public void setXLoc(double location){
        this.xLoc = location;
    }

    public void setYLoc(double location){
        this.yLoc = location;
    }
}
