package comp1110.ass2.gui;

import java.util.*;
import java.util.function.Consumer;

import java.util.List;

import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.geometry.Orientation;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;

/**
 * The Library of selectable tiles.
 */
public class LibraryView extends ListView<Object> {
	private ObservableList<Object> content = FXCollections.observableArrayList();
	static final int SQUARE_WIDTH = 30;
	private int selected = -1;
	private Consumer<String> onSelectionChanged; // TODO

	public LibraryView() {
		super();
		setOrientation(Orientation.HORIZONTAL);
		setCellFactory(list -> new TileCell());

		// Add a listener to the selectedItemProperty to handle selection changes
		getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection == null) {
				selected = -1;
				if (onSelectionChanged != null) {
					onSelectionChanged.accept(null);
				}
			}
			else {
				selected = content.indexOf(newSelection);
				if (onSelectionChanged != null) {
					onSelectionChanged.accept(((LibraryItem)newSelection).getName());
				}
			}
		});
		setItems(content);
	}

	/** View for Tiles. */
	private static class TileView extends GridPane {

		public TileView(LibraryItem tile) {
			super();
			Color tileColor = tile.getFXColor();
			Coordinate ll = tile.getBBoxLL();
			Coordinate tr = tile.getBBoxTR();
			int height = tr.getY() - ll.getY();
			for (var coordinate : tile.brickCoords) {
				Rectangle sq = new Rectangle(SQUARE_WIDTH, SQUARE_WIDTH);
				sq.setFill(tileColor);
				add(sq, coordinate.getX() - ll.getX(), height - (coordinate.getY() - ll.getY()));
			}
		}
	}

	private static class TileCell extends ListCell<Object> {
		@Override
		protected void updateItem(Object item, boolean empty) {
			// calling super here is very important - don't skip this!
			super.updateItem(item, empty);
			if (item != null) {
				setText(((LibraryItem)item).getName());
				setGraphic(new TileView((LibraryItem)item));
			} else {
				setText(null);
				setGraphic(null);
			}
		}
	}


	void show(List<String> tileNames) {
		content.clear();
		for (var name : tileNames) {
			content.add(new LibraryItem(name));
		}
	}

	void setOnSelectionChanged(Consumer<String> cb) {
		onSelectionChanged = cb;
	}

	void clearSelection() {
		selected = -1;
		getSelectionModel().clearSelection();
	}

	void setSelected(String tileName) {
		for (int i = 0; i < content.size(); i++) {
			if (((LibraryItem)content.get(i)).getName().equals(tileName)) {
				selected = i;
				return;
			}
		}
		// Exception?
	}

	String getSelected() {
		if (0 <= selected && selected < content.size())
			return ((LibraryItem)content.get(selected)).getName();
		else
			return null;
	}

	static class Coordinate {
		private int x;
		private int y;

		Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		int getX() {
			return x;
		}

		int getY() {
			return y;
		}

		void rotate() {
			int tmp = y;
			y = -x;
			x = tmp;
		}

		void shift(int dx, int dy) {
			x += dx;
			y += dy;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Coordinate that = (Coordinate) o;
			return x == that.x && y == that.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}

	}

	static class LibraryItem {
		String name;
		List<Coordinate> brickCoords; // ordered
		boolean[] windows; // ith break is window (O)/[ ] here, else X/no pattern here
		private int rotation = 0; // 0-3

		LibraryItem(String name) {
			System.out.println(name);
			this.name = name;
			var coordinates = tiles.get(name);
			this.brickCoords = fromCoordArrays(coordinates[0], coordinates[1]);
			this.windows = new boolean[this.getSize()];
			this.setBrick(0); // default all windows except 1st brick
			if (name.equals("S1O")) this.setAllWindows(true);
		}

		LibraryItem(Placement p) {
			name = p.tileName;
			var coordinates = tiles.get(name);
			this.brickCoords = fromCoordArrays(coordinates[0], coordinates[1]);
			this.windows = new boolean[this.getSize()];
			this.setBrick(0); // default all windows except 1st brick
			if (name.equals("S1O")) this.setAllWindows(true);
			for (int k = 0; k < p.getRotation(); k++)
				this.rotateAndShift();
		}

		static int[][] l2 = new int[][]{{0, 0}, {0, 1}};
		static int[][] l3 = new int[][]{{0, 0, 0}, {0, 1, 2}};
		static int[][] a3 = new int[][]{{0, 1, 1}, {0, 0, 1}};
		static int[][] s1 = new int[][]{{0}, {0}};

		static Map<String, int[][]> tiles = Map.ofEntries
				( // Red
						Map.entry("R2", l2),
						Map.entry("R3", a3),
						Map.entry("R4L", new int[][]{{0, 0, 1, 1}, {0, 1, 0, 1}}),
						Map.entry("R4R", new int[][]{{0, 0, 1, 1}, {0, 1, 0, 1}}),
						// x2
						Map.entry("R5", new int[][]{{0, 1, 1, 2, 2}, {0, 0, 1, 0, 1}}),
						// Blue
						Map.entry("B2", l2),
						Map.entry("B3", a3),
						Map.entry("B4L", new int[][]{{0, 1, 1, 1}, {0, 0, 1, 2}}),
						Map.entry("B4R", new int[][]{{0, 0, 0, 1}, {0, 1, 2, 0}}),
						Map.entry("B5", new int[][]{{0, 1, 1, 1, 2}, {0, 0, 1, 2, 0}}),
						// Purple
						Map.entry("P2", l2),
						Map.entry("P3", l3),
						Map.entry("P4L", new int[][]{{0, 0, 0, 0}, {0, 1, 2, 3}}),
						Map.entry("P4R", new int[][]{{0, 0, 0, 0}, {0, 1, 2, 3}}),
						Map.entry("P5", new int[][]{{0, 1, 2, 3, 4}, {0, 0, 0, 0, 0}}),
						// Green
						Map.entry("G2", l2),
						Map.entry("G3", l3), // TODO fix bug
						Map.entry("G4L", new int[][]{{0, 1, 1, 1}, {1, 0, 1, 2}}),
						Map.entry("G4R", new int[][]{{0, 0, 0, 1}, {0, 1, 2, 1}}),
						Map.entry("G5", new int[][]{{0, 1, 1, 1, 2}, {1, 0, 1, 2, 1}}),
						// Yellow
						Map.entry("Y2", l2),
						Map.entry("Y3", a3),
						Map.entry("Y4L", new int[][]{{0, 0, 1, 1}, {0, 1, 1, 2}}),
						Map.entry("Y4R", new int[][]{{0, 0, 1, 1}, {1, 2, 0, 1}}),
						Map.entry("Y5", new int[][]{{0, 0, 1, 2, 2}, {0, 1, 1, 1, 2}}),
						// Special tiles ("S" -> Colour.GRAY)
						Map.entry("S1X", s1) // [X] fixed
//						Map.entry("S1O", s1)  // [O] fixed - bonu
				);


		String getName() {
			return this.name;
		}

		int getSize() {
			return this.brickCoords.size();
		}

		int getRotation() {
			return this.rotation;
		}

		void rotate() {
			for (Coordinate c : brickCoords)
				c.rotate();
			rotation = (rotation + 1) % 4;
		}

		void rotateAndShift() {
			Coordinate ll0 = getBBoxLL();
			rotate();
			Coordinate ll1 = getBBoxLL();
			shift(ll0.getX() - ll1.getX(), ll0.getY() - ll1.getY());
		}

		void shift(int dx, int dy) {
			for (Coordinate c : brickCoords)
				c.shift(dx, dy);
		}

		static List<Coordinate> fromCoordArrays(int[] xCoords, int[] yCoords) {
			List<Coordinate> coordinates = new ArrayList<>();
			for (int i = 0; i < xCoords.length; i++) {
				coordinates.add(new Coordinate(xCoords[i], yCoords[i]));
			}
			return coordinates;
		}

		Coordinate getBBoxLL() {
			List<Coordinate> coordinates = this.brickCoords;
			int minX = coordinates.stream().mapToInt(Coordinate::getX).min().orElse(Integer.MAX_VALUE);
			int minY = coordinates.stream().mapToInt(Coordinate::getY).min().orElse(Integer.MAX_VALUE);
			return new Coordinate(minX, minY);
		}

		Coordinate getBBoxTR() {
			List<Coordinate> coordinates = this.brickCoords;
			int maxX = coordinates.stream().mapToInt(Coordinate::getX).max().orElse(Integer.MIN_VALUE);
			int maxY = coordinates.stream().mapToInt(Coordinate::getY).max().orElse(Integer.MIN_VALUE);
			return new Coordinate(maxX, maxY);
		}

		int getX(int i) {return this.brickCoords.get(i).getX();}

		int getY(int i) {return this.brickCoords.get(i).getY();}

		Color getFXColor() {
			String colorKey = this.name.substring(0, 1); // Get the first character
			Colour colourEnum = Colour.getColour(colorKey);
			if (colourEnum != null) {
				return colourEnum.getFXColor();
			} else {
				// Handle default or error case
				return Colour.GREY.getFXColor(); // or any default color you prefer
			}
		}


		void setBrick(int i) {
			for (int j = 0; j < this.getSize(); j++)
				windows[j] = (j != i);
		}

		void setAllWindows(boolean on) {
			for (int j = 0; j < this.getSize(); j++) {
				windows[j] = on;
			}
		}
	}



}
