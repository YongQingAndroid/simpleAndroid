package com.zyq.simplestore.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 默认ORM数据库
 * package kotlinTest:qing.posun.com.calender.orm.DBHelper.class
 * 作者：zyq on 2017/6/9 14:33
 * 邮箱：zyq@posun.com
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String db_name="light_help.db",sdCard="/mnt/sdcard/light_help.db";
    public static final int vesion=1;
    public DBHelper(Context context) {
        super(context, db_name, null, vesion);
    }
    /**
     * 数据库保存在Sdcard
     * 注意数据安全
     * @param context
     * @param isSdCard
     * */
    public DBHelper(Context context,boolean isSdCard) {
        super(context, isSdCard?sdCard:db_name, null, vesion);
    }
    /**
     * 废弃该方法
     * */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }
    /**
     * 废弃该方法
     * */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

}
