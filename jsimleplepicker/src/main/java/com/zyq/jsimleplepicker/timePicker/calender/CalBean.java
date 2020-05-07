package com.zyq.jsimleplepicker.timePicker.calender;

/**
 * package Kotlin3:com.posun.lightui.timePicker.calender.CalBean.class
 * 作者：zyq on 2018/1/6 15:39
 * 邮箱：zyq@posun.com
 */

public class CalBean {
    private String text;
    private MapPoint leftTop, rightBottom;
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MapPoint getLeftTop() {
        return leftTop;
    }

    public void setLeftTop(MapPoint leftTop) {
        this.leftTop = leftTop;
    }

    public MapPoint getRightBottom() {
        return rightBottom;
    }

    public void setRightBottom(MapPoint rightBottom) {
        this.rightBottom = rightBottom;
    }

    public static class MapPoint {
        private int x, y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
