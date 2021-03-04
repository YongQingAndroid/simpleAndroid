package com.zyq.ui.guide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

/***
 * view 浮层构造器
 */
public class LayoutBuilder {
    private View LayoutView;
    private View targetView;
    private GuideLayout guideLayout;
    private int id;
    private boolean isCircle = false;
    private int bgColor = Color.parseColor("#7f000000");
    private Position position = Position.TOP;

    /**
     * 布局位置
     *
     * @param position
     * @return
     */
    public LayoutBuilder setFloatPosition(Position position) {
        this.position = position;
        return this;
    }

    /***
     * 目标View
     * @param targetView
     * @return
     */
    public LayoutBuilder setTargetView(View targetView) {
        this.targetView = targetView;
        return this;
    }

    /***
     * 提示View
     * @param id
     * @return
     */
    public LayoutBuilder setLayoutRes(int id) {
        this.id = id;
        return this;
    }

    /**
     * 高亮是否为圆形
     *
     * @param circle
     * @return
     */
    public LayoutBuilder setCircle(boolean circle) {
        isCircle = circle;
        return this;
    }

    /***
     * 创建布局遮罩浮层
     * @return
     */
    public Partbuilder creat() {
        return new LayoutPartbuilderImp();
    }

    class LayoutPartbuilderImp extends Partbuilder {
        @Override
        protected void show(Activity activity) {
            LayoutView = activity.getLayoutInflater().inflate(id, guideLayout, false);
            FrameLayout frameLayout = (FrameLayout) activity.getWindow().getDecorView();
            guideLayout = new GuideLayout(activity);
            frameLayout.addView(guideLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            targetView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (LayoutView.getParent() == null) {
                        if(  Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2&&LayoutView instanceof RelativeLayout){
                            //android4.2 下RelativeLayout没有父容器无法计算高度
                            LinearLayout viewGroup=new LinearLayout(activity);
                            viewGroup.setOrientation(LinearLayout.VERTICAL);
                            viewGroup.addView(LayoutView);
                            LayoutView=viewGroup;
                        }
                        LayoutView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        RectF rectF = GuideViewManager.getRectF(targetView);
                        int positionY = 0;
                        if (position == Position.TOP) {
                            positionY = (int) rectF.top - LayoutView.getMeasuredHeight();
                        } else {
                            positionY = (int) rectF.bottom;
                        }
                        LayoutView.setY(positionY + getStateHeight(activity));
                        LayoutView.setX((targetView.getLeft()));
                        guideLayout.addView(LayoutView);
                        guideLayout.setBackgroundDrawable(new JFZDrawable(targetView, bgColor).setCircle(isCircle));

//                       LightDialog.MakeDialog(guideLayout, LightDialog.QGriavty.FULL).show();
                    }
                }
            });
        }

        @Override
        protected void dismiss() {
            guideLayout.remove();
        }

        @Override
        View getContentView() {
            return guideLayout;
        }

        @Override
        View getFloatView() {
            return LayoutView;
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStateHeight(Context activity) {
        int height = 70;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            height = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
}

public enum Position {
    TOP,
    BOTTOM;
}
}