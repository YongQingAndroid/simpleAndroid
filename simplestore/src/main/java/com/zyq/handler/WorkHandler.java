package com.zyq.handler;


import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 链式接口 任务和线程调度
 *
 * @param <T>
 */
public class WorkHandler<T> {
    protected WorkThread workThread = null;
    protected T obj;

    protected List<ExecuteEvent> executeEvents = new ArrayList<>();
    protected ResultCallBack<T> resultCallBack;


    WorkHandler(T obj) {
        this.obj = obj;
        this.executeEvents.clear();
    }

    public static <M> ArrayWorkHandler<M> fromArray(List<M> obj) {
        return new ArrayWorkHandler(obj);
    }

    /**
     * 只有当前数据为list时才可以调用
     *
     * @param <M>
     * @return
     */
    public <M> ArrayWorkHandler<M> toArrayHandler() {
        return (ArrayWorkHandler<M>) fromArray((List) obj);
    }

    public <M> ArrayWorkHandler<M> toArrayHandler(Class<M> tClass) {
        return (ArrayWorkHandler<M>) fromArray((List) obj);
    }

    /**
     * 只有当前数据为map时才可以调用
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> MapWorkHandler<K, V> toMapHandler() {
        return (MapWorkHandler<K, V>) fromHashMap((HashMap) obj);
    }

    public <K, V> MapWorkHandler<K, V> toMapHandler(Class<K> key, Class<V> value) {
        return (MapWorkHandler<K, V>) fromHashMap((HashMap) obj);
    }

    public static <K, V> MapWorkHandler<K, V> fromHashMap(HashMap<K, V> obj) {
        return new MapWorkHandler(obj);
    }

    public static <M> WorkHandler<M> from(M obj) {
        return new WorkHandler(obj);
    }

    public static WorkHandler from() {
        return new WorkHandler(null);
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

    /**
     * 待实现
     *
     * @return
     */
    public static WorkHandler zip() {
        return null;
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
        execute();

    }

    public void execute() {
        if (executeEvents.size() == 0) {
            getCurrentWorkThread().work(() -> {
                if (this.resultCallBack != null)
                    this.resultCallBack.onSuccess(obj);
            });
            return;
        }
        ExecuteEvent event = executeEvents.get(0);
        executeEvents.remove(0);
        getCurrentWorkThread(event).work(() -> {
            try {
                this.obj = (T) executeEvent(event);
                setResult(this.resultCallBack);
            } catch (Exception e) {
                if (this.resultCallBack != null)
                    this.resultCallBack.onError(e);
            }

        });
    }

    protected Object executeEvent(ExecuteEvent event) {
        if (event.execute instanceof MapExecute) {
            return ((MapExecute) event.execute).execute(obj);
        }
        return obj;
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


    public static WorkThread schedulerAndroidMainThread() {
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

    public interface MapExecute<R, T> extends Execute {
        R execute(T t);
    }

    public interface Execute {
    }

    public interface ResultCallBack<T> {
        void onSuccess(T obj);

        void onError(Exception e);
    }

    public static class ExecuteEvent {
        WorkThread workThread;
        Execute execute;

        ExecuteEvent(WorkThread workThread, Execute execute) {
            this.workThread = workThread;
            this.execute = execute;
        }
    }

}