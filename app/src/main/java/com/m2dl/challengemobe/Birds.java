package com.m2dl.challengemobe;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Random;

public class Birds {
    private Point p;
    private Bitmap b;


    private int accellerator;

    public Point getP() {
        return p;
    }

    public void setP(Point p) {
        this.p = p;
    }

    public Bitmap getB() {
        return b;
    }

    public void setB(Bitmap b) {
        this.b = b;
    }
    public int getAccellerator() {
        return accellerator;
    }

    public void setAccellerator(int accellerator) {
        this.accellerator = accellerator;
    }

    public Birds(Point point, Bitmap bitmap){
        Random rn = new Random();
        this.p=point;
        this.b=bitmap;
        this.accellerator= rn.nextInt(30) + 1;

    }
}
