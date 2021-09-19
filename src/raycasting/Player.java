package raycasting;

import input.Input;

public class Player{

    private double healthValue;
    private double sprintValue;
    private double speedValue;

    public Player(double health, double sprint, double speed) {
        healthValue = health;
        sprintValue = sprint;
        speedValue = speed;
    }

    public void ApplyUpdates(Input input){
        if(input.isSprinting && sprintValue <= 100 && sprintValue >= 0)
            sprintValue -= 1.5;
        else
            sprintValue += 1.5;

        if(sprintValue >= 100)
            sprintValue = 100;
    }

    public double getSprintValue(){
        return sprintValue;
    }

    public double getHealthValue(){
        return healthValue;
    }


    public void setSprintValue(double newSprintValue) {sprintValue = newSprintValue;}

    public void setHealthValue(double newHealthValue) {healthValue = newHealthValue;}
}
