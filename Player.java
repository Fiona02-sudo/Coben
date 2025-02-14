package comp1110.ass2;
import comp1110.ass2.gui.Colour;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player {
    private String name;
    private Board board;
    private AbilityTrack abilityTrack;
    private Game game; // Reference to the game instance
    private int score;
    private Tile new_tile;
    private int totalBoardScore = 0;
    private int totalAbilityScore = 0;
    private Dice dice;
    private boolean hasUsedPurpleAbility = false;


    public Player(String name, Game game) {
        this.name = name;
        this.board = new Board();
        this.abilityTrack = new AbilityTrack(this.board);
        this.game = game;
        this.score = 0;
        this.dice = new Dice(5);
    }

    // Getter and Setter for hasUsedPurpleAbility
    public boolean hasUsedPurpleAbility() {
        return hasUsedPurpleAbility;
    }

    public void setHasUsedPurpleAbility(boolean hasUsedPurpleAbility) {
        this.hasUsedPurpleAbility = hasUsedPurpleAbility;
    }

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public Board getBoard() {
        return board;
    }

    public AbilityTrack getAbilityTrack() {
        return abilityTrack;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean canPlaceTile(Tile tile, Position position, int rotation) {
        Tile movedTile = changeTile(tile, position, rotation);
        return board.canPlaceTile(movedTile);
    }

    public Tile changeTile(Tile tile, Position position, int rotation) {
        // Rotate Tile
        Tile rotatedTile = tile.rotated(rotation);

        // Move Tile based on the position
        Tile movedTile = rotatedTile.moveBy(position);
        return movedTile;
    }

    public Tile placeTile(Tile tile, Position position, int rotation) {
        if (game.isGameEnded()) {
            throw new IllegalStateException("Game has ended. Cannot place tile.");
        }

        Tile movedTile = changeTile(tile, position, rotation);

        if (canPlaceTile(tile, position, rotation)) {
            board.placeTile(movedTile);

            if (TileFactory.isLargeTile(movedTile.getName())) {
                TileFactory.markTileAsUsed(movedTile.getName());
            }
            System.out.println("xywyy");

            // Handle shields, using ShieldActionProvider to provide choices
            handleShieldBonuses();
            System.out.println("ad "+abilityTrack.getMarkedOff(Colour.RED));

            // Calculate score
            calculateScore();

            // Game end conditions are checked within calculateScore()

            return movedTile;

        } else {
            throw new IllegalArgumentException("Cannot place tile at the specified position.");
        }
    }



    public void handleShieldBonuses() {
        System.out.println("shield is selected");
        List<Board.CompletionInfo> completions = board.checkForCompletions();

        // Mark the scored rows and columns
        for (Board.CompletionInfo info : completions) {
            board.markCompletionAsScored(info);
        }

        // Get newly unlocked shields
        List<String> newShields = board.getUnlockedShields(completions);

        for (String shieldId : newShields) {
            // Get the shield action choice from ShieldActionProvider
            String choice = game.getShieldActionProvider().getShieldAction(shieldId);
            chooseShieldAction(choice);
            System.out.println("this is red1"+abilityTrack.getMarkedOff(Colour.RED));
        }
    }

    public void chooseShieldAction(String choice) {
        if (choice.equals("a")) {
            placeSingleSquareWithO();
        } else if (choice.equals("b")) {
            comp1110.ass2.gui.Colour abilityColor = chooseAbilityTrackToAdvance();
            System.out.println(abilityColor);
            abilityTrack.advanceTrack(abilityColor, 2);
            System.out.println("this is red   "+abilityTrack.getMarkedOff(Colour.RED));
            System.out.println(name + " advanced the " + abilityColor + " ability track by 2 steps.");

        }
    }

    public void placeSingleSquareWithO() {
        // Find a valid position to place a single square
        Position position = findValidPositionForSingleSquare();
        if (position != null) {
            // Create a single-square Tile
            Tile singleTile = TileFactory.createTile("S1X");
            Tile movedTile = changeTile(singleTile, position, 0);// 移动 Tile 到指定位置
            // Place the Tile at the specified position
            board.placeTile(movedTile);
            board.addWindowsToTile(movedTile);
            // Mark the square with O
            board.getSquareAt(position).setMarkedO(true);

            System.out.println(name + " placed a single square at position " + position + " and marked it with O.");

            // Check if a new row or column is completed
            handleShieldBonuses(); // Recursively handle new shields

        } else {
            throw new IllegalStateException("No valid position to place a single square.");
        }
    }



    public Position findValidPositionForSingleSquare() {
        // Simple strategy: search from left to right, bottom to top, to find the first valid position
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                Position position = new Position(x, y);
                Tile singleTile = TileFactory.createTile("S1X");
                Tile movedTile = changeTile(singleTile, position, 0);
                if (board.canPlaceTile(movedTile)) {
                    return position;
                }
            }
        }
        return null; // No valid position found
    }


    /**
     * Let the player choose which ability track to advance
     * Simplified here to automatically choose a track, in practice user input can be used
     */
    // Update the chooseAbilityTrackToAdvance method to handle the case where an ability track cannot be advanced further
    private comp1110.ass2.gui.Colour chooseAbilityTrackToAdvance() {
        // Get the ability tracks that can be advanced
        List<comp1110.ass2.gui.Colour> availableTracksList = new ArrayList<>();
        for (Map.Entry<comp1110.ass2.gui.Colour, AbilityTrackEntry> entry : abilityTrack.getTracks().entrySet()) {
            if (entry.getValue().getMarkedOff() + 2 <= 9) {
                availableTracksList.add(entry.getKey());
            }
        }

        // If no tracks can be advanced, throw an exception
        if (availableTracksList.isEmpty()) {
            throw new IllegalStateException("No ability tracks can be advanced by 2 steps.");
        }

        // Convert the available ability tracks to a string array
        String[] availableTracks = availableTracksList.stream().map(Enum::toString).toArray(String[]::new);

        // Use ShieldActionProvider to get the player's choice
        String chosenTrackStr = game.getShieldActionProvider().chooseAbilityTrack(availableTracks);

        // Convert the chosen track string to the enum type Colour
        comp1110.ass2.gui.Colour chosenTrack = comp1110.ass2.gui.Colour.valueOf(chosenTrackStr.toUpperCase());

        // Check if the selected ability track can be advanced by 2 more steps
        AbilityTrackEntry entry = abilityTrack.getTracks().get(chosenTrack);
        if (entry.getMarkedOff() + 2 > 9) {
            throw new IllegalStateException("Cannot advance ability track " + chosenTrack + " by 2 steps.");
        }

        return chosenTrack;
    }


    public int calculateScore() {
        // Calculate the current board score
        int currentBoardScore = board.calculateScore();

        // Calculate the new board score since the last score update
        int newBoardPoints = currentBoardScore - this.totalBoardScore;
        this.totalBoardScore = currentBoardScore;

        // Check newly completed ability tracks
        List<Colour> completedTracks = abilityTrack.getCompletedTracks();

        // Add 2 points for each newly completed ability track
        int newAbilityPoints = completedTracks.size() * 2;
        this.totalAbilityScore += newAbilityPoints;

        // Update the player's total score
        this.score = this.totalBoardScore + this.totalAbilityScore;
        System.out.println("total ability score: "+this.totalAbilityScore);
        // Output score information
        System.out.println(name + " gained " + newBoardPoints + " points from the board.");
        if (!completedTracks.isEmpty()) {
            System.out.println(name + " completed ability tracks: " + completedTracks + " and gained " + newAbilityPoints + " extra points.");
        }
        System.out.println(name + "'s total score is now: " + this.score);

        // Check if the game has ended
        if (score >= game.getMaxScore() && !game.isGameEnded()) {
            game.endGame(this);
        }
        return score;
    }

    public void unlockAbility(Colour color) {
        abilityTrack.markTrack(color);
    }


    public List<Die> useAbility(Colour color, Tile tile, Game game, String changecolor, List<Die> dice) {
        if (color == Colour.PURPLE) {
            this.setHasUsedPurpleAbility(true);}
        return abilityTrack.useAbility(color, tile, game, changecolor, dice);
    }
    public List<Die> cleardie(){
        List<Die> diceList = this.dice.getRolledDice();

        for (int i = 0; i < diceList.size(); i++) {
            if (diceList.get(i).isCheck()) {
                diceList.get(i).changeCheck();
            }
        }
        return diceList;
    }
    public List<Die> rollDice() {
        this.dice.rollAll(); // Call the rollAll method from the Dice class

        return this.dice.getRolledDice(); // Return the updated list of Die
    }

    public List<Die> getDice() {
        return this.dice.getRolledDice(); // Return the list of Die stored in the Dice class
    }

    public List<Die> setDice(List<String> colours) {
        List<Die> diceList = this.dice.getRolledDice();

        for (int i = 0; i < diceList.size(); i++) {
            diceList.get(i).setColor(colours.get(i));
        }
        return diceList;
    }
}