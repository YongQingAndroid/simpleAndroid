package com.zyq.handler;

import android.os.Handler;
import android.os.Looper;

public class SimpleThreadHandler {
    Handler handler;

    public static SimpleThreadHandler getInstance() {
        return new SimpleThreadHandler();
    }

    public void execute(SimpleHandlerCall call) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        ThreadPool.execute(() -> {
            Object object = call.doInBackground();
            handler.post(() -> {
                call.complete(object);
            });
        });
    }

    interface ThreadHandlerCall<T> {
        T doInBackground();

        void complete(T obj);

        void progress(int arg);
    }

    public abstract static class SimpleHandlerCall<T> implements ThreadHandlerCall<T> {

        Handler handler;

        @Override
        public void complete(T obj) {

        }

        @Override
        public void progress(int arg) {

        }

        public void postProgress(int arg) {
            handler.post(() -> {
                progress(arg);
            });
        }

        public void postComplete(T obj) {
            handler.post(() -> {
                complete(obj);
            });
        }

    }
}
