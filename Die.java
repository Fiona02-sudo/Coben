package comp1110.ass2;/*The Die class represents a single die used in a game. It stores the color of the die
and the current value showing on the die after it has been rolled.*/

import java.util.Random;

public class Die {
    private String color;
    private int value;
    private boolean check;

    private static final String[] COLORS = {"Red", "Blue", "Purple", "Green", "Yellow", "White"};
    public Die(String color) {
        if (color == null || color.isEmpty()) {
            this.color = randomColor();
        } else {
            this.color = color;
        }
        this.value = 0;
        this.check = false;
    }


    // Key Methods
    public void roll() {
        Random random = new Random();
        this.value = random.nextInt(6) + 1;
        this.color = randomColor();
    }
    public String randomColor() {
        Random random = new Random();
        int index = random.nextInt(COLORS.length);
        return COLORS[index];
    }

    public int getValue() { /* Return the value of the die*/
        return value;
    }
    public String getColor() { /*Return the color of the die*/
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public boolean isCheck() {
        return check;
    }
    public boolean changeCheck(){
        if(check){
            check = false;
        }else{
            check = true;
        }
        return check;
    }
}
