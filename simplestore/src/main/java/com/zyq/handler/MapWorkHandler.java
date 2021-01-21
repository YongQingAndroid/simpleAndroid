package com.zyq.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 链式接口 任务和线程调度
 */
public class MapWorkHandler<K, V> extends WorkHandler<Map<K, V>> {


    MapWorkHandler(Map<K, V> obj) {
        super(obj);
    }

    public MapWorkHandler<K, V> forEach(MapExecuteEvent<K, V> mExecute) {
        executeEvents.add(new ExecuteEvent(workThread, mExecute));
        return this;
    }

    public <M, N> MapWorkHandler<M, N> praseType(Class<M> key, Class<N> value) {
        return (MapWorkHandler<M, N>) this;
    }

    public MapWorkHandler<K, V> filter(MapFilterExecuteEvent<K, V> mExecute) {
        executeEvents.add(new ExecuteEvent(workThread, mExecute));
        return this;
    }


    @Override
    public MapWorkHandler<K, V> executeOn(WorkThread workThread) {
        setCurrentWorkThread(workThread);
        this.workThread = workThread;
        return this;
    }

    @Override
    protected Object executeEvent(ExecuteEvent event) {
        if (event.execute instanceof MapExecuteEvent) {
            V t = null;
            for (K key : obj.keySet()) {
                t = obj.get(key);
                ((MapExecuteEvent) event.execute).execute(key, t);
            }
            return obj;
        } else if (event.execute instanceof MapFilterExecuteEvent) {
            Map map = new HashMap();
            V t = null;
            for (K key : obj.keySet()) {
                t = obj.get(key);

                if (((MapFilterExecuteEvent) event.execute).execute(key, t)) {
                    map.put(key, t);
                }
            }
            obj = map;
            return obj;
        } else {
            return super.executeEvent(event);
        }
    }

    public interface MapExecuteEvent<K, V> extends Execute {
        void execute(K key, V value);
    }

    public interface MapFilterExecuteEvent<K, V> extends Execute {
        boolean execute(K key, V value);
    }
}