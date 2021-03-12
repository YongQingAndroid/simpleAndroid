package com.zyq.ui.recyler;


import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class SuspensionManager {
    private FrameLayout frameLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private SuspensionAdapter suspensionAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    final int NULL_INDEX = -10;
    private TreeMap<Integer, RecyclerView.ViewHolder> stickyViewCatch = new TreeMap<>();
    private int stickyIndex = NULL_INDEX;
    private View mStickyView;

    FrameLayout mStickyGroup;

    /***
     * @param childView
     * @param parentView
     */
    public void replaceViewParent(View childView, ViewGroup parentView) {
        ViewGroup ac = (ViewGroup) childView.getParent();
        ViewGroup.LayoutParams lp = childView.getLayoutParams();
        ac.removeView(childView);
        parentView.addView(childView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        ac.addView(parentView, lp);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        mLayoutManager = recyclerView.getLayoutManager();
        adapter = recyclerView.getAdapter();
        if (!(adapter instanceof SuspensionAdapter)) {
            throw new RuntimeException("not have SuspensionAdapter");
        }
        suspensionAdapter = (SuspensionAdapter) adapter;
        frameLayout = new FrameLayout(recyclerView.getContext());
        mStickyGroup = new FrameLayout(recyclerView.getContext());
        replaceViewParent(recyclerView, frameLayout);
        frameLayout.addView(mStickyGroup, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int adapterFirstVisibleItem = getFirstVisibleItem();
                int previousPosition = getNextStickyHeaderIndex(adapterFirstVisibleItem, dy > 0);
//                Log.i("qing","previousPosition="+previousPosition+"adapterFirstVisibleItem="+adapterFirstVisibleItem);
                if (stickyIndex != previousPosition && previousPosition >= 0) {
                    mStickyView = getStickyView(previousPosition);
                    ViewGroup.LayoutParams layoutParams = mStickyView.getLayoutParams();
                    if (!(layoutParams instanceof SuspensionLayoutParams)) {
                        View mView = mLayoutManager.findViewByPosition(previousPosition);
                        SuspensionLayoutParams lp = new SuspensionLayoutParams(mView.getMeasuredWidth(), mView.getMeasuredHeight());
                        lp.OtherLayoutParams = mView.getLayoutParams();
                        mStickyView.setLayoutParams(lp);
                    }
                    mStickyGroup.removeAllViews();
                    mStickyGroup.addView(mStickyView);
                    if (mStickyGroup.getVisibility() != View.VISIBLE) {
                        mStickyGroup.setVisibility(View.VISIBLE);
                    }
                    stickyIndex = previousPosition;
                    mStickyGroup.setTag(previousPosition);

                } else if (previousPosition == Integer.MIN_VALUE) {
                    mStickyGroup.setVisibility(View.GONE);
                    stickyIndex = NULL_INDEX;
                }
                View sectionView = getNextStickyView(adapterFirstVisibleItem);
                if (sectionView == null || mStickyView == null)
                    return;
                int sectionTop = sectionView.getTop();
                int height = mStickyGroup.getHeight();
                if (sectionTop < height && sectionTop > 0) {
                    int top = sectionTop - height;
                    mStickyView.layout(mStickyView.getLeft(), top, mStickyView.getRight(), sectionTop);
                } else {
                    resitLayout(mStickyView);
                }
            }
        });

    }

    private void resitLayout(View view) {
//        View itemView = mLayoutManager.findViewByPosition(stickyIndex);
        View itemView = null;
        if (itemView != null) {
            view.layout(itemView.getLeft(), 0, itemView.getRight(), view.getMeasuredHeight());
        } else {
            view.layout(view.getLeft(), 0, view.getRight(), view.getMeasuredHeight());
        }
    }


    public int getNextStickyHeaderIndex(int position, boolean isDown) {
        int previousPosition = NULL_INDEX;
        if (mLayoutManager instanceof GridLayoutManager) {
            previousPosition = findGridSticky(position, isDown);
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            previousPosition = getstickyIndexFromLinearLayout(position, isDown);

        }
        return previousPosition;
    }

    public int getstickyIndexFromLinearLayout(int position, boolean isDown) {

        if (position != stickyIndex && suspensionAdapter.isSuspension(position)) {
            if (!stickyViewCatch.containsKey(position))
                stickyViewCatch.put(position, null);
            return position;
        }
        if (position > stickyIndex && stickyIndex >= 0) {
            return stickyIndex;
        }
        int previousPosition = NULL_INDEX;
        if (!isDown && position < stickyIndex && stickyIndex > 0) {
            SortedMap<Integer, RecyclerView.ViewHolder> sortedSet = stickyViewCatch.headMap(stickyIndex);
            if (sortedSet.size() != 0) {
                previousPosition = sortedSet.lastKey();
            } else {
                previousPosition = Integer.MIN_VALUE;
            }
        }
        return previousPosition;
    }

    private int findGridSticky(int position, boolean isDown) {

        int previousPosition = NULL_INDEX;
        if (!isDown && position < stickyIndex && stickyIndex > 0) {
            SortedMap<Integer, RecyclerView.ViewHolder> sortedSet = stickyViewCatch.headMap(stickyIndex);
            if (sortedSet.size() != 0) {
                previousPosition = sortedSet.lastKey();
            } else {
                previousPosition = Integer.MIN_VALUE;
            }
        }

        GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
        int childSize = gridLayoutManager.getChildCount();
        int spanCount = gridLayoutManager.getSpanCount();
        GridLayoutManager.SpanSizeLookup sizeLookup = gridLayoutManager.getSpanSizeLookup();
        int index = 0, spanSize = 0;
        do {
            spanSize += sizeLookup.getSpanSize(position + index);

            if (suspensionAdapter.isSuspension(position + index) && spanSize <= spanCount) {
                previousPosition = position + index;
                break;
            }

            index++;
        } while (index < childSize);

        return previousPosition;
    }

    private int getNextStickyPosition(int start) {
        int result = NULL_INDEX;
        int childSize = mLayoutManager.getChildCount();
        int index = 0;
        do {
            if (stickyIndex != (index + start) && suspensionAdapter.isSuspension(index + start)) {
                result = index;
                break;
            }
            index++;
        } while (index <= childSize);
        return result;
    }

    private View getNextStickyView(int start) {
        View result = null;
        int index = getNextStickyPosition(start);
        if (index != NULL_INDEX) {
            result = mLayoutManager.getChildAt(index);
        }
        return result;
    }

    /**
     * 获取固定在顶部的View
     *
     * @return View
     */
    private View getStickyView(int adapterPosition) {
        RecyclerView.ViewHolder viewHolder = null;
        if (stickyViewCatch.containsKey(adapterPosition)) {
            viewHolder = stickyViewCatch.get(adapterPosition);
            if (viewHolder != null) {
                adapter.bindViewHolder(viewHolder, adapterPosition);
                return viewHolder.itemView;
            }
        }
        int type = adapter.getItemViewType(adapterPosition);
        viewHolder = adapter.createViewHolder(mRecyclerView, type);
        adapter.bindViewHolder(viewHolder, adapterPosition);
        stickyViewCatch.put(adapterPosition, viewHolder);
        return viewHolder.itemView;
    }


    /**
     * 获取当前第一个显示的item .
     */
    private int getFirstVisibleItem() {
        int firstVisibleItem = -1;
        if (mLayoutManager != null) {
            if (mLayoutManager instanceof LinearLayoutManager) {
                firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            } else if (mLayoutManager instanceof GridLayoutManager) {
                firstVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] firstPositions = new int[((StaggeredGridLayoutManager) mLayoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) mLayoutManager).findFirstVisibleItemPositions(firstPositions);
                firstVisibleItem = getMin(firstPositions);
            }
        }
        return firstVisibleItem;
    }

    private int getMin(int[] arr) {
        int min = arr[0];
        for (int x = 1; x < arr.length; x++) {
            if (arr[x] < min)
                min = arr[x];
        }
        return min;
    }

    public static class SuspensionLayoutParams extends FrameLayout.LayoutParams {
        private ViewGroup.LayoutParams OtherLayoutParams;

        public SuspensionLayoutParams(int width, int height) {
            super(width, height);
        }

        public ViewGroup.LayoutParams getOtherLayoutParams() {
            return OtherLayoutParams;
        }

        public SuspensionLayoutParams setOtherLayoutParams(ViewGroup.LayoutParams otherLayoutParams) {
            OtherLayoutParams = otherLayoutParams;
            return this;
        }
    }

//    public int getViewHolderPosition(RecyclerView.ViewHolder viewHolder) {
//        int position = viewHolder.getAdapterPosition();
//        if (position == -1) {
//            position = trygetStickyPosition(viewHolder);
//        }
//        return position;
//    }

//    private int trygetStickyPosition(RecyclerView.ViewHolder viewHolder) {
//        for (int key : stickyViewCatch.keySet()) {
//            if (viewHolder == stickyViewCatch.get(key)) {
//                return key;
//            }
//        }
//        return -1;
//
//    }


    public static int getAdapterPosition(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position == -1) {
            View view = (View) viewHolder.itemView.getParent();
            Object obj = view.getTag();
            if (obj != null) {
                position = (int) obj;
            }
        }
        return position;

    }


    public interface SuspensionAdapter {
        boolean isSuspension(int index);
    }

}
