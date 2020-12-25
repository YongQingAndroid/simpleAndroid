package com.zyq.simplestore.test;

import com.zyq.simplestore.SimpleStore;
import com.zyq.simplestore.core.CustomerDbHelper;
import com.zyq.simplestore.core.DbOrmHelper;
import com.zyq.simplestore.core.WhereBulider;
import com.zyq.simplestore.imp.DbColumn;
import com.zyq.simplestore.imp.DbIgnore;
import com.zyq.simplestore.imp.DbToMany;
import com.zyq.simplestore.imp.DbPrimaryKey;
import com.zyq.simplestore.imp.DbTableName;
import com.zyq.simplestore.imp.DbToOne;

public class Main {
    void test() {
        //key-value存储**********************************************
        //存储
        SimpleStore.store("key", "");
        SimpleStore.store("key", new TestBean());
        //获取
        SimpleStore.praseKey("key").get(String.class);
        SimpleStore.praseKey("key").get(TestBean.class);
        //异步获取
        SimpleStore.praseKey("key").get(TestBean.class, obj -> {

        });
        //数据库操作**********************************************
        //
        TestBean obj1 = new TestBean();
        //增
        DbOrmHelper.getInstent().save(obj1);
        //删
        DbOrmHelper.getInstent().remove(obj1); //删除数据
        DbOrmHelper.getInstent().remove(TableBean.class);//删除表
        //改
        obj1.name = "修改名字";
        DbOrmHelper.getInstent().save(obj1);
        DbOrmHelper.getInstent().updata(obj1);
        //查
        DbOrmHelper.getInstent().query(TableBean.class);//查询表所有数据
        DbOrmHelper.getInstent().query(TableBean.class, WhereBulider.creat().where("name=?", "lise").OR("name=?", "tom"));//条件查询
        DbOrmHelper.getInstent().query(TableBean.class, WhereBulider.creat().limit(1, 15));//分页查找
        //*********数据库多线程多连接操作
        DbOrmHelper dbOrmHelper1 = new DbOrmHelper();
        DbOrmHelper dbOrmHelper2 = new DbOrmHelper();
        DbOrmHelper dbOrmHelper3 = new DbOrmHelper();
        //*******兼容旧数据库
        CustomerDbHelper customerDbHelper = new CustomerDbHelper(null, null);
        customerDbHelper.save(null, "tableTable");
    }

    class TestBean {
        private String name;
    }

    @DbTableName("tableTable") //自定义表名(默认使用类名作为表名)
    static class TableBean {
        @DbPrimaryKey //指定主键(默认使用字段名为id的字段作为主键)
                String id;
        @DbIgnore //数据库忽略当前字段
                String name;
        @DbColumn("cid")
        String cid;

        @DbToOne
        TableBean1 msg;
    }

    static class TableBean1 {
        @DbPrimaryKey
        String id;
        String name;
    }
}
