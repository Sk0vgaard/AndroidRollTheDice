package com.example.skovgaard.androidrollthedice.BLL;

import com.example.skovgaard.androidrollthedice.R;

/**
 * Created by Adamino.
 */

public class DieManager {
    public static int getImage(int dieNumber) {
        switch (dieNumber) {
            case 1: {
                return R.drawable.dice_one;
            }
            case 2: {
                return R.drawable.dice_two;
            }
            case 3: {
                return R.drawable.dice_three;
            }
            case 4: {
                return R.drawable.dice_four;
            }
            case 5: {
                return R.drawable.dice_five;
            }
            case 6: {
                return R.drawable.dice_six;
            }
            default:
                return 0;
        }
    }
}
