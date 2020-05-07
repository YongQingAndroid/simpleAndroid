package com.zyq.jsimleplepicker.textView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

/**
 * package kotlinTest:com.qing.lightview.drawable.LightShapeDrawable.class
 * 作者：zyq on 2017/8/3 10:40
 * 邮箱：zyq@posun.com
 */
public class LightShapeDrawable extends ShapeDrawable {
  public  LightShapeDrawable(Shape shape){
       super(shape);
  }
    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        super.onDraw(shape, canvas, paint);
    }
}
