package com.zyq.jsimleplepicker.timePicker.calender;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * package Kotlin3:com.posun.lightui.timePicker.calender.CalenderTitleView.class
 * 作者：zyq on 2018/1/8 15:23
 * 邮箱：zyq@posun.com
 */

public class CalenderTitleView extends View {
    private CalenderManager calenderManager;

    public CalenderTitleView(Context context) {
        super(context);
        initUI();
    }

    private void initUI() {
        calenderManager = CalenderManager.getTitleManager();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        calenderManager.drawTitle(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
