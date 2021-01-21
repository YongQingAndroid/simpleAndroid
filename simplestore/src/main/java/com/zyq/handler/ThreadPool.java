package com.zyq.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    public static void execute(Runnable runnable){
        fixedThreadPool.execute(runnable);
    }
}
