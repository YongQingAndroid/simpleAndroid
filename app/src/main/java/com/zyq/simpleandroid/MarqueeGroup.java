package com.zyq.simpleandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MarqueeGroup extends ViewGroup {
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = null;
    private boolean isStart = true, mPause = false;
    private Builder Builder;
    private RecycleCallBack recycleCallBack;
    private ObjectAnimatorEvent objectAnimatorEvent = new ObjectAnimatorEvent();
    ExecutorService cachedThreadPool = Executors.newSingleThreadExecutor();
    int offset = 0;
    Runnable rotationThread;

    public MarqueeGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Builder == null)
            Builder = new Builder();
        init();
    }

    private void init() {
        runnable = () -> animation();
        rotationThread = () -> {
            try {
                while (isStart) {
                    Thread.sleep(Builder.animationTime + Builder.intervals);
                    if (!mPause)
                        handler.post(runnable);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    public void pause(boolean flag) {
        this.mPause = flag;
    }

    public void pause() {
        pause(!this.mPause);
    }

    private void stop() {
        isStart = false;
    }


    public void start() {
        isStart = false;
        if (Builder != null && Builder.adapterManager != null) {
            removeAllViews();
            Builder.bindRootView(this);
        }
        cachedThreadPool.execute(rotationThread);
        isStart = true;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        //记录当前的高度位置
        l = 0;
        int curHeight = offset;
        //将子View逐个摆放
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            //摆放子View，参数分别是子View矩形区域的左、上、右、下边
            child.layout(l, curHeight, l + width, curHeight + height);
            curHeight += height;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();

        if (childCount == 0) {//如果没有子View,当前ViewGroup没有存在的意义，不用占用空间
            setMeasuredDimension(0, 0);
        } else {
            if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                int height = getFirstChildHeight();
                int width = getMaxChildWidth();
                setMeasuredDimension(width, height);

            } else if (heightMode == MeasureSpec.AT_MOST) {//如果只有高度是包裹内容
                //宽度设置为ViewGroup自己的测量宽度，高度设置为所有子View的高度总和
                setMeasuredDimension(widthSize, getFirstChildHeight());
            } else if (widthMode == MeasureSpec.AT_MOST) {//如果只有宽度是包裹内容
                //宽度设置为子View中宽度最大的值，高度设置为ViewGroup自己的测量值
                setMeasuredDimension(getMaxChildWidth(), heightSize);

            }
        }
    }

    @Override
    protected void attachViewToParent(View child, int index, LayoutParams params) {
        super.attachViewToParent(child, index, params);

    }

    private int getFirstChildHeight() {
        int childCount = getChildCount();
        if (childCount > 0) {
            return getChildAt(0).getMeasuredHeight() * Builder.showLine;
        }
        return 0;
    }

    private int getMaxChildWidth() {
        int childCount = getChildCount();
        int maxWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getMeasuredWidth() > maxWidth)
                maxWidth = childView.getMeasuredWidth();

        }

        return maxWidth;
    }


    float maxY = 0;

    void animation() {
        isStart = getChildCount() > 1;
        if (isStart) {
            commonCall();
        }
    }

    List<View> handleView = new ArrayList<>();

    void commonCall() {
        int end = getChildAt(0).getMeasuredHeight();
        int childCount = getChildCount();
        View view;
        ObjectAnimator objectAnimator;
        for (int i = 0; i < childCount; i++) {
            view = getChildAt(i);
            objectAnimator = ObjectAnimator.ofFloat(view, "translationY", +(0 + view.getTranslationY()), (-end + view.getTranslationY())).setDuration(Builder.animationTime);
            objectAnimatorEvent.childCount = childCount;
            objectAnimatorEvent.bindEvent(objectAnimator).doEvent(objectAnimator);
        }
    }

    class ObjectAnimatorEvent extends AnimatorListenerAdapter {
        int index = 0;
        int childCount;

        ObjectAnimatorEvent bindEvent(ObjectAnimator animator) {
            animator.addListener(this);
            return this;
        }

        void doEvent(ObjectAnimator animator) {
            index = 0;
            animator.start();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            animation.removeAllListeners();
            animation.end();
            index++;
            if (index >= childCount) {
                recycle();
                index = 0;
                return;
            }

        }
    }

    /***
     * 回收屏幕外的视图并重新定位
     */
    void recycle() {
        View view = null;
        handleView.clear();
        maxY = 0;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);
            if (view.getY() < 0) {
                handleView.add(view);
            } else {
                maxY = maxY > view.getY() ? maxY : view.getY();
            }
        }
        int curHeight = 0;
        int MeasuredHeight = 0;
        if (handleView.size() > 0) {
            MeasuredHeight = handleView.get(0).getMeasuredHeight();
        }
        for (View mView : handleView) {
            curHeight += MeasuredHeight;
            mView.setTranslationY(0);
            mView.layout(mView.getLeft(), (int) (maxY + curHeight), mView.getRight(), (int) (maxY + curHeight + MeasuredHeight));
            if (recycleCallBack != null) {
                handler.post(() -> {
                    recycleCallBack.call(mView);
                });

            }
        }
    }

    protected interface RecycleCallBack {
        void call(View view);
    }

    public static class Builder {
        private int showLine = 3;
        private int intervals = 3000; //间隔时间
        private int animationTime = 700;//动画时间
        private AdapterManager adapterManager;
        private int catchSize = 3;
        private boolean isRecycle = false;//复用视图目前不稳定

        public int getShowLine() {
            return showLine;
        }

        public int getCatchSize() {
            return catchSize;
        }

        public Builder setCatchSize(int catchSize) {
            this.catchSize = catchSize;
            return this;
        }

        public Builder setAdapter(MarqueeAdapter adapter) {
            adapterManager = new AdapterManager();
            adapterManager.Builder = this;
            adapterManager.adapter = adapter;
            return this;
        }

        public boolean isRecycle() {
            return isRecycle;
        }

        public Builder setRecycle(boolean recycle) {
            isRecycle = recycle;
            return this;
        }

        public Builder setShowLine(int showLine) {
            this.showLine = showLine;
            return this;
        }

        public int getIntervals() {
            return intervals;
        }

        public Builder setIntervals(int intervals) {
            this.intervals = intervals;
            return this;
        }

        public int getAnimationTime() {
            return animationTime;
        }

        public Builder setAnimationTime(int animationTime) {
            animationTime = animationTime;
            return this;
        }

        public void bindView(MarqueeGroup marqueeGroup) {
            marqueeGroup.Builder = this;
            if (adapterManager != null) {
                marqueeGroup.recycleCallBack = (view) -> {
                    adapterManager.changeUI(view);
                };
            }
        }

        private void bindRootView(ViewGroup viewGroup) {
            if (adapterManager != null) {
                adapterManager.bindRootView(viewGroup);
            }
        }
    }

    static class AdapterManager {
        int number = 0;
        MarqueeAdapter adapter;
        Builder Builder;

        public MarqueeAdapter getAdapter() {
            return adapter;
        }

        public void setAdapter(MarqueeAdapter adapter) {
            this.adapter = adapter;
        }

        public Builder getMarqueeBuilder() {
            if (Builder == null)
                Builder = new Builder();
            return Builder;
        }

        public void setMarqueeBuilder(Builder Builder) {
            this.Builder = Builder;
        }

        private void bindRootView(ViewGroup viewGroup) {
            number = 0;
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            viewGroup.removeAllViews();
            int i = 0;
            FrameLayout layout;
            int size = getAdapter().getItemCount();
            size = size < (Builder.showLine + 2) ? size : (Builder.showLine + 2);
            size = size > Builder.catchSize ? size : Builder.catchSize;
            while (i < size) {
                layout = new FrameLayout(viewGroup.getContext());
                viewGroup.addView(layout, layoutParams);
                bindHolder(layout, i);
                i++;
            }
        }

        protected void changeUI(View viewGroup) {
            if (Builder.isRecycle) {
                bindHolder((MarqueeHolder) viewGroup.getTag(), number % adapter.getItemCount());
            }
        }

        private void bindHolder(MarqueeHolder holder, int position) {
            if (position >= adapter.getItemCount()) {
                return;
            }
            if (holder == null) {
                return;
            }
            adapter.bindViewHolder(holder, position);
            holder.adapterPosition = position;
            number++;
        }

        private void bindHolder(ViewGroup viewGroup, int position) {
            MarqueeHolder holder = getHolder(viewGroup, adapter.getItemViewType(position));
            bindHolder(holder, position);
        }

        MarqueeHolder getHolder(ViewGroup viewGroup, int type) {
            MarqueeHolder holder = (MarqueeHolder) viewGroup.getTag();
            if (holder == null) {
                holder = adapter.createViewHolder(viewGroup, type);
                if (holder.itemView.getLayoutParams() == null) {
                    holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                }
                viewGroup.addView(holder.itemView);
                viewGroup.setTag(holder);
            }
            return holder;
        }

    }

    public abstract static class MarqueeAdapter<T extends MarqueeHolder> implements BaseMarqueeAdapter<T> {

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }

    public static class MarqueeHolder {
        public View itemView;
        private int adapterPosition = 0;

        MarqueeHolder(View itemView) {
            this.itemView = itemView;
        }

        public int getAdapterPosition() {
            return adapterPosition;
        }
    }

    public interface BaseMarqueeAdapter<T extends MarqueeHolder> {
        int getItemCount();

        T createViewHolder(ViewGroup viewGroup, int type);

        void bindViewHolder(T holder, int position);

        int getItemViewType(int position);

    }
}
