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
    int space = 15, Alpha = 150;

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
        int radio = 10;
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        setRectf(canvasWidth, canvasHeight);
        Paint paint = new Paint();
        paint.setColor(bgColor);
        paint.setTextSize(sp2px(15));
        paint.setAlpha(150);
        String msg = "请把证件放入框内";
        float ww = paint.measureText(msg);
        canvas.drawText(msg, (canvasWidth - ww) / 2, canvasHeight / 2, paint);

        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);


        canvas.drawRect(0, 0, canvasWidth, canvasHeight, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(rectF, radio, radio, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(layerId);

        Paint paint1 = new Paint();
        paint1.setColor(Color.RED);
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
