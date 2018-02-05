package com.example.skovgaard.androidrollthedice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private Button rollDiceBtn;
    private ImageView dice1, dice2;

    private static final Random RANDOM = new Random();


    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rollDiceBtn = findViewById(R.id.rollDiceBtn);
        dice1 = findViewById(R.id.dice1);
        dice2 = findViewById(R.id.dice2);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        rollDiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                doAnimation();

            }
        });

    }

    private void doAnimation() {

        //Animation shake from anim folder. (anim folder is being used for animations).
        final Animation anim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        ;
        final Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        ;

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
                } else if (animation == anim2) {
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

    public static int randomDiceValue() {
        return RANDOM.nextInt(6) + 1;
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if (mAccel > 10) {
                Toast toast = Toast.makeText(getApplicationContext(), "Do not shake to hard!", Toast.LENGTH_LONG);
                toast.show();
                doAnimation();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

}
