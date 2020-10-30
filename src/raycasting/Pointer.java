package raycasting;

import input.Input;
import sprites.Sprite;

import javax.tools.Tool;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Pointer{

    private BufferedImage pointerSprite;
    private Cursor pointer;
    public Robot robot;
    private GraphicsDevice gd;

    public Pointer(BufferedImage pointerImage){
        try{
            robot = new Robot();
            gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            Raycasting.SCREEN_WIDTH = gd.getDisplayMode().getWidth();
            Raycasting.SCREEN_HEIGHT = gd.getDisplayMode().getHeight();
            robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);
        }
        catch (AWTException e){
            System.exit(1);
        }

        pointerSprite = pointerImage;//new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        pointer = Toolkit.getDefaultToolkit().createCustomCursor(pointerSprite, new Point(0, 0), "blank");
    }

    public static Pointer gamePointer = new Pointer(Sprite.menuCursor.getSpriteImage());
    public static Pointer blankPointer = new Pointer(Sprite.blankCursor.getSpriteImage());

    public Cursor getPointer(){
        return pointer;
    }

    public void setPointerSprite(BufferedImage sprite){
        pointerSprite = sprite;
    }

    public void setMenuCursorImage(){
        pointerSprite = Sprite.menuCursor.getSpriteImage();
        pointer = Toolkit.getDefaultToolkit().createCustomCursor(pointerSprite, new Point(0, 0), "menu");
    }

    public void setGameCursorImage(){

    }


}
