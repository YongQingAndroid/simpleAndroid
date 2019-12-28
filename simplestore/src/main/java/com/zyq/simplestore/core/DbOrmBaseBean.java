package com.zyq.simplestore.core;

/**
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
