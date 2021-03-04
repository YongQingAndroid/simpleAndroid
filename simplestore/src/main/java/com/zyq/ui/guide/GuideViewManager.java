package com.zyq.ui.guide;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/***
 *引导管理
 */
public class GuideViewManager implements View.OnClickListener {
    private Activity activity;
    private List<Partbuilder> parts = new ArrayList<>();
    LineTaskExecuter<Partbuilder> lineTaskExecuter;
    private static String isFist, GUIDEVIEWMANAGER = "GuideViewManager";

    /**
     * @param activity
     * @return
     */
    public static GuideViewManager with(Activity activity) {
        if (isFist == null) {
            isFist = activity.getSharedPreferences(GUIDEVIEWMANAGER, Context.MODE_MULTI_PROCESS).getString(GUIDEVIEWMANAGER, "true");
        }
        return new GuideViewManager(activity);
    }

    GuideViewManager(Activity activity) {
        this.activity = activity;

    }

    /**
     * 添加引导层
     * 可添加多个引导层
     * @param partbuilder
     * @return
     */
    public GuideViewManager addPart(Partbuilder partbuilder) {
        if ("true".equals(isFist)) {
            parts.add(partbuilder);
        }
        return this;
    }

    /**
     * 销毁当前引导层
     */
    public void dismissAll() {
        parts.clear();
        FrameLayout frameLayout = (FrameLayout) activity.getWindow().getDecorView() ;
        View view = frameLayout.getChildAt(frameLayout.getChildCount() - 1);
        if (view instanceof GuideLayout) {
            frameLayout.removeView(view);
        }
    }

    /**
     * 判断当前activity是否存在引导层
     * @return
     */
    public boolean haveGuide() {
        FrameLayout frameLayout = (FrameLayout) activity.getWindow().getDecorView();
        View view = frameLayout.getChildAt(frameLayout.getChildCount() - 1);
        if (view instanceof GuideLayout) {
            return true;
        }
        return false;
    }

    public GuideViewManager setCallBack() {
        return this;
    }

    /**
     *
     */
    public void show() {
        if (!"true".equals(isFist)) {
            return;
        }
        lineTaskExecuter = new LineTaskExecuter(parts);
        lineTaskExecuter.setCallBack(new LineTaskExecuter.CallBack<Partbuilder>() {
            @Override
            public void call(Partbuilder arg) {
                arg.show(activity);
//                if(arg.getFloatView()!=null){
//                    arg.getFloatView().setOnClickListener(GuideViewManager.this);
//                    arg.getFloatView().setTag(arg);
//                }else{
                arg.getContentView().setOnClickListener(GuideViewManager.this);
                arg.getContentView().setTag(arg);
//                }
            }
        }).exeCute();
    }

    @Override
    public void onClick(View v) {
        Partbuilder arg = (Partbuilder) v.getTag();
        arg.dismiss();
        lineTaskExecuter.finsh();
        isFist = "false";
        activity.getSharedPreferences(GUIDEVIEWMANAGER, Context.MODE_MULTI_PROCESS).edit().putString(GUIDEVIEWMANAGER, isFist).commit();
    }

    /**
     * 获取当前View的位置
     * @param view
     * @return
     */
    public static RectF getRectF(View view) {
        RectF rectF = new RectF();
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];
        int mStatusBarHeight=LayoutBuilder.getStateHeight(view.getContext());
        if (view != null) {
            rectF.left = x;
            rectF.top = (y - view.getMeasuredHeight() / 2)+mStatusBarHeight;
            rectF.right = x + view.getMeasuredWidth();
            rectF.bottom = (y + view.getMeasuredHeight() / 2)+mStatusBarHeight;
        }

        return rectF;
    }
}
