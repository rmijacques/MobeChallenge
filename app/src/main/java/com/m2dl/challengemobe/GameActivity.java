package com.m2dl.challengemobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class GameActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor gameRotationVectorSensor;
    private GameView gameView;
    private double sensitivity;

    private float lightValue;

    private LinearLayout buttonsLayout;
    private LinearLayout gameLayout;

    private Date startDate;
    private Integer gameViewHeight;
    private Integer gameViewWidth;
    private boolean gameOver;
    private boolean isLinkedToSensors;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FULLSCREEN
        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);



        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        int valeur_y = sharedPref.getInt("valeur_y", 0);
        int nbParties = sharedPref.getInt("nb_parties", 0);
        valeur_y = (valeur_y + 100) % 400;
        nbParties = nbParties++;

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("valeur_y", valeur_y);
        editor.putInt("nb_parties", nbParties);
        editor.apply();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_layout);

        //récupération des elements de la vue
        gameView = new GameView(this);
        gameLayout = findViewById(R.id.main_game_layout);
        gameLayout.addView(gameView);
        majsensitivityInfo();


        gameOver = false;
        isLinkedToSensors = false;
        setUpSensors();

        startDate = new Date();

        gameLayout.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    gameView.setDragPoint((int)event.getX(), (int) event.getY());
                    gameView.refreshDrawableState();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    gameView.setEtat(GameView.gamestate.LAUNCHING);
                }

                return true;
            }
        });

        gameView.post(new Runnable() {
            @Override
            public void run() {
                gameViewWidth = Integer.valueOf(gameView.getWidth());
                gameViewHeight = Integer.valueOf(gameView.getHeight());
            }
        });
    }

    private void setUpSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        gameRotationVectorSensor = sensorManager.getDefaultSensor(27);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);


        if (!isLinkedToSensors) listenToSensors();
    }

    private void listenToSensors() {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
        this.isLinkedToSensors = true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            gameView.setInclinaison(y);
        }
        }



    private void checkGameOver(float x, float y) {
        float rayonBalle = 100;
        if ((gameViewWidth < (rayonBalle + x) || (x - rayonBalle) < 0) ||
                (gameViewHeight < (rayonBalle + y) || (y - rayonBalle) < 0)) {
            // TODO: aller a l'activité Game Over avec le
            long score = (new Date().getTime() - startDate.getTime()) / 1000;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override()
    public void onPause() {
        super.onPause();
        stopSensorsListenners();
    }

    private void stopSensorsListenners() {
        this.isLinkedToSensors = false;
        sensorManager.unregisterListener(this, lightSensor);
        sensorManager.unregisterListener(this, gameRotationVectorSensor);
    }

    @Override()
    public void onResume() {
        super.onResume();
        if (!isLinkedToSensors) listenToSensors();
    }

    public void majsensitivityInfo() {
        //   sensitivityInfo.setText(String.valueOf(sensitivity));
    }

    public float getLightValue() {
        return lightValue;
    }

    private double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
            return null;

        double[] output = new double[input.length];

        for (int i = 0; i < input.length; i++)
            output[i] = input[i];

        return output;
    }


}