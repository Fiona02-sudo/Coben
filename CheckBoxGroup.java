package comp1110.ass2.gui;
import comp1110.ass2.*;
import java.util.List;
import java.util.ArrayList;

import javafx.scene.control.CheckBox;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.util.function.IntConsumer;

public class CheckBoxGroup {
    int size;
    CheckBox[] selectors;
    boolean inhibitSelectionEvent = false;

    public CheckBoxGroup(int n) {
        size = n;
        selectors = new CheckBox[n];
        for (int i = 0; i < n; i++) {
            selectors[i] = new CheckBox("");
            selectors[i].setDisable(true);
        }
    }

    public CheckBox getCheckBox(int i) {
        return selectors[i];
    }

    public void setOnSelectionChanged(IntConsumer handler) {
        for (int i = 0; i < size; i++) {
	    final int fi = i;
            selectors[i].setOnAction(e -> {
		    if (!inhibitSelectionEvent) handler.accept(fi);
		});
	}
    }

    public List<Integer> getSelection() {
        ArrayList<Integer> sel = new ArrayList<>();
        for (int i = 0; i < selectors.length; i++)
            if (selectors[i].isSelected()) sel.add(i);
        return sel;
    }

    public void enableRange(int from, int to) {
        for (int i = from; i < to; i++) {
            this.selectors[i].setDisable(false);
        }
    }

    public void disableRange(int from, int to) {
        inhibitSelectionEvent = true;
        for (int i = from; i < to; i++) {
            this.selectors[i].setSelected(false);
            this.selectors[i].setDisable(true);
        }
        inhibitSelectionEvent = false;
    }

    public void clearSelection() {
        inhibitSelectionEvent = true;
        for (CheckBox selector : selectors) selector.setSelected(false);
        inhibitSelectionEvent = false;
    }


    public void setDisable(int index, boolean disable) {
        if (index >= 0 && index < selectors.length) {
            selectors[index].setDisable(disable);
        }
    }


}
