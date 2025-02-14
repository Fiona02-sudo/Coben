package comp1110.ass2;

import comp1110.ass2.gui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class GameTemplate extends Application {

	GameGUI gui;
	private Game game;

	@Override
	public void start(Stage stage) throws Exception {
		gui = new GameGUI();
		Scene scene = new Scene(gui, GameGUI.WINDOW_WIDTH, GameGUI.WINDOW_HEIGHT);

		gui.setOnStartGame((np, isAI) -> {
			// Initialize the game object
			game = new Game(np);
			gui.setGame(game); // Ensure the game object is accessible in the GUI

			// Set ShieldActionProvider to the GUI implementation
			game.setShieldActionProvider(new GUIShieldActionProvider());

			// Initialize players and pass the game object
			for (Player player: game.getPlayers()) {

				System.out.println("create player: " + player.getName());
				gui.setAvailableDice(List.of("Red", "White", "Blue", "Red", "Yellow"), player);
			}
			System.out.println("============================");
			System.out.println("Game started with " + np + " players.");
			System.out.println("total players: " + game.getPlayers().size());

			// Set available tiles (retrieve from TileFactory)
			List<String> availableTiles = TileFactory.getAvailableTilesBasedOnDice(List.of("Red", "White", "Blue", "Red", "Yellow"));
			gui.setAvailableTiles(availableTiles);

			// Set available actions
			List<String> availableActions = List.of("Confirm", "Use ability", "Reroll","Use bonus");
			gui.setAvailableActions(availableActions);
			List<String> availableColors = List.of("Red", "Green", "Blue", "Purple", "Yellow");
			gui.setAvailablecolors(availableColors);

		});


		gui.setOnTilePlaced(placement -> {
			int currentPlayerIndex = gui.getSelectedPlayer();
			System.out.println("Player " + currentPlayerIndex + " placed a tile");
			Player currentPlayer = game.getPlayers().get(currentPlayerIndex);

			// Create the Tile after rotation and movement
			Tile selectedTile = TileFactory.createTile(placement.getTileName());
			Tile rotatedTile = selectedTile.rotated(placement.getRotation());
			Tile movedTile = rotatedTile.moveBy(new Position(placement.getX(), placement.getY()));

			// Check if the tile can be placed
			boolean canPlace = currentPlayer.getBoard().canPlaceTile(movedTile);
			if (canPlace) {
				// Place the tile
				currentPlayer.placeTile(selectedTile,new Position(placement.getX(), placement.getY()),placement.getRotation());

				// Update the frontend display
				gui.updateBoardDisplay(currentPlayerIndex);

				// Clear the selected tile
				gui.clearTileSelection();

				// Update the score, etc.
				currentPlayer.calculateScore();
				gui.setScore(currentPlayerIndex, currentPlayer.getScore());
				gui.updateDice();
				gui.showState();
				// Check if the game has ended
				if (game.isGameEnded()) {
					int[] finalScores = game.getPlayers().stream().mapToInt(Player::getScore).toArray();
					gui.endGame(finalScores);
				}
			} else {
				gui.setMessage("Cannot place tile at the specified position.");
			}
		});


		gui.setOnDiceSelectionChanged((i) -> {
			gui.setMessage("dice selection: " + gui.getSelectedDice());
		});

		gui.setOnTrackSelectionChanged((i) -> {
			gui.setMessage("track selection: " + gui.getSelectedTracks());
		});
		gui.setOnColourChanged((s) -> {
			gui.setMessage("color change: " + s);
			if (s.equals("Red"))
				//gui.setAvailableActions(List.of("Reroll"));
				gui.setchangecolor(s);
			if(s.equals("Green"))
				//gui.reroll();
				gui.setchangecolor(s);
			if(s.equals("Blue")){
				//gui.useAbility();
				gui.setchangecolor(s);
			}if(s.equals("Purple")){
				gui.setchangecolor(s);
				System.out.println("purple");
			}if(s.equals("Yellow")){
				gui.setchangecolor(s);
			}
			gui.setOnColourChanged(action -> {
				gui.setMessage("color change: " + action);
				if ("Red".equals(action)) {
					gui.setchangecolor("Red");
				} else if ("Green".equals(action)) {
					//gui.confirmPlacement();
					gui.setchangecolor("Green");
				} else if ("Blue".equals(action)) {
					gui.setchangecolor("Blue");
				} else if ("Purple".equals(action)) {
					gui.setchangecolor("Purple");
					System.out.println("Use ability");
				}else if ("Yellow".equals(action)){
					gui.setchangecolor("Yellow");
				}
			});
		});
		gui.setOnGameAction((s) -> {
			int i = gui.getSelectedPlayer();
			gui.setMessage("action: " + s);
			if (s.equals("Give up"))
				gui.setAvailableActions(List.of("Reroll"));
			if(s.equals("Reroll"))
				gui.reroll();
			if(s.equals("Use ability")){
				gui.useAbility();
				System.out.println("Use ability 88");
			}
			if ("Rotate".equals(s)) {
				gui.rotateSelectedTile();
			} else if ("Confirm".equals(s)) {
				gui.confirmPlacement();
				System.out.println("Use confirm");
			} else if ("Pass".equals(s)) {
			}else if("Use bonus".equals(s)){
				gui.usebonus();
			}

		});

		gui.setOnConfirm((s) -> {
			gui.setMessage("confirm: " + s);
		});

		gui.setOnPass((s) -> {
			gui.setMessage("pass: " + s);
		});

		// Start the application:
		stage.setScene(scene);
		stage.setTitle("Copenhagen Roll & Write");
		stage.show();
	}

}
