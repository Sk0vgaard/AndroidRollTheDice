package com.example.skovgaard.androidrollthedice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private Button rollDiceBtn;
    private ImageView dice1, dice2;


    private static final Random RANDOM = new Random();


    public static int randomDiceValue() {
        return RANDOM.nextInt(6) +1;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rollDiceBtn = findViewById(R.id.rollDiceBtn);
        dice1 = findViewById(R.id.dice1);
        dice2 = findViewById(R.id.dice2);

        rollDiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doAnimation();

            }

            private void doAnimation() {

                //Animation shake from anim folder. (anim folder is being used for animations).
                final Animation anim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);;
                final Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);;

                //Animation class
                final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    //onAnimationEnd since we want to see the result AFTER the animation.
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        int diceNumber = randomDiceValue();

                        //Drawable is where images is saved. defPackage is for MainActivity.
                        int newRandomDice = getResources().getIdentifier("dice" + diceNumber, "drawable", "com.example.skovgaard.androidrollthedice");

                        //Sets a "new" dice if the dice is the same.
                        if (animation == anim1) {
                            dice1.setImageResource(newRandomDice);
                        }else if (animation == anim2) {
                            dice2.setImageResource(newRandomDice);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                };


                anim1.setAnimationListener(animationListener);
                anim2.setAnimationListener(animationListener);

                //Runs the animation.
                dice1.startAnimation(anim1);
                dice2.startAnimation(anim2);
            }
        });

    }
}
