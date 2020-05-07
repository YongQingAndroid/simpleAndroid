package com.zyq.jsimleplepicker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dell on 2017/1/9.
 * (int)TypedValue.applyDimension(TypedValue.C, dpValue,context.getResources().getDisplayMetrics());
 */
public class QlightUnit {
    /**
     * 自动分配id
     */
    public static final AtomicInteger sNextGeneratedId = new AtomicInteger(20000);

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Application getApplication() throws Exception {
        return (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽高
     */
    public static DisplayMetrics getDisplay(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static DisplayMetrics getDisplay() {
        try {
            return getApplication().getResources().getDisplayMetrics();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /***
     * 判空
     * @param arg 参数
     * **/
    public static boolean isEmpty(CharSequence arg) {
        if (arg == null || arg.toString().trim().length() == 0) {
            return true;
        }
        return false;
    }

    /***
     * 判空
     * @param arg 参数
     * **/
    public static boolean isEmpty(Collection<?> arg) {
        if (arg == null || arg.size() == 0) {
            return true;
        }
        return false;
    }

    /***
     * 判空
     * @param arg 参数
     * **/
    public static boolean isEmpty(Object[] arg) {
        if (arg == null || arg.length == 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取ContentView
     **/
    public static View getContentView(Activity ac) {
        ViewGroup view = (ViewGroup) ac.getWindow().getDecorView();
        FrameLayout content = (FrameLayout) view.findViewById(android.R.id.content);
        return content.getChildAt(0);
    }

    /**
     * 获取ContentView
     **/
    public static FrameLayout getContentFrameLayout(Activity ac) {
        ViewGroup view = (ViewGroup) ac.getWindow().getDecorView();
        FrameLayout content = (FrameLayout) view.findViewById(android.R.id.content);
        return content;
    }

    public static String getUrlFileName(String url) {
        String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc|rar|zip|7z";
        Pattern pat = Pattern.compile("[\\w||-]+[\\.](" + suffixes + ")");
        Matcher mc = pat.matcher(url);
        while (mc.find()) {
            return mc.group();
        }
        return UUID.randomUUID().toString();
    }
}
