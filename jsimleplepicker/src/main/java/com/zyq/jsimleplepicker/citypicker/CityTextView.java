package com.zyq.jsimleplepicker.citypicker;

import android.content.Context;
import android.graphics.Color;

import com.zyq.jsimleplepicker.textView.LightRichBubbleText;


/**
 * package Kotlin3:com.posun.lightui.citypicker.CityTextView.class
 * 作者：zyq on 2017/11/28 15:21
 * 邮箱：zyq@posun.com
 */

public class CityTextView extends LightRichBubbleText {
    public CityTextView(Context context) {
        super(context);
        initUi();
    }

    private void initUi() {
        setText_active_color(Color.WHITE);
        setText_press_color(Color.WHITE);
    }

    @Override
    public void setTextColor(int color) {
        setText_color(color);

        resitTextTextColor();
    }
    public void setTextColors(int color) {
        setText_color(color);
        setText_active_color(color);
        setText_press_color(color);
        resitTextTextColor();
    }
}
