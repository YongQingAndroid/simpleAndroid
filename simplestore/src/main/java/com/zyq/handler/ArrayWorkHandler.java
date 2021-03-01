package com.zyq.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * 链式接口 任务和线程调度
 */
public class ArrayWorkHandler<T> extends WorkHandler<List<T>> {


    ArrayWorkHandler(List<T> obj) {
        super(obj);
    }

    public ArrayWorkHandler<T> forEach(ArrayExecuteEvent<T> mExecute) {
        executeEvents.add(new ExecuteEvent(workThread, mExecute));
        return this;
    }

    public ArrayWorkHandler<T> filter(ArrayFilterExecuteEvent<T> mExecute) {
        executeEvents.add(new ExecuteEvent(workThread, mExecute));
        return this;
    }




    public <M> ArrayWorkHandler<M> praseType(Class<M> mClass) {
        return (ArrayWorkHandler<M>) this;
    }

    @Override
    public ArrayWorkHandler<T> executeOn(WorkThread workThread) {
        setCurrentWorkThread(workThread);
        this.workThread = workThread;
        return this;
    }

    @Override
    protected Object executeEvent(ExecuteEvent event) {
        if (event.execute instanceof ArrayExecuteEvent) {
            int size = obj.size();
            T t = null;
            for (int i = 0; i < size; i++) {
                t = obj.get(i);
                ((ArrayExecuteEvent) event.execute).execute(t, i);
            }
            return obj;
        } else if (event.execute instanceof ArrayFilterExecuteEvent) {
            ArrayList arrayList = new ArrayList();
            int size = obj.size();
            T t = null;
            for (int i = 0; i < size; i++) {
                t = obj.get(i);
                if (((ArrayFilterExecuteEvent) event.execute).execute(t, i)) {
                    arrayList.add(t);
                }
            }
            obj = arrayList;
            return obj;
        } else {
            return super.executeEvent(event);
        }
    }

    public interface ArrayExecuteEvent<T> extends Execute {
        void execute(T t, int index);
    }

    public interface ArrayFilterExecuteEvent<T> extends Execute {
        boolean execute(T t, int index);
    }


}