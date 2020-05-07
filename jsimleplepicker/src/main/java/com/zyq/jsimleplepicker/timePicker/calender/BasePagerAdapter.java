package com.zyq.jsimleplepicker.timePicker.calender;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

public abstract class BasePagerAdapter extends PagerAdapter {
    protected List<View> viewcatch = new ArrayList<>();
    private static final int MAX_SIZE = 2;

    protected List<View> getViewcatch() {
        return viewcatch;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = null;
        if (viewcatch.size() > 0)
            contentView = viewcatch.remove(0);
        View view = getView(container, contentView, position);
        container.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        if (viewcatch.size() < MAX_SIZE) {
            viewcatch.add((View) object);
        }
        object = null;
    }

    protected abstract View getView(ViewGroup container, View contentView, int position);

}