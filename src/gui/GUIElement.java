package gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUIElement {

    private String fileLocation;
    private BufferedImage elementImage;
    private Rectangle elementBounds;
    private int elementWidth;
    private int elementHeight;
    private int[] pixels;

    public GUIElement(String location){
        this.fileLocation = location;
        load();
    }

    private void load(){
        try {
            elementImage = ImageIO.read(new File(fileLocation));
            elementWidth = elementImage.getWidth();
            elementHeight = elementImage.getHeight();
            elementImage.getRGB(0, 0, elementWidth, elementHeight, pixels, 0, elementWidth);
            elementBounds = new Rectangle(elementWidth, elementHeight);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static GUIElement optionsScreen = new GUIElement("src/gui/options_container.png");
    public static GUIElement button = new GUIElement("src/gui/button.png");
    public static GUIElement inventoryScreen = new GUIElement("src/gui/inventory_container.png");
    public static GUIElement barOverlay = new GUIElement("src/gui/bar_overlay.png");

    public static CustomButton saveGameButton = new CustomButton(320, 160, 40, "Save game", StyleColors.white);
    public static CustomButton loadGameButton = new CustomButton(320, 235, 40, "Load game", StyleColors.white);
    public static CustomButton exitGameButton = new CustomButton(325, 400, 40, "Exit game", StyleColors.black);

    public BufferedImage getElementImage() {
        return elementImage;
    }

    public Rectangle getElementBounds(){
        return elementBounds;
    }

    public int[] getPixels() {
        return pixels;
    }
}
