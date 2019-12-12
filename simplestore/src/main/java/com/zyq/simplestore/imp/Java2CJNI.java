package com.zyq.simplestore.imp;
public class Java2CJNI {

    static {

        System.loadLibrary("Java2C");
    }

    public native String java2C();
}
