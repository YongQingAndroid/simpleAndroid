package com.zyq.jsimleplepicker.timePicker.calender;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.joda.time.DateTime;

import androidx.annotation.Nullable;

/**
 * package Kotlin3:com.posun.lightui.timePicker.calender.BaseCalenderView.class
 * 作者：zyq on 2017/11/23 15:08
 * 邮箱：zyq@posun.com
 */

public class BaseCalenderView extends View {
    private CalenderManager mCalenderManager;
    private DateTime dateTime;
    private SelectListener listener;

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setIsmonth(boolean arg) {
        mCalenderManager.setIsmonth(arg);
    }

    public BaseCalenderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public BaseCalenderView(Context context, DateTime dateTime) {
        super(context);
        this.dateTime = dateTime;
        initUI();
    }

    public void setListener(SelectListener listener) {
        this.listener = listener;
    }

    private void initUI() {
        mCalenderManager = new CalenderManager();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int item = mCalenderManager.cilck((int) event.getX(), (int) event.getY());
                DateTime selectDate = getDateTimebyItem(item);
                int cha = selectDate.getMonthOfYear() - dateTime.getMonthOfYear();
                int cha_year = selectDate.getYear() - dateTime.getYear();
                if (cha_year != 0) {
                    cha = cha_year;
                }
                if (listener != null)
                    listener.select(this, item, selectDate, cha);
                break;
        }
        return true;
    }

    private DateTime getDateTimebyItem(int index) {
        CalenderManager.CalenderBean calenderBean = mCalenderManager.getCalenderBeanList().get(index);
        return calenderBean.getDateTime();
    }

    public void removeSelect() {
        mCalenderManager.removeSelect();
        invalidate();
    }

    public void validate(DateTime dateTime, DateTime mSelect) {
        this.dateTime = dateTime;
        mCalenderManager.selectpoint = -1;
        mCalenderManager.selectDate = mSelect;
        mCalenderManager.removeCatch();
        invalidate();
        Log.i("Tag", "validateDateTime");
    }

    public void clean(boolean ismonth) {
        mCalenderManager.ismonth = ismonth;
        mCalenderManager.selectpoint = -1;
        mCalenderManager.selectDate = null;
        mCalenderManager.removeCatch();
    }

    private int praseDateForIndex(DateTime mSelect) {
        if (mSelect != null && dateTime.getYear() == mSelect.getYear() && mSelect.getMonthOfYear() == dateTime.getMonthOfYear()) {
            DateTime arg1 = mSelect.dayOfMonth().withMinimumValue();
            int index = arg1.getDayOfWeek() % 7 + mSelect.getDayOfMonth() - 1;
            return index;
        }
        return 0;
    }

    public void validate(DateTime mSelect) {
        DateTime arg1 = mSelect.dayOfMonth().withMinimumValue();
        int index = 0;
        if (mCalenderManager.ismonth) {
            index = arg1.getDayOfWeek() % 7 + mSelect.getDayOfMonth() - 1;
        } else {
            index = mSelect.getDayOfWeek() % 7;
        }
        mCalenderManager.setSelect(index);
        mCalenderManager.selectDate = mSelect;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCalenderManager.drawCalenderDate(dateTime == null ? DateTime.now() : dateTime, canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight());
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = mCalenderManager.getWidth();
        if (specMode == MeasureSpec.AT_MOST) {
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    private int measureHeight() {
        return (int) (mCalenderManager.getWidth() * 0.618);
    }

    public interface SelectListener {
        void select(BaseCalenderView view, int arg, DateTime selectDate, int cha);
    }
}
