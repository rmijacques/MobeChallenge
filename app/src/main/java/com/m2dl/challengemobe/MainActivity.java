package com.m2dl.challengemobe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Sensor proximitySensor;
    boolean allReadyStarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = findViewById(R.id.start_game_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getApplicationContext(),GameActivity.class);
                startActivity(a);

            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,proximitySensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor == proximitySensor){
            if(Math.abs(sensorEvent.values[0])+Math.abs(sensorEvent.values[1])+Math.abs(sensorEvent.values[2]) > 25){
                System.out.println("START ACTIVITY");
                if(!allReadyStarted){
                    allReadyStarted = true;
                    Intent a = new Intent(getApplicationContext(),GameActivity.class);
                    startActivity(a);
                }

            }
            System.out.println(Math.abs(sensorEvent.values[0])+Math.abs(sensorEvent.values[1])+Math.abs(sensorEvent.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}