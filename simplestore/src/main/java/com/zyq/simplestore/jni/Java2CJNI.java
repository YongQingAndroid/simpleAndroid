package com.zyq.simplestore.jni;
public class Java2CJNI {

    static {

        System.loadLibrary("Java2C");
    }

    public  native String java2C();
}
