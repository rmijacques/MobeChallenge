package com.m2dl.challengemobe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.Date;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private GameActivity context;
    private Point circlePosition;
    private int contextHeight;
    private int contextWidth;
    private SharedPreferences sharedPref;
    private int bgXPosition = 0;
    private Bitmap bitmap;
    private int bgHeight;
    private int bgWidth;
    private int bgFullWidth;
    private Date lastDrawDate;
    double vitesse = 1.2; // 12 km/h

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
        circlePosition = new Point(contextWidth / 2, contextHeight / 2);
        initBackground();
        lastDrawDate = new Date();
    }

    private void initBackground() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        bgHeight = (bitmap.getHeight()) / 2;
        bgWidth = (bitmap.getWidth()) / 3;
        bgFullWidth = bitmap.getWidth();
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
        if (canvas != null) {
            Date d = new Date();
            long tempsPasse = (d.getTime() - lastDrawDate.getTime()) / 10;
            lastDrawDate = d;

            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();


            float lightValue = context.getLightValue();
            float maxLight = 1200;


            float hue = (lightValue / maxLight) * 359.f;
            int rgb = ColorUtils.HSLToColor(new float[]{hue, 1f, .6f});
            int red = Color.red(rgb);
            int green = Color.green(rgb);
            int blue = Color.blue(rgb);


            paint.setColor(Color.rgb(0, 0, 0));
            drawBackground(canvas, tempsPasse);
            // canvas.drawCircle(circlePosition.x, circlePosition.y, 100, paint);
        }
    }

    private void drawBackground(Canvas canvas, long tempsPasse) {
        if (bgXPosition >= bgFullWidth - bgWidth) bgXPosition = 0;
        Rect srcRectForRender = new Rect(bgXPosition, bgHeight, bgXPosition + bgWidth, bgHeight * 2);
        Rect dstRectForRender = new Rect(0, 0, contextWidth, 800);
        canvas.drawBitmap(bitmap, srcRectForRender, dstRectForRender, null);
        System.out.println(tempsPasse);
        bgXPosition = bgXPosition + (int) (tempsPasse * vitesse);

    }

    public Point getCirclePosition() {
        return circlePosition;
    }

    public void setCirclePosition(Point circlePosition) {
        this.circlePosition = circlePosition;
    }

}
