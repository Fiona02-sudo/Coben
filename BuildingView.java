package comp1110.ass2.gui;

import comp1110.ass2.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;

import java.util.Arrays;
import java.util.List;

/**
 * Displays a Building.
 */
public class BuildingView extends GridPane {
    private final SquareView[][] grid;
    static final int SQUARE_SIZE = 50;

    // Display coat-of-arms on building edges
    private final ImageView[] topRow;
    private final ImageView[] rightCol;

    static final Image goldCoA = new Image(BuildingView.class.getResource("/goldCoA.png").toExternalForm());
    static final Image blackCoA = new Image(BuildingView.class.getResource("/blackCoA.png").toExternalForm());


    private int width;
    private int height;

    public BuildingView(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        grid = new SquareView[width][height];
        content = new Square[GameGUI.MAX_N_PLAYERS][width][height];
        showColCoA = new boolean[GameGUI.MAX_N_PLAYERS][width];
        showRowCoA = new boolean[GameGUI.MAX_N_PLAYERS][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                grid[x][y] = new SquareView(SQUARE_SIZE);
                add(grid[x][y], x, height - y);
                for (int i = 0; i < GameGUI.MAX_N_PLAYERS; i++)
                    content[i][x][y] = new Square();
            }
        topRow = new ImageView[width];
        for (int x = 0; x < width; x++) {
            topRow[x] = new ImageView();
            topRow[x].setFitWidth(SQUARE_SIZE);
            topRow[x].setFitHeight(SQUARE_SIZE);
            add(topRow[x], x, 0);
        }
        rightCol = new ImageView[height];
        for (int y = 0; y < height; y++) {
            rightCol[y] = new ImageView();
            rightCol[y].setFitWidth(SQUARE_SIZE);
            rightCol[y].setFitHeight(SQUARE_SIZE);
            add(rightCol[y], width, height - y);
        }
    }

    Node getSquare(int x, int y) {
        return grid[x][y];
    }

    private static class Square {
        Boolean window; // 0: window, 1: no-window; null: not filled
        Colour colour;

        public Square() {
            window = null;
            colour = Colour.WHITE;
        }

        public Square(Boolean window, Colour colour) {
            this.window = window;
            this.colour = colour;
        }

        public boolean isFilled() {
            return (this.window != null);
        }

        public boolean hasWindow() {
            return this.isFilled() && this.window;
        }

        public Colour getColour() {
            return this.colour;
        }

        public Color getFXColor() {
            return this.colour.getFXColor();
        }
    }

    private Square[][][] content;
    private boolean[][] showColCoA;
    private boolean[][] showRowCoA;

    /**
     * Update the view to show the state of a given building.
     */
    void show(int player) {
        System.out.println("BuildingView.show called for player: " + player);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                SquareView squareView = grid[x][y];
                Square square = content[player][x][y];
                if (square.isFilled()) {
                    squareView.setState(true, square.hasWindow(), square.getFXColor());
                } else {
                    squareView.setState(false, false, Color.WHITE);
                }
            }
        }

        // Update shield images
        for (int x : List.of(1, 3)) {
            Image image = ((player >= 0) && showColCoA[player][x] ? goldCoA : blackCoA);
            topRow[x].setImage(image);
        }
        for (int y : List.of(1, 3, 5)) {
            Image image = ((player >= 0) && showRowCoA[player][y] ? goldCoA : blackCoA);
            rightCol[y].setImage(image);
        }
    }

    void show(int player, Placement p, boolean valid) {
        show(player);
        var mark = new LibraryView.LibraryItem(p);
        for (int i = 0; i < mark.getSize(); i++) {
            int x = p.getX() + mark.getX(i);
            int y = p.getY() + mark.getY(i);
            if (0 <= x && x < grid.length)
                if (0 <= y && y < grid[x].length)
                    grid[x][y].markPlacement(valid, p.getWindow(i));
        }
    }

    static class SquareView extends StackPane {
        Rectangle outer;
        Rectangle inner;
        Rectangle border;

        // New properties for candidate tile interaction
        boolean isCandidate = false; // Marks whether it's part of a candidate tile
        int candidateIndex;          // Index in the candidate tile
        CandidateClickHandler candidateClickHandler; // Click event handler

        SquareView(int width) {
            outer = new Rectangle(width, width);
            outer.setStroke(Color.BLACK);
            outer.setFill(Color.WHITE);

            inner = new Rectangle(width / 2.0, width / 2.0);
            inner.setStroke(Color.BLACK);
            inner.setFill(Color.WHITE);
            inner.setVisible(false); // Default: window is hidden

            border = new Rectangle(width - 8, width - 8);
            border.setStroke(Color.BLACK);
            border.setFill(null);
            border.setStrokeWidth(5);
            border.setStrokeLineJoin(StrokeLineJoin.ROUND);
            border.setVisible(false); // Default: border is hidden

            getChildren().addAll(outer, inner, border);

            // Set alignment for the inner window rectangle
            StackPane.setAlignment(inner, Pos.CENTER);
        }

        void setState(boolean filled, boolean window, Color colour) {
            if (!filled) {
                outer.setFill(Color.WHITE);
                inner.setVisible(false);
            } else {
                outer.setFill(colour);
                inner.setVisible(window);
            }
            // Clear placement mark
            border.setVisible(false);

            // Reset candidate tile mark
            isCandidate = false;
            candidateClickHandler = null;
            this.setOnMouseClicked(null);
        }

        void markPlacement(boolean valid, boolean window) {
            border.setVisible(true);
            if (valid) border.setStroke(Color.BLACK);
            else border.setStroke(Color.GRAY);
            inner.setVisible(window);
        }

        void setCandidateState(Colour colour, boolean hasWindow, int index, CandidateClickHandler handler) {
            outer.setFill(colour.getFXColor());
            inner.setVisible(hasWindow);

            // Set candidate tile mark and index
            isCandidate = true;
            candidateIndex = index;
            candidateClickHandler = handler;

            // Set click event handler
            this.setOnMouseClicked(e -> {
                // Invoke the callback method to update the window configuration of the candidate tile
                if (candidateClickHandler != null) {
                    candidateClickHandler.onCandidateClick(candidateIndex);
                }
            });
        }

        void clearCandidateState() {
            outer.setFill(Color.WHITE);
            inner.setVisible(false);
            border.setVisible(false);
            isCandidate = false;
            candidateClickHandler = null;
            this.setOnMouseClicked(null);
        }
        interface CandidateClickHandler {
            void onCandidateClick(int index);
        }
    }


    void setSquare(int p, int x, int y, Colour colour, boolean window) {

        if (colour != Colour.WHITE)
            content[p][x][y] = new Square(window, colour);
        else
            content[p][x][y] = new Square();
    }

    void setRowCoA(int player, int y, boolean highlightOn) {
        showRowCoA[player][y] = highlightOn;
    }

    void setColumnCoA(int player, int x, boolean highlightOn) {
        showColCoA[player][x] = highlightOn;
    }

    // New method: Display candidate tile
    public void showCandidateTile(Placement candidate, GameGUI gameGUI) {
        // Clear the previous candidate tile display
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                SquareView square = grid[x][y];
                if (square.isCandidate) {
                    square.clearCandidateState();
                }
            }
        }

        if (candidate != null) {
            Tile tile = TileFactory.createTile(candidate.getTileName());
            tile.setWindows(candidate.windows);
            Tile rotatedTile = tile.rotated(candidate.getRotation());
            List<Position> positions = rotatedTile.getShape();

            for (int i = 0; i < positions.size(); i++) {
                Position pos = positions.get(i);
                int x = candidate.getX() + pos.getX();
                int y = candidate.getY() + pos.getY();

                if (x >= 0 && x < width && y >= 0 && y < height) {
                    SquareView square = grid[x][y]; // Note the coordinate system conversion
                    // Inside BuildingView's showCandidateTile method
                    square.setCandidateState(Colour.getColour(tile.getColor()), candidate.windows[i], i, index -> {
                        // Update candidate's window configuration
                        candidate.setBrick(index);
                        // Redisplay the candidate tile
                        showCandidateTile(candidate, gameGUI);
                        // Request focus back to BuildingView
                        this.requestFocus();
                    });

                }
            }
        }
    }


}
