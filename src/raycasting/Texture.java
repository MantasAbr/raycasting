package raycasting;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Mantas Abramavičius
 */
public class Texture {
       
    //Used to hold the data for all the pixels in the image of a texture
    public int[] pixels;
    //Used to indicate where the texture is located
    private String location;
    //How big the texture is on one side
    public final int SIZE;
    public String name;
    
    /**
     * The constructor will initialize the loc and SIZE variables and call
     * the method to load the image data into pixels
     * @param location
     * @param size 
     */
    public Texture(String location, int size, String name){
        this.location = location;
        this.name = name;
        SIZE = size;
        pixels = new int[SIZE * SIZE];
        load();
    }
    
    /**
     * Used to get data from images and store them in an array of pixel data
     */
    protected void load(){
        try {
            BufferedImage image = ImageIO.read(new File(location));
            int width = image.getWidth();
            int height = image.getHeight();
            image.getRGB(0, 0, width, height, pixels, 0, width);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static Texture wood = new Texture("src/textures/wood.png", 64, "wood");
    public static Texture brick = new Texture("src/textures/brick.png", 64, "brick");
    public static Texture stone = new Texture("src/textures/stone.png", 64, "stone");
    public static Texture woodBricks = new Texture("src/textures/wood_brick.png", 64, "woodbrick");
    public static Texture door = new Texture("src/textures/door.png", 64, "door");
}

