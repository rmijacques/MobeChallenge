package com.m2dl.challengemobe;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap cloudsAndTreesBitmap;
    private int cloudsAndTreesHeight;
    private int cloudsAndTreesWidth;
    private int cloudsAndTreesFullWidth;
    private int cloudsAndTreesFullHeight;
    private int cloudsAndTreesXPosition = 0;

    static enum etat {WAITING, LAUNCHING, LAUNCHED}

    ;

    private GameThread thread;
    private GameActivity context;
    private Canvas canvas;
    private double inclinaison;
    private int contextHeight;
    private int contextWidth;
    private SharedPreferences sharedPref;
    private double bgXPosition = 0;
    private double bgYPosition = 0;
    private Bitmap bgBitmap;
    private int bgHeight;
    private int bgWidth;
    private int bgFullWidth;
    private double lastbypos;
    private int jeatpackincl=0;
    private int bgFullHeight;
    private Date lastDrawDate;
    double vitesse = 1.2; // 12 km/h
    long tempsGlobal = 0;
    public static int OBSTACLE_HEIGHT = 100;
    private List<Birds> obstacles;


    private float a;
    private float g;

    private int newEnnemiTime = 150;
    private int newEnnemiCompteur = 0;


    public GameView(GameActivity context) {
        super(context);
        this.context = context;
        setFocusable(true);
        getHolder().addCallback(this);
        this.sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        contextHeight = displayMetrics.heightPixels + getNavigationBarHeight(context);
        contextWidth = displayMetrics.widthPixels;

        initBackground();
        initCloudsAndTrees();
        lastDrawDate = new Date();

        obstacles = new ArrayList<>();


        g = 0.125f;
        a = (float) (Math.PI / 4);

    }

    private void initBackground() {
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        bgHeight = (bgBitmap.getHeight()) / 2;
        bgWidth = (bgBitmap.getWidth()) / 3;
        bgFullWidth = bgBitmap.getWidth();
        bgFullHeight = bgBitmap.getHeight();
    }

    private void initCloudsAndTrees() {
        cloudsAndTreesBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clouds);
        cloudsAndTreesHeight = (cloudsAndTreesBitmap.getHeight());
        cloudsAndTreesWidth = (int) (cloudsAndTreesBitmap.getWidth() / 7.81);
        cloudsAndTreesFullWidth = cloudsAndTreesBitmap.getWidth();
        cloudsAndTreesFullHeight = cloudsAndTreesBitmap.getHeight();
    }


    private int getNavigationBarHeight(GameActivity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
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
        if (canvas == null) return;
      
        newEnnemiCompteur += 1;
        if (checkNewEnnemiTime()) {
            createRandomObstacle();
            System.out.println("create bird");
        }
        Date d = new Date();
        long tempsPasse = (d.getTime() - lastDrawDate.getTime()) / 10;
        tempsGlobal += tempsPasse;
        lastDrawDate = d;
        drawBackground(canvas, tempsPasse);
        drawCloudAndTrees(canvas, tempsPasse);

        Paint paint = new Paint();
        paint.setColor(Color.rgb(0, 0, 0));

        calculTrajectoire();
        drawJetPack(canvas);
        paint.setColor(Color.rgb(0, 0, 0));
        // canvas.drawCircle(circlePosition.x, circlePosition.y, 100, paint);

    }

    private void drawBackground(Canvas canvas, long tempsPasse) {
        if (bgXPosition >= bgFullWidth - bgWidth) bgXPosition = 0;
        //bgXPosition = bgXPosition + (int) (tempsPasse * vitesse);
        if (bgYPosition >= bgFullHeight - bgHeight) bgYPosition = bgHeight;
        if (bgYPosition <= 0) bgYPosition = 0;
        Rect srcRectForRender = new Rect((int) bgXPosition, (int) (bgHeight - bgYPosition), (int) (bgXPosition + bgWidth), (int) (bgHeight * 2 - bgYPosition));
        Rect dstRectForRender = new Rect(0, 0, contextWidth, contextHeight);
        canvas.drawBitmap(bgBitmap, srcRectForRender, dstRectForRender, null);
    }

    private void drawCloudAndTrees(Canvas canvas, long tempsPasse) {
        if (cloudsAndTreesXPosition >= cloudsAndTreesFullWidth - cloudsAndTreesWidth)
            cloudsAndTreesXPosition = 0;
        cloudsAndTreesXPosition += (int) (tempsPasse * vitesse);
        Rect srcRectForRender = new Rect((int) cloudsAndTreesXPosition, (int)(0 - bgYPosition), cloudsAndTreesXPosition + cloudsAndTreesWidth, (int) (cloudsAndTreesHeight - bgYPosition));
        Rect dstRectForRender = new Rect(0, 0, contextWidth, contextHeight);
        canvas.drawBitmap(cloudsAndTreesBitmap, srcRectForRender, dstRectForRender, null);
    }
    public void drawJetPack(Canvas canvas){
        Resources res = getResources();
        Bitmap bitmapJetPack = BitmapFactory.decodeResource(res, R.drawable.jetpack);
        Paint myPaint = new Paint();
        Bitmap resized = Bitmap.createScaledBitmap(bitmapJetPack, 250, 250, true);
        Matrix matrix = new Matrix();

        if(bgYPosition>=lastbypos) {
            matrix.postRotate(0);
            lastbypos=bgYPosition;
        }
        else{
            if(jeatpackincl<120){
                jeatpackincl+=5;


            }
            matrix.postRotate(jeatpackincl);
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, true);
        canvas.drawBitmap(rotatedBitmap,30, contextHeight/2,  myPaint);


    }

    public void createRandomObstacle() {
        Random rand = new Random();
        int posy = rand.nextInt(contextHeight - OBSTACLE_HEIGHT) + OBSTACLE_HEIGHT;
        Point obstaclePoint = new Point(contextWidth - OBSTACLE_HEIGHT, posy);
        obstacles.add(new Birds(obstaclePoint, randomBird()));
    }


    //A modifier en fonction de la position du background
    public void drawAllObstacles(Canvas canvas) {


        for (Birds obstacle : obstacles) {
            //modifier aussi le y en fonction du background
            obstacle.getP().x = obstacle.getP().x - obstacle.getAccellerator();
            if (obstacle.getP().x <= 0) {
                obstacle.getP().x = obstacle.getP().x - 10;
                System.out.println("x=" + obstacle.getP().x);
            }
            if (obstacle.getP().x <= 0) {

                obstacles.remove(obstacle);
                System.out.println("Obstacle removed");
            }
            Paint myPaint = new Paint();
            myPaint.setColor(Color.rgb(0, 0, 0));
            myPaint.setStrokeWidth(10);

            canvas.drawBitmap(obstacle.getB(), obstacle.getP().x, obstacle.getP().y, myPaint);

        }
    }


    public void calculTrajectoire() {
        double v = vitesse * 20;
        bgXPosition = (cos(a) * v * tempsGlobal);
        bgYPosition = ((-0.5) * g * tempsGlobal * tempsGlobal + sin(a) * v * tempsGlobal);
        if (bgXPosition >= bgFullWidth - bgWidth) bgXPosition = 0;
        //bgXPosition = bgXPosition + (int) (tempsPasse * vitesse);
        if (bgYPosition >= bgFullHeight - bgHeight) bgYPosition = bgHeight;
        if (bgYPosition <= 0) bgYPosition = 0;
        System.out.println("bgx " + bgXPosition);
        System.out.println("bgy " + bgYPosition);

    }

    public void setInclinaison(double inclinaison) {
        this.inclinaison = inclinaison;
    }

    public Bitmap randomBird() {
        Resources res = getResources();
        Bitmap bitmap = null;
        Random rn = new Random();
        int answer = rn.nextInt(3) + 1;
        System.out.println(answer);
        if (answer == 1) {
            bitmap = BitmapFactory.decodeResource(res, R.drawable.birdpink);
        }
        if (answer == 2) {
            bitmap = BitmapFactory.decodeResource(res, R.drawable.birdblue);
        }
        if (answer == 3) {
            bitmap = BitmapFactory.decodeResource(res, R.drawable.birdbrown);
        }
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        return resized;
    }

    public boolean checkNewEnnemiTime() {
        Boolean res = false;
        if (newEnnemiCompteur >= newEnnemiTime) {
            newEnnemiCompteur = 0;
            res = true;
        } else {
            newEnnemiCompteur += 1;
        }
        return res;
    }


}
