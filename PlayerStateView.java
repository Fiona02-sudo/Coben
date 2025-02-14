package comp1110.ass2.gui;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Labeled;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import comp1110.ass2.*;

class PlayerStateView extends GridPane {
    // display and button cells within the grid, by row
    Labeled[][] cells;
    CheckBoxGroup selectors;

    private static final Border cellBorder = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN));

    private static final int TRACKS_LEFT_X = 1;
    private static final int TRACKS_HEADER_Y = 2;
    private static final int TRACKS_FIRST_Y = 4;
    private static final int DIGIT_WIDTH = 24;

    static final String BONUS_TEXT = "\u271A";
    static final String ABILITY_TEXT = "\u272A";

    PlayerStateView() {
        super();  // setHgap(5); setVgap(5); setAlignment(Pos.BASELINE_LEFT);
        cells = new Labeled[7][];
        // row 0: Player id:
        cells[0] = new Labeled[1];
        cells[0][0] = new Label("         ");
        cells[0][0].setFont(Font.font(GameGUI.LARGE_FONT_SIZE));
        add(cells[0][0], 0, 0, GridPane.REMAINING, 1);
        // row 1: Score
        Label l = new Label("Score");
        l.setFont(Font.font(GameGUI.MEDIUM_FONT_SIZE));
        add(l, 0, TRACKS_HEADER_Y + 1);
        cells[1] = new Labeled[1];
        cells[1][0] = new Label("0");
        cells[1][0].setFont(Font.font(GameGUI.TITLE_FONT_SIZE));
        cells[1][0].setAlignment(Pos.CENTER);
        cells[1][0].setBorder(cellBorder);
        cells[1][0].setPrefWidth(47);
        cells[1][0].setPrefHeight(47);
        add(cells[1][0], 0, TRACKS_FIRST_Y, 1, 2);
        selectors = new CheckBoxGroup(5);
        // header for the track rows:
        l = new Label("  \u2718  ");
        l.setFont(Font.font(GameGUI.MEDIUM_FONT_SIZE));
        l.setAlignment(Pos.BASELINE_CENTER);
        l.setPrefWidth(2 * DIGIT_WIDTH);
        add(l, TRACKS_LEFT_X, TRACKS_HEADER_Y + 1);
        l = new Label("Next");
        l.setFont(Font.font(12));
        l.setAlignment(Pos.BASELINE_CENTER);
        add(l, TRACKS_LEFT_X + 1, TRACKS_HEADER_Y, 2, 1);
        l = new Label("Avail");
        l.setFont(Font.font(GameGUI.SMALL_FONT_SIZE));
        l.setAlignment(Pos.BASELINE_CENTER);
        add(l, TRACKS_LEFT_X + 3, TRACKS_HEADER_Y, 2, 1);
        l = new Label("\u271B");
        l.setFont(Font.font(GameGUI.MEDIUM_FONT_SIZE));
        l.setAlignment(Pos.BASELINE_CENTER);
        l.setPrefWidth(DIGIT_WIDTH);
        add(l, TRACKS_LEFT_X + 1, TRACKS_HEADER_Y + 1);
        l = new Label("\u2730");
        l.setFont(Font.font(GameGUI.MEDIUM_FONT_SIZE));
        l.setAlignment(Pos.BASELINE_CENTER);
        l.setPrefWidth(DIGIT_WIDTH);
        add(l, TRACKS_LEFT_X + 2, TRACKS_HEADER_Y + 1);
        l = new Label(BONUS_TEXT);
        l.setFont(Font.font(GameGUI.MEDIUM_FONT_SIZE));
        l.setAlignment(Pos.BASELINE_CENTER);
        l.setPrefWidth(DIGIT_WIDTH);
        add(l, TRACKS_LEFT_X + 3, TRACKS_HEADER_Y + 1);
        l = new Label(ABILITY_TEXT); // alt. 272D, 272E
        l.setFont(Font.font(GameGUI.MEDIUM_FONT_SIZE));
        l.setAlignment(Pos.BASELINE_CENTER);
        l.setPrefWidth(DIGIT_WIDTH);
        add(l, TRACKS_LEFT_X + 4, TRACKS_HEADER_Y + 1);
        // track rows:
        for (int i = 0; i < 5; i++) {
            Colour c = Colour.values()[i];
            Background b = new Background(new BackgroundFill(c.getFXColor(), CornerRadii.EMPTY, Insets.EMPTY));
            cells[i + 2] = new Labeled[6];
            for (int j = 0; j < 5; j++) {
                cells[i + 2][j] = new Label("");
                cells[i + 2][j].setFont(Font.font(GameGUI.MEDIUM_FONT_SIZE));
                cells[i + 2][j].setBackground(b);
                cells[i + 2][j].setBorder(cellBorder);
                cells[i + 2][j].setAlignment(Pos.BASELINE_CENTER);
                cells[i + 2][j].setPrefHeight(24);
                cells[i + 2][j].setPrefWidth(((j == 0) ? 2 : 1) * DIGIT_WIDTH);
                add(cells[i + 2][j], TRACKS_LEFT_X + j, TRACKS_FIRST_Y + i);
            }
            CheckBox cb = selectors.getCheckBox(i);
            cb.setPrefHeight(24);
            cb.setPrefWidth(24);
            add(cb, TRACKS_LEFT_X + 5, TRACKS_FIRST_Y + i);
        }
	score = new int[GameGUI.MAX_N_PLAYERS];
	trackInfo = new ArrayList<Map<String, int[]>>();
	for (int k = 0; k < GameGUI.MAX_N_PLAYERS; k++) {
	    trackInfo.add(new HashMap<String, int[]>());
	    for (int i = 0; i < 5; i++) {
		Colour c = Colour.values()[i];
		trackInfo.get(k).put(c.toString(), new int[5]);
	    }
	}
	selectors.enableRange(0, 5);
    }

    CheckBoxGroup getTrackSelectors() {
        return selectors;
    }


    private int[] score;
    private List<Map<String, int[]>> trackInfo;

    public void show(int player) {
        cells[1][0].setText(Integer.toString(score[player]));
        for (int i = 0; i < 5; i++) {
            String c = Colour.values()[i].toString();
	        int[] info = trackInfo.get(player).get(c);
            cells[i + 2][0].setText(info[0] + "/9"); // track length is 9 for all
            int toNextBonus = info[1];
            int toNextAbility = info[2];
            int nBonusAva = info[3];
            int nAbilityAva = info[4];
            cells[i + 2][1].setText(toNextBonus < 0 ? "-" : Integer.toString(toNextBonus));
            cells[i + 2][2].setText(toNextAbility < 0 ? "-" : Integer.toString(toNextAbility));
            cells[i + 2][3].setText(Integer.toString(nBonusAva));
            cells[i + 2][4].setText(Integer.toString(nAbilityAva));
        }
    }

    void setScore(int player, int score) {
        this.score[player] = score;
    }

    void setTrackInfo(int player, String colour, int nMarked,
		      int nBonusAvailable, int nAbilityAvailable,
		      int nBonusToNext, int nAbilityToNext) {
        if (trackInfo.get(player).get(colour) == null) {
            trackInfo.get(player).put(colour, new int[5]);
        }
	trackInfo.get(player).get(colour)[0] = nMarked;
	trackInfo.get(player).get(colour)[1] = nBonusToNext;
	trackInfo.get(player).get(colour)[2] = nAbilityToNext;
	trackInfo.get(player).get(colour)[3] = nBonusAvailable;
	trackInfo.get(player).get(colour)[4] = nAbilityAvailable;
    }


}
