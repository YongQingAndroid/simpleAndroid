package com.zyq.handler;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * 链式接口 任务和线程调度
 */
public class ProgressHandler<T> extends WorkHandler<T> {


    ProgressHandler(T obj) {
        super(obj);
    }

    /**
     * 执行循环发射任务
     * @param mExecute
     * @return
     */
    public ProgressHandler<T> handleProgress(ProgressExecuter<T> mExecute) {
        executeEvents.add(new ExecuteEvent(workThread, mExecute));
        return this;
    }


    @Override
    public ProgressHandler<T> executeOn(WorkThread workThread) {
        setCurrentWorkThread(workThread);
        this.workThread = workThread;
        return this;
    }

    @Override
    protected Object executeEvent(ExecuteEvent event) {
        if (event.execute instanceof ProgressExecuteEvent) {
            ProgressExecuter event1 = (ProgressExecuter) event.execute;
            event1.doInBackground(obj);
            return obj;
        } else {
            return super.executeEvent(event);
        }
    }

    public interface ProgressExecuteEvent<T> extends Execute {
        void doInBackground(T t);

        void progress(T t, int progress);
    }

    public abstract static class ProgressExecuter<T> implements ProgressExecuteEvent<T> {
        Handler handler;

        public void postProgress(T t, int progress) {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper());
            }
            handler.post(() -> {
                progress(t, progress);
            });
        }

    }
}