package com.example.skovgaard.androidrollthedice.Model;

import java.util.ArrayList;

/**
 * Created by Adamino.
 */

public class DiceModel {
    private static DiceModel instance = null;

    public ArrayList<Integer> getDiceList() {
        return new ArrayList<>(mDiceList);
    }

    private ArrayList<Integer> mDiceList;

    public static DiceModel getInstance() {
        if (instance == null) {
            instance = new DiceModel();
        }
        return instance;
    }

    private DiceModel() {
        this.mDiceList = new ArrayList<>();
    }

    public void addDice(int diceNumber) {
        mDiceList.add(diceNumber);
    }
}
