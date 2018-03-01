package com.example.skovgaard.androidrollthedice.Model;

import com.example.skovgaard.androidrollthedice.BE.Roll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adamino.
 */

public class DiceModel {
    private static DiceModel instance = null;



    private List<Roll> mDiceList;

    public static DiceModel getInstance() {
        if (instance == null) {
            instance = new DiceModel();
        }
        return instance;
    }

    public ArrayList<Roll> getDiceList() {
        return new ArrayList<>(mDiceList);
    }

    private DiceModel() {
        this.mDiceList = new ArrayList<>();
    }

    public void addRoll(Roll roll) {
        mDiceList.add(roll);
    }

    public Roll getRoll(int postitionInList) {
        return mDiceList.get(postitionInList);
    }
}
