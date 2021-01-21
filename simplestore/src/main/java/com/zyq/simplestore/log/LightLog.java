package com.zyq.simplestore.log;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志处理类
 * 丰富日志会影响软件性能
 * package kotlinTest:com.qing.orm.log.LightLog.class
 * 作者：zyq on 2017/7/3 14:28
 * 邮箱：zyq@posun.com
 */
public class LightLog {
    public static boolean isDebug = true;
    private static final String TAG = "LightLog";
    private static int methodCount = 2;
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╠';
    private static final char HORIZONTAL_LINE = '║';
    private static final String STARTDOUBLE_DIVIDER = "═════╦════════════════════════════════════════════════════════════════";
    private static final String ENDDOUBLE_DIVIDER = "═════╩════════════════════════════════════════════════════════════════";
    private static final String CENTERDOUBLE_DIVIDER = "═════╬════════════════════════════════════════════════════════════════";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + STARTDOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + ENDDOUBLE_DIVIDER;

    //    "┌ ┬ ┐├ ┼ ┤└ ┴ ┘╠ ╬ ║═ ╣╝╔ ╩╚ "
    public static void i(String arg) {
        if (isDebug)
            I(arg);
    }

    public static void e(String arg) {
        if (isDebug)
            E(arg);
    }

    public static void simpleI(String tag, String arg) {
        if (isDebug)
            Log.i(tag, arg);
    }

    public static void simpleE(String tag, String arg) {
        if (isDebug)
            Log.e(tag, arg);
    }

    public static void E(String arg) {
        if (isDebug)
            simpleE(TAG, getRichTagString(arg).toString());
    }

    public static void I(String arg) {
        if (isDebug)
            simpleI(TAG, getRichTagString(arg).toString());
    }

    /***
     * 获取封装之后的文本
     * */
    private static StringBuilder getRichTagString(String arg) {
        if (arg == null) {
            arg = "NULL";
        }
        StringBuilder stringBuilder = new StringBuilder();
        ShowTopLine(stringBuilder);
        showThreadMsg(stringBuilder);
        ShowCenterLine(stringBuilder);
        showPrintCodeLineString(methodCount, stringBuilder);
        ShowCenterLine(stringBuilder);
        showArgMsg(arg, stringBuilder);
        return stringBuilder;
    }

    private static void ShowCenterLine(StringBuilder stringBuilder) {
        stringBuilder.append(MIDDLE_CORNER);
        stringBuilder.append(CENTERDOUBLE_DIVIDER);
        stringBuilder.append("\n");
    }

    private static void ShowTopLine(StringBuilder stringBuilder) {
        stringBuilder.append("----\n");
        stringBuilder.append(TOP_BORDER);
        stringBuilder.append("\n");
    }

    /**
     * 判断时候包含中文
     **/
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 显示长文本
     **/
    private static void showArgMsg(String arg, StringBuilder stringBuilder) {
        int size = arg.length();
        int row_size = 60;
        int row = (int) Math.ceil(size / 60.0);
        if (!isContainChinese(arg)) {
            row_size = row_size * 2;
            row = (int) Math.ceil(size / 120.0);
        }
        for (int i = 0; i < row; i++) {
            stringBuilder.append(HORIZONTAL_LINE);
            if (i == 0) {
                stringBuilder.append("Msg       ");
            } else {
                stringBuilder.append("          ");
            }
            stringBuilder.append(HORIZONTAL_LINE);
            if (i == row - 1) {
                stringBuilder.append(arg.substring(i * row_size, size));
            } else {
                stringBuilder.append(arg.substring(i * row_size, (i + 1) * row_size));
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append(BOTTOM_BORDER);
    }

    /**
     * 获取当前线程的信息
     */
    private static void showThreadMsg(StringBuilder stringBuilder) {
        stringBuilder.append(HORIZONTAL_LINE);
        stringBuilder.append("Thread    ");
        stringBuilder.append(HORIZONTAL_LINE);
        stringBuilder.append(Thread.currentThread().getName());
        stringBuilder.append("\n");
    }

    /**
     * 显示打印数据栈
     */
    private static void showPrintCodeLineString(int methodCount, StringBuilder stringBuilder) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int end = getStackOffset(trace) - 1;
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + end;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(HORIZONTAL_LINE);
            if (i == methodCount || stackIndex == trace.length - 1) {
                builder.append("CodeLine  ");
            } else {
                builder.append("          ");
            }
            builder.append(HORIZONTAL_LINE);
            showMethodSpace(i, builder);
            builder.append(' ')
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            stringBuilder.append(builder);
            stringBuilder.append("\n");
        }
    }

    /**
     * 过滤掉log框架相关的日志输出
     */
    private static int getStackOffset(StackTraceElement[] trace) {
        for (int i = 3; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(Log.class.getName()) && !name.equals(LightLog.class.getName())) {
                return i;
            }
        }
        return 5;
    }

    /**
     * 打印显示组合
     */
    private static void showMethodSpace(int i, StringBuilder stringBuilder) {
        for (int m = i; m < methodCount; m++) {
            stringBuilder.append("   ");
        }
        stringBuilder.append("∟");
    }

    /**
     * 获取类名
     */
    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    /**
     * 打印Json日志 并自动格式化
     **/
    public static void JSON(String arg) {
        StringBuilder stringBuilder = new StringBuilder();
        ShowTopLine(stringBuilder);
        showThreadMsg(stringBuilder);
        ShowCenterLine(stringBuilder);
        showPrintCodeLineString(methodCount, stringBuilder);
        ShowCenterLine(stringBuilder);
        try {
            ShowJson(arg, stringBuilder);
            stringBuilder.append("\n");
            stringBuilder.append(BOTTOM_BORDER);
        } catch (JSONException e) {
            showArgMsg(e.toString(), stringBuilder);
        }
        simpleI(TAG, stringBuilder.toString());
    }

    /**
     * 显示Json数据
     */
    private static void ShowJson(String arg, StringBuilder stringBuilder) throws JSONException {
        String formatString = formatJson(arg);
        stringBuilder.append(HORIZONTAL_LINE);
        stringBuilder.append("          ");
        stringBuilder.append(HORIZONTAL_LINE);
        stringBuilder.append(formatString);

    }

    /**
     * 格式化Json数据
     */
    private static String formatJson(String arg) throws JSONException {
        if (arg.startsWith("{")) {
            return new JSONObject(arg).toString(1).replaceAll("\\n", "\n" + HORIZONTAL_LINE + "          " + HORIZONTAL_LINE);
        } else {
            return new JSONArray(arg).toString(1).replaceAll("\\n", "\n" + HORIZONTAL_LINE + "          " + HORIZONTAL_LINE);
        }
    }
}
