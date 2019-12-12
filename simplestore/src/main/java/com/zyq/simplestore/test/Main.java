package com.zyq.simplestore.test;

import com.zyq.simplestore.SimpleStore;
import com.zyq.simplestore.core.DbOrmHelper;
import com.zyq.simplestore.core.WhereBulider;
import com.zyq.simplestore.imp.DbIgnore;
import com.zyq.simplestore.imp.DbPrimaryKey;
import com.zyq.simplestore.imp.DbTableName;

public class Main {
   void test(){
       //key-value存储**********************************************
       //存储
       SimpleStore.store("key","");
       SimpleStore.store("key",new TestBean());
       //获取
       SimpleStore.praseKey("key").get(String.class);
       SimpleStore.praseKey("key").get(TestBean.class);
       //异步获取
       SimpleStore.praseKey("key").get(TestBean.class, obj -> {

       });
       //数据库操作**********************************************
       //
       TestBean ded=  new TestBean();
       //增
       DbOrmHelper.getInstent().save(ded);
       //删
       DbOrmHelper.getInstent().remove(ded); //删除数据
       DbOrmHelper.getInstent().remove(TableBean.class);//删除表
       //改
       ded.name="修改名字";
       DbOrmHelper.getInstent().save(ded);
       DbOrmHelper.getInstent().updata(ded);
       //查
       DbOrmHelper.getInstent().query(TableBean.class);//查询表所有数据
       DbOrmHelper.getInstent().query(TableBean.class, WhereBulider.creat().where("name=?","lise").OR("name=?","tom"));//条件查询
       DbOrmHelper.getInstent().query(TableBean.class,WhereBulider.creat().limit(1,15));//分页查找

   }
   class TestBean{
       private String name;
   }
   @DbTableName("tableTable") //自定义表名
   static class TableBean{
      @DbPrimaryKey //指定主键(默认使用字段名为id的字段作为主键)
       String id;
      @DbIgnore //数据库忽略当前字段
      String name;
   }
}