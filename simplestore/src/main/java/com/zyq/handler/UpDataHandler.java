package com.zyq.handler;

public class UpDataHandler<T> extends WorkHandler<T> {
    public UpDataHandler(T obj) {
        super(obj);
    }

    public UpDataHandler<T> upDataProgress() {
        return this;
    }


//    public interface ProgressEvent extends Execute {
//        boolean execute(T t, int index);
//
//        void postToUI();
//    }
}
