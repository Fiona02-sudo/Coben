package comp1110.ass2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

public class TileFactory {
    private static Map<String, int[][]> tiles = new HashMap<>();
    private static Set<String> usedLargeTiles = new HashSet<>(); // Records used large tiles

    static {
        // Initialize tiles map
        //Red tiles
        tiles.put("R2", new int[][]{{0, 0}, {0, 1}});
        tiles.put("R3", new int[][]{{0, 1, 1}, {0, 0, 1}});
        tiles.put("R4L", new int[][]{{0, 1, 0, 1}, {0, 0, 1, 1}});
        tiles.put("R4R", new int[][]{{0, 1, 0, 1}, {0, 0, 1, 1}});
        tiles.put("R5", new int[][]{{0, 1, 2, 1, 2}, {0, 0, 0, 1, 1}});
        //Blue tiles
        tiles.put("B2", new int[][]{{0, 0}, {0, 1}});
        tiles.put("B3", new int[][]{{0, 1, 1}, {0, 0, 1}});
        tiles.put("B4L", new int[][]{{0, 1, 1, 1}, {0, 0, 1, 2}});
        tiles.put("B4R", new int[][]{{0, 0, 0, 1}, {0, 1, 2, 0}});
        tiles.put("B5", new int[][]{{0, 1, 2, 1, 1}, {0, 0, 0, 1, 2}});
        //Purple tiles
        tiles.put("P2", new int[][]{{0, 0}, {0, 1}});
        tiles.put("P3", new int[][]{{0, 0, 0}, {0, 1, 2}});
        tiles.put("P4L", new int[][]{{0, 0, 0, 0}, {0, 1, 2, 3}});
        tiles.put("P4R", new int[][]{{0, 0, 0, 0}, {0, 1, 2, 3}});
        tiles.put("P5", new int[][]{{0, 1, 2, 3, 4}, {0, 0, 0, 0, 0}});
        //Green tiles
        tiles.put("G2", new int[][]{{0, 0}, {0, 1}});
        tiles.put("G3", new int[][]{{0, 0, 0}, {0, 1, 2}});
        tiles.put("G4L", new int[][]{{1, 1, 1, 0}, {0, 1, 2, 1}});
        tiles.put("G4R", new int[][]{{0, 0, 0, 1}, {0, 1, 2, 1}});
        tiles.put("G5", new int[][]{{1, 0, 2, 1, 1}, {1, 1, 1, 2, 0}});
        //Yellow tiles
        tiles.put("Y2", new int[][]{{0, 0}, {0, 1}});
        tiles.put("Y3", new int[][]{{0, 1, 1}, {0, 0, 1}});
        tiles.put("Y4L", new int[][]{{0, 0, 1, 1}, {0, 1, 1, 2}});
        tiles.put("Y4R", new int[][]{{1, 1, 0, 0}, {0, 1, 1, 2}});
        tiles.put("Y5", new int[][]{{0, 0, 1, 2, 2}, {0, 1, 1, 1, 2}});
        tiles.put("S1X", new int[][]{{0}, {0}});
    }


    public static Tile createTile(String name) {
        // Check if large tiles (size 4 and 5) have already been used
        if (isLargeTile(name) && isTileUsed(name)) {
            System.out.println("The tile " + name + " has already been used and cannot be used again.");
            return null; // This tile has been used and cannot be used again
        }
        int[][] coords = tiles.get(name);
        if (coords == null) return null;
        int[] xs = coords[0];
        int[] ys = coords[1];
        List<Position> shape = new ArrayList<>();
        for (int i = 0; i < xs.length; i++) {
            shape.add(new Position(xs[i], ys[i]));
        }
        String color = getColorFromName(name);
        int size = xs.length;
        return new Tile(name, color, size, shape);
    }

    public static void markTileAsUsed(String name) {
        if (isLargeTile(name)) {
            usedLargeTiles.add(name);
        }
    }


    /**
     * Check if the specified tile has already been used
     * @param name the name of the tile
     * @return true if the tile has been used, otherwise false
     */
    public static boolean isTileUsed(String name) {
        return usedLargeTiles.contains(name);
    }

    /**
     * Check if a tile is a large tile (size 4 or 5)
     * @param name the name of the tile
     * @return true if the tile is a size 4 or 5 tile, otherwise false
     */
    public static boolean isLargeTile(String name) {
        int[][] coords = tiles.get(name);
        if (coords == null) return false;
        int size = coords[0].length;
        return size >= 4;
    }

    /**
     * Get the color of a tile based on its name
     * @param name the name of the tile
     * @return the color as a string
     */
    private static String getColorFromName(String name) {
        char c = name.charAt(0);
        switch (c) {
            case 'R': return "Red";
            case 'B': return "Blue";
            case 'G': return "Green";
            case 'Y': return "Yellow";
            case 'P': return "Purple";
            case 'W': return "White";
            default: return "Grey";
        }
    }

    public static List<String> getAvailableTiles() {
        List<String> available = new ArrayList<>();
        for (String name : tiles.keySet()) {
            if (isLargeTile(name)) {
                if (!isTileUsed(name)) {
                    available.add(name);
                }
            } else {
                available.add(name);
            }
        }
        return available;
    }

    // New method to generate a list of available tiles based on the dice colors and quantities
    public static List<String> getAvailableTilesBasedOnDice(List<String> diceColors) {
        Map<String, Integer> colorCounts = new HashMap<>();
        String lastDie = diceColors.get(diceColors.size() - 1);
        // Count the number of dice for each color
        for (String color : diceColors) {
            if (color.equalsIgnoreCase("White")) {
                // White dice will be handled later
                continue;
            }
            colorCounts.put(color, colorCounts.getOrDefault(color, 0) + 1);
        }

        int whiteDiceCount = (int) diceColors.stream().filter(color -> color.equalsIgnoreCase("White")).count();

        // Handle white dice by distributing them to other colors, increasing their count
        // Simple approach: Distribute white dice evenly among existing colors

        List<String> colors = new ArrayList<>(colorCounts.keySet());
        int numColors = colors.size();
            for (int i1 = 0; i1 < colors.size(); i1++) {
                String color = colors.get(i1);
                colorCounts.put(color, colorCounts.get(color) + whiteDiceCount);
            }

        // Determine available tiles based on the number of dice for each color
        List<String> availableTiles = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            String color = entry.getKey();
            if(diceColors.size() >5){
                if(lastDie.equals(color)){
                    entry.setValue(entry.getValue() + 1);
                }

            }
            int count = entry.getValue();

            // Add tiles based on the quantity
            if (count > 1) {
                availableTiles.add(color.substring(0, 1) + "2"); // Tile of size 2
            }
            if (count > 2) {
                availableTiles.add(color.substring(0, 1) + "3"); // Tile of size 3
            }
            if (count >3) {
                availableTiles.add(color.substring(0, 1) + "4L"); // Tile of size 4
                availableTiles.add(color.substring(0, 1) + "4R"); // Tile of size 4 (another shape)
            }
            if (count > 4) {
                availableTiles.add(color.substring(0, 1) + "5"); // Tile of size 5
            }
        }

        // Filter out used tiles (especially large tiles)
        List<String> filteredTiles = new ArrayList<>();
        for (String tileName : availableTiles) {
            if (!isLargeTile(tileName) || !isTileUsed(tileName)) {
                filteredTiles.add(tileName);
            }
        }

        return filteredTiles;
    }
    public static List<String> addTilesWithSizeGreaterThan4(List<String> diceColors) {
        List<String> largeTiles = getAvailableTilesBasedOnDice(diceColors);
        Set<String> generatedTiles = new HashSet<>();
        System.out.println("Tiles with size > 4:");

        // Iterate through the tiles map and find tiles with size > 4
        for (Map.Entry<String, int[][]> entry : tiles.entrySet()) {
            String tileName = entry.getKey();
            int[][] tileCoordinates = entry.getValue();
            int size = tileCoordinates[0].length;  // The number of elements in the first row represents the size

            if (size >= 4) {
                largeTiles.add(entry.getKey());  // Add the tile's name to the list
                generatedTiles.add(tileName);
            }
        }

        return largeTiles;
    }
    public static List<String> addTilesWithSizeEqualTo1() {
        List<String> smallTiles = new ArrayList<>();

        // Iterate through the tiles map and find tiles with size = 1
        for (Map.Entry<String, int[][]> entry : tiles.entrySet()) {
            String tileName = entry.getKey();
            int[][] tileCoordinates = entry.getValue();
            int size = tileCoordinates[0].length;  // The number of elements in the first row represents the size

            if (size == 1) {
                smallTiles.add(tileName);  // Add the tile's name to the list
            }
        }

        return smallTiles;
    }


}
