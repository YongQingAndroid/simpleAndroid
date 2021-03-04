package com.zyq.ui.guide;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import android.view.View;

/**
 *
 */
public abstract class Partbuilder {
    public static LayoutBuilder buildWithView(int id) {
        return new LayoutBuilder().setLayoutRes(id);
    }

    /**
     * @param drawable
     * @return
     */
    public static DrawableBuilder buildWithDrawable(Drawable drawable) {
        return new DrawableBuilder().setDrawable(drawable);
    }

    /**
     * @param drawable
     * @return
     */
    public static DrawableBuilder buildWithDrawable(int drawable) {
        return new DrawableBuilder().setDrawableRes(drawable);
    }

    /**
     * @param activity
     */
    abstract protected void show(Activity activity);

    abstract protected void dismiss();

    abstract View getContentView();

    abstract View getFloatView();
}
