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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    public static final int SENSOR_SENSITIVITY = 10;
    public static final String DICE_KEY = "DICE";
    public static final String SPINER_KEY = "SPINNER";

    private final int MAX_ROW_SIZE = 3;

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
    private List<Integer> mDiceValues;
    private Spinner mAmountOfDice;
    private int mSpinnerValue;
    private LinearLayout mDiceLayout;
    private LinearLayout firstRow;
    private LinearLayout secondRow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRollModel = RollModel.getInstance();
        mDiceValues = new ArrayList<>();

        //XML
        Button rollDiceBtn = findViewById(R.id.rollDiceBtn);

        mDiceLayout = findViewById(R.id.llDices);

        initializeSpinner();

        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


        rollDiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shakeDiceAnimation();
            }
        });

        sensor();

        // Placed at end to ensure dice will be reinstated at the right time
        if (savedInstanceState != null) {
            mDiceValues = (List<Integer>) savedInstanceState.getSerializable(DICE_KEY);
            mSpinnerValue = savedInstanceState.getInt(SPINER_KEY);
            reinstateDice(mDiceValues);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDiceValues.clear();
        for (Dice die : mDiceList) {
            mDiceValues.add(die.getValue());
        }
        outState.putSerializable(DICE_KEY, (Serializable) mDiceValues);
        outState.putInt(SPINER_KEY, mSpinnerValue);
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
                // Check if spinner value is the same
                if (amountOfDice != 0 && amountOfDice != mSpinnerValue) {
                    mSpinnerValue = amountOfDice;
                    createDice(amountOfDice);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * Reinstate original dice from previous state
     * @param diceValues
     */
    private void reinstateDice(List<Integer> diceValues) {
        mDiceList = new ArrayList<>();
        mDiceLayout.removeAllViews();
        firstRow = new LinearLayout(this);
        firstRow.setGravity(Gravity.CENTER);
        for (int i = 0; i < MAX_ROW_SIZE; i++){
            if(i < diceValues.size()){
                createSingleDie(firstRow, mDiceList, diceValues.get(i));
            }
        }
        mDiceLayout.addView(firstRow);

        if(diceValues.size() > MAX_ROW_SIZE){
            secondRow = new LinearLayout(this);
            secondRow.setGravity(Gravity.CENTER);
            for(int i = MAX_ROW_SIZE; i < diceValues.size(); i++){
                createSingleDie(secondRow, mDiceList, diceValues.get(i));
            }
            mDiceLayout.addView(secondRow);
        }
    }


    /**
     * Create new random dice
     * @param amountOfDice
     */
    private void createDice(int amountOfDice){
        mDiceList = new ArrayList<>();
        mDiceLayout.removeAllViews();
        firstRow = new LinearLayout(this);
        firstRow.setGravity(Gravity.CENTER);
        for (int i = 0; i < MAX_ROW_SIZE; i++){
            if(i < amountOfDice){
                createSingleDie(firstRow, mDiceList);
            }
        }
        mDiceLayout.addView(firstRow);
        if(amountOfDice> MAX_ROW_SIZE){
            secondRow = new LinearLayout(this);
            secondRow.setGravity(Gravity.CENTER);
            for(int i = MAX_ROW_SIZE; i < amountOfDice; i++){
                createSingleDie(secondRow, mDiceList);
            }
            mDiceLayout.addView(secondRow);
        }
    }

    /**
     * Create Single die randomly
     * @param layout
     * @param list
     */
    private void createSingleDie(LinearLayout layout, List<Dice> list){
        Dice dice = new Dice(this);
        dice.setPadding(5, 5, 5, 5);
        list.add(dice);
        layout.addView(dice);
    }

    private void createSingleDie(LinearLayout layout, List<Dice> list, int value){
        Dice dice = new Dice(this, value);
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

    private class Dice extends AppCompatImageView {
        private final int MAX = 6;

        private int mValue;

        /**
         * Create new random die
         * @param context
         */
        public Dice(Context context) {
            super(context);
            rollDie();
        }

        public Dice(Context context, int value) {
            super(context);
            mValue = value;
            setImage();
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



