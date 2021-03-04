package com.zyq.ui.guide;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/***
 * 创建Drawable浮层
 */
public class DrawableBuilder {
    private View targetView;
    private GuideLayout guideLayout;
    private int bgColor = Color.parseColor("#7f000000");
    private List<DrawableBean> drawables = new ArrayList<>();

    public DrawableBuilder setTargetView(View targetView) {
        this.targetView = targetView;
        return this;
    }

    /**
     * 设置Drawable上下间距
     * @param space
     * @return
     */
    public DrawableBuilder setSpace(long space) {
        if (drawables.size() > 0) {
            DrawableBean drawableBean = drawables.get(drawables.size() - 1);
            drawableBean.setSpace(space);
        }
        return this;
    }

    /***
     * 添加Drawable
     * @param id
     * @return
     */
    public DrawableBuilder setDrawableRes(int id) {
        drawables.add(new DrawableBean(id));
        return this;
    }

    /**
     * 添加Drawable
     * @param drawable
     * @return
     */
    public DrawableBuilder setDrawable(Drawable drawable) {
        drawables.add(new DrawableBean(drawable));
        return this;
    }

    /**
     * 创建浮层
     * @return
     */
    public Partbuilder creat() {
        return new DrawPartbuilderImp();
    }

    /***
     * 构造浮层
     */
    class DrawPartbuilderImp extends Partbuilder {
        @Override
        protected void show(Activity activity) {
            checkDrawable(activity);
            FrameLayout frameLayout = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            guideLayout = new GuideLayout(activity);
            frameLayout.addView(guideLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            targetView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    guideLayout.setBackgroundDrawable(new JFZDrawable(targetView, drawables.toArray(new DrawableBean[]{}), bgColor));
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
            return null;
        }
    }

    /**
     *
     * @param activity
     */
    private void checkDrawable(Activity activity) {
        for (DrawableBean drawableBean : drawables) {
            if (drawableBean.getDrawable() != null) {
                continue;
            } else {
                drawableBean.setDrawable(activity.getResources().getDrawable(drawableBean.getDrawableRes()));
            }
        }
    }
}