package sprites;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Mantas Abramavičius
 */
public class Sprite{


    protected String fileLocation;
    protected BufferedImage[] spriteImageSides = new BufferedImage[4];
    protected int spriteWidth;
    protected int spriteHeight;

    public int[] pixels;
    protected BufferedImage spriteImage;

    public Sprite(String location){
        this.fileLocation = location;
        load(fileLocation);
    }

    public Sprite(String first, String second, String third, String fourth){
        loadSides(first, second, third, fourth);
    }

    public Sprite(){}

    private void loadSides(String first, String second, String third, String fourth){
        try {
            spriteImage = ImageIO.read(new File(first));
            spriteImageSides[0] = ImageIO.read(new File(first));
            spriteImageSides[1] = ImageIO.read(new File(second));
            spriteImageSides[2] = ImageIO.read(new File(third));
            spriteImageSides[3] = ImageIO.read(new File(fourth));
            spriteWidth = spriteImageSides[0].getWidth();
            spriteHeight = spriteImageSides[0].getHeight();
            pixels = new int[spriteWidth * spriteHeight];
            spriteImage.getRGB(0, 0, spriteWidth, spriteHeight, pixels, 0, spriteWidth);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void load(String location){
        try {
            spriteImage = ImageIO.read(new File(location));
            spriteWidth = spriteImage.getWidth();
            spriteHeight = spriteImage.getHeight();
            pixels = new int[spriteWidth * spriteHeight];
            //makeTransparent(spriteImage);
            spriteImage.getRGB(0, 0, spriteWidth, spriteHeight, pixels, 0, spriteWidth);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void makeTransparent(BufferedImage image){
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int argb = image.getRGB(x, y);
                if ((argb & 0x00FFFFFF) == 0x000000FF)
                {
                    image.setRGB(x, y, 0);
                }
            }
        }
    }

    public int getSpriteWidth(){return spriteWidth;}

    public int getSpriteHeight(){return spriteHeight;}

    public BufferedImage getSpriteImage(){return spriteImage;}

    public BufferedImage getSpriteImageSide(int side){return spriteImageSides[side];}

    public static Sprite menuCursor = new Sprite("src/sprites/img/menu_cursor.png");
    public static Sprite blankCursor = new Sprite("src/sprites/img/blank_cursor.png");


    public static Sprite swordSprite = new Sprite("src/sprites/img/sword.png");
    public static Sprite pistolSprite = new Sprite("src/sprites/img/pistol.png");
    public static Sprite blankItem = new Sprite();
}
