package com.zyq.simplestore;

import com.zyq.handler.ArrayWorkHandler;
import com.zyq.handler.ProgressHandler;
import com.zyq.handler.SimpleThreadHandler;
import com.zyq.simplestore.SimpleStore;
import com.zyq.simplestore.core.CustomerDbHelper;
import com.zyq.simplestore.core.DbOrmHelper;
import com.zyq.simplestore.core.WhereBulider;
import com.zyq.handler.WorkHandler;
import com.zyq.simplestore.imp.DbColumn;
import com.zyq.simplestore.imp.DbIgnore;
import com.zyq.simplestore.imp.DbToMany;
import com.zyq.simplestore.imp.DbPrimaryKey;
import com.zyq.simplestore.imp.DbTableName;
import com.zyq.simplestore.imp.DbToOne;
import com.zyq.simplestore.log.LightLog;

import java.util.ArrayList;
import java.util.List;

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


        WorkHandler.from(DbOrmHelper.getInstent())
                .executeOn(WorkHandler.schedulerWorkThread())
                .map(dbOrmHelper -> dbOrmHelper.query(TestBean.class))
                .executeOn(WorkHandler.schedulerAndroidMainThread())
                .map(list -> list.get(0).name)
                .setResult(new WorkHandler.ResultCallBack<String>() {
                    @Override
                    public void onSuccess(String obj) {
                        LightLog.i("我从数据库获得了" + obj);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        TestBean testBean1 = new TestBean();
        WorkHandler.from(testBean1)
                .executeOn(WorkHandler.schedulerWorkThread())
                .map(bean -> "name=" + bean.name)
                .executeOn(WorkHandler.schedulerAndroidMainThread())
                .setResult(new WorkHandler.ResultCallBack<String>() {
                    @Override
                    public void onSuccess(String obj) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
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


        WorkHandler.from(DbOrmHelper.getInstent())
                .executeOn(WorkHandler.schedulerWorkThread())
                .map(dbOrmHelper -> dbOrmHelper.query(TestBean.class))
                .toProgress()
                .handleProgress(new ProgressHandler.ProgressExecuter<List<TestBean>>() {
                    @Override
                    public void doInBackground(List<TestBean> testBeans) {
                        postProgress(null, 0);
                    }

                    @Override
                    public void progress(List<TestBean> testBeans, int progress) {

                    }
                })
                .executeOn(WorkHandler.schedulerAndroidMainThread())
                .map(list -> list.get(0).name)
                .setResult(new WorkHandler.ResultCallBack<String>() {
                    @Override
                    public void onSuccess(String obj) {
                        LightLog.i("我从数据库获得了" + obj);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        SimpleThreadHandler.getInstance().execute(new SimpleThreadHandler.SimpleHandlerCall<String>() {

            @Override
            public String doInBackground() {
                return null;
            }

        });
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

        @DbToMany(c1 = "id", c2 = "cid")
        @DbToOne
        List<TableBean1> msg;
    }

    static class TableBean1 {
        @DbPrimaryKey
        String id;
        String name;
    }
}
