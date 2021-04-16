package com.zyq.simpleandroid;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LooperManager {
    private static LooperManager looperManager;
    private List<Event> eventList=new ArrayList<>();
    private List<Event> badEvent=new ArrayList<>();
    private  boolean flag=false;
    private  ExecutorService cachedThreadPool = Executors.newSingleThreadExecutor();

    public static synchronized LooperManager getInstance() {
        if(looperManager==null){
            looperManager= new LooperManager();
        }
        return looperManager;
    }
   public interface Listener{
       void execute();
    }
    public synchronized LooperManager register(Listener listener,int space){
        for(Event event:eventList){
            if(listener==event.listener.get()){ //避免重复注册
                return this;
            }
        }
        Event event=  new Event(listener);
        if(space>100){
            event.space=space;
        }
        eventList.add(event);
        return this;
    }
    public void stop(){
        flag=false;
    }
    public void commit(){
        if(!flag){
            start();
        }
    }
    private void start(){
        flag=true;
        if(eventList.size()<1)
            return;
        cachedThreadPool.execute(() -> {
            exeLoop();
        });
    }

    private void exeLoop() {
        while (true){
            try {
                Thread.sleep(100);
                badEvent.clear();
                for (Event event:eventList){
                    if(!event.loop()){
                        badEvent.add(event);
                    }
                }
                for (Event event:badEvent){
                    eventList.remove(event);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

   static class Event{
        int space=1000;
        long start;
        WeakReference<Listener> listener;
        Event(  Listener listener){
            this.listener=new WeakReference<>(listener);
            this.start=System.currentTimeMillis();
        }
       private boolean loop(){
            if(listener.get()==null){
                return false;
            }
          if(System.currentTimeMillis()-start>=space){
              listener.get().execute();
              start=System.currentTimeMillis();
          }
          return true;
       }
    }
}
