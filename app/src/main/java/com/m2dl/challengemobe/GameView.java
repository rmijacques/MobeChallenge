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

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private GameActivity context;
    private Point circlePosition;
    private int contextHeight;
    private int contextWidth;
    private SharedPreferences sharedPref;
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



        }
    }
    public void calculTrajectoire(){
        x = (float) (cos(a)*v0*t + x0);
        y = (float) ((-0.5)*g*t*t + sin(a)*v0*t + y0);
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
