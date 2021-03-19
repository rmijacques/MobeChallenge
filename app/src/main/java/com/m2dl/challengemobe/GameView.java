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
    private MainActivity context;
    private Point circlePosition;
    private int contextHeight;
    private int contextWidth;
    private SharedPreferences sharedPref;


    public GameView(MainActivity context) {
        super(context);
        this.context = context;
        setFocusable(true);
        getHolder().addCallback(this);
        this.sharedPref = context.getPreferences(Context.MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        contextHeight = displayMetrics.heightPixels;
        contextWidth = displayMetrics.widthPixels;
        circlePosition = new Point(contextWidth / 2, contextHeight / 2);


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


            /*float lightValue = context.getLightValue();
            float maxLight = 1200;


            float hue = (lightValue / maxLight) * 359.f;
            int rgb = ColorUtils.HSLToColor(new float[]{hue, 1f, .6f});
            int red = Color.red(rgb);
            int green = Color.green(rgb);
            int blue = Color.blue(rgb);
            *

            paint.setColor(Color.rgb(red, green, blue));

             */
            canvas.drawCircle(circlePosition.x, circlePosition.y, 100, paint);
        }
    }

    public Point getCirclePosition() {
        return circlePosition;
    }

    public void setCirclePosition(Point circlePosition) {
        this.circlePosition = circlePosition;
    }

}
