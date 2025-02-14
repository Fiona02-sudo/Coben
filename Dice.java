package comp1110.ass2;

import java.util.ArrayList;
import java.util.List;

/*The Dice class is used to manage a group of dice.
 It allows you to roll all the dice at once, check the results,
 and access a specific die by its index. */
public class Dice {
    private List<Die> dice;// A list to hold multiple Die objects

    public Dice(int numberOfDice) {
        this.dice = new ArrayList<>(numberOfDice);
        // Initialize the list with the given number of Die objects
        for (int i = 0; i < numberOfDice; i++) {
            dice.add(new Die(null));
        }
    }


    public void rollAll() {
        for (Die die : dice) {
            die.roll();
        }
        /* Roll each die in the dice list */
    }
    public List<Die> getRolledDice() {
        /* Return the list of dice after they have been rolled */

        return dice;
    }
    public Die getDie(int index) {
        if (index >= 0 && index < dice.size()) {
            return dice.get(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid dice index: " + index);
        }
        /* Return the die at the specified index */
    }

}
