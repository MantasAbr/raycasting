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

    public int[] pixels;
    private String fileLocation;
    private BufferedImage spriteImage;
    private int spriteWidth;
    private int spriteHeight;

    public Sprite(String location){
        this.fileLocation = location;
        load();
    }

    public Sprite(){

    }

    private void load(){
        try {
            spriteImage = ImageIO.read(new File(fileLocation));
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

    public static Sprite menuCursor = new Sprite("src/sprites/menu_cursor.png");
    public static Sprite blankCursor = new Sprite("src/sprites/blank_cursor.png");


    public static Sprite swordSprite = new Sprite("src/sprites/sword.png");
    public static Sprite pistolSprite = new Sprite("src/sprites/pistol.png");
    public static Sprite blankItem = new Sprite();
}
