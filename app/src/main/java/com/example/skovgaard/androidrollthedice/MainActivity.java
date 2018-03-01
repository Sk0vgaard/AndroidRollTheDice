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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.skovgaard.androidrollthedice.Model.DiceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    public static final String PACKAGE = "com.example.skovgaard.androidrollthedice";
    private static final Random RANDOM = new Random();
    public static final int SENSOR_SENSITIVITY = 10;

    private final int ROW_DIVIDER = 3;

    private Button mRollDiceBtn, mHistoryBtn;

    //TODO RKL: Remove
//    private ImageView mDice1, mDice2;

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


    private DiceModel mDiceModel;

    //Created by RKL
    private List<Dice> mDiceList;
    private Spinner mAmountOfDice;
    private LinearLayout mDiceLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDiceModel = DiceModel.getInstance();

        //XML
        mRollDiceBtn = findViewById(R.id.rollDiceBtn);
        mHistoryBtn = findViewById(R.id.historyBtn);

        mDiceLayout = findViewById(R.id.llDices);
        initializeSpinner();


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

    /**
     * Created by RKL
     */
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


    /**
     * Created by RKL
     */
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

    /**
     * Created by RKL
     * @param layout
     * @param list
     */
    private void createSingleDie(LinearLayout layout, List<Dice> list){
        Dice dice = new Dice(this);
        dice.setPadding(5, 5, 5, 5);
        list.add(dice);
        layout.addView(dice);
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

        //TODO RKL: Remove
        //Runs the animation.
//        mDice1.startAnimation(anim1);
//        mDice2.startAnimation(anim2);


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
        int diceNumber = randomDiceValue();

        //Drawable is where images is located. defPackage is for MainActivity.
        int newRandomDice = getResources().getIdentifier("dice" + diceNumber, "drawable", PACKAGE);

        //TODO RKL: Set anim on programmatic dice.
        //Sets a "new" dice if the dice is the same.
//        if (animation == anim1) {
//
//            mDice1.setImageResource(newRandomDice);
//        } else if (animation == anim2) {
//            mDice2.setImageResource(newRandomDice);
//        }

        // TODO ALH: Refactor to reflect new implementation
        addDiceToSpinner(diceNumber);

    }

    public void addDiceToSpinner(int diceNumber) {
        mDiceModel.addDice(diceNumber);
        //Sort by newest to be on top.
//        Collections.reverse(diceList);
//        Log.d("Catching", mDiceModel.getDiceList() + "TEST");
//
//        //Adds the new roll to the spinner.
//        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mDiceModel.getDiceList());
//        mSpinner.setAdapter(adapter);
    }

    private class Dice extends AppCompatImageView{
        private final int MAX = 6;

        private int mValue;

        public Dice(Context context) {
            super(context);
            rollDice();
        }

        public int getValue(){
            return mValue;
        }

        public void rollDice(){
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



