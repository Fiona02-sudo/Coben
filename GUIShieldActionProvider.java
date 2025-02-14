package comp1110.ass2.gui;

import comp1110.ass2.ShieldActionProvider;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GUIShieldActionProvider implements ShieldActionProvider {
    GameGUI gameGUI = new GameGUI();

    @Override
    public String getShieldAction(String shieldId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Shield Unlocked");
        alert.setHeaderText("You have unlocked a shield!");
        alert.setContentText("Choose an action:");

        ButtonType buttonA = new ButtonType("Option A: Place single square");
        ButtonType buttonB = new ButtonType("Option B: Advance ability track");

        alert.getButtonTypes().setAll(buttonA, buttonB);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonA) {
            gameGUI.showState();
            return "a";
        } else {
            gameGUI.showState();
            return "b";
        }
    }


    @Override
    public String chooseAbilityTrack(String[] availableTracks) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choose Ability Track");
        alert.setHeaderText("Select an ability track to advance by 2 steps:");

        // Clear the default button and add the available ability track button
        alert.getButtonTypes().clear();
        for (String track : availableTracks) {
            alert.getButtonTypes().add(new ButtonType(track));
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            gameGUI.showState();
            return result.get().getText();
        } else {
            gameGUI.showState();
            return availableTracks[0];
        }
    }

}
