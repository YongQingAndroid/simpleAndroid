package com.zyq.ui.camare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;


public class CameraDrawable extends Drawable {
    int bgColor = android.graphics.Color.BLACK;
    RectF rectF;
    Context context;
    int space = 15, Alpha = 160;
    int canvasWidth = -1, canvasHeight = -1;

    CameraDrawable(Context context) {
        this.context = context;
    }

    CameraDrawable(Context context, int alpha) {
        this.context = context;
        this.Alpha = alpha;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    private void setRectf(int width, int height) {
        space = sp2px(space);
        width = width - (space * 2);

        int top = (height - (int) (width * 0.75)) / 2;
        rectF = new RectF();
        rectF.bottom = top + (int) (width * 0.75);
        rectF.top = top;
        rectF.left = space;
        rectF.right = space + width;
    }

    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    @Override
    public void draw(Canvas canvas) {
        int radio = sp2px(5);
        if (canvasWidth == -1)
            canvasWidth = canvas.getWidth();
        if (canvasHeight == -1)
            canvasHeight = canvas.getHeight();
        if (rectF == null)
            setRectf(canvasWidth, canvasHeight);
        Paint paint1 = new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setAntiAlias(true);
        paint1.setTextSize(sp2px(15));

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(bgColor);

        paint.setAlpha(Alpha);


        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);


        canvas.drawRect(0, 0, canvasWidth, canvasHeight, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(rectF, radio, radio, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(layerId);

        paint1.setAlpha(120);
        String msg = "请把证件放入框内";
        float ww = paint1.measureText(msg);
        canvas.drawText(msg, (canvasWidth - ww) / 2, canvasHeight / 2, paint1);

        String msg2 = "点击屏幕对焦";
        float ww2 = paint1.measureText(msg2);
        paint1.setTextSize(sp2px(12));
        canvas.drawText(msg2, (canvasWidth - ww2) / 2, canvasHeight / 2 + space + paint1.getTextSize(), paint1);

//        paint1.setAlpha(255);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(5);
        canvas.drawRoundRect(rectF, radio, radio, paint1);


        canvas.save();
    }

    @Override
    public void setAlpha(int i) {
        this.Alpha = i;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 150;
    }


}
