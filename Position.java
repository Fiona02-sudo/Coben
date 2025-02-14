package comp1110.ass2;

public class Position {
    private int x;
    private int y;
    // Represents a location on the board/grid.

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Key Methods
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    /**
     * Returns a new Position moved by the given delta.
     *
     * @param delta The position to move by.
     * @return A new Position shifted by delta.
     */
    public Position moveBy(Position delta) {
        return new Position(this.x + delta.x, this.y + delta.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Position) {
            Position p = (Position) obj;
            return this.x == p.x && this.y == p.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }
}
