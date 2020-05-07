package com.zyq.jsimleplepicker.timePicker.calender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;


import com.zyq.jsimleplepicker.QlightUnit;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * package Kotlin3:com.posun.lightui.timePicker.calender.CalenderManager.class
 * 作者：zyq on 2018/1/8 09:50
 * 邮箱：zyq@posun.com
 */

public class CalenderManager {
    private Paint paint, lunarPaint;
    private int width = 600, xspace, yspace, titleheight = 0, singlefont = 20, doublefont = 20, lunarfont = 20;
    private int height = 600;
    private int font_size = 20, lunar_size = 15;
    protected int size = 0, selectpoint = -1;
    protected DateTime selectDate;
    private List<CalenderBean> calenderBeanList;
    protected boolean ismonth = true,haveLunar=true;

    public CalenderManager(String arg) {
        try {
            font_size = QlightUnit.sp2px(QlightUnit.getApplication(), 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTypeface(font);
        paint.setTextSize(font_size);
        initMeasure();
        titleheight = yspace;
    }

    public int getWidth() {
        return width;
    }

    public List<CalenderBean> getCalenderBeanList() {
        return calenderBeanList;
    }

    public void setCalenderBeanList(List<CalenderBean> calenderBeanList) {
        this.calenderBeanList = calenderBeanList;
    }

    public void removeSelect() {
        selectDate = null;
        if (selectpoint == -1)
            return;
        if (calenderBeanList != null && calenderBeanList.size() > selectpoint)
            calenderBeanList.get(selectpoint).setIscheck(false);
    }

    public CalenderManager() {
        try {
            font_size = QlightUnit.sp2px(QlightUnit.getApplication(), 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTypeface(font);
        paint.setTextSize(font_size);

        lunarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lunarPaint.setColor(Color.BLACK);
//        lunarPaint.setTypeface(font);
        lunarPaint.setTextSize(lunar_size);
        initMeasure();
    }

    private int helftop, helfLeft, lunarLeft, doublehelfLeft;

    private void initMeasure() {
        width = QlightUnit.getDisplay().widthPixels;
        height = (int) (width * 0.618);
        doublefont = (int) paint.measureText("00");
        singlefont = (int) paint.measureText("0");
        if (lunarPaint != null)
            lunarfont = (int) lunarPaint.measureText("初一");
        xspace = width / 7;
        yspace = height / 6;
        helftop = ((yspace + font_size) / 2) + titleheight;
        helfLeft = (xspace - singlefont) / 2;
        doublehelfLeft = (xspace - doublefont) / 2;
        lunarLeft = (xspace - lunarfont) / 2;
    }

    public static CalenderManager getTitleManager() {
        return new CalenderManager("");
    }

    public static class CalenderBean {
        private boolean ischeck;
        private DateTime dateTime;
        private CalenderState calenderstate;
        private String remark;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public CalenderState getCalenderstate() {
            return calenderstate;
        }

        public void setCalenderstate(CalenderState calenderstate) {
            this.calenderstate = calenderstate;
        }

        public DateTime getDateTime() {
            return dateTime;
        }

        public String getLabletext() {
            return dateTime.getDayOfMonth() + "";
        }

        public void setDateTime(DateTime dateTime) {
            this.dateTime = dateTime;
        }

        public boolean isIscheck() {
            return ischeck;
        }

        public void setIscheck(boolean ischeck) {
            this.ischeck = ischeck;
        }
    }

    public void setIsmonth(boolean ismonth) {
        this.ismonth = ismonth;
        removeCatch();
    }

    public enum CalenderState {
        NOMER, SELECT, NOW, EVENT, OUT, LUNAR, WEEK
    }

    public void removeCatch() {
        if (calenderBeanList != null)
            calenderBeanList.clear();
    }

    public List<CalenderBean> getWeekData(DateTime dateTime) {
        int start = (dateTime.getDayOfWeek() % 7);
        if (calenderBeanList != null && calenderBeanList.size() > 0) {
            return calenderBeanList;
        } else {
            calenderBeanList = new ArrayList<>();
        }
        for (int i = start; i > 0; i--) {
            addList(dateTime, 0 - i, CalenderState.WEEK, true);
        }
        for (int i = 0; i < 7 - start; i++) {
            addList(dateTime, i, CalenderState.WEEK, true);
        }
        return calenderBeanList;
    }

    public List<CalenderBean> getMonthData(DateTime dateTime) {
        DateTime now = DateTime.now();
        boolean isNow = (now.getYear() == dateTime.getYear() && dateTime.getMonthOfYear() == now.getMonthOfYear());
        DateTime startDate = dateTime.dayOfMonth().withMinimumValue();
        int start = (startDate.getDayOfWeek()) % 7;
        if (calenderBeanList != null && calenderBeanList.size() > 0) {
            return calenderBeanList;
        } else {
            calenderBeanList = new ArrayList<>();
        }
        for (int i = start; i > 0; i--) {
            addList(startDate, 0 - i, CalenderState.OUT);
        }
        int size = startDate.dayOfMonth().getMaximumValue();
        int nowpoint = now.getDayOfMonth() - 1;
        for (int i = 0; i < size; i++) {
            if (isNow && nowpoint == i) {
                addList(startDate, i, CalenderState.NOW);
            } else {
                addList(startDate, i, CalenderState.NOMER);
            }
        }
        int cha = (6 * 7) - calenderBeanList.size();
        startDate = startDate.plusMonths(1);
        for (int i = 0; i < cha; i++) {
            addList(startDate, i, CalenderState.OUT);
        }
        if (selectpoint != -1) {
            calenderBeanList.get(selectpoint).setIscheck(true);
        }
        return calenderBeanList;
    }

    /***
     * 添加月
     * @param startDate
     * @param i
     * @param state
     */
    private void addList(DateTime startDate, int i, CalenderState state) {
        CalenderBean calenderBean = new CalenderBean();
        calenderBean.setDateTime(startDate.plusDays(i));
        calenderBean.setCalenderstate(state);
        if (CalenderState.OUT!=state&&selectpoint == -1 && selectDate != null && isSameDay(selectDate, calenderBean.dateTime)) {
            selectpoint = calenderBeanList.size();
            calenderBean.setIscheck(true);
        }
        if(haveLunar)
        calenderBean.setRemark(new Lunar(calenderBean.getDateTime().toCalendar(Locale.CHINA)).getChinaDayString());
        calenderBeanList.add(calenderBean);
    }

    private boolean isSameDay(DateTime arg, DateTime arg1) {
        if (arg.getYear() == arg1.getYear() && arg.getDayOfYear() == arg1.getDayOfYear()) {
            return true;
        }
        return false;
    }

    /***
     * 添加周
     * @param startDate
     * @param i
     * @param state
     * @param isweek
     */
    private void addList(DateTime startDate, int i, CalenderState state, boolean isweek) {
        CalenderBean calenderBean = new CalenderBean();
        calenderBean.setDateTime(startDate.plusDays(i));
        if (calenderBean.getDateTime().getDayOfYear() == DateTime.now().getDayOfYear()) {
            calenderBean.setCalenderstate(CalenderState.NOW);
        } else {
            calenderBean.setCalenderstate(state);
        }
        if (CalenderState.OUT!=state&&selectpoint == -1 && selectDate != null && isSameDay(selectDate, calenderBean.dateTime)) {
            selectpoint = calenderBeanList.size();
            calenderBean.setIscheck(true);
        }
        if(haveLunar)
        calenderBean.setRemark(new Lunar(calenderBean.getDateTime().toCalendar(Locale.CHINA)).getChinaDayString());
        calenderBeanList.add(calenderBean);
    }

    public void drawTitle(Canvas canvas) {
        String[] title = new String[]{"日", "一", "二", "三", "四", "五", "六"};
        int titlefont = (int) paint.measureText("一");
        int helftop = (titleheight + font_size) / 2;
        int helfLeft = (xspace - titlefont) / 2;
        for (int i = 0; i < 7; i++) {
            canvas.drawText(title[i], getTopX(i) + helfLeft, getTopY(i) + helftop, paint);
        }
    }

    public void drawCalenderDate(DateTime dateTime, Canvas canvas) {
        if (ismonth) {
            getMonthData(dateTime);
        } else {
            getWeekData(dateTime);
        }
        size = calenderBeanList.size();
        for (int i = 0; i < size; i++) {
            CalenderBean item = calenderBeanList.get(i);
            if (item.isIscheck()) {
                drawItem(CalenderState.SELECT, canvas, item, helftop, i, paint, null);
            }
            drawItem(item.getCalenderstate(), canvas, item, helftop, i, paint, null);
            drawItem(CalenderState.LUNAR, canvas, item, helftop, i, lunarPaint, item.getRemark());
        }
    }

    private int getPaintColor(CalenderState type) {
        int color = Color.BLACK;
        switch (type) {
            case NOW:
                color = Color.RED;
                break;
            case EVENT:
                color = Color.LTGRAY;
                break;
            case SELECT:
                color = Color.LTGRAY;
                break;
            case OUT:
                color = Color.GRAY;
                break;
            case LUNAR:
                color = Color.GRAY;
                break;
            default:
                break;
        }
        return color;
    }

    private void drawItem(CalenderState type, Canvas canvas, CalenderBean item, int helftop, int i, Paint paint, String islunar) {
        int x = 0;
        int selectx = getTopX(i);
        if (item.getLabletext().length() > 1) {
            x = selectx + doublehelfLeft;
        } else {
            x = selectx + helfLeft;
        }
        int y = getTopY(i);
        int arg = 10;
        paint.setColor(getPaintColor(type));
        switch (type) {
            case SELECT:
                canvas.drawRect(selectx, y, selectx + xspace, y + yspace, paint);
                break;
            case EVENT:
                break;
            case LUNAR:
                helftop = helftop - arg;
                if (islunar != null&&haveLunar) {
                    canvas.drawText(islunar, selectx + lunarLeft, y + helftop + 20, paint);
                }
                break;
            default:
                helftop = helftop - arg;
                canvas.drawText(item.getLabletext(), x, y + helftop, paint);
                break;
        }
    }


    public void setSelect(int arg) {
        if (calenderBeanList != null && calenderBeanList.size() > arg) {
            if (selectpoint != -1 && calenderBeanList.size() > selectpoint)
                calenderBeanList.get(selectpoint).setIscheck(false);
            calenderBeanList.get(arg).setIscheck(true);
            selectDate = calenderBeanList.get(arg).dateTime;
        }
        selectpoint = arg;

    }

    public void setSelect(DateTime arg) {
        this.selectDate = arg;
    }

    public int cilck(int x, int y) {
        y = y - titleheight;
        if (y < 0)
            y = 0;
        int column = x / xspace;
        int row = y / yspace;
        int check_item = (row * 7) + column;
        return check_item > (size - 1) ? (size - 1) : check_item;
    }

    private int getTopX(int arg) {
        return (arg % 7) * xspace;
    }

    private int getTopY(int arg) {
        return (arg / 7) * yspace;
    }
}
