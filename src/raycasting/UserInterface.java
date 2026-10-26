package raycasting;

import gui.CustomButton;
import gui.GUIElement;
import gui.StyleColors;
import input.Input;
import items.InventorySlot;
import items.Inventory;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class UserInterface {

    private Player player;
    private ArrayList<GUIElement> gui;
    private Inventory inventorySlots;
    public static ArrayList<CustomButton> buttons;

    private final Point sprintBarPoint = new Point(140, Raycasting.SCREEN_HEIGHT - 50);
    private final Point healthBarPoint = new Point(140, Raycasting.SCREEN_HEIGHT - 100);
    private final int barHeight = 20;
    private final Point selectedItemPoint = new Point ((int)(Raycasting.SCREEN_WIDTH - Raycasting.SCREEN_WIDTH * 0.05), Raycasting.SCREEN_HEIGHT - 100);


    private final Font font = new Font("Yoster Island", Font.PLAIN ,22);
    private final Font inventoryText = new Font("Yoster Island", Font.PLAIN, 60);
    private final Font inventoryMessageText = new Font("Yoster Island", Font.PLAIN, 18);

    // Text offsets inside the inventory panel, independent of monitor resolution.
    private static final int INVENTORY_TITLE_TOP_PADDING = 24;
    private static final int INVENTORY_MESSAGE_BOTTOM_PADDING = 32;

    private Rectangle exitButton;
    private Rectangle saveGameButton;
    private Rectangle loadGameButton;

    public UserInterface(Player player, ArrayList<GUIElement> gui, ArrayList<CustomButton> buttons, Inventory inventorySlots){
        this.player = player;
        this.gui = gui;
        this.buttons = buttons;
        this.inventorySlots = inventorySlots;
    }

    // In-game GUI elements
    public void drawInterface(Graphics g, boolean shouldDraw){
        if(shouldDraw){
            g.setFont(font);
            drawSprintInfo(g, player);
            drawHealthInfo(g, player);
            drawSelectedItem(g, inventorySlots);
        }
    }

    private void drawHealthInfo(Graphics g, Player player){
        g.setColor(Color.WHITE);
        g.drawString("Health", healthBarPoint.x - 110, (int)(healthBarPoint.y + barHeight - 1));
        g.setColor(Color.RED);
        g.fillRect(healthBarPoint.x, healthBarPoint.y, (int)(player.getHealthValue() * 1.5), barHeight);
        g.drawImage(gui.get(3).getElementImage(), healthBarPoint.x - 3, healthBarPoint.y - 3, null);
    }

    private void drawSprintInfo(Graphics g, Player player){
        g.setColor(Color.WHITE);
        g.drawString("Sprint", sprintBarPoint.x - 110, (int)(sprintBarPoint.y + barHeight - 1));
        g.setColor(Color.BLUE);
        g.fillRect(sprintBarPoint.x, sprintBarPoint.y, (int)(player.getSprintValue() * 1.5), barHeight);
        g.drawImage(gui.get(3).getElementImage(), sprintBarPoint.x - 3, sprintBarPoint.y - 3, null);
    }

    private void drawSelectedItem(Graphics g, Inventory items){
        InventorySlot selected = items.getCurrentItem();
        if (selected != null)
            g.drawImage(selected.getItem().getSprite().getSpriteImage(), selectedItemPoint.x, selectedItemPoint.y, null);
    }

    // Options screen GUI elements
    public void drawOptionsScreen(Graphics g){

        g.drawImage(gui.get(0).getElementImage(), (Raycasting.SCREEN_WIDTH / 2) - (gui.get(0).getElementWidth() / 2),
                (Raycasting.SCREEN_HEIGHT / 2) - (Raycasting.SCREEN_HEIGHT / 5), null);

        saveGameButton = drawButton(g, buttons.get(0).getX(), buttons.get(0).getY(),
                                    buttons.get(0).getHeight(), buttons.get(0).getText(), font, buttons.get(0).getTextColor());

        loadGameButton = drawButton(g, buttons.get(1).getX(), buttons.get(1).getY(),
                                    buttons.get(1).getHeight(), buttons.get(1).getText(), font, buttons.get(1).getTextColor());

        exitButton = drawButton(g, buttons.get(2).getX(), buttons.get(2).getY(),
                                    buttons.get(2).getHeight(), buttons.get(2).getText(), font, buttons.get(2).getTextColor());
    }

    // Inventory screen GUI elements
    public void drawInventoryScreen(Graphics g){
        GUIElement panel = gui.get(2);
        int panelX = Raycasting.SCREEN_WIDTH / 2 - panel.getElementWidth() / 2;
        int panelY = Raycasting.SCREEN_HEIGHT / 2 - panel.getElementHeight() / 2;
        g.drawImage(panel.getElementImage(), panelX, panelY, null);

        g.setColor(StyleColors.darkbrown);
        drawCenteredString(g, "Inventory", panelX, panelY + INVENTORY_TITLE_TOP_PADDING,
                panel.getElementWidth(), g.getFontMetrics(inventoryText).getHeight(), inventoryText);
        drawInventorySlots(g, inventorySlots);

        if(Input.carryMessageAlertOn){
            int messageHeight = g.getFontMetrics(inventoryMessageText).getHeight();
            drawCenteredString(g, "select item to carry with right-click", panelX,
                    panelY + panel.getElementHeight() - INVENTORY_MESSAGE_BOTTOM_PADDING - messageHeight,
                    panel.getElementWidth(), messageHeight, inventoryMessageText);
        }
    }

    public void drawInventorySlots(Graphics g, Inventory inventoryList){
        for (InventorySlot current : inventoryList.getSlots()) {
            g.drawImage(current.getImage().getElementImage(), current.getBounds().x, current.getBounds().y, null);
            g.drawImage(current.getItem().getSprite().getSpriteImage(), current.getBounds().x + 5, current.getBounds().y + 5, null);
            if (current == inventoryList.getCurrentItem()) {
                Rectangle bounds = current.getBounds();
                g.setColor(StyleColors.darkbrown);
                g.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 3, bounds.height + 3);
            }
        }
    }


    // Utility functions
    private Rectangle drawButton(Graphics g, int x, int y, int height, String text, Font font, Color fontColor){

        g.setColor(fontColor);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(text, g);
        int offset = 30;

        int centeredX = (int)(x - ((bounds.getWidth() + offset) / 2));
        int centeredY = (int)(y - (height / 2));

        Rectangle button = new Rectangle(centeredX, centeredY, (int) bounds.getWidth() + offset, height);
        g.drawImage(gui.get(1).getElementImage().getScaledInstance((int) bounds.getWidth() + offset, height, 1), centeredX, centeredY, null);

        int stringX = centeredX + (int)(offset / 2);
        int stringY = centeredY + (int)((height / 2) + (int)(bounds.getHeight() / 3));
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
