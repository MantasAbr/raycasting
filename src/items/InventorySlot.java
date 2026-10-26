package items;

import gui.GUIElement;

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

}
