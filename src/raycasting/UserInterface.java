package raycasting;

import gui.CustomButton;
import gui.GUIElement;
import gui.StyleColors;
import input.Input;
import items.InventorySlot;
import items.ItemLinkedList;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class UserInterface {

    private Player player;
    private ArrayList<GUIElement> gui;
    private ItemLinkedList inventorySlots;
    public static ArrayList<CustomButton> buttons;

    private Point SprintBarPoint = new Point(140, Raycasting.WINDOW_HEIGHT - 50);
    private Point HealthBarPoint = new Point(140, Raycasting.WINDOW_HEIGHT - 100);
    private int barHeight = 20;

    private Font font = new Font("Yoster Island", Font.PLAIN ,22);
    private Font inventoryText = new Font("Yoster Island", Font.PLAIN, 60);

    private Rectangle exitButton;
    private Rectangle saveGameButton;
    private Rectangle loadGameButton;

    public UserInterface(Player player, ArrayList<GUIElement> gui, ArrayList<CustomButton> buttons, ItemLinkedList inventorySlots){
        this.player = player;
        this.gui = gui;
        this.buttons = buttons;
        this.inventorySlots = inventorySlots;
    }

    public void drawInterface(Graphics g, boolean shouldDraw){
        if(shouldDraw){
            g.setFont(font);
            drawSprintInfo(g, player);
            drawHealthInfo(g, player);
        }
        else
            g.dispose();
    }

    private void drawHealthInfo(Graphics g, Player player){
        g.setColor(Color.WHITE);
        g.drawString("Health", HealthBarPoint.x - 110, (int)(HealthBarPoint.y + barHeight - 1));
        g.setColor(Color.RED);
        g.fillRect(HealthBarPoint.x, HealthBarPoint.y, (int)(player.getHealthValue() * 1.5), barHeight);
        g.drawImage(gui.get(3).getElementImage(), HealthBarPoint.x - 3, HealthBarPoint.y - 3, null);
    }

    private void drawSprintInfo(Graphics g, Player player){
        g.setColor(Color.WHITE);
        g.drawString("Sprint", SprintBarPoint.x - 110, (int)(SprintBarPoint.y + barHeight - 1));
        g.setColor(Color.BLUE);
        g.fillRect(SprintBarPoint.x, SprintBarPoint.y, (int)(player.getSprintValue() * 1.5), barHeight);
        g.drawImage(gui.get(3).getElementImage(), SprintBarPoint.x - 3, SprintBarPoint.y - 3, null);
    }

    public void drawOptionsScreen(Graphics g){
        g.drawImage(gui.get(0).getElementImage(), 250, 100, null);

        saveGameButton = drawButton(g, buttons.get(0).getX(), buttons.get(0).getY(), buttons.get(0).getHeight(), buttons.get(0).getText(), font, buttons.get(0).getTextColor());
        loadGameButton = drawButton(g, buttons.get(1).getX(), buttons.get(1).getY(), buttons.get(1).getHeight(), buttons.get(1).getText(), font, buttons.get(1).getTextColor());
        exitButton = drawButton(g, buttons.get(2).getX(), buttons.get(2).getY(), buttons.get(2).getHeight(), buttons.get(2).getText(), font, buttons.get(2).getTextColor());
    }

    public void drawInventoryScreen(Graphics g){
        g.drawImage(gui.get(2).getElementImage(), 100, 100, null);
        drawCenteredString(g, "Inventory", 0, 0, Raycasting.WINDOW_WIDTH, 300, inventoryText);
        drawInventorySlots(g, inventorySlots);
    }

    public void drawInventorySlots(Graphics g, ItemLinkedList inventoryList){
        for(int i = 0; i < inventoryList.getCount(); i++){
            InventorySlot current = inventoryList.getCurrentItem();
            g.drawImage(current.getImage().getElementImage(), current.getBounds().x, current.getBounds().y, null);
            inventoryList.goRight();
        }
    }

    private Rectangle drawButton(Graphics g, int x, int y, int height, String text, Font font, Color fontColor){

        g.setColor(fontColor);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(text, g);
        int offset = 30;

        g.drawImage(gui.get(1).getElementImage().getScaledInstance((int) bounds.getWidth() + offset, height, 1), x, y, null);
        Rectangle button = new Rectangle(x, y, (int) bounds.getWidth() + offset, height);

        int stringX = x + (int)(offset / 2);
        int stringY = y + (int)((height / 2) + (int)(bounds.getHeight() / 3));
        g.drawString(text, stringX, stringY);

        return button;
    }

    private void drawCenteredString(Graphics g, String text, int boundX, int boundY, int width, int height, Font font){
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = boundX + (width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = boundY + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public static void resetTextColor(ArrayList<CustomButton> buttons){
        for(CustomButton button : buttons){
            if(button.getText() == "Exit game")
                button.setTextColor(StyleColors.black);
            else
                button.setTextColor(StyleColors.white);
        }
    }

    public Rectangle getExitButton() {
        return exitButton;
    }
    public Rectangle getSaveGameButton() { return saveGameButton; }
    public Rectangle getLoadGameButton(){return loadGameButton;}
}
