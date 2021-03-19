package com.m2dl.challengemobe;

import android.graphics.Bitmap;
import android.graphics.Point;

public class Birds {
    private Point p;
    private Bitmap b;

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

    public Birds(Point point, Bitmap bitmap){
        this.p=point;
        this.b=bitmap;
    }
}
