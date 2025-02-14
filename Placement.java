package comp1110.ass2.gui;

import java.util.Arrays;

public class Placement {
    String tileName;
    int x;
    int y;
    int rotation;
    public boolean[] windows;

    Placement(String name, int size, int x, int y, int r) {
	this.tileName = name;
	this.x = x;
	this.y = y;
	this.rotation = r;
	windows = new boolean[size];
	for (int i = 1; i < size; i++)
	    windows[i] = true;
    }

    public String getTileName() {
	return tileName;
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public int getRotation() {
	return rotation;
    }

    public boolean getWindow(int i) {
	return windows[i];
    }

    public String toString() {
	return "(" + tileName
	    + ", x=" + Integer.toString(x)
	    + ", y=" + Integer.toString(y)
	    + ", r=" + Integer.toString(rotation)
	    + ", " + Arrays.toString(windows) + ")";
    }

    void setPosition(int x, int y) {
	this.x = x;
	this.y = y;
    }

    void movePosition(int dx, int dy) {
	this.x += dx;
	this.y += dy;
    }

    void rotateClockwise() {
	rotation = (rotation + 1) % 4;
    }

    void setNoBrick() {
	for (int i = 0; i < windows.length; i++) windows[i] = true;
    }

    public void setBrick(int index) {
        for (int i = 0; i < this.windows.length; i++) {
            this.windows[i] = (i != index);
        }
    }

}
