package com.m2dl.challengemobe;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private GameActivity context;
    private Canvas canvas;

    public void setInclinaison(double inclinaison) {
        this.inclinaison = inclinaison;
    }

    private double inclinaison;
    private int contextHeight;
    private int contextWidth;
    private SharedPreferences sharedPref;


    public GameView(GameActivity context) {
        super(context);
        this.context = context;
        setFocusable(true);
        getHolder().addCallback(this);
        this.sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        contextHeight = displayMetrics.heightPixels;
        contextWidth = displayMetrics.widthPixels;


    }

    public GameThread getThread() {
        return thread;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);

            Paint paint = new Paint();

            canvas.drawCircle(20,0+contextHeight, 100, paint);

//
        }
    }



    public void goToGameOver(int score){
        thread.setRunning(false);
        //go to gameover activity
    }

    public void drawText(String text){

            System.out.println("sensor text");

    }


}
