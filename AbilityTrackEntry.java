package comp1110.ass2;
import comp1110.ass2.gui.Colour;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityTrackEntry {
    private final Colour color;
    //private String color;

    private int markedOff;
    private int bonusesAvailable;
    private int abilitiesAvailable;
    private Map<String, Boolean> abilities;

    private List<Integer> bonusPositions;
    private List<Integer> abilityPositions;

    public AbilityTrackEntry(Colour color, List<Integer> bonusPositions, List<Integer> abilityPositions) {
        this.color = color;
        this.markedOff = 0;
        this.bonusesAvailable = 0;
        if (this.color == Colour.RED) {
            this.abilitiesAvailable = 2;
        } else {
            this.abilitiesAvailable = 0;
        }
        this.bonusPositions = bonusPositions;
        this.abilityPositions = abilityPositions;

        abilities = new HashMap<>();
        abilities.put("Re-Roll", false);
        abilities.put("No X", false);
        abilities.put("Color Change", false);
        abilities.put("Use Again", false);
        abilities.put("One X", false);
    }

    public Colour getColor() {
        return color;
    }

    public int getMarkedOff() {
        return markedOff;
    }

    public int getBonusesAvailable() {
        return bonusesAvailable;
    }

    public int getAbilitiesAvailable() {
        return abilitiesAvailable;

    }

    public void markOff() {
        if (markedOff < 9) {
            markedOff++;
            System.out.println("mark  " + bonusPositions.contains(markedOff));
            if (bonusPositions.contains(markedOff)) {
                bonusesAvailable++;
                unlockBonus();
            }
            if (abilityPositions.contains(markedOff)) {
                abilitiesAvailable++;
                unlockAbility();
            }
        } else {
            markedOff = 9;
        }

    }

    private void unlockAbility() {
        switch (markedOff) {
            case 2:
                abilities.put("Re-Roll", true);
                break;
            case 4:
                abilities.put("No X", true);
                break;
            case 6:
                abilities.put("Color Change", true);
                break;
            case 8:
                abilities.put("Use Again", true);
                break;
            default:
                break;
        }
    }

    private void unlockBonus() {

    }


    public boolean useAbility() {
        if (abilitiesAvailable > 0) {
            abilitiesAvailable--;
        }
        return true;
    }


    public boolean useBonus() {
        if (bonusesAvailable > 0) {
            bonusesAvailable--;
            return true;
        }
        return false;
    }
    public int getNextBonus() {
        for (int n: bonusPositions) {
            if (n > markedOff) {
                return n;
            }
        }
        return 0;
    }
    public int getNextAbility() {
        for (int n: abilityPositions) {
            if (n > markedOff) {
                return n;
            }
        }
        return 0;
    }

    public boolean isCompleted() {
        return markedOff >= 9;
    }

    private boolean scoredForCompletion = false;

    public boolean hasScoredForCompletion() {
        return scoredForCompletion;
    }

    public void setScoredForCompletion(boolean scored) {
        this.scoredForCompletion = scored;
    }




}