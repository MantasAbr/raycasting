package gui;

import java.awt.*;

public class StyleColors {

    private Color color;

    public StyleColors(int r, int g, int b){
        color = new Color(r, g, b);
    }

    public Color getColor() {
        return color;
    }

    public static StyleColors darkred = new StyleColors(139, 0, 0);
    public static StyleColors white = new StyleColors(255, 255, 255);
}
