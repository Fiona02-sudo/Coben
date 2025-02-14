package comp1110.ass2;

import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Board {
    private Square[][] grid; // grid of 9 rows and 5 columns
    private Map<Tile, List<Position>> tilePositions;
    private Set<Integer> scoredRows;      // Track scored rows
    private Set<Integer> scoredColumns;   // Track scored columns
    private Set<Integer> shieldRows;
    private Set<Integer> shieldColumns;
    private Set<String> unlockedShields;

    public static final int WIDTH = 5;   // Number of columns
    public static final int HEIGHT = 9;  // Number of rows

    public Board() {
        this.shieldRows = new HashSet<>();
        this.shieldColumns = new HashSet<>();
        this.unlockedShields = new HashSet<>();

        shieldRows.add(1);
        shieldRows.add(3);
        shieldRows.add(5);

        shieldColumns.add(1);
        shieldColumns.add(3);

        this.grid = new Square[HEIGHT][WIDTH];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = new Square();
            }
        }
        this.scoredRows = new HashSet<>();
        this.scoredColumns = new HashSet<>();
        tilePositions = new HashMap<>();
    }

    public Square[][] getGrid() {
        return grid;
    }

    public boolean canPlaceTile(Tile tile) {
        List<Position> shape = tile.getShape();
        boolean restsOnExisting = false;

        for (int i = 0; i < shape.size(); i++) {
            Position pos = shape.get(i);
            int x = pos.getX();
            int y = pos.getY();

            // Check bounds
            if (y < 0 || y >= HEIGHT || x < 0 || x >= WIDTH) {
                System.out.println("Out of bounds");
                return false; // out of bounds
            }

            // Check if square is empty
            if (!grid[y][x].isEmpty()) {
                System.out.println("Overlaps existing tile");
                return false; // Overlaps existing tile
            }

            // Check if at least one square rests on the base or on an existing filled square
            if (y == 0 || (y > 0 && !grid[y - 1][x].isEmpty())) {
                restsOnExisting = true;
            }
        }

        if (!restsOnExisting) {
            System.out.println("Does not rest on base or existing tile");
            return false; // Does not rest on base or existing tile
        }

        return true; // All checks passed
    }

    public void placeTile(Tile tile) {
        if (canPlaceTile(tile)) {
            List<Position> shape = tile.getShape();
            boolean[] windows = tile.getWindows();
            List<Position> placedPositions = new ArrayList<>();

            for (int i = 0; i < shape.size(); i++) {
                Position pos = shape.get(i);
                int x = pos.getX();
                int y = pos.getY();

                grid[y][x].setTile(tile, windows[i]);
                placedPositions.add(new Position(x, y));
                System.out.println("Placed tile " + tile.getName() + " at (" + x + ", " + y + "), window: " + windows[i]);
            }
            tilePositions.put(tile, placedPositions);
            printBoardTiles();
        } else {
            throw new IllegalArgumentException("Tile cannot be placed at the specified position.");
        }
    }

    public void addWindowsToTile(Tile tile) {
        List<Position> positions = tilePositions.get(tile);
        if (positions != null) {
            // Update the windows array in the tile
            tile.addWindows();

            // Update the squares on the board
            boolean[] windows = tile.getWindows();
            for (int i = 0; i < positions.size(); i++) {
                Position pos = positions.get(i);
                int x = pos.getX();
                int y = pos.getY();
                grid[y][x].setHasWindow(windows[i]);
            }
        } else {
            throw new IllegalArgumentException("Tile not found on the board.");
        }
    }

    public int calculateScore() {
        int score = 0;

        // Check each row
        for (int y = 0; y < HEIGHT; y++) {
            boolean rowComplete = true;
            boolean allWindows = true;
            for (int x = 0; x < WIDTH; x++) {
                Square square = grid[y][x];
                if (square.isEmpty()) {
                    rowComplete = false;
                    break;
                }
                if (!square.hasWindow()) {
                    allWindows = false;
                }
            }
            if (rowComplete) {
                score += 1;
                if (allWindows) {
                    score += 1;
                }
            }
        }

        // Check each column
        for (int x = 0; x < WIDTH; x++) {
            boolean columnComplete = true;
            boolean allWindows = true;
            for (int y = 0; y < HEIGHT; y++) {
                Square square = grid[y][x];
                if (square.isEmpty()) {
                    columnComplete = false;
                    break;
                }
                if (!square.hasWindow()) {
                    allWindows = false;
                }
            }
            if (columnComplete) {
                score += 2;
                if (allWindows) {
                    score += 2;
                }
            }
        }

        return score;
    }

    // Function to clear the board by resetting all squares
    public void clearBoard() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = new Square(); // Reset each square
            }
        }
        System.out.println("Board has been cleared.");
    }

    // Function to print all tiles and their positions on the board
    public void printBoardTiles() {
        System.out.println("Current Board Tiles:");
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (!grid[y][x].isEmpty()) {
                    System.out.println("Tile at (" + x + ", " + y + "): " + grid[y][x].getTile().getName() + ", Window: " + (grid[y][x].hasWindow() ? "Yes" : "No"));
                }
            }
        }
    }

    public Square getSquareAt(Position position) {
        int x = position.getX();
        int y = position.getY();
        if (y < 0 || y >= HEIGHT || x < 0 || x >= WIDTH) {
            throw new IllegalArgumentException("Position out of bounds.");
        }
        return grid[y][x];
    }

    // Function to return the count of distinct tiles placed on the board
    public int getTileCount() {
        Set<Tile> uniqueTiles = new HashSet<>();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (!grid[y][x].isEmpty()) {
                    uniqueTiles.add(grid[y][x].getTile());
                }
            }
        }

        return uniqueTiles.size();
    }

    // Check if the specified shield is unlocked
    public boolean isShieldUnlocked(String shieldId) {
        return unlockedShields.contains(shieldId);
    }

    // Check for completion status and see if any shields are unlocked
    public List<CompletionInfo> checkForCompletions() {
        List<CompletionInfo> completions = new ArrayList<>();

        // Check rows
        for (int y = 0; y < HEIGHT; y++) {
            if (scoredRows.contains(y)) continue; // Skip rows that have already been scored

            boolean isComplete = true;
            boolean allWindows = true;

            for (int x = 0; x < WIDTH; x++) {
                Square square = grid[y][x];
                if (square.isEmpty()) {
                    isComplete = false;
                    break;
                }
                if (!square.hasWindow()) {
                    allWindows = false;
                }
            }

            if (isComplete) {
                CompletionInfo info = new CompletionInfo();
                info.isRow = true;
                info.index = y;
                info.allWindows = allWindows;
                info.hasShield = shieldRows.contains(y);
                completions.add(info);
            }
        }

        // Check columns
        for (int x = 0; x < WIDTH; x++) {
            if (scoredColumns.contains(x)) continue; // Skip columns that have already been scored

            boolean isComplete = true;
            boolean allWindows = true;

            for (int y = 0; y < HEIGHT; y++) {
                Square square = grid[y][x];
                if (square.isEmpty()) {
                    isComplete = false;
                    break;
                }
                if (!square.hasWindow()) {
                    allWindows = false;
                }
            }

            if (isComplete) {
                CompletionInfo info = new CompletionInfo();
                info.isRow = false;
                info.index = x;
                info.allWindows = allWindows;
                info.hasShield = shieldColumns.contains(x);  // Use the new shield column
                completions.add(info);
            }
        }

        return completions;
    }

    // Mark score based on completed rows or columns
    public void markCompletionAsScored(CompletionInfo info) {
        if (info.isRow) {
            scoredRows.add(info.index);
        } else {
            scoredColumns.add(info.index);
        }
    }

    // Return newly unlocked shields
    public List<String> getUnlockedShields(List<CompletionInfo> completions) {
        List<String> newShields = new ArrayList<>();
        for (CompletionInfo info : completions) {
            if (info.hasShield) {
                String shieldId = (info.isRow ? "row" : "col") + info.index;
                if (!unlockedShields.contains(shieldId)) {
                    unlockedShields.add(shieldId);
                    newShields.add(shieldId);
                }
            }
        }
        return newShields;
    }


    public static class CompletionInfo {
        public boolean isRow; // true for row, false for column
        public int index;     // Index of the row or column
        public boolean allWindows; // Whether all squares have windows
        public boolean hasShield;  // Whether the row or column has a shield
    }


    public boolean isShieldRowCompleted(int y) {
        // Adjust y based on your board's coordinate system
        // Ensure y is within the valid range
        if (!shieldRows.contains(y)) {
            return false;
        }
        for (int x = 0; x < WIDTH; x++) {
            if (grid[y][x].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isShieldColumnCompleted(int x) {
        // Adjust x based on your board's coordinate system
        // Ensure x is within the valid range
        if (!shieldColumns.contains(x)) {
            return false;
        }
        for (int y = 0; y < HEIGHT; y++) {
            if (grid[y][x].isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
