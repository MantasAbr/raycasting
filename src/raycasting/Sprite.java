package raycasting;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Mantas Abramavičius
 */
public class Sprite{

    private double xLoc;
    private double yLoc;
    public int[] pixels;
    private String location;
    private String name;
    private int spriteWidth;
    private int spriteHeight;
    private int texture;

    public Sprite(double xLoc, double yLoc, String location, String name, int texture){
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.location = location;
        this.name = name;
        this.texture = texture;
        load();
    }

    private void load(){
        try {
            BufferedImage image = ImageIO.read(new File(location));
            spriteWidth = image.getWidth();
            spriteHeight = image.getHeight();
            pixels = new int[spriteWidth * spriteHeight];
            image.getRGB(0, 0, spriteWidth, spriteHeight, pixels, 0, spriteWidth);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public double getXLoc(){
        return xLoc;
    }

    public double getYLoc(){
        return yLoc;
    }

    public int getSpriteWidth(){return spriteWidth;}

    public int getSpriteHeight(){return spriteHeight;}

    public int getTexture(){return texture;}

    //public static Sprite test = new Sprite(7.5, 7.5, "src/sprites/lamp.png", "test", 0);
}
