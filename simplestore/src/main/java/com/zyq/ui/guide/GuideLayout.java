package com.zyq.ui.guide;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class GuideLayout extends LinearLayout {
    public GuideLayout(Context context) {
        super(context);
    }

    public void remove() {
        ((ViewGroup) getParent()).removeView(this);
    }
}
