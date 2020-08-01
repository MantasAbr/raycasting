package raycasting;

public class Player extends Entity {

    private double healthValue;
    private double sprintValue;
    private double speedValue;

    public Player(double xLoc, double yLoc, double health, double sprint, double speed) {
        super(xLoc, yLoc);
        healthValue = health;
        sprintValue = sprint;
        speedValue = speed;
    }

    public void ApplyUpdates(Camera camera){
        if(camera.sprint && sprintValue <= 100 && sprintValue >= 0)
            sprintValue--;
        else
            sprintValue++;

        if(sprintValue >= 100)
            sprintValue = 100;
    }

    public double getXLocation(){
        return super.getXLoc();
    }

    public double getYLocation(){
        return super.getYLoc();
    }

    public double getSprintValue(){
        return sprintValue;
    }

    public double getHealthValue(){
        return healthValue;
    }
}
