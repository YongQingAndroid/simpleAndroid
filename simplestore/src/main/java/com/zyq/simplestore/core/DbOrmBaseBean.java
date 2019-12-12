package com.zyq.simplestore.core;

/**
 * package kotlinTest:com.qing.lightormdatabase.lightorm.DbOrmBaseBean.class
 * 作者：zyq on 2017/10/30 15:31
 * 邮箱：zyq@posun.com
 */
public class DbOrmBaseBean {
    public void insert(){
     DbOrmHelper.getInstent().save(this);
    }
    public void updata(){
        DbOrmHelper.getInstent().save(this);
    }
    public void remove(){
        DbOrmHelper.getInstent().remove(this);
    }

}
