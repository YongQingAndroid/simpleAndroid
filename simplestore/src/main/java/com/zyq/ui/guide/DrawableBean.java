package com.zyq.ui.guide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class DrawableBean {
    private Drawable drawable = null;
    private int drawableRes = -1;
    private Bitmap bitmap;
    private long space = 0;
    private int position = -1;

    DrawableBean(Drawable drawable) {
        this.drawable = drawable;
    }
    DrawableBean(int drawableRes) {
        this.drawableRes = drawableRes;
    }
    private DrawableBean(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }
}