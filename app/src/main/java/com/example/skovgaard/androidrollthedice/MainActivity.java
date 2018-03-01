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
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.skovgaard.androidrollthedice.BE.Roll;
import com.example.skovgaard.androidrollthedice.Model.RollModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    public static final String PACKAGE = "com.example.skovgaard.androidrollthedice";
    private static final Random RANDOM = new Random();
    public static final int SENSOR_SENSITIVITY = 10;

    private final int ROW_DIVIDER = 3;
    private final String TAG = "Test";

    private Button mRollDiceBtn;

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
                shakeDiceAnimation();
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private RollModel mRollModel;

    private List<Dice> mDiceList;
    private Spinner mAmountOfDice;
    private LinearLayout mDiceLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRollModel = RollModel.getInstance();

        //XML
        mRollDiceBtn = findViewById(R.id.rollDiceBtn);

        mDiceLayout = findViewById(R.id.llDices);
        initializeSpinner();


        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


        mRollDiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeDiceAnimation();
            }
        });

        sensor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_goToHistory:{
                Intent intent = HistoryActivity.newIntent(this);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeSpinner(){
        mAmountOfDice = findViewById(R.id.spnAmountOfDice);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.number_of_dice, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAmountOfDice.setAdapter(adapter);
        mAmountOfDice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int amountOfDice = Integer.parseInt(parent.getItemAtPosition(position).toString());
                createDice(amountOfDice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void createDice(int amountOfDice){
        mDiceList = new ArrayList<>();
        mDiceLayout.removeAllViews();
        LinearLayout firstRow = new LinearLayout(this);
        firstRow.setGravity(Gravity.CENTER);
        for (int i = 0; i < ROW_DIVIDER; i++){
            if(i < amountOfDice){
                createSingleDie(firstRow, mDiceList);
            }
        }
        mDiceLayout.addView(firstRow);
        if(amountOfDice> ROW_DIVIDER){
            LinearLayout secondRow = new LinearLayout(this);
            secondRow.setGravity(Gravity.CENTER);
            for(int i = ROW_DIVIDER; i < amountOfDice; i++){
                createSingleDie(secondRow, mDiceList);
            }
            mDiceLayout.addView(secondRow);
        }
    }

    private void createSingleDie(LinearLayout layout, List<Dice> list){
        Dice dice = new Dice(this);
        dice.setPadding(5, 5, 5, 5);
        list.add(dice);
        layout.addView(dice);
    }


    private void rollDice(){
        Roll roll = new Roll();
        for(Dice die: mDiceList){
            die.rollDie();
            roll.addDie(die.getValue());
        }
        mRollModel.addRoll(roll);
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

    private void shakeDiceAnimation() {

        //Animation shake from anim folder. (anim folder is being used for animations).
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        shakeAnimation.setAnimationListener(shakeAnimationListener());

        for(Dice die: mDiceList){
            die.startAnimation(shakeAnimation);
        }
        //Length of the vibrate.
        mVibrator.vibrate(500);

    }

    @NonNull
    private Animation.AnimationListener shakeAnimationListener() {
        //Animation class
        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            //onAnimationEnd since we want to see the result AFTER the animation.
            @Override
            public void onAnimationEnd(Animation animation) {
                rollDice();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
    }

    private class Dice extends AppCompatImageView{
        private final int MAX = 6;

        private int mValue;

        public Dice(Context context) {
            super(context);
            rollDie();
        }

        public int getValue(){
            return mValue;
        }

        public void rollDie(){
            Random rand = new Random();
            mValue = rand.nextInt(MAX) + 1;
            setImage();
        }

        private void setImage(){
            switch (mValue){
                case 1:{
                    setImageResource(R.drawable.dice_one);
                    break;
                }
                case 2:{
                    setImageResource(R.drawable.dice_two);
                    break;
                }
                case 3:{
                    setImageResource(R.drawable.dice_three);
                    break;
                }
                case 4:{
                    setImageResource(R.drawable.dice_four);
                    break;
                }
                case 5:{
                    setImageResource(R.drawable.dice_five);
                    break;
                }
                case 6:{
                    setImageResource(R.drawable.dice_six);
                    break;
                }
            }
        }
    }
}



