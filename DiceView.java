package comp1110.ass2.gui;

import java.util.List;

import comp1110.ass2.Die;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.control.CheckBox;
import javafx.geometry.Pos;

class DiceView extends GridPane {

    static final int DICE_WIDTH = 60;

    private int maxShowing;
    private Rectangle[] dice;
    private CheckBoxGroup selected;

    // Make the constructor non-public
    DiceView(int width) {
        super();
        setHgap(5);
        setVgap(5);
        setAlignment(Pos.BASELINE_CENTER);
        // setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderStroke.MEDIUM)));
        maxShowing = width;
        dice = new Rectangle[maxShowing];
        selected = new CheckBoxGroup(maxShowing);
        for (int i = 0; i < maxShowing; i++) {
            dice[i] = new Rectangle(DICE_WIDTH, DICE_WIDTH);
            dice[i].setStroke(Color.BLACK);
            dice[i].setStrokeWidth(7);
            dice[i].setStrokeLineJoin(StrokeLineJoin.ROUND);
            dice[i].setVisible(false);
            add(dice[i], i, 0);
            CheckBox cb = selected.getCheckBox(i);
            add(cb, i, 1);
            dice[i].setOnMouseClicked(e -> cb.fire());
        }
    }

    CheckBoxGroup selectors() {
        return selected;
    }

    void show(List<String> diceToShow) {
        assert (diceToShow.size() <= maxShowing+1);
        if(diceToShow.size() == 5){
            for (int i = 0; i < diceToShow.size(); i++) {
                this.dice[i].setVisible(true);
                this.dice[i].setFill(Colour.getColour(diceToShow.get(i)).getFXColor());
            }
            for (int i = diceToShow.size(); i < maxShowing; i++) {
                this.dice[i].setVisible(false);
            }
        }

        selected.enableRange(0, diceToShow.size());
        selected.disableRange(diceToShow.size(), maxShowing);
    }
    void clearSelection() {
        for (int i = 0; i < maxShowing; i++) {
            CheckBox cb = selected.getCheckBox(i);
            cb.setSelected(false);
        }
    }



}
