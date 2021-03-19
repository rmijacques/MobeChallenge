package com.m2dl.challengemobe;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class GameThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private Canvas canvas;
    private boolean running;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    @Override
    public void run(){
        while(running) {
            try {
                sleep(16,70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {;
                    this.gameView.draw(canvas);
                }
            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
