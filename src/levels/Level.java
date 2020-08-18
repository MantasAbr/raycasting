package levels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Level {

    private int mapWidth;
    private int mapHeight;
    private double playerLocX;
    private double playerLocY;
    private int[][] map;
    private String location;

    public Level(double playerX, double playerY, String location) {
        this.playerLocX = playerX;
        this.playerLocY = playerY;
        this.location = location;
        loadFromFile();
    }

    private void loadFromFile(){
        try {
            Scanner input = new Scanner(new File(location));

            //Get the dimensions of the level
            mapHeight = input.nextInt();
            mapWidth = input.nextInt();
            map = new int[mapHeight][mapWidth];

            input.nextLine();

            for(int i = 0; i < mapHeight; i++){
                for(int j = 0; j < mapWidth; j++){
                    map[i][j] = input.nextInt();
                }
                input.nextLine();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public double getPlayerLocX(){
        return playerLocX;
    }

    public double getPlayerLocY(){
        return playerLocY;
    }

    public int[][] getMap(){
        return map;
    }

    public int getMapWidth(){ return mapWidth; }

    public int getMapHeight(){
        return mapHeight;
    }

    public static Level firstLevel = new Level(3, 3, "src/levels/source/01.txt");

}
