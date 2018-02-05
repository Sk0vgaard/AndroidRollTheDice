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


    public static final Random RANDOM = new Random();


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
                final Animation anim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
                final Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);

                final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        int value = randomDiceValue();
                        int res = getResources().getIdentifier("dice" + value, "drawable", "com.example.skovgaard.androidrollthedice");

                        if (animation == anim1) {
                            dice1.setImageResource(res);
                        }else if (animation == anim2) {
                            dice2.setImageResource(res);
                        }

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                anim1.setAnimationListener(animationListener);
                anim2.setAnimationListener(animationListener);

                dice1.startAnimation(anim1);
                dice2.startAnimation(anim2);
            }
        });

    }
}
