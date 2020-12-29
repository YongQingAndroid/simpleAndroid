package com.zyq.simplestore.core;


import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * 链式接口 任务和线程调度
 *
 * @param <T>
 */
public class WorkHandler<T> {
    WorkThread workThread = null;
    T obj;

    List<ExecuteEvent> executeEvents = new ArrayList<>();
    ResultCallBack<T> resultCallBack;

//    WorkHandler(MapExecute mapExecute) {
//        this.executeEvents.clear();
//        this.executeEvents.add(new ExecuteEvent(null, mapExecute));
//    }

    WorkHandler(T obj) {
        this.obj = obj;
        this.executeEvents.clear();
    }

    public static <M> WorkHandler<M> from(M obj) {
        return new WorkHandler(obj);
    }

    WorkHandler(WorkThread workThread, T obj, List<ExecuteEvent> executeEvents) {
        this.workThread = workThread;
        this.obj = obj;
        this.executeEvents.addAll(executeEvents);
    }

    public WorkHandler<T> executeOn(WorkThread workThread) {
        setCurrentWorkThread(workThread);
        this.workThread = workThread;
        return this;
    }

    public <R> WorkHandler<R> map(MapExecute<R, T> mapExecute) {
        executeEvents.add(new ExecuteEvent(workThread, mapExecute));
        return new WorkHandler(workThread, obj, executeEvents);
    }


    protected WorkThread setCurrentWorkThread(WorkThread workThread) {
        if (this.workThread == null) {
            for (ExecuteEvent event : executeEvents) {
                if (event.workThread == null) {
                    event.workThread = workThread;
                }
            }
        }
        return workThread;
    }

    protected WorkThread getCurrentWorkThread(ExecuteEvent executeEvent) {
        if (executeEvent.workThread == null) {
            executeEvent.workThread = getCurrentWorkThread();
        }
        return executeEvent.workThread;
    }

    protected WorkThread getCurrentWorkThread() {
        if (workThread == null) {
            workThread = new DefaultWorkThread();
        }
        return workThread;
    }

    public void setResult(ResultCallBack<T> resultCallBack) {
        this.resultCallBack = resultCallBack;
        if (executeEvents.size() == 0) {
            getCurrentWorkThread().work(() -> {
                resultCallBack.onSuccess(obj);
            });
            return;
        }
        ExecuteEvent event = executeEvents.get(0);
        executeEvents.remove(0);
        getCurrentWorkThread(event).work(() -> {
            try {
                this.obj = (T) event.execute.execute(obj);
                setResult(this.resultCallBack);
            } catch (Exception e) {
                resultCallBack.onError(e);
            }

        });

    }

    interface WorkThread {
        void work(HandlerEvent event);
    }

    public interface HandlerEvent {
        void doEvent();
    }

    public static class DefaultWorkThread implements WorkThread {

        @Override
        public void work(HandlerEvent event) {
            event.doEvent();
        }
    }


    public static WorkThread schedulerMainThread() {
        return new MainThread();
    }

    public static WorkThread schedulerWorkThread() {
        return new WorkerThread();
    }

    public static class MainThread implements WorkThread {
        Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void work(HandlerEvent event) {
            handler.post(() -> {
                event.doEvent();
            });

        }
    }

    public static class WorkerThread implements WorkThread {

        @Override
        public void work(HandlerEvent event) {
            ThreadPool.execute(() -> event.doEvent());
        }
    }

    public interface MapExecute<R, T> {
        R execute(T t);
    }

    public interface ResultCallBack<T> {
        void onSuccess(T obj);

        void onError(Exception e);
    }

    public static class ExecuteEvent {
        WorkThread workThread;
        MapExecute execute;

        ExecuteEvent(WorkThread workThread, MapExecute execute) {
            this.workThread = workThread;
            this.execute = execute;
        }
    }

}