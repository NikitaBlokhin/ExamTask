package lab.isu.raklit.examtask2;

import android.annotation.SuppressLint;

public class Card {
    public int count;
    public int fill;
    public int shape;
    public int color;

    public Card() {

    }

    @SuppressLint("DefaultLocale")
    public String toString() {
        return String.format("Count: %d Fill: %d Shape: %d Color: %d", count, fill, shape, color);
    }
}
