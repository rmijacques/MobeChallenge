package com.m2dl.challengemobe;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        gameView = new GameView(this);

    }

}
