package gui;

import raycasting.Raycasting;

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
    public long elapsedTime;

    public GUIElement(String location){
        this.fileLocation = location;
        load();
    }

    private void load(){
        try {
            long startTime = System.currentTimeMillis();
            elementImage = ImageIO.read(new File(fileLocation));
            elementWidth = elementImage.getWidth();
            elementHeight = elementImage.getHeight();
            elementImage.getRGB(0, 0, elementWidth, elementHeight, pixels, 0, elementWidth);
            elementBounds = new Rectangle(elementWidth, elementHeight);
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static GUIElement optionsScreen = new GUIElement("src/gui/options_container.png");
    public static GUIElement button = new GUIElement("src/gui/button.png");
    public static GUIElement inventoryScreen = new GUIElement("src/gui/inventory_container.png");
    public static GUIElement barOverlay = new GUIElement("src/gui/bar_overlay.png");
    public static GUIElement inventorySlot = new GUIElement("src/gui/inventory_slot.png");
    public static GUIElement lighterInventorySlot = new GUIElement("src/gui/inventory_slot_lighter.png");

    public static CustomButton saveGameButton = new CustomButton(Raycasting.SCREEN_WIDTH / 2, 160 + Raycasting.SCREEN_HEIGHT / 4, 40, "Save game", StyleColors.white);
    public static CustomButton loadGameButton = new CustomButton(Raycasting.SCREEN_WIDTH / 2, 235 + Raycasting.SCREEN_HEIGHT / 4, 40, "Load game", StyleColors.white);
    public static CustomButton exitGameButton = new CustomButton(Raycasting.SCREEN_WIDTH / 2, 400 + Raycasting.SCREEN_HEIGHT / 4, 40, "Exit game", StyleColors.black);

    public BufferedImage getElementImage() {
        return elementImage;
    }

    public void setElementImage(BufferedImage elementImage) {
        this.elementImage = elementImage;
    }

    public Rectangle getElementBounds(){
        return elementBounds;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getElementWidth() {
        return elementWidth;
    }

    public void setElementWidth(int elementWidth) {
        this.elementWidth = elementWidth;
    }

    public int getElementHeight() {
        return elementHeight;
    }

    public void setElementHeight(int elementHeight) {
        this.elementHeight = elementHeight;
    }

    public int getElementXCenter(){
        return (int)elementWidth / 2;
    }

    public int getElementYCenter(){
        return (int)elementHeight / 2;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

}
