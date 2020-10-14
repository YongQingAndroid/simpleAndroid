package com.zyq.simpleandroid.bean;

public class CommonBean<T,K> {
    T obj;
    K[] objArry;
    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public K[] getObjArry() {
        return objArry;
    }

    public void setObjArry(K[] objArry) {
        this.objArry = objArry;
    }
}
