package comp1110.ass2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Tile {
    private String name;
    private String color;
    private int size;
    private List<Position> shape; // positions of squares relative to (0,0)
    private boolean[] windows; // whether each square has a window or not

    public Tile(String name, String color, int size, List<Position> shape) {
        this.name = name;
        this.color = color;
        this.size = size;
        this.shape = shape;
        this.windows = new boolean[size];
        // Initialize windows: all squares have windows except one
        for (int i = 0; i < size; i++) {
            windows[i] = true;
        }
        windows[0] = false; // By default, first square has no window
    }

    public String getName() { return name; }
    public String getColor() { return color; }
    public int getSize() { return size; }
    public List<Position> getShape() { return shape; }
    public boolean[] getWindows() { return windows; }

    // In Tile class
    public void addWindows() {
        for (int i = 0; i < windows.length; i++) {
            windows[i] = true;
        }
        System.out.println("所有窗口已设置为 O.");
    }



    // Methods to manipulate windows
    public void setWindows(boolean[] windows) {
        if (windows.length != size) {
            throw new IllegalArgumentException("Windows array length does not match tile size");
        }
        this.windows = Arrays.copyOf(windows, windows.length);
    }

    public void rotateClockwise() {
        // Temporarily store the new positions after rotation
        List<Position> newPositions = new ArrayList<>();

        for (Position pos : shape) {
            int x = pos.getX();
            int y = pos.getY();

            // Rotate 90 degrees clockwise: x' = y, y' = -x
            int newX = y;
            int newY = -x;

            newPositions.add(new Position(newX, newY));
        }

        // Find the new minimum x and y for translation
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        for (Position pos : newPositions) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
        }

        int shiftX = -minX;
        int shiftY = -minY;

        // Update the coordinates in shape
        for (int i = 0; i < shape.size(); i++) {
            Position pos = newPositions.get(i);
            pos.setX(pos.getX() + shiftX);
            pos.setY(pos.getY() + shiftY);
            shape.set(i, pos);
        }
    }


    // Return a new Tile rotated clockwise by the given number of times
    public Tile rotated(int rotation) {
        Tile newTile = new Tile(name, color, size, deepCopyPositions(shape));
        for (int i = 0; i < rotation; i++) {
            newTile.rotateClockwise();
        }
        newTile.setWindows(Arrays.copyOf(this.windows, this.windows.length));
        System.out.println("Rotated Tile Shape: " + newTile.getShape());
        return newTile;

    }

    // Method to move the Tile by a given position
    public Tile moveBy(Position position) {
        List<Position> newShape = new ArrayList<>();

        // Traverse the shape and adjust each position by the given (x, y) of the position
        for (Position pos : shape) {
            int newX = pos.getX() + position.getX();
            int newY = pos.getY() + position.getY();
            newShape.add(new Position(newX, newY));
        }
        Tile newTile = new Tile(name, color, size, newShape);
        newTile.setWindows(Arrays.copyOf(this.windows, this.windows.length));
        System.out.println("Moved Tile Shape: " + newTile.getShape());
        // Return a new Tile with the adjusted positions
        return newTile;
    }

    // Helper method to deep copy the positions
    private List<Position> deepCopyPositions(List<Position> positions) {
        List<Position> newPositions = new ArrayList<>();
        for (Position pos : positions) {
            newPositions.add(new Position(pos.getX(), pos.getY()));
        }
        return newPositions;
    }

}
