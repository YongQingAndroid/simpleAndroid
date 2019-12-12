package com.zyq.simplestore.imp;

/**
 * package kotlinTest:com.qing.orm.DbCallback.class
 * 作者：zyq on 2017/7/4 09:50
 * 邮箱：zyq@posun.com
 */
public interface DbCallback<T> {
    void excute(T arg);
}
