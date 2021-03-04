package com.zyq.ui.guide;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @param <T>
 */
public class LineTaskExecuter<T> {
    Iterator<T> tasks = null;
    private CallBack<T> callBack;

    /**
     *
     * @param tasks
     */
    public LineTaskExecuter(List<T> tasks) {
        this.tasks = tasks.iterator();
    }

    /**
     *
     * @param callBack
     * @return
     */
    public LineTaskExecuter setCallBack(CallBack<T> callBack) {
        this.callBack = callBack;
        return this;
    }

    /**
     *
     */
    public void exeCute() {
        if (tasks.hasNext()) {
            callBack.call(tasks.next());
        }
    }

    public void finsh() {
        exeCute();
    }

    /**
     *
     * @param <M>
     */
    interface CallBack<M> {
        void call(M arg);
    }
}
