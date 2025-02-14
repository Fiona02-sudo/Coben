package comp1110.ass2;

public class Square {
    private Tile tile; // The tile that occupies this square
    private boolean hasWindow;
    private boolean markedO = false;

    public boolean isMarkedO() {
        return markedO;
    }

    public void setMarkedO(boolean markedO) {
        this.markedO = markedO;
    }

    public Square() {
        this.tile = null;
        this.hasWindow = false;
    }

    public boolean isEmpty() {
        return tile == null;
    }

    public boolean hasWindow() {
        return hasWindow;
    }

    public void setTile(Tile tile, boolean hasWindow) {
        this.tile = tile;
        this.hasWindow = hasWindow;
    }

    public Tile getTile() {
        return tile;
    }
    public void setHasWindow(boolean hasWindow) {
        this.hasWindow = hasWindow;
    }

}
