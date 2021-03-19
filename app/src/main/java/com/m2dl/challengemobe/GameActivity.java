package com.m2dl.challengemobe;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

public class GameActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor gameRotationVectorSensor;
    private GameView gameView;
    private double sensitivity;

    private float lightValue;

    private LinearLayout buttonsLayout;
    private LinearLayout gameLayout;
    private ImageButton sensitivityPlusButton;
    private ImageButton sensitivityMoinsButton;
    private TextView sensitivityInfo;
    private Date startDate;
    private Integer gameViewHeight;
    private Integer gameViewWidth;
    private boolean gameOver;
    private boolean isLinkedToSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sensitivity = 1.0;

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
        gameRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        if (!isLinkedToSensors) listenToSensors();
    }

    private void listenToSensors() {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
        this.isLinkedToSensors = true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.equals(lightSensor)) {
            lightValue = sensorEvent.values[0];
        } else if (sensorEvent.sensor.equals(gameRotationVectorSensor)) {
            float x = sensorEvent.values[1];
            float y = sensorEvent.values[0];
            Point point = gameView.getCirclePosition();
            point.x += x * 150 * sensitivity;
            point.y += y * 150 * sensitivity;
            gameView.setCirclePosition(point);
            if (!(gameViewHeight == null || gameViewWidth == null)) {
                checkGameOver(point.x, point.y);
            }
        }
    }

    private void checkGameOver(float x, float y) {
        float rayonBalle = 100;
        if ((gameViewWidth < (rayonBalle + x) || (x - rayonBalle) < 0) ||
                (gameViewHeight < (rayonBalle + y) || (y - rayonBalle) < 0)) {
            // TODO: aller a l'activité Game Over avec le
            long score = (new Date().getTime() - startDate.getTime()) / 1000;
            if (!gameOver) {
                gameOver = true;
            }

            System.out.println("le jeu est terminé et le score est : " +
                    (new Date().getTime() - startDate.getTime()) / 1000);
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


}