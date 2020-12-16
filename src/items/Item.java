package items;

import sprites.Sprite;

public class Item {

    private String name;
    private Sprite sprite;

    public Item(){}

    public Item(String name, Sprite sprite){
        this.name = name;
        this.sprite = sprite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public static Item sword = new Item("Sword", Sprite.swordSprite);
    public static Item gun = new Item("Gun", Sprite.swordSprite);
    public static Item food = new Item("Food", Sprite.swordSprite);
    public static Item water = new Item("Water", Sprite.swordSprite);
    public static Item bottle = new Item("Bottle", Sprite.swordSprite);
}
