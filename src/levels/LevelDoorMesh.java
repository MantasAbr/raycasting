package levels;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LevelDoorMesh {

    private int mapWidth;
    private int mapHeight;
    private int[][] map;
    private String location;

    public LevelDoorMesh(String location) {
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

    public int[][] getMap(){
        return map;
    }

    public int getMapWidth(){
        return mapWidth;
    }

    public int getMapHeight(){
        return mapHeight;
    }

    public static LevelDoorMesh firstLevelMesh = new LevelDoorMesh("src/levels/source/01_doors.txt");
    public static LevelDoorMesh secondLevelMesh = new LevelDoorMesh("src/levels/source/02_doors.txt");
    public static LevelDoorMesh thirdLevelMesh = new LevelDoorMesh("src/levels/source/03_doors.txt");
}
