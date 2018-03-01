package com.example.skovgaard.androidrollthedice.Model;

import com.example.skovgaard.androidrollthedice.BE.Roll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adamino.
 */

public class RollModel {
    private static RollModel instance = null;



    private List<Roll> mDiceList;

    public static RollModel getInstance() {
        if (instance == null) {
            instance = new RollModel();
        }
        return instance;
    }

    private RollModel() {
        this.mDiceList = new ArrayList<>();
    }

    public ArrayList<Roll> getDiceList() {
        return new ArrayList<>(mDiceList);
    }

    public void addRoll(Roll roll) {
        mDiceList.add(roll);
    }

    public Roll getRoll(int postitionInList) {
        return mDiceList.get(postitionInList);
    }

    public List<Roll> getRolls(){
        return mDiceList;
    }

    public void clearRolls() {
        mDiceList.clear();
    }
}
