package com.zyq.simplestore;

import android.content.Context;

import com.zyq.SuperCompression.QPhotoUtils;
import com.zyq.SuperCompression.QCompression;
import com.zyq.handler.WorkHandler;
import com.zyq.simplestore.imp.DbPrimaryKey;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;

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
                .ParsingType(Double.class)
                .forEach((aDouble, index) -> {
                })
                .map(value -> new HashMap<String, TableBean>())
                .toMapHandler()
                .ParsingType(String.class, TableBean.class)
                .map(value -> "66666666")
                .map(value -> new ArrayList<TableBean>())
                .toArrayHandler(TableBean.class)
                .execute();


        /**
         * 同步压缩
         */
        QCompression.newInstance()
                .getCompressionBuilder(null)
                .from("")
                .setMaxSize(100)
                .get();
        /**
         * 异步压缩 （自动切换到子线程成功后回调到主线程）
         */
        QCompression.newInstance()
                .getCompressionBuilder(null)
                .from("")
                .setMaxSize(100)
                .get(new QCompression.CompressionCallback() {
                    @Override
                    public void onStart(Context context) {

                    }

                    @Override
                    public void onSuccess(List<File> files) {

                    }

                    @Override
                    public void onErr(Exception e) {

                    }
                });
        /***
         *配合线程调度工具使用
         */
        WorkHandler.from(QCompression.newInstance())
                .executeOn(WorkHandler.schedulerWorkThread())
                .map(arg -> arg.getCompressionBuilder(null))
                .map(arg -> arg.setWidth(100).setMaxSize(100).setHeight(100))
                .map(arg -> arg.from("path").get())
                .executeOn(WorkHandler.schedulerAndroidMainThread())
                .setResult(new WorkHandler.ResultCallBack<List<File>>() {
                    @Override
                    public void onSuccess(List<File> obj) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        //调用相册
        QPhotoUtils.select((Fragment) null, (uri, result, arg) -> {
            if (result) {
                QCompression.newInstance()
                        .getCompressionBuilder(null)
                        .from(uri)
                        .setMaxSize(100)
                        .get();
            }
        });

    }

    static class TableBean {
        @DbPrimaryKey
        String id;
        String name;
    }
}
