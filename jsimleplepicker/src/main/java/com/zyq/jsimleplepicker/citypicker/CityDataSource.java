package com.zyq.jsimleplepicker.citypicker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.zyq.jsimleplepicker.QlightUnit;
import com.zyq.jsimleplepicker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * package kotlinTest:com.qing.lightview.material.CityDataSource.class
 * 作者：zyq on 2017/7/31 15:41
 * 邮箱：zyq@posun.com
 */
public class CityDataSource {
    private String dnname="simple_city.db";
    private String filePath="";
    public static String configFile="assets/city.db";

    private SQLiteDatabase openDatabase(Context context) {
        filePath=context.getApplicationContext().getCacheDir().getAbsolutePath()+dnname;
        File jhPath = new File(filePath);
        // 查看数据库文件是否存在
        if (jhPath.exists()) {
//            LightViewLog.i("存在数据库");
            return SQLiteDatabase.openOrCreateDatabase(jhPath, null);
        } else {
            try {
//                InputStream stream =  ClassLoader.getSystemClassLoader().getResourceAsStream(configFile);
                InputStream stream= context.getResources().openRawResource(R.raw.city);
                FileOutputStream fos = new FileOutputStream(jhPath);
                byte[] buffer = new byte[2048];
                int count = 0;
                while ((count = stream.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.flush();
                fos.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return openDatabase(context);
        }
    }
    public CityDataArrayList getCity(String pid){
        SQLiteDatabase database=null;
        try {
             database= openDatabase(QlightUnit.getApplication());
           Cursor cursor= database.rawQuery("SELECT * FROM city WHERE pid= '"+pid+"'",new String[]{});
            if(cursor==null)
                return null;
            CityDataArrayList arrayList=new CityDataArrayList();
            while (cursor.moveToNext()){
                CityBean item=new CityBean();
                item.setId(cursor.getString(0));
                item.setPid(cursor.getString(1));
                item.setName(cursor.getString(2));
                arrayList.add(item);
            }
            cursor.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(database!=null)
                database.close();
        }
        return null;
    }
    public CityDataArrayList getProvince(){
        SQLiteDatabase database=null;
        try {
            database= openDatabase(QlightUnit.getApplication());
            Cursor cursor= database.rawQuery("SELECT * FROM province",new String[]{});
            if(cursor==null)
                return null;
            CityDataArrayList arrayList=new CityDataArrayList();
            while (cursor.moveToNext()){
                CityBean item=new CityBean();
                item.setId(cursor.getString(0));
                item.setName(cursor.getString(1));
                arrayList.add(item);
            }
            cursor.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(database!=null)
                database.close();
        }
        return null;
    }
    public CityDataArrayList getArea(String pid){
        SQLiteDatabase database=null;
        try {
            database= openDatabase(QlightUnit.getApplication());
            Cursor cursor= database.rawQuery("SELECT * FROM area WHERE pid='"+pid+"'",new String[]{});
            if(cursor==null)
                return null;
            CityDataArrayList arrayList=new CityDataArrayList();
            while (cursor.moveToNext()){
                CityBean item=new CityBean();
                item.setId(cursor.getString(0));
                item.setPid(cursor.getString(1));
                item.setName(cursor.getString(2));
                arrayList.add(item);
            }
            cursor.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(database!=null)
                database.close();
        }
        return null;
    }
    public CityDataArrayList getStreet(String pid){
        SQLiteDatabase database=null;
        try {
            database= openDatabase(QlightUnit.getApplication());
            Cursor cursor= database.rawQuery("SELECT * FROM street WHERE pid='"+pid+"'",new String[]{});
            if(cursor==null)
                return null;
            CityDataArrayList arrayList=new CityDataArrayList();
            while (cursor.moveToNext()){
                CityBean item=new CityBean();
                item.setId(cursor.getString(0));
                item.setPid(cursor.getString(1));
                item.setName(cursor.getString(2));
                arrayList.add(item);
            }
            cursor.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(database!=null)
                database.close();
        }
        return null;
    }
}
