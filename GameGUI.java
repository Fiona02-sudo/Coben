package comp1110.ass2.gui;

import comp1110.ass2.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

public class GameGUI extends BorderPane {

    static final int BUILDING_WIDTH = 5;
    static final int BUILDING_HEIGHT = 9;

    static final int TITLE_FONT_SIZE = 24;
    static final int LARGE_FONT_SIZE = 20;
    static final int MEDIUM_FONT_SIZE = 18;
    static final int SMALL_FONT_SIZE = 12;

    private static final Border boxBorder = new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderStroke.MEDIUM));

    // GUI components
    private Label current_player_view;
    private TabPane player_selector;
    private PlayerStateView player_view;
    private BuildingView building_view;
    private LibraryView library_view;
    private FlowPane price_view;
    private DiceView dice_view;
    private StackPane control_view;
    private Node game_setup_controls;
    private Node current_player_controls;
    private MenuButton b_reroll;
    private Button b_confirm;
    private Button b_pass;
    private MenuButton b_colour_change;

    private BiConsumer<Integer, boolean[]> onStartGame;
    private Consumer<String> onTileSelected;
    private Consumer<Placement> onTilePlaced;
    private Consumer<String> onConfirm;
    private Consumer<String> onPass;
    private Consumer<String> onGameAction;
    private Consumer<String> onReroll;
    private Consumer<String> onColourChange;

    private Placement candidate = null;
    private int candidate_index = -1;
    private Player[] players;
    private Game game;
    private Tile selectedTile;
    private int rotation = 0;
    private int previewX = 0;
    private int previewY = 0;
    private String changecolor;

    public void updateTilePreview() {
        if (selectedTile != null) {
            int currentPlayerIndex = getSelectedPlayer();
            // Create a new Placement object
            Placement placement = new Placement(selectedTile.getName(), selectedTile.getSize(), previewX, previewY, rotation);
            // Set window information if necessary
            boolean[] windows = selectedTile.getWindows();
            for (int i = 0; i < windows.length; i++) {
                placement.windows[i] = windows[i];
            }
            // Update BuildingView to show the preview
            building_view.show(currentPlayerIndex, placement, true); // The 'true' indicates it's a preview
        }
    }


    public void confirmPlacement() {
        if (candidate != null) {
            int currentPlayerIndex = getSelectedPlayer();
            System.out.println("Confirming placement for player " + currentPlayerIndex);
            Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

            // Create and configure the Tile object based on the candidate
            Tile tile = TileFactory.createTile(candidate.getTileName());
            if (tile != null) {
                tile.setWindows(candidate.windows);

                // Apply rotation and position to the tile
                Tile rotatedTile = tile.rotated(candidate.getRotation());
                Tile movedTile = rotatedTile.moveBy(new Position(candidate.getX(), candidate.getY()));

                // Check if the tile can be placed at the specified location
                boolean canPlace = currentPlayer.canPlaceTile(tile, new Position(candidate.getX(), candidate.getY()), candidate.getRotation());
                if (canPlace) {
                    // Place the tile on the player's board
                    currentPlayer.placeTile(tile, new Position(candidate.getX(), candidate.getY()), candidate.getRotation());

                    // If it's a large tile, mark it as used and update available tiles
                    if (TileFactory.isLargeTile(tile.getName())) {
                        TileFactory.markTileAsUsed(tile.getName());
                        List<String> availableTiles = TileFactory.getAvailableTiles();
                        setAvailableTiles(availableTiles);
                    }

                    // Update the board display for the current player
                    updateBoardDisplay(currentPlayerIndex);

                    // Clear the candidate tile and selection
                    candidate = null;
                    candidate_index = -1;
                    clearTileSelection();

                    // Update the player's score
                    currentPlayer.calculateScore();
                    setScore(currentPlayerIndex, currentPlayer.getScore());
                    int i =currentPlayerIndex;
                    if(game.getPlayers().get(i).getDice().size() > 5){
                        // Remove the last dice from the player's dice list (since it was used to place the tile)
                        game.getPlayers().get(i).getDice().remove(game.getPlayers().get(i).getDice().get(game.getPlayers().get(i).getDice().size()-1));
                    }
                    //game.getPlayers().get(i).getDice().remove(game.getPlayers().get(i).getDice().get(game.getPlayers().get(i).getDice().size()-1));
                    updateDice();

                    // Check if the game has ended
                    if (game.isGameEnded()) {
                        int[] finalScores = game.getPlayers().stream().mapToInt(Player::getScore).toArray();
                        endGame(finalScores);
                    }

                    // Handle the purple ability if applicable
                    if (tile.getSize() == 1 && currentPlayer.hasUsedPurpleAbility()) {
                        currentPlayer.setHasUsedPurpleAbility(false);
                        updateAvailableTilesBasedOnSelectedDice();
                    }

                    // Refresh the game state
                    showState();

                    // Update the game phase to ability selection
                    game.setCurrentPhase(Game.GamePhase.ABILITY_SELECTION);

                    // Initialize the ability selection queue with other players
                    game.initializeAbilitySelectionQueue(currentPlayerIndex);

                    // Get the indices of dice selected by the current player
                    List<Integer> selectedDiceIndices = getSelectedDice();

                    // Get all dice of the current player
                    List<Die> allDice = currentPlayer.getDice();

                    // Find the colors of unselected dice
                    List<String> remainingColors = new ArrayList<>();
                    for (int i1 = 0; i1 < allDice.size(); i1++) {
                        if (!selectedDiceIndices.contains(i1)) {
                            remainingColors.add(allDice.get(i1).getColor());
                        }
                    }

                    // Save the remaining dice colors to the game object
                    game.setRemainingDiceColors(remainingColors);

                    // Clear the current player's dice selection
                    clearDiceSelection(currentPlayer);

                    // Check if there are other players to select abilities
                    if (!game.getAbilitySelectionQueue().isEmpty()) {
                        // Automatically switch to the next player who needs to choose an ability track
                        int nextPlayerIndex = game.getAbilitySelectionQueue().peek();
                        setSelectedPlayer(nextPlayerIndex);

                        // Display a message prompting the next player to choose an ability track
                        setMessage("Player " + nextPlayerIndex + ", please choose an ability track.");
                    } else {
                        // No other players to select abilities, proceed to current player's ability phase
                        game.setCurrentPhase(Game.GamePhase.CURRENT_PLAYER_ABILITY);
                        setSelectedPlayer(currentPlayerIndex);
                        setMessage("Player " + currentPlayerIndex + ", you may use an ability or click 'Pass' to end your turn.");
                    }

                } else {
                    setMessage("Cannot place the tile at the specified location.");
                }
            } else {
                setMessage("Tile does not exist.");
            }
        } else {
            setMessage("Please select a tile first.");
        }
    }





    public void updateBoardDisplay(int playerIndex) {
        // Retrieve the player and their board based on the provided index
        Player player = game.getPlayers().get(playerIndex);
        Board board = player.getBoard();
        Square[][] grid = board.getGrid(); // Get the grid (2D array of squares) from the board

        // Loop through the board grid's rows and columns
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                Square square = grid[y][x]; // Get the square at the current position
                int displayY = y; // Use the current y-coordinate for display purposes

                // Check if the square is not empty (i.e., has a tile placed)
                if (!square.isEmpty()) {
                    // Get the color of the tile and whether the square has a window
                    String color = square.getTile().getColor();
                    boolean hasWindow = square.hasWindow();
                    // Update the frontend with the square's information
                    setFacadeSquare(playerIndex, x, displayY, color, hasWindow);
                } else {
                    // If the square is empty, set it to "White" with no window
                    setFacadeSquare(playerIndex, x, displayY, "White", false);
                }
            }
        }
    }




    private void makeSetupControls() {
        VBox controls = new VBox();
        controls.setSpacing(4);
        ToggleGroup np = new ToggleGroup();
        FlowPane npPane = new FlowPane();
        npPane.setHgap(2);
        npPane.getChildren().add(new Text("Number of players:"));
        for (int i = 2; i <= 4; i++) {
            RadioButton b = new RadioButton(Integer.toString(i));
            b.setToggleGroup(np);
            b.setUserData(i);
            npPane.getChildren().add(b);
        }
        controls.getChildren().add(npPane);
        FlowPane aiPane = new FlowPane();
        aiPane.setHgap(2);
        aiPane.getChildren().add(new Text("AI:"));
        for (int i = 0; i < 4; i++) {
            CheckBox b = new CheckBox(Integer.toString(i + 1));
            aiPane.getChildren().add(b);
        }
        controls.getChildren().add(aiPane);
        FlowPane gsPane = new FlowPane();
        gsPane.setHgap(2);
        gsPane.getChildren().add(new Text("Init state:"));
        TextField initStateString = new TextField();
        gsPane.getChildren().add(initStateString);
        controls.getChildren().add(gsPane);
        Button b_start = new Button("Start");
        b_start.setOnAction(e -> {
            Toggle np_selected = np.getSelectedToggle();
            boolean[] isAI = new boolean[4];
            for (int i = 0; i < 4; i++)
                isAI[i] = ((CheckBox) (aiPane.getChildren().get(i + 1))).isSelected();
            String gameString = initStateString.getText();
            if (gameString.length() > 0) {
                doStart(0, gameString, isAI);
            } else if (np_selected != null) {
                int n = (Integer) np_selected.getUserData();
                System.out.println("selected " + n + " players");
                doStart(n, null, isAI);
                showState();

            }
        });
        controls.getChildren().add(b_start);
        game_setup_controls = controls;

    }

    private void doStart(int nPlayers, String gameString, boolean[] isAI) {
        player_selector.getTabs().clear();
        for (int i = 0; i < nPlayers; i++) {
            Tab t = new Tab("Player " + Integer.toString(i));
            player_selector.getTabs().add(t);

            t.setOnSelectionChanged(event -> {
                if (t.isSelected()) {
                    System.out.println("=================88=====================");
                    int selectedIndex = getSelectedPlayer();
                    updatePlayerButtons(selectedIndex);
                    makePlayerControls();
                    clearDiceSelection(game.getPlayers().get(selectedIndex));
                    clearTrackSelection();
                    System.out.println("Selected Player Index: " + selectedIndex);
                    updateDice();
                    showState();
                    System.out.println("=================99=====================");
                }
            });
        }

        control_view.getChildren().clear();
        control_view.getChildren().add(current_player_controls);
        player_selector.getSelectionModel().select(0);
        player_selector.getSelectionModel().selectedIndexProperty().addListener(
                (property, old_value, new_value) -> {
                    showState();
                });
        if (onStartGame != null)
            onStartGame.accept(nPlayers, isAI);
        showState();
    }

    private Pane makeGameOverControls(int[] finalScores) {
        GridPane controls = new GridPane();
        controls.setAlignment(Pos.CENTER);
        controls.setHgap(10);
        controls.setVgap(4);
        Text header = new Text("Final scores");
        header.setFont(Font.font(TITLE_FONT_SIZE));
        controls.add(header, 0, 0, 2, 1);
        GridPane.setHalignment(header, HPos.CENTER);
        for (int i = 0; (i < finalScores.length) && (i < 4); i++) {
            Text player_i = new Text("Player" + Integer.toString(i));
            controls.add(player_i, 0, 1 + i, 1, 1);
            GridPane.setHalignment(player_i, HPos.LEFT);
            Text score_i = new Text(Integer.toString(finalScores[i]));
            controls.add(score_i, 1, 1 + i, 1, 1);
            GridPane.setHalignment(score_i, HPos.RIGHT);
        }
        Button b_again = new Button("Play again");
        b_again.setOnAction(e -> {
            control_view.getChildren().clear();
            control_view.getChildren().add(game_setup_controls);
            showState();
        });
        int n = Math.min(finalScores.length, 4);
        controls.add(b_again, 0, n + 1, 2, 1);
        GridPane.setHalignment(b_again, HPos.CENTER);
        Button b_quit = new Button("Quit");
        b_quit.setOnAction(e -> Platform.exit());
        controls.add(b_quit, 0, n + 2, 2, 1);
        GridPane.setHalignment(b_quit, HPos.CENTER);
        return controls;
    }

    private void makePlayerControls() {
        int player_num = getSelectedPlayer();
        GridPane controls = new GridPane();
        System.out.println("Player makeplayercontrols: " + player_num);
        controls.setHgap(3);
        controls.setVgap(3);
        controls.setPadding(new Insets(3, 3, 3, 3));
        if (player_num >= 0) {
            b_reroll = new MenuButton("Change color");
            controls.add(b_reroll, 0, 3);
            b_reroll.setOnAction((e) -> {
                if (onReroll != null)
                {onReroll.accept(b_reroll.getText());}
                updateDice();
                List<String> diceColors = new ArrayList<>();
                for (Die die : players[player_num].getDice()) {
                    System.out.println(die.getColor());
                    diceColors.add(die.getColor());
                }
                dice_view.show(diceColors);
            });
        }

        b_confirm = new Button("Confirm Ability");
        System.out.println("Text"+b_confirm.getText());
        controls.add(b_confirm, 0, 1);
        b_confirm.setOnAction((e) -> {
            int selectedPlayerIndex = getSelectedPlayer();
            Player selectedPlayer = game.getPlayers().get(selectedPlayerIndex);
            Game.GamePhase phase = game.getCurrentPhase();

            switch (phase) {
                case PLACEMENT:
                    if (selectedPlayerIndex == getSelectedPlayer()) {
                        if (candidate != null) {
                            confirmPlacement();
                        } else {
                            setMessage("Please select a tile first.");
                        }
                    } else {
                        setMessage("Please wait for your turn.");
                    }
                    break;

                case ABILITY_SELECTION:
                    // Other players select ability tracks
                    if (!game.getAbilitySelectionQueue().isEmpty() &&
                            selectedPlayerIndex == game.getAbilitySelectionQueue().peek()) {

                        // Ability track selection logic
                        List<Integer> selectedTracks = getSelectedTracks();

                        if (selectedTracks.size() != 1) {
                            setMessage("You can only select one ability track.");
                            return;
                        }

                        int trackIndex = selectedTracks.get(0);
                        Colour selectedColour = Colour.values()[trackIndex];

                        // Get remaining dice colors
                        List<String> remainingColors = game.getRemainingDiceColors();

                        // Check if the selected ability track color is in the remaining dice colors
                        if (!remainingColors.contains(selectedColour.toString())) {
                            setMessage("You can only select an ability track matching the remaining dice colors.");
                            return;
                        }

                        // Advance the ability track
                        selectedPlayer.getAbilityTrack().markTrack(selectedColour);
                        setScore(selectedPlayerIndex, selectedPlayer.calculateScore());
                        showState();

                        // Remove the selected color from the remaining colors
                        remainingColors.remove(selectedColour.toString());
                        game.setRemainingDiceColors(remainingColors);

                        // Clear ability track selection
                        clearTrackSelection();

                        // Remove the player from the queue
                        game.getAbilitySelectionQueue().poll();

                        // Check if there are more players in the queue
                        if (!game.getAbilitySelectionQueue().isEmpty()) {
                            // Switch to the next player in the queue
                            int nextPlayerIndex = game.getAbilitySelectionQueue().peek();
                            setSelectedPlayer(nextPlayerIndex);
                            setMessage("Player " + nextPlayerIndex + ", please choose an ability track.");
                        } else {
                            // After all other players have selected their ability tracks
                            if (game.getAbilitySelectionQueue().isEmpty()) {
                                // No more players to select abilities, proceed to current player's ability phase
                                game.setCurrentPhase(Game.GamePhase.CURRENT_PLAYER_ABILITY);
                                int currentPlayerIndex = game.getCurrentPlayerIndex(); // Get the current player index from the game logic
                                setSelectedPlayer(currentPlayerIndex); // This now calls showState()
                                setMessage("Player " + currentPlayerIndex + ", you may use an ability or click 'Pass' to end your turn.");
                            }


                        }
                    } else {
                        setMessage("Please wait for your turn.");
                    }

                    break;

                case CURRENT_PLAYER_ABILITY:
                    // Current player uses abilities or ends turn
                    if (selectedPlayerIndex == getSelectedPlayer()) {
                        // Implement ability usage logic here if needed
                        for (Colour color : Colour.values()) {
                            player_view.getTrackSelectors().setDisable(color.ordinal(), false);
                        }
                        setMessage("You have completed your actions. Click 'Pass' to end your turn.");
                    } else {
                        setMessage("Please wait for your turn.");
                    }
                    break;

                default:
                    setMessage("Unknown game phase.");
                    break;
            }
        });








        b_pass = new Button("Pass");
        controls.add(b_pass, 0, 2);
        b_pass.setOnAction((e) -> {
            int selectedPlayerIndex = getSelectedPlayer();
            Game.GamePhase phase = game.getCurrentPhase();

            if (phase == Game.GamePhase.CURRENT_PLAYER_ABILITY && selectedPlayerIndex == game.getCurrentPlayerIndex()) {

                reroll();
                // Current player clicks "Pass" to end their turn
                game.setCurrentPhase(Game.GamePhase.END_TURN);

                // Advance to the next player's turn
                game.nextTurn();

                // Get the new current player index
                int newCurrentPlayerIndex = game.getCurrentPlayerIndex();

                // Set the selected player to the new current player
                setSelectedPlayer(newCurrentPlayerIndex); // This will update the interface to the next player

                // Set the game phase to PLACEMENT for the new player's turn
                game.setCurrentPhase(Game.GamePhase.PLACEMENT);

                setMessage("Player " + newCurrentPlayerIndex + ", it's your turn to place a tile.");
            } else {
                setMessage("You cannot end the turn at this time.");
            }
        });



        b_colour_change = new MenuButton("Action...");
        controls.add(b_colour_change, 0, 0);
        current_player_controls = controls;
        System.out.println("player button in makeplayercontrols: " + player_num);
        updatePlayerButtons(player_num);
    }
    private void setupPlayerSelectorListener() {
        player_selector.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            makePlayerControls();
        });
    }
    public void showState() {
        int currentPlayerIndex = getSelectedPlayer();
        System.out.println("showState: " + currentPlayerIndex);
        if (currentPlayerIndex >= 0) {
            Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

            // Update shields based on the current player's Board
            updateShields(currentPlayer, currentPlayerIndex);

            building_view.show(currentPlayerIndex); // Display placed tiles

            if (candidate != null && candidate_index == currentPlayerIndex) {
                // Show candidate tile
                building_view.showCandidateTile(candidate, this);
            } else {
                // Clear candidate tile display
                building_view.showCandidateTile(null, this);
            }

            // Update ability track information
            System.out.println("showState ability: " + currentPlayerIndex + " " + currentPlayer.getName());
            AbilityTrack abilityTrack = currentPlayer.getAbilityTrack();
            for (Colour color : Colour.values()) {
                int nMarked = abilityTrack.getMarkedOff(color);
                int nBonusAvailable = abilityTrack.getBonusesAvailable(color);
                int nAbilityAvailable = abilityTrack.getAbilitiesAvailable(color);
                int nBonusToNext = abilityTrack.getNextBonus(color);
                int nAbilityToNext = abilityTrack.getNextAbility(color);
                setTrackInfo(currentPlayerIndex, color.toString(), nMarked, nBonusAvailable, nAbilityAvailable, nBonusToNext, nAbilityToNext);
            }

            Game.GamePhase phase = game.getCurrentPhase();

            if (phase == Game.GamePhase.ABILITY_SELECTION) {
                // Other players select ability tracks
                if (!game.getAbilitySelectionQueue().isEmpty() &&
                        currentPlayerIndex == game.getAbilitySelectionQueue().peek()) {

                    // Get remaining dice colors
                    List<String> remainingColors = game.getRemainingDiceColors();

                    // Enable only the ability tracks matching the remaining dice colors
                    for (Colour color : Colour.values()) {
                        if (remainingColors.contains(color.toString())) {
                            player_view.getTrackSelectors().setDisable(color.ordinal(), false);
                        } else {
                            player_view.getTrackSelectors().setDisable(color.ordinal(), true);
                        }
                    }
                } else {
                    // Disable all ability track selections
                    for (Colour color : Colour.values()) {
                        player_view.getTrackSelectors().setDisable(color.ordinal(), true);
                    }
                }
            } else if (phase == Game.GamePhase.CURRENT_PLAYER_ABILITY) {
                // Current player uses abilities
                if (currentPlayerIndex == getSelectedPlayer()) {
                    // Enable ability track selections as needed
                    for (Colour color : Colour.values()) {
                        player_view.getTrackSelectors().setDisable(color.ordinal(), false);
                    }
                } else {
                    // Disable all ability track selections
                    for (Colour color : Colour.values()) {
                        player_view.getTrackSelectors().setDisable(color.ordinal(), false);
                    }
                }
            } else {
                // Other phases, disable all ability track selections
                for (Colour color : Colour.values()) {
                    player_view.getTrackSelectors().setDisable(color.ordinal(), false);
                }
            }

            player_view.show(currentPlayerIndex);
        }
    }







    private void makeMainLayout() {
        current_player_view = new Label("                 ");
        current_player_view.setFont(Font.font(24));
        this.setTop(current_player_view);

        VBox left = new VBox();
        left.setBorder(boxBorder);
        left.setAlignment(Pos.CENTER);
        left.setFillWidth(true);

        player_selector = new TabPane();
        player_selector.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        left.getChildren().add(player_selector);
        GridPane player_pane = new GridPane();

        player_pane.setHgap(10);
        player_pane.setVgap(4);
        player_pane.setPadding(new Insets(2, 2, 2, 2));
        player_pane.setAlignment(Pos.CENTER);

        player_view = new PlayerStateView();
        player_pane.add(player_view, 0, 0);
        building_view = new BuildingView(BUILDING_WIDTH, BUILDING_HEIGHT);
        building_view.setFocusTraversable(true);
        player_pane.add(building_view, 1, 0, 1, 2);
        for (int x = 0; x < BUILDING_WIDTH; x++)
            for (int y = 0; y < BUILDING_HEIGHT; y++) {
                final int fx = x;
                final int fy = y;
                building_view.getSquare(x, y).setOnMouseClicked(e -> {
                    if (candidate != null) {
                        if (e.getButton() == MouseButton.SECONDARY) {
                        } else {
                            candidate.setPosition(fx, fy);
                        }
                        showState();
                    }
                    building_view.requestFocus();
                });
            }

        building_view.setOnKeyPressed(e -> {
            if (candidate == null) {
                e.consume();
                return;
            }
            switch (e.getCode()) {
                case RIGHT -> {
                    candidate.movePosition(1, 0);
                    showState();
                }
                case LEFT -> {
                    candidate.movePosition(-1, 0);
                    showState();
                }
                case UP -> {
                    candidate.movePosition(0, 1);
                    showState();
                }
                case DOWN -> {
                    candidate.movePosition(0, -1);
                    showState();
                }
                case SPACE, R -> {
                    candidate.rotateClockwise();
                    showState();
                }
                case DIGIT0, NUMPAD0 -> {
                    candidate.setNoBrick();
                    showState();
                }
                case DIGIT1, NUMPAD1 -> {
                    candidate.setBrick(0);
                    showState();
                }
                case DIGIT2, NUMPAD2 -> {
                    candidate.setBrick(1);
                    showState();
                }
                case DIGIT3, NUMPAD3 -> {
                    candidate.setBrick(2);
                    showState();
                }
                case DIGIT4, NUMPAD4 -> {
                    candidate.setBrick(3);
                    showState();
                }
                case DIGIT5, NUMPAD5 -> {
                    candidate.setBrick(4);
                    showState();
                }

            }
            e.consume();
        });

        left.getChildren().add(player_pane);
        VBox.setVgrow(player_pane, Priority.ALWAYS);
        this.setCenter(left);
        BorderPane.setAlignment(left, Pos.CENTER);
        BorderPane.setMargin(left, new Insets(4, 4, 4, 4));

        VBox right = new VBox();
        right.setSpacing(4);
        right.setFillWidth(true);
        library_view = new LibraryView();
        library_view.setPrefWidth(400);
        library_view.setPrefHeight(200);
        library_view.setBorder(boxBorder);
        library_view.setPadding(new Insets(2, 2, 2, 2));

        // Place the code here
        library_view.setOnSelectionChanged((tileName) -> {
            if (tileName != null) {
                // Validate if the tile is allowed based on the selected dice
                if (isTileValidForSelectedDice(tileName)) {
                    Tile tile = TileFactory.createTile(tileName);
                    if (tile != null) {
                        candidate = new Placement(tileName, tile.getSize(), 0, 0, 0);
                        candidate_index = player_selector.getSelectionModel().getSelectedIndex();
                        showState();
                    } else {
                        setMessage("Tile does not exist.");
                        library_view.clearSelection();
                    }
                } else {
                    // Show error message
                    setMessage("You cannot select this tile. It doesn't match the selected dice.");
                    // Deselect the tile
                    library_view.clearSelection();
                }
            } else {
                candidate = null;
                if (!library_view.getItems().isEmpty()) {
                    library_view.clearSelection();
                }
                showState();
            }
            if (onTileSelected != null)
                onTileSelected.accept(tileName);
        });

        right.getChildren().add(library_view);
        price_view = new FlowPane();
        price_view.setAlignment(Pos.CENTER_LEFT);
        price_view.setBorder(boxBorder);
        price_view.setPadding(new Insets(2, 2, 2, 2));
        price_view.setMinHeight(40);
        dice_view = new DiceView(5);
        dice_view.setBorder(boxBorder);
        dice_view.setPadding(new Insets(2, 2, 2, 2));
        right.getChildren().add(dice_view);
        control_view = new StackPane();
        control_view.setBorder(boxBorder);
        control_view.setPadding(new Insets(2, 2, 2, 2));
        control_view.setAlignment(Pos.CENTER);
        right.getChildren().add(control_view);
        VBox.setVgrow(control_view, Priority.ALWAYS);
        BorderPane.setAlignment(right, Pos.CENTER_RIGHT);
        BorderPane.setMargin(right, new Insets(4, 4, 4, 4));
        this.setRight(right);
    }

    private boolean isTileValidForSelectedDice(String tileName) {
        int currentPlayerIndex = getSelectedPlayer();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);
        List<Integer> selectedDiceIndices = dice_view.selectors().getSelection();

        int whiteDiceCount = 0;
        Map<String, Integer> colorCounts = new HashMap<>();
        List<Die> dice = currentPlayer.getDice();

        for (int index : selectedDiceIndices) {
            if (index >= 0 && index < dice.size()) {
                String color = dice.get(index).getColor();
                if (color.equalsIgnoreCase("White")) {
                    whiteDiceCount++;
                } else {
                    color = capitalizeFirstLetter(color);
                    colorCounts.put(color, colorCounts.getOrDefault(color, 0) + 1);
                }
            }
        }

        // Get the tile color and size
        String tileColorInitial = tileName.substring(0, 1).toUpperCase();
        String tileColor = getColorFromInitial(tileColorInitial);
        Tile tile = TileFactory.createTile(tileName);
        if (tile == null) return false;
        int tileSize = tile.getSize();

        if (tileName.equals("S1X")) {
            return currentPlayer.hasUsedPurpleAbility();
        }

        // Number of selected dice (including white)
        int totalSelectedDice = selectedDiceIndices.size();

        // Number of selected dice of the tile color
        int colorDiceCount = colorCounts.getOrDefault(tileColor, 0);

        // The number of dice of the tile color plus white dice must be at least the tile size
        int maxPossibleDiceForColor = colorDiceCount + whiteDiceCount;
        if(dice.size()>5){
            System.out.println("dice size is: "+dice.size());
            for(int i=5;i<dice.size();i++){
                maxPossibleDiceForColor +=1;
                totalSelectedDice +=1;
            }
        }
        // The total number of selected dice must equal tile size
        return maxPossibleDiceForColor >= tileSize && totalSelectedDice == tileSize && tileSize >= 2;
    }


    private String getColorFromInitial(String initial) {
        switch (initial) {
            case "R": return "Red";
            case "B": return "Blue";
            case "G": return "Green";
            case "Y": return "Yellow";
            case "P": return "Purple";
            default: return null;
        }
    }


    // public interface

    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;
    public static final int MAX_N_PLAYERS = 4;

    public GameGUI() {
        super(); // BorderPane no-arg constructor
        makeMainLayout();
        makeSetupControls();
        makePlayerControls();
        setupPlayerSelectorListener();
        control_view.getChildren().add(game_setup_controls);
        showState();
        for (int x = 0; x < BUILDING_WIDTH; x++)
            for (int y = 0; y < BUILDING_HEIGHT; y++) {
                final int fx = x;
                final int fy = y;
                building_view.getSquare(x, y).setOnMouseClicked(e -> {
                    if (selectedTile != null) {
                        previewX = fx;
                        previewY = BUILDING_HEIGHT - fy - 1;
                        updateTilePreview();
                    }
                    building_view.requestFocus();
                });
            }

    }


    /**
     * Set text in the message field at the top.
     */
    public void setMessage(String msg) {
        current_player_view.setText(msg);
    }

    /**
     * Set list of tiles to be shown in the tile "library" (top right).
     * This will also clear any current selection.
     */
    public void setAvailableTiles(List<String> tiles) {
        candidate = null;
        library_view.clearSelection();

        int currentPlayerIndex = getSelectedPlayer();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

        // If the player has already used the purple ability, add a square of size 1 to the list
        if (currentPlayer.hasUsedPurpleAbility()) {
            List<String> smallTiles = TileFactory.addTilesWithSizeEqualTo1();
            for (String tile : smallTiles) {
                if (!tiles.contains(tile)) {
                    tiles.add(tile);
                }
            }
        }

        library_view.show(tiles);
    }


    /**
     * Clear selected tile. This does not change which tiles are shown
     * in the tile library, but will unselect the currently selected
     * tile, if any. This also means the selected tile will disappear
     * from the building view.
     */
    public void clearTileSelection() {
        selectedTile = null;
        rotation = 0;
        library_view.clearSelection();
    }

    /**
     * Set list of dice (colours) to be shown in the dice view.
     * This will also clear any current dice selection.
     */
    public void setAvailableDice(List<String> colours, Player player) {
        if (player.getDice() == null) {
            player.setDice(colours);
            dice_view.selectors().clearSelection();
            dice_view.show(colours);
        } else {
            player.setDice(colours);
            dice_view.selectors().clearSelection();
            dice_view.show(colours);
            clearDiceSelection(player);
        }

    }

    /**
     * Clear dice selection. This does not change which dice are shown
     * in the dice view, but will unselect any currently selected dice.
     */
    public void clearDiceSelection (Player player){
        dice_view.selectors().clearSelection();
        player.cleardie();
    }

    /**
     * Get the currently selected dice.
     * @return a list of indices of the currently selected dice.
     */
    public List<Integer> getSelectedDice () {
        return dice_view.selectors().getSelection();
    }

    /**
     * Set the square at (x,y) in the specified player's building
     * facade to show the specified colour and window
     *
     * Use colour = "White" and window = false to make a square empty.
     * @param player The player whose building should be updated
     *        (0 to number of players - 1).
     * @param x The x position (column) of the square (0-4)
     * @param y The y position (row) of the square (0-8)
     * @param colour The colour to show. Must be one of the strings
     *        "Red", "Blue", "Purple", "Green", "Yellow", "Gray", or
     *        "White". The colour can be abbreviated to the initial
     *        letter only.
     * @param window true iff the square should show a window, false
     *        if it should not.
     */
    public void setFacadeSquare(int player, int x, int y, String colour,boolean window){
        Colour c = Colour.getColour(colour);
        building_view.setSquare(player, x, y, c, window);
    }


    /**
     * Set the score shown for one of the players.
     */
    public void setScore ( int player, int score){
        player_view.setScore(player, score);
    }

    /**
     * Set/update the information to be shown for one of a player's
     * ability tracks.
     * @param player The player whose ability track should be updated
     *        (0 to number of players - 1).
     * @param colour The colour of the track. Must be one of "Red",
     *        "Blue", "Purple", "Green" or "Yellow".
     * @param nMarked Number to be shown in the "X" column.
     * @param nBonusAvailable Number to show in the "Avail/+" column.
     * @param nAbilityAvailable Number to show in the "Avail/star" column.
     * @param nBonusToNext Number to show in the "Next/+" column.
     * @param nAbilityToNext Number to show in the "Next/star" column.
     */
    public void setTrackInfo ( int player, String colour,int nMarked,
                               int nBonusAvailable, int nAbilityAvailable,
                               int nBonusToNext, int nAbilityToNext){
        player_view.setTrackInfo(player, colour, nMarked,
                nBonusAvailable, nAbilityAvailable,
                nBonusToNext, nAbilityToNext);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void rotateSelectedTile() {
        if (selectedTile != null) {
            rotation = (rotation + 1) % 4;
            updateTilePreview();
        } else {
            setMessage("No tile selected to rotate.");
        }
    }

    /**
     * Clear track selection.
     */
    public void clearTrackSelection () {
        player_view.getTrackSelectors().clearSelection();
    }

    /**
     * Get the currently selected ability track(s).
     * @return a list of indices of the currently selected track(s).
     */
    public List<Integer> getSelectedTracks () {
        return player_view.getTrackSelectors().getSelection();
    }

    /**
     * End the current game.
     * This will bring up the end of game screen (in the lower right
     * corner), which shows the final scores and offers the choice to
     * quit or play again.
     */
    public void endGame ( int[] finalScores){
        Pane gameOverControls = makeGameOverControls(finalScores);
        control_view.getChildren().clear();
        control_view.getChildren().add(gameOverControls);
    }

    /**
     * Returns the index of the player whose score sheet is currently
     * being shown in the left part of the GUI.
     */
    public int getSelectedPlayer () {
        int i = player_selector.getSelectionModel().getSelectedIndex();
        System.out.println("Get Selected player: " + i);
        return i;
    }

    /**
     * Set the list of actions to appear in the "Action..." menu.
     */
    public void setAvailableActions (List < String > actions) {
        b_colour_change.getItems().clear();
        for (String s : actions) {
            MenuItem act = new MenuItem(s);
            act.setOnAction((e) -> {
                if (onGameAction != null)
                    onGameAction.accept(act.getText());
                showState();
            });
            b_colour_change.getItems().add(act);
        }
    }
    public void setAvailablecolors (List < String > actions) {
        b_reroll.getItems().clear();
        for (String s : actions) {
            MenuItem act = new MenuItem(s);
            act.setOnAction((e) -> {
                if (onColourChange != null)
                    onColourChange.accept(act.getText());
                showState();
            });
            b_reroll.getItems().add(act);
        }
    }


    /**
     * Set the event handler to be called when a new game is started.
     * The handler will receive two arguments: the number of players
     * (an integer) and an array of boolean values, of the same length
     * as the number of players, indicating which players should be AI
     * controlled. (Of course, if your game does not have an AI, you
     * can ignore the second argument.)
     */
    public void setOnStartGame (BiConsumer < Integer,boolean[]>handler){
        onStartGame = handler;
    }

    /**
     * Set the event handler to be called when the user selects a tile
     * from the "tile library" (on the top-right in the display). The
     * handler will receive one argument, which is the name of the
     * tile.
     *
     * The selected tile will be displayed as a "candidate" (outline)
     * on the building display of the currently selected player's
     * score sheet.
     */
    public void setOnTileSelected (Consumer < String > handler) {
        onTileSelected = handler;
    }

    /**
     * Set the player whose score sheet should be shown in the left part
     * of the GUI.
     * @param player The player to display (0 to number of players - 1).
     */
    public void setSelectedPlayer(int player) {
        player_selector.getSelectionModel().select(player);
        showState(); // Ensure the interface updates to the selected player
    }


    /**
     * Set the event handler to be called when the user confirms
     * placement of a selected tile.
     * The handler will receive one argument, which is an object of
     * type `Placement` that contains all the details of the intended
     * placement.
     */
    public void setOnTilePlaced (Consumer < Placement > handler) {
        onTilePlaced = handler;
    }

    /**
     * Set the event handler to be called when the user changes the
     * selection of any die in the dice diplay. The event handler will
     * receive one argument, which is the index of of the die whose
     * selection status has changed.
     * This event only informs the handler that a dies selection has
     * changed, not whether the die is now selected or unselected. You
     * can use the `getSelectedDice()` method to get the indices of
     * currently selected dice.
     */
    public void setOnDiceSelectionChanged (IntConsumer handler){
        dice_view.selectors().setOnSelectionChanged((i) -> {
            handler.accept(i);
            updateAvailableTilesBasedOnSelectedDice();
            showState();
        });

    }

    public void updateAvailableTilesBasedOnSelectedDice() {
        // Get the indices of selected dice
        List<Integer> selectedDiceIndices = dice_view.selectors().getSelection();

        // Get the dice objects
        int currentPlayerIndex = getSelectedPlayer();
        Player currentPlayer = game.getPlayers().get(currentPlayerIndex);
        List<Die> dice = currentPlayer.getDice();
        Die lastDie = dice.get(dice.size() - 1);
        // Count the colors of the selected dice
        Map<String, Integer> colorCounts = new HashMap<>();
        int whiteDiceCount = 0;

        for (int index : selectedDiceIndices) {
            if (index >= 0 && index < dice.size()) {
                String color = dice.get(index).getColor();

                if (color.equalsIgnoreCase("White")) {
                    whiteDiceCount++;
                } else {
                    color = capitalizeFirstLetter(color);
                    colorCounts.put(color, colorCounts.getOrDefault(color, 0) + 1);
                }
            }
        }

        // If no colored dice are selected but white dice are selected, we can't proceed
        if (colorCounts.isEmpty() && whiteDiceCount > 0) {
            setMessage("Please select at least one colored die along with white dice.");
            setAvailableTiles(new ArrayList<>()); // Clear the tile library
            return;
        }

        // Generate the list of available tiles based on the selected dice
        List<String> availableTiles = new ArrayList<>();

        int totalSelectedDice = selectedDiceIndices.size();
        if(dice.size()>5){
            for(int i=5;i<dice.size();i++){
                totalSelectedDice += 1;
            }
            System.out.println("Total selected dice: " + totalSelectedDice+"dice size: "+dice.size());
        }

        for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            String color = entry.getKey();
            int colorDiceCount = entry.getValue();
            if(dice.size()>5){
                for(int i=5;i<dice.size();i++){
                    colorDiceCount += 1;
                }
            }

            int maxPossibleDiceForColor = colorDiceCount + whiteDiceCount;

            // Only consider tiles where the total number of selected dice matches the tile size
            if (maxPossibleDiceForColor >= totalSelectedDice && totalSelectedDice >= 2) {
                String colorInitial = color.substring(0, 1).toUpperCase();

                if (totalSelectedDice == 4) {
                    String tileNameL = colorInitial + "4L";
                    String tileNameR = colorInitial + "4R";
                    if (isTileAvailable(tileNameL)) {
                        availableTiles.add(tileNameL);
                    }
                    if (isTileAvailable(tileNameR)) {
                        availableTiles.add(tileNameR);
                    }
                } else {
                    String tileName = colorInitial + totalSelectedDice;
                    if (isTileAvailable(tileName)) {
                        availableTiles.add(tileName);
                    }
                }
            }
        }

        if (currentPlayer.hasUsedPurpleAbility()) {
            List<String> smallTiles = TileFactory.addTilesWithSizeEqualTo1();
            for (String tile : smallTiles) {
                if (!availableTiles.contains(tile)) {
                    availableTiles.add(tile);
                }
            }
        }


        setAvailableTiles(availableTiles);
    }

    private boolean isTileAvailable(String tileName) {
        Tile tile = TileFactory.createTile(tileName);
        if (tile != null) {
            if (!TileFactory.isTileUsed(tileName) || !TileFactory.isLargeTile(tileName)) {
                return true;
            }
        }
        return false;
    }


    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }



    /**
     * Set the event handler to be called when the user changes the
     * selection (checkbox) of any of the ability tracks. The event
     * handler will receive one argument, which is the index (0-4)
     * of the track whose selection status has changed.
     * This event only informs the handler that a selection has
     * changed, not whether the track is now selected or unselected.
     * You can use the `getSelectedTracks()` method to get the indices
     * of currently selected tracks.
     */
    public void setOnTrackSelectionChanged (IntConsumer handler){
        player_view.getTrackSelectors().setOnSelectionChanged((i) -> {
            handler.accept(i);
            showState();
        });

    }

    /**
     * Set the event handler to be called when the "Confirm" button is
     * pressed in any situation except when it is pressed to confirm a
     * tile placement (this will generate a TilePlaced event instead).
     * The event handler will receive one argument, which is the
     * current label of the button.
     */
    public void setOnConfirm (Consumer < String > handler) {
        onConfirm = handler;
    }

    /**
     * Set the event handler to be called when the "Pass" button is
     * pressed. The event handler will receive one argument, which is
     * the current label of the button.
     */
    public void setOnPass (Consumer < String > handler) {
        onPass = handler;
    }

    /**
     * Set the event handler to be called when an item from the "Action"
     * menu button is selected. The event handler will receive one
     * argument, which is the label of the menu item.
     */
    public void setOnGameAction (Consumer < String > handler) {
        onGameAction = handler;
    }
    public void setOnColourChanged (Consumer < String > handler) {
        onColourChange = handler;
    }

    public Player[] getplayers () {
        players = this.players;
        return players;
    }
    public void reroll(){
        int i =getSelectedPlayer();
        List<Die> dice=game.getPlayers().get(i).rollDice();
        List<String> diceColors = new ArrayList<>();
        for (Die d : dice) {
            System.out.println(d.getColor());
            diceColors.add(d.getColor());
        }
        updateDice();
        dice_view.show(diceColors);
    }

    public void updateDice(){
        int i =getSelectedPlayer();
        List<Die> dice=game.getPlayers().get(i).getDice();
        List<String> diceColors = new ArrayList<>();
        for (Die d : dice) {
            System.out.println(d.getColor());
            diceColors.add(d.getColor());
        }
        updateAvailableTilesBasedOnDice(diceColors);
        dice_view.show(diceColors);
    }
    public void updateAvailableTilesBasedOnDice(List<String> diceColors) {
        List<String> availableTiles = TileFactory.getAvailableTilesBasedOnDice(diceColors);
        setAvailableTiles(availableTiles);
    }

    private void updatePlayerButtons(int playerNum) {
        System.out.println("updatePlayerButtons: " + playerNum);
        b_confirm.setText("Confirm ability");
        b_pass.setText("Pass");
        //System.out.println(b_confirm.getText());
    }
    public void useAbility() {
        int i =getSelectedPlayer();
        //AbilityTrack track = players[i].getAbilityTrack();
        List<Integer> selectedTracks= getSelectedTracks();
        List<Colour> selectedColours = new ArrayList<>();
        for (Integer index : selectedTracks) {
            if (index >= 0 && index < Colour.values().length) {
                selectedColours.add(Colour.values()[index]);
            }
        }
        for(Colour c:selectedColours){
            if(c==Colour.RED){
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getAbilitiesAvailable()>0){
                    List<Integer> selectedDiceIndices = dice_view.selectors().getSelection();
                    System.out.println("select: "+selectedDiceIndices);
                    for (int index : selectedDiceIndices) {
                        game.getPlayers().get(i).getDice().get(index).changeCheck();
                        System.out.println(game.getPlayers().get(i).getDice().get(index).isCheck()+" "+game.getPlayers().get(i).getDice().get(index).getColor());
                    }
                    List<Die> dice_player =game.getPlayers().get(i).useAbility(Colour.RED, null, game, null, game.getPlayers().get(i).getDice());
                    List<String> diceColors = new ArrayList<>();
                    for (Die die : dice_player) {
                        diceColors.add(die.getColor());
                    }
                    dice_view.show(diceColors);
                    for (int index : selectedDiceIndices) {
                        game.getPlayers().get(i).getDice().get(index).changeCheck();
                    }
                }
            }
            if(c==Colour.GREEN){// How to input the color to be changed to

                List<Integer> selectedDiceIndices = dice_view.selectors().getSelection();
                System.out.println("select: "+selectedDiceIndices);
                for (int index : selectedDiceIndices) {
                    game.getPlayers().get(i).getDice().get(index).changeCheck();
                    System.out.println(game.getPlayers().get(i).getDice().get(index).isCheck()+" "+game.getPlayers().get(i).getDice().get(index).getColor());
                }
                List<Die> dice_player_green =game.getPlayers().get(i).useAbility(Colour.GREEN, null, game, getchangecolor(), game.getPlayers().get(i).getDice());

                List<String> diceColors_green = new ArrayList<>();
                for (Die die : dice_player_green) {
                    diceColors_green.add(die.getColor());
                }
                dice_view.show(diceColors_green);
                for (int index : selectedDiceIndices) {
                    game.getPlayers().get(i).getDice().get(index).changeCheck();
                }
            }
            if(c==Colour.YELLOW){
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getAbilitiesAvailable()>0){
                    List<Die> dice=game.getPlayers().get(i).getDice();
                    List<String> diceColors = new ArrayList<>();
                    for (Die d : dice) {
                        System.out.println(d.getColor());
                        diceColors.add(d.getColor());
                    }
                    library_view.show(TileFactory.addTilesWithSizeGreaterThan4(diceColors));
                    //If these large blocks still exist, make them disappear in the upper right corner

                    game.getPlayers().get(i).useAbility(Colour.YELLOW, selectedTile, game, null, game.getPlayers().get(i).getDice());
                }
            }
            if(c==Colour.BLUE){
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getAbilitiesAvailable()>0){
                    candidate.setNoBrick();
                    game.getPlayers().get(i).getAbilityTrack().getTrack(c).useAbility();

                }

            }
            if (c == Colour.PURPLE) {
                // Player uses the purple ability
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getAbilitiesAvailable()>0){
                    game.getPlayers().get(i).useAbility(Colour.PURPLE, null, game, null, game.getPlayers().get(i).getDice());
                    game.getPlayers().get(i).setHasUsedPurpleAbility(true);

                    // Find a valid position for the single square tile
                    Position validPosition = game.getPlayers().get(i).findValidPositionForSingleSquare();
                    if (validPosition != null) {
                        // Create the candidate for placement at the valid position
                        String singleTileName = "S1X"; // Ensure this tile is defined in TileFactory
                        Tile singleTile = TileFactory.createTile(singleTileName);
                        candidate = new Placement(singleTileName, singleTile.getSize(), validPosition.getX(), validPosition.getY(), 0);
                        candidate_index = i;
                        setMessage("You can now place a single square tile.");
                        showState();
                    } else {
                        // No valid position found; notify the player
                        setMessage("No valid position available for placing the single square tile.");
                    }
                }

            }

            System.out.println("ability ava "+game.getPlayers().get(i).getAbilityTrack().getTrack(c).getAbilitiesAvailable()+" "+game.getPlayers().get(i).getName());
        }
        dice_view.clearSelection();
        showState();
    }
    public Colour usebonus(){
        int i =getSelectedPlayer();
        List<Integer> selectedTracks= getSelectedTracks();
        List<Colour> selectedColours = new ArrayList<>();
        for (Integer index : selectedTracks) {
            if (index >= 0 && index < Colour.values().length) {
                selectedColours.add(Colour.values()[index]);
            }
        }
        for(Colour c:selectedColours){
            if(c==Colour.RED){
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getBonusesAvailable()>0){
                    game.getPlayers().get(i).getAbilityTrack().useBonus(Colour.RED,game.getPlayers().get(i).getDice());
                    List<String> diceColors = new ArrayList<>();
                    for (Die d : game.getPlayers().get(i).getDice()) {
                        System.out.println(d.getColor());
                        diceColors.add(d.getColor());
                    }
                    updateAvailableTilesBasedOnDice(diceColors);
                    return Colour.RED;
                }
            }else if (c==Colour.BLUE){
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getBonusesAvailable()>0){
                    game.getPlayers().get(i).getAbilityTrack().useBonus(Colour.BLUE,game.getPlayers().get(i).getDice());
                    System.out.println("====bonus: "+game.getPlayers().get(i).getDice().size());
                    List<String> diceColors = new ArrayList<>();
                    for (Die d : game.getPlayers().get(i).getDice()) {
                        System.out.println(d.getColor());
                        diceColors.add(d.getColor());
                    }
                    updateAvailableTilesBasedOnDice(diceColors);
                    return Colour.BLUE;
                }
            }else if (c==Colour.GREEN){
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getBonusesAvailable()>0){
                    game.getPlayers().get(i).getAbilityTrack().useBonus(Colour.GREEN,game.getPlayers().get(i).getDice());
                    System.out.println("====bonus: "+game.getPlayers().get(i).getDice().size());
                    List<String> diceColors = new ArrayList<>();
                    for (Die d : game.getPlayers().get(i).getDice()) {
                        System.out.println(d.getColor());
                        diceColors.add(d.getColor());
                    }
                    updateAvailableTilesBasedOnDice(diceColors);
                    return Colour.GREEN;
                }
            }else if (c==Colour.PURPLE){
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getBonusesAvailable()>0){
                    game.getPlayers().get(i).getAbilityTrack().useBonus(Colour.PURPLE,game.getPlayers().get(i).getDice());
                    System.out.println("====bonus: "+game.getPlayers().get(i).getDice().size());
                    List<String> diceColors = new ArrayList<>();
                    for (Die d : game.getPlayers().get(i).getDice()) {
                        System.out.println(d.getColor());
                        diceColors.add(d.getColor());
                    }
                    updateAvailableTilesBasedOnDice(diceColors);
                    return Colour.PURPLE;
                }
            }else if (c==Colour.YELLOW){
                if(game.getPlayers().get(i).getAbilityTrack().getTrack(c).getBonusesAvailable()>0){
                    game.getPlayers().get(i).getAbilityTrack().useBonus(Colour.YELLOW,game.getPlayers().get(i).getDice());
                    System.out.println("====bonus: "+game.getPlayers().get(i).getDice().size());
                    List<String> diceColors = new ArrayList<>();
                    for (Die d : game.getPlayers().get(i).getDice()) {
                        System.out.println(d.getColor());
                        diceColors.add(d.getColor());
                    }
                    updateAvailableTilesBasedOnDice(diceColors);
                    return Colour.YELLOW;
                }
            }

        }
        return null;
    }
    public String setchangecolor(String changecolor){
        this.changecolor=changecolor;
        return changecolor;
    }
    public String getchangecolor(){
        return changecolor;
    }

    private void updateShields(Player player, int playerIndex) {
        Board board = player.getBoard();

        // Adjust indices if necessary
        // For example, if GUI rows are from bottom to top:
        int[] shieldRowsGUI = {1, 3, 5}; // GUI shield row indices

        for (int idx = 0; idx < shieldRowsGUI.length; idx++) {
            int guiRow = shieldRowsGUI[idx];
            boolean isCompleted = board.isShieldRowCompleted(guiRow);
            building_view.setRowCoA(playerIndex, guiRow, isCompleted);
        }

        // Similarly for columns
        for (int x : List.of(1, 3)) {
            boolean isCompleted = board.isShieldColumnCompleted(x);
            building_view.setColumnCoA(playerIndex, x, isCompleted);
        }
    }



}
