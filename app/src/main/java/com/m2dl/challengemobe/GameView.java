package com.m2dl.challengemobe;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private GameActivity context;
    private Canvas canvas;
    private double inclinaison;
    private int contextHeight;
    private int contextWidth;
    private SharedPreferences sharedPref;

    public static int OBSTACLE_HEIGHT = 100;
    private List<Point> obstacles;


    private float x0=100;
    private float y0;
    private float x;
    private float y;
    private float a;
    private float t;
    private float v0;
    private float g;


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


        obstacles = new ArrayList<>();
        createRandomObstacle();
        createRandomObstacle();



        x0 = 100;
        y0 = contextHeight/2;
        g = 0.5f;
        a = (float) (Math.PI/4);
        v0 = 25;

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

            canvas.drawColor(Color.BLUE);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(0, 0, 0));

            calculTrajectoire();
            canvas.drawCircle(x, y, 50, paint);


            canvas.drawCircle(20,0+contextHeight, 100, paint);

//
        }
    }







    public void createRandomObstacle(){
        Random rand = new Random();
        int posy = rand.nextInt(contextHeight - OBSTACLE_HEIGHT) + OBSTACLE_HEIGHT;
        Point obstacle = new Point(contextWidth-OBSTACLE_HEIGHT,posy);
        obstacles.add(obstacle);
    }



    //A modifier en fonction de la position du background
    public void drawAllObstacles(Canvas canvas){
        for (Point obstacle : obstacles) {
            //modifier aussi le y en fonction du background
            obstacle.x = obstacle.x - 10;
            if(obstacle.x <= 0){
                obstacles.remove(obstacle);
                System.out.println("Obstacle removed");
            }
            Paint myPaint = new Paint();
            myPaint.setColor(Color.rgb(0, 0, 0));
            myPaint.setStrokeWidth(10);

            canvas.drawRect(obstacle.x, obstacle.y, contextWidth -(contextWidth - obstacle.x- OBSTACLE_HEIGHT), obstacle.y+OBSTACLE_HEIGHT, myPaint);
        }
    }


    public void calculTrajectoire(){
        x = (float) (cos(a)*v0*t + x0);
        y = (float) ((-0.5)*g*t*t + sin(a)*v0*t + y0);
        //y = contextHeight - y;
        t = t+0.25f;
    }

    public void setInclinaison(double inclinaison) {
        this.inclinaison = inclinaison;
    }




}
