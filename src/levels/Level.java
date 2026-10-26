package levels;

import sprites.Entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Level {

    private int mapWidth;
    private int mapHeight;
    private double playerLocX;
    private double playerLocY;
    private int[][] map;
    private String location;
    private ArrayList<Entity> sprites;
    private final List<WallSegment> walls = new ArrayList<>();

    public Level(double playerX, double playerY, String location, ArrayList<Entity> sprites) {
        this.playerLocX = playerX;
        this.playerLocY = playerY;
        this.location = location;
        this.sprites = sprites;
        loadFromFile();
    }

    private void loadFromFile(){
        try (Scanner input = new Scanner(new File(location))) {
            input.useLocale(Locale.ROOT);

            //Get the dimensions of the level
            mapHeight = input.nextInt();
            mapWidth = input.nextInt();
            if (mapHeight <= 0 || mapWidth <= 0)
                throw new IllegalArgumentException("Map dimensions must be positive in " + location);
            map = new int[mapHeight][mapWidth];

            for(int i = 0; i < mapHeight; i++){
                for(int j = 0; j < mapWidth; j++){
                    map[i][j] = input.nextInt();
                }
            }
            if (input.hasNext()) {
                if (!input.next().equals("WALLS"))
                    throw new IllegalArgumentException("Expected WALLS section in " + location);
                int count = input.nextInt();
                if (count < 0)
                    throw new IllegalArgumentException("Negative wall count in " + location);
                for (int i = 0; i < count; i++) {
                    double x1 = input.nextDouble(), y1 = input.nextDouble();
                    double x2 = input.nextDouble(), y2 = input.nextDouble();
                    int texture = input.nextInt();
                    double thickness = input.nextDouble();
                    if (Math.min(x1, x2) < 0 || Math.max(x1, x2) > map.length
                            || Math.min(y1, y2) < 0 || Math.max(y1, y2) > map[0].length)
                        throw new IllegalArgumentException("Wall endpoints outside map in " + location);
                    walls.add(new WallSegment(x1, y1, x2, y2, texture, thickness));
                }
                if (input.hasNext())
                    throw new IllegalArgumentException("Unexpected data after walls in " + location);
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

    public List<WallSegment> getWalls() {
        return Collections.unmodifiableList(walls);
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

    public static Level firstLevel = new Level(1.5, 1.5, "src/levels/source/01.txt", Entity.firstLevelEntities);
    public static Level secondLevel = new Level(2, 4.5, "src/levels/source/02.txt", Entity.secondLevelEntities);
    public static Level thirdLevel = new Level(8.5, 1.5, "src/levels/source/03.txt", Entity.firstLevelEntities);


}
