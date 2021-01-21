package com.zyq.simplestore.test;

import com.zyq.handler.WorkHandler;
import com.zyq.simplestore.imp.DbPrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrmTest {
    public static void main(String[] args) {
        List<TableBean> beanList = new ArrayList<>();
        beanList.add(new TableBean());
        beanList.add(new TableBean());
        beanList.add(new TableBean());
        beanList.add(new TableBean());
        beanList.add(new TableBean());

        WorkHandler.fromArray(beanList)
                .executeOn(WorkHandler.schedulerWorkThread())
                .forEach((obj, i) -> System.out.println(i))
                .filter((obj, i) -> i % 2 == 0)
                .setResult(new WorkHandler.ResultCallBack<List<TableBean>>() {
                    @Override
                    public void onSuccess(List<TableBean> obj) {

                        System.out.println("onSuccess" + obj.size());
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        HashMap<String, String> hashMap = new HashMap<>();
        WorkHandler.fromHashMap(hashMap)
                .executeOn(WorkHandler.schedulerWorkThread())
                .forEach(((key, value) -> {
                }))
                .filter(((key, value) -> false))
                .setResult(new WorkHandler.ResultCallBack<Map<String, String>>() {
                    @Override
                    public void onSuccess(Map<String, String> obj) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        WorkHandler.from("")
                .executeOn(WorkHandler.schedulerWorkThread())
                .map(value -> new ArrayList<Double>())
                .toArrayHandler()
                .praseType(Double.class)
                .forEach((aDouble, index) -> {
                })
                .map(value -> new HashMap<String, TableBean>())
                .toMapHandler()
                .praseType(String.class, TableBean.class)
                .map(value->"66666666")
                .map(value->new ArrayList<TableBean>())
                .toArrayHandler(TableBean.class)
                .execute();


    }

    static class TableBean {
        @DbPrimaryKey
        String id;
        String name;
    }
}
