package com.example.skovgaard.androidrollthedice;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.skovgaard.androidrollthedice.BE.Roll;
import com.example.skovgaard.androidrollthedice.Model.RollModel;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    public static final String PACKAGE = "com.example.skovgaard.androidrollthedice";
    private static final Random RANDOM = new Random();
    public static final int SENSOR_SENSITIVITY = 10;

    private Button mRollDiceBtn, mHistoryBtn;
    private ImageView mDice1, mDice2;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private Vibrator mVibrator;
    private final SensorEventListener mSensorListener = new SensorEventListener() {

        //Checks if shake'd.
        public void onSensorChanged(SensorEvent se) {
            //Axis
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            // After shake, Last gets the same axis as Current.
            mAccelLast = mAccelCurrent;
            //Checks which axis is being used. (Two axis is a must)
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            //mAccel is a high-pass filter on the input. It has the effect of making sudden changes in acceleration. -
            //Checks the sensitive to the forces at the extremes of each shake.
            //0.0f tells the compiler that we intend to make the value a single precision floating point number. (part of High-pass Filter)
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter


            if (mAccel > SENSOR_SENSITIVITY) {
//                Toast toast = Toast.makeText(getApplicationContext(), "Don't shake to hard", Toast.LENGTH_SHORT);
//                toast.show();
                doAnimation();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private RollModel mRollModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRollModel = RollModel.getInstance();

        //XML
        mRollDiceBtn = findViewById(R.id.rollDiceBtn);
        mHistoryBtn = findViewById(R.id.historyBtn);
        mDice1 = findViewById(R.id.dice1);
        mDice2 = findViewById(R.id.dice2);

        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


        mRollDiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAnimation();
            }
        });

        mHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = HistoryActivity.newIntent(view.getContext());
                startActivity(intent);
            }
        });

        sensor();
    }

    private void sensor() {
        //sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private static int randomDiceValue() {
        //Number from 1-6
        return RANDOM.nextInt(6) + 1;
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    @Override
//    protected void onPause() {
//        mSensorManager.unregisterListener(mSensorListener);
//        super.onPause();
//    }IF NO VIBRATE UNCOMMENT THIS.

    private void doAnimation() {

        //Animation shake from anim folder. (anim folder is being used for animations).
        final Animation anim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        final Animation anim2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        final Animation.AnimationListener animationListener = startAnimation(anim1, anim2);


        anim1.setAnimationListener(animationListener);
        anim2.setAnimationListener(animationListener);

        //Runs the animation.
        mDice1.startAnimation(anim1);
        mDice2.startAnimation(anim2);
        //Length of the vibrate.
        mVibrator.vibrate(500);

    }

    @NonNull
    private Animation.AnimationListener startAnimation(final Animation anim1, final Animation anim2) {
        //Animation class
        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            //onAnimationEnd since we want to see the result AFTER the animation.
            @Override
            public void onAnimationEnd(Animation animation) {
                rollTheDice(animation, anim1, anim2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
    }

    public void rollTheDice(Animation animation, Animation anim1, Animation anim2) {
        int dieNumber = randomDiceValue();

        //Drawable is where images is located. defPackage is for MainActivity.
        int newRandomDie = getResources().getIdentifier("dice" + dieNumber, "drawable", PACKAGE);

        //Sets a "new" dice if the dice is the same.
        if (animation == anim1) {
            mDice1.setImageResource(newRandomDie);
        } else if (animation == anim2) {
            mDice2.setImageResource(newRandomDie);
        }
        Roll newRoll = new Roll();
        newRoll.addDie(dieNumber);

        mRollModel.addRoll(newRoll);

    }

} 



