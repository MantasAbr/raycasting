package gui;

import java.awt.*;

public class CustomButton{

    private int x;
    private int y;
    private int height;
    private double width;
    private String text;
    private Color textColor;

    public CustomButton(int x, int y, int height, String text, Color textColor){
        this.x = x;
        this.y = y;
        this.height = height;
        this.text = text;
        this.textColor = textColor;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWidth(){return width;}

    public void setWidth(double width){
        this.width = width;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

}
