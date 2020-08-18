package raycasting;

public class Player{

    private double healthValue;
    private double sprintValue;
    private double speedValue;

    public Player(double health, double sprint, double speed) {
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

    public double getSprintValue(){
        return sprintValue;
    }

    public double getHealthValue(){
        return healthValue;
    }
}
