package items;

import gui.GUIElement;

import java.awt.*;

public class InventorySlot {

    private Rectangle bounds;
    private GUIElement image;
    private Item item;
    private int id;

    public InventorySlot(int id, int x, int y, GUIElement image, Item item){
        this.id = id;
        this.image = image;
        this.item = item;
        bounds = new Rectangle(x, y, image.getElementWidth(), image.getElementHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public GUIElement getImage() {
        return image;
    }

    public Item getItem() {
        return item;
    }

    public void setImage(GUIElement image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public static InventorySlot first = new InventorySlot(0, 161, 200, GUIElement.inventorySlot, Item.empty);
    public static InventorySlot second = new InventorySlot(1, 261, 200, GUIElement.inventorySlot, Item.sword);
    public static InventorySlot third = new InventorySlot(2, 361, 200, GUIElement.inventorySlot, Item.empty);
    public static InventorySlot fourth = new InventorySlot(3, 461, 200, GUIElement.inventorySlot, Item.gun);
    public static InventorySlot fifth = new InventorySlot(4, 561, 200, GUIElement.inventorySlot, Item.empty);

}
