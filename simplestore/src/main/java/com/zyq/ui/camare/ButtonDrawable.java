package com.zyq.ui.camare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;


public class ButtonDrawable extends Drawable {
    int canvasWidth = -1, canvasHeight = -1, color = Color.WHITE;
    Context context;

    ButtonDrawable(Context context) {
        this.context = context;
    }

    ButtonDrawable(Context context, int color) {
        this.context = context;
        this.color = color;
    }

    @Override
    public void draw(Canvas canvas) {
        int space = sp2px(3);
        int w = -1;
        if (canvasWidth == -1)
            canvasWidth = canvas.getWidth();
        if (canvasHeight == -1)
            canvasHeight = canvas.getHeight();
        w = canvasWidth > canvasHeight ? canvasHeight : canvasWidth;
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        int radius = (w / 2) - space;
        canvas.drawCircle(w / 2, w / 2, radius, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(w / 2, w / 2, radius - space, paint);
    }

    @Override
    public void setAlpha(int i) {

    }

    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
