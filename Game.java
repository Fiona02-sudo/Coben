package comp1110.ass2;

import java.util.*;

import comp1110.ass2.gui.Colour;

public class Game {


    public enum GamePhase {
        PLACEMENT,       // Current player tile placement phase
        ABILITY_SELECTION, // Other players' ability track advancement phase

        CURRENT_PLAYER_ABILITY, // Current player's ability usage phase
        END_TURN         // End of the current turn
    }
    private GamePhase currentPhase;

    private int currentPlayerIndex;
    private List<Player> players;

    public Map<String, Boolean> largeTileUsed; // Track used large tiles
    private int maxScore;
    private boolean gameEnded;

    private List<String> remainingDiceColors;

    private int abilitySelectionPlayerIndex = -1;

    private Queue<Integer> abilitySelectionQueue = new LinkedList<>();

    private ShieldActionProvider shieldActionProvider;

    public Game(int numberOfPlayers) {
        this.players = new ArrayList<>();
        this.largeTileUsed = new HashMap<>();
        this.currentPlayerIndex = -1;
        this.maxScore = 12;
        this.gameEnded = false;
        this.players = players;
        this.currentPlayerIndex = 0;

        // Initialize players and pass the game instance
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player("Player " + i, this));
        }
        initializeGamePhase();
    }


    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    // Setter for ShieldActionProvider
    public void setShieldActionProvider(ShieldActionProvider provider) {
        this.shieldActionProvider = provider;
    }

    public void setRemainingDiceColors(List<String> colors) {
        this.remainingDiceColors = colors;
    }

    public List<String> getRemainingDiceColors() {
        return remainingDiceColors;
    }

    // Getter for ShieldActionProvider
    public ShieldActionProvider getShieldActionProvider() {
        return shieldActionProvider;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public boolean endGame(Player winner) {
        this.gameEnded = winner.getScore()>=12;
        System.out.println("Game has ended. " + winner.getName() +
                " wins with a score of " + winner.getScore() + "!");
        return gameEnded;
    }

    public void nextTurn() {
        if (gameEnded) {
            throw new IllegalStateException("Game has ended.");
        }
        currentPlayerIndex = (currentPlayerIndex+1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean canUseTile(Tile tile) {
        String tileName = tile.getName();
        if (tile.getSize() >= 4) {
            return !largeTileUsed.getOrDefault(tileName, false);
        }
        return true;
    }

    public void markTileAsUsed(Tile tile) {
        if (tile.getSize() >= 4) {
            largeTileUsed.put(tile.getName(), true);
        }
    }

    public boolean canPlayerSelectTile(Player player, Tile tile, List<Die> dice) {
        String tileColor = tile.getColor();
        Colour colorEnum = Colour.valueOf(tileColor.toUpperCase());
        int tileSize = tile.getSize();

        // Check if the tile has already been used
        if (tileSize >= 4 && largeTileUsed.getOrDefault(tile.getName(), false)) {
            return false;
        }

        // Count the number of matching dice
        int matchingDice = 0;
        for (Die die : dice) {
            String dieColor = die.getColor();
            if (dieColor.equals(tileColor) || dieColor.equals("White")) {
                matchingDice++;
            }
        }

        // Get bonuses available from the ability track
        int bonusesAvailable = player.getAbilityTrack()
                .getBonusesAvailable(colorEnum);

        // The player can select the tile if matchingDice + bonuses >= tileSize
        boolean canSelect = (matchingDice + bonusesAvailable >= tileSize);

        return canSelect;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Queue<Integer> getAbilitySelectionQueue() {
        return abilitySelectionQueue;
    }

    // Initialize the ability selection queue, excluding the current player
    public void initializeAbilitySelectionQueue(int currentPlayerIndex) {
        abilitySelectionQueue.clear();
        int totalPlayers = players.size();
        // Start from the next player and add all other players
        for (int i = 1; i < totalPlayers; i++) {
            int playerIndex = (currentPlayerIndex + i) % totalPlayers;
            abilitySelectionQueue.add(playerIndex);
        }
    }



    // Initialize the game phase
    public void initializeGamePhase() {
        currentPhase = GamePhase.PLACEMENT;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase phase) {
        currentPhase = phase;
    }

}

