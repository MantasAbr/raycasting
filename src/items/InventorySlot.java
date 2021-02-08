package items;

import gui.GUIElement;
import raycasting.Raycasting;

import java.awt.*;

public class InventorySlot {

    private Rectangle bounds;
    private GUIElement image;
    private Item item;
    private int id;

    public InventorySlot(){}

    public InventorySlot(InventorySlot item){
        this.bounds = item.getBounds();
        this.image = item.getImage();
        this.item = item.getItem();
        this.id = item.getId();
    }

    public InventorySlot(int id, int x, int y, GUIElement image, Item item){
        this.id = id;
        this.image = image;
        this.item = item;
        bounds = new Rectangle(x, y, image.getElementWidth(), image.getElementHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getBoundsX(){return bounds.x;}

    public GUIElement getImage() {
        return image;
    }

    public Item getItem() {
        return item;
    }

    public int getId() {
        return id;
    }


    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void setBounds(int x){
        this.bounds.x = x;
    }

    public void setImage(GUIElement image) {
        this.image = image;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Because the inventory has an uneven number of slots, and we want them to snap horizontally in the middle
    // of the screen, we can simply draw the middle one in the center of the screen, and then reference its location for
    // the other slots
    public static InventorySlot third = new InventorySlot(3, Raycasting.SCREEN_WIDTH / 2 - GUIElement.inventorySlot.getElementXCenter(),
                                                          Raycasting.SCREEN_HEIGHT / 2 - GUIElement.inventorySlot.getElementYCenter(),
                                                             GUIElement.inventorySlot, Item.gun);

    public static InventorySlot first = new InventorySlot(1, third.getBoundsX() + 200, Raycasting.SCREEN_HEIGHT / 2 - GUIElement.inventorySlot.getElementYCenter(), GUIElement.inventorySlot, Item.empty);
    public static InventorySlot second = new InventorySlot(2, third.getBoundsX() + 100, Raycasting.SCREEN_HEIGHT / 2 - GUIElement.inventorySlot.getElementYCenter(), GUIElement.inventorySlot, Item.sword);
    public static InventorySlot fourth = new InventorySlot(4, third.getBoundsX() - 100, Raycasting.SCREEN_HEIGHT / 2 - GUIElement.inventorySlot.getElementYCenter(), GUIElement.inventorySlot, Item.empty);
    public static InventorySlot fifth = new InventorySlot(5, third.getBoundsX() - 200, Raycasting.SCREEN_HEIGHT / 2 - GUIElement.inventorySlot.getElementYCenter(), GUIElement.inventorySlot, Item.empty);

}
