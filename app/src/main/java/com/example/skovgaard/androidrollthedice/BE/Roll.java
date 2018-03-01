package com.example.skovgaard.androidrollthedice.BE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adamino.
 */

public class Roll {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

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
        return SIMPLE_DATE_FORMAT.format(mDate);
    }

    public void addDie(int die) {
        mDice.add(die);
    }
}
