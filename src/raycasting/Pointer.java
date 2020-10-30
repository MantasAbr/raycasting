package raycasting;

import input.Input;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Pointer{

    private BufferedImage pointerSprite;
    private Cursor pointer;
    private Robot robot;
    private GraphicsDevice gd;
    private Input input;

    public Pointer(Input input){
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

        pointerSprite = cursorImage;
        pointer = Toolkit.getDefaultToolkit().createCustomCursor(pointerSprite, new Point(0, 0), "blank");
        this.input = input;
    }

    public Cursor getPointer(){
        return pointer;
    }

    public void mouseMovementHandling(double sensitivity){

        if(input.newX <= 10 || input.newY <= 10)
            robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);
        if(input.newX >= Raycasting.WINDOW_WIDTH - 10 || input.newY >= Raycasting.WINDOW_HEIGHT - 10)
            robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);

        input.newX = input.mouseX;
        input.newY = input.mouseY;

        input.rotationValue = (Math.abs(input.newX - input.oldX) / sensitivity);

        if(input.newX > input.oldX)
            input.turningRight = true;
        if(input.newX < input.oldX)
            input.turningLeft = true;
        if(input.newX == input.oldX){
            input.turningRight = false;
            input.turningLeft = false;
        }

        input.oldX = input.newX;
        input.oldY = input.newY;
    }

    BufferedImage cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

}
