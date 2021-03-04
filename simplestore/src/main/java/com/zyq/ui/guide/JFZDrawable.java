package com.zyq.ui.guide;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/***
 * 浮层遮罩
 */
public class JFZDrawable extends Drawable {
    RectF rectF;
    DrawableBean[] drawables;
    private int bgColor;
    private int mStatusBarHeight=70;
    private boolean isUp = true, isCircle = false;

    /**
     * 构造方法
     * @param view
     * @param bgColor
     */
    public JFZDrawable(View view, int bgColor) {
        rectF = GuideViewManager.getRectF(view);
        this.bgColor = bgColor;
    }

    public JFZDrawable(View view, DrawableBean[] drawables, int bgColor) {
        this.drawables = drawables;
        rectF = GuideViewManager.getRectF(view);
        this.bgColor = bgColor;
    }



    @Override
    public void draw( Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        Paint paint = new Paint();
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        paint.setColor(bgColor);
        canvas.drawRect(0, 0, canvasWidth, canvasHeight, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        if (isCircle) {
            canvas.drawCircle(rectF.centerX(), rectF.centerY(), (rectF.right - rectF.left) / 2, paint);
        } else {
            canvas.drawRect(rectF, paint);
        }
        paint.setXfermode(null);
        canvas.restoreToCount(layerId);
        canvas.save();

        Paint drawablePaint = new Paint();
        drawablePaint.setColor(Color.BLACK);
        drawBitmap(canvas, drawablePaint);

    }

    private void drawBitmap(Canvas canvas, Paint paint) {
        if (drawables == null) {
            return;
        }
        int y, x;
        x = (int) rectF.centerX();
        if (isUp) {
            y = (int) rectF.top;
        } else {
            y = (int) rectF.bottom;
        }
        for (DrawableBean drawable : drawables) {
            if (null == drawable) {
                continue;
            }
            if (drawable.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable.getDrawable()).getBitmap();
                y = drawBitmap(canvas, paint, y, x, drawable, bitmap);
            } else {
                y = drawBitmap(canvas, paint, y, x, drawable, drawable.getBitmap());
            }
        }
    }

    private int drawBitmap(Canvas canvas, Paint paint, int y, int x, DrawableBean drawable, Bitmap bitmap) {
        if (isUp) {
            y -= bitmap.getHeight() + drawable.getSpace();
            canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y, paint);
        } else {
            y += drawable.getSpace();
            canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y, paint);
            y += bitmap.getHeight();
        }
        return y;
    }

    public JFZDrawable setCircle(boolean circle) {
        isCircle = circle;
        return this;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter( ColorFilter colorFilter) {

    }

    @SuppressLint("WrongConstant")
    @Override
    public int getOpacity() {
        return 0;
    }
}
