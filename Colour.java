package comp1110.ass2.gui;

import javafx.scene.paint.Color;
import java.util.List;

public enum Colour {
    RED(Color.RED, "Red"),
    BLUE(Color.BLUE, "Blue"),
    PURPLE(Color.PURPLE, "Purple"),
    GREEN(Color.GREEN, "Green"),
    YELLOW(Color.YELLOW, "Yellow"),
    WHITE(Color.WHITE, "White"),
    GREY(Color.GREY, "Grey"); // Changed "GRAY" to "GREY" and "Gray" to "Grey"

    private Color fxColor;
    private String name;

    Colour(Color fxColor, String name) {
        this.fxColor = fxColor;
        this.name = name;
    }

    public Color getFXColor() {
        return fxColor;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Colour getColour(String name) {
        if (name == null) {
            return null;
        }
        String input = name.trim().toLowerCase();
        for (Colour value : Colour.values()) {
            String colourName = value.toString().toLowerCase();
            if (colourName.equals(input) || colourName.startsWith(input)) {
                return value;
            }
        }
        return null;
    }

}
