package com.example.skovgaard.androidrollthedice.BE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adamino.
 */

public class Roll {
    public List<Integer> getDice() {
        return mDice;
    }

    private Date mDate;

    private List<Integer> mDice;

    public Roll() {
        mDate = new Date();
        mDice = new ArrayList<>();
    }

    public String getTimeAsString() {
        // TODO: Fix this!
        return "Test";
    }

    public void addDie(int die) {
        mDice.add(die);
    }
}
