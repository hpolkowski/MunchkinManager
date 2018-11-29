package jaanuszek0700.munchkinmanager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Random;

import jaanuszek0700.munchkinmanager.utils.ShakeDetector;

public class DiceActivity extends FullscreenActivity {

    private static final Random RANDOM = new Random();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private final Handler mHideHandler = new Handler();
    private boolean isRolling = false;

    /**
     * Proces wibracji
     */
    private final Runnable vibrateRunnable = new Runnable() {
        @Override
        public void run() {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);
        initializeContentView(findViewById(R.id.fullscreen_dice));

        final ImageView dice1 = (ImageView) findViewById(R.id.dice_1);
        final ImageView dice2 = (ImageView) findViewById(R.id.dice_2);

        dice1.setImageResource(getDiceImage());
        dice2.setImageResource(getDiceImage());

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if(count > 1)
                    rollDices();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    /**
     * Cofa się do poprzedniego widoku
     *
     * @param view widok
     */
    public void backToGame(View view) {
        finish();
    }

    /**
     * Inicjalizuje rzut kośćmi
     *
     * @param view widok
     */
    public void rollDices(View view) {
        rollDices();
    }

    /**
     * Rzuca kośćmi
     */
    private void rollDices() {
        if (!isRolling) {
            final Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.shake);
            final Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.shake);

            final ImageView dice1 = (ImageView) findViewById(R.id.dice_1);
            final ImageView dice2 = (ImageView) findViewById(R.id.dice_2);

            final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isRolling = true;

                    vibrateRunnable.run();
                    mHideHandler.postDelayed(vibrateRunnable, 200);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (animation == anim1) {
                        dice1.setImageResource(getDiceImage());
                    } else if (animation == anim2) {
                        dice2.setImageResource(getDiceImage());
                    }

                    isRolling = false;
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
    }

    /**
     * Zwraca id odpowiedniego obrazka kostki
     *
     * @return id abrazka
     */
    private int getDiceImage() {
        int value = randomDiceValue();
        int res;

        switch (value) {
            case 1:
                res = R.mipmap.dice_1_icon;
                break;
            case 2:
                res = R.mipmap.dice_2_icon;
                break;
            case 3:
                res = R.mipmap.dice_3_icon;
                break;
            case 4:
                res = R.mipmap.dice_4_icon;
                break;
            case 5:
                res = R.mipmap.dice_5_icon;
                break;
            case 6:
                res = R.mipmap.dice_6_icon;
                break;
            default:
                res = R.mipmap.dice_1_icon;
                break;
        }

        return res;
    }

    /**
     * Losuje cyfrę od 1 do 6
     *
     * @return liczba
     */
    private static int randomDiceValue() {
        return RANDOM.nextInt(6) + 1;
    }
}
