package com.zyq.jsimleplepicker.textView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;


/**
 * 支持TextView所有属性
 * 使用方法
 app:bubble_radio="10dp" 圆角弧度 ，没有则不设置
 app:bubble_radio_right_top="20dp" 右上角圆角弧度， 没有则不设置
 app:bubble_radio_left_bottom="20dp"左下角圆角弧度， 没有则不设置
 app:bubble_radio_right_bottom="20dp"右下角圆角弧度， 没有则不设置
 app:bubble_radio_left_top="20dp"左上角圆角弧度 ，没有则不设置

 app:bubble_color_bg="#ff0000"默认字体颜色， 没有则不设置
 app:bubble_press_bg_color="#00ff00"按压字体颜色，没有则不设置
 app:bubble_active_bg_color="#00ff00"选中字体颜色，没有则不设置

 app:bubble_press_text_color="#00ff00"按压背景颜色， 没有则不设置
 app:bubble_text_color="#ff0000"默认背景颜色， 没有则不设置
 app:bubble_active_text_color="#ff0000"选中背景颜色， 没有则不设置

 app:bubble_press_bg_drawable="@drawable/"按压背景， 没有则不设置
 app:bubble_drawable_bg="@drawable/"默认背景， 没有则不设置
 app:bubble_active_bg_drawable="@drawable/"选中背景， 没有则不设置

 app:bubble_stock="true" 是否支持stock，没有则不设置
 app:bubble_dotted_line="true"是否支持虚线，没有则不设置

 * package kotlinTest:com.qing.lightview.common.LightRichBubbleText.class
 * 作者：zyq on 2017/8/2 09:37
 * 邮箱：zyq@posun.com
 */
@SuppressLint("AppCompatCustomView")
public class LightRichBubbleText extends CheckBox {
    private int bg_color= Color.TRANSPARENT, bg_press_color= Color.TRANSPARENT,
            bg_active_color= Color.TRANSPARENT, text_color= Color.BLACK, text_press_color= Color.GRAY,
            text_active_color= Color.BLACK;
    private Drawable bg_drawable, bg_press_drawable, bg_active_drawable;
    private boolean isdotted_line = false, isstock = false;
    private float radio_left_top = 0, radio_left_bottom = 0, radio_right_top = 0, radio_right_bottom = 0;
    private int[] state_press = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, state_checked = new int[]{android.R.attr.state_checked, android.R.attr.state_enabled};

    public LightRichBubbleText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightRichBubbleText(Context context) {
        super(context);
        init();
    }


    @Override
    public void setBackgroundColor(int color) {
       setBg_color(color);
    }

    private void init() {
        /*****去除默认样式并向下兼容到4.0***/
        setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
        /****字体颜色状态***/
        setTextColorState();
        setBgState();
    }
    public void resitTextTextColor(){
        setTextColorState();
    }
    private void setTextColorState() {
        int[] colors = new int[]{text_press_color,text_active_color, text_color};
        int[][] states = new int[3][];
        states[0] = state_press;
        states[1] = state_checked;
        states[2] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        setTextColor(colorList);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBgState() {
        StateListDrawable drawable = new StateListDrawable();
        float outRectr[] = new float[]{radio_left_top, radio_left_top, radio_right_top, radio_right_top, radio_right_bottom, radio_right_bottom, radio_left_bottom, radio_left_bottom};
        RoundRectShape rectShape = new RoundRectShape(outRectr, null, null);
        if(bg_press_drawable!=null){
            drawable.addState(state_press, bg_press_drawable);
        }else if (bg_press_color != Color.TRANSPARENT) {
            ShapeDrawable pressedDrawable = new ShapeDrawable(rectShape);
            /**设置颜色*/
            pressedDrawable.getPaint().setColor(bg_press_color);
            if (isstock) {
                /**设置Style**/
                pressedDrawable.getPaint().setStyle(Paint.Style.STROKE);
            }
            if (isdotted_line) {
                /**设置虚线****/
                pressedDrawable.getPaint().setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
            }
            drawable.addState(state_press, pressedDrawable);
        }
        if(bg_active_drawable!=null){
            drawable.addState(state_checked, bg_active_drawable);
        }else if (bg_active_color != Color.TRANSPARENT) {
            ShapeDrawable checkedDrawable = new ShapeDrawable(rectShape);
            checkedDrawable.getPaint().setColor(bg_active_color);
            if (isstock) {
                checkedDrawable.getPaint().setStyle(Paint.Style.STROKE);
            }
            if (isdotted_line) {
                checkedDrawable.getPaint().setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
            }
            drawable.addState(state_checked, checkedDrawable);
        }
        if(bg_active_drawable!=null){
            drawable.addState(new int[]{}, bg_drawable);
        }else{
            ShapeDrawable nomerDrawable =null;
                nomerDrawable = new LightShapeDrawable(rectShape);
                nomerDrawable.getPaint().setColor(bg_color);
                if (isstock) {
                    nomerDrawable.getPaint().setStyle(Paint.Style.STROKE);
                }
                if (isdotted_line) {
                    nomerDrawable.getPaint().setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
                }
//            }
            drawable.addState(new int[]{}, nomerDrawable);
        }
        setBackground(drawable);
    }
   public void setText_press_color(int color){
       this.text_press_color=color;
   }

    public void setBg_color(int bg_color) {
        this.bg_color = bg_color;
    }

    public void setBg_press_color(int bg_press_color) {
        this.bg_press_color = bg_press_color;
    }

    public void setBg_active_color(int bg_active_color) {
        this.bg_active_color = bg_active_color;
    }

    public void setText_color(int text_color) {
        this.text_color = text_color;
    }

    public void setText_active_color(int text_active_color) {
        this.text_active_color = text_active_color;
    }

    public void setBg_drawable(Drawable bg_drawable) {
        this.bg_drawable = bg_drawable;
    }

    public void setBg_press_drawable(Drawable bg_press_drawable) {
        this.bg_press_drawable = bg_press_drawable;
    }

    public void setBg_active_drawable(Drawable bg_active_drawable) {
        this.bg_active_drawable = bg_active_drawable;
    }

    public void setIsdotted_line(boolean isdotted_line) {
        this.isdotted_line = isdotted_line;
    }

    public void setIsstock(boolean isstock) {
        this.isstock = isstock;
    }

    public void setRadio_left_top(float radio_left_top) {
        this.radio_left_top = radio_left_top;
    }

    public void setRadio_left_bottom(float radio_left_bottom) {
        this.radio_left_bottom = radio_left_bottom;
    }

    public void setRadio_right_top(float radio_right_top) {
        this.radio_right_top = radio_right_top;
    }

    public void setRadio_right_bottom(float radio_right_bottom) {
        this.radio_right_bottom = radio_right_bottom;
    }
    public void setRadio(float radio){
       radio_left_bottom=radio;
       radio_left_top=radio;
       radio_right_bottom=radio;
       radio_right_top=radio;
    }
    public void commit(){
       init();
   }
}
