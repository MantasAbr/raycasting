package levels;

import sprites.GameSprite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Level {

    private int mapWidth;
    private int mapHeight;
    private double playerLocX;
    private double playerLocY;
    private int[][] map;
    private String location;
    private ArrayList<GameSprite> sprites;

    public Level(double playerX, double playerY, String location, ArrayList<GameSprite> sprites) {
        this.playerLocX = playerX;
        this.playerLocY = playerY;
        this.location = location;
        this.sprites = sprites;
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

    public int getMapWidth(){
        return mapWidth;
    }

    public int getMapHeight(){
        return mapHeight;
    }


    public void setPlayerLocX(double location){
        this.playerLocX = location;
    }

    public void setPlayerLocY(double location){
        this.playerLocY = location;
    }

    public static Level firstLevel = new Level(1.5, 1.5, "src/levels/source/01.txt", GameSprite.firstLevelSprites);
    public static Level secondLevel = new Level(2, 4.5, "src/levels/source/02.txt", GameSprite.secondLevelSprites);
    public static Level thirdLevel = new Level(8.5, 1.5, "src/levels/source/03.txt", GameSprite.firstLevelSprites);


}
