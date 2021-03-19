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


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private GameActivity context;
    private Point circlePosition;
    private int contextHeight;
    private int contextWidth;
    private SharedPreferences sharedPref;

    public static int OBSTACLE_HEIGHT = 100;
    private List<Point> obstacles;


    private float x0=100;
    private float y0;
    private float backgroundX;
    private float backgroundY;
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
            canvas.drawCircle(backgroundX, backgroundY, 50, paint);


            float lightValue = context.getLightValue();
            float maxLight = 1200;


            float hue = (lightValue / maxLight) * 359.f;
            int rgb = ColorUtils.HSLToColor(new float[]{hue, 1f, .6f});
            int red = Color.red(rgb);
            int green = Color.green(rgb);
            int blue = Color.blue(rgb);

            paint.setColor(Color.rgb(0, 0, 0));
            calculTrajectoire();
            

            canvas.drawCircle(backgroundX, backgroundY, 100, paint);
            drawAllObstacles(canvas);
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
            int tempObstacleY = Math.round(backgroundY) - obstacle.y;
            if(obstacle.x <= 0){
                obstacles.remove(obstacle);
                System.out.println("Obstacle removed");
            }
            Paint myPaint = new Paint();
            myPaint.setColor(Color.rgb(0, 0, 0));
            myPaint.setStrokeWidth(10);

            canvas.drawRect(obstacle.x, tempObstacleY, contextWidth -(contextWidth - obstacle.x- OBSTACLE_HEIGHT), tempObstacleY+OBSTACLE_HEIGHT, myPaint);
        }
    }

    public boolean checkIfPlayerHitAnyObstacle(){
        for (Point obstacle : obstacles) {
            if((circlePosition.x+100 >= obstacle.x && circlePosition.x <= obstacle.x + OBSTACLE_HEIGHT) || (circlePosition.y - 100 >= obstacle.y && circlePosition.y<=obstacle.y + OBSTACLE_HEIGHT))
            {
                System.out.println("Collision avec un obstacle haaaaaaaaaaaaaa");
                return true;
            }

        }
        return false;
    }
    public void calculTrajectoire(){
        backgroundX = (float) (cos(a)*v0*t + x0);
        backgroundY = (float) ((-0.5)*g*t*t + sin(a)*v0*t + y0);
        //y = contextHeight - y;
        t = t+0.25f;
    }
    public Point getCirclePosition() {
        return circlePosition;
    }

    public void setCirclePosition(Point circlePosition) {
        this.circlePosition = circlePosition;
    }

}
