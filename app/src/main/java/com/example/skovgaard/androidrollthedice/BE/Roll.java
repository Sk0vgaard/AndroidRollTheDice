package com.example.skovgaard.androidrollthedice.BE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adamino.
 */

public class Roll {
    public List<Integer> getDice() {
        return dice;
    }

    private Date mDate;

    private List<Integer> dice;

    public Roll() {
        mDate = new Date();
        this.dice = new ArrayList<>();
    }

    public void setDice(List<Integer> dice) {
        this.dice = dice;
    }
}
