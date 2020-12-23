package com.zyq.simplestore.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zyq.simplestore.imp.BaseSQLiteOpenHelper;

/**
 * 默认ORM数据库
 */
public class DBHelper extends BaseSQLiteOpenHelper {
    public static String db_name = "light_help.db", sdCard = "/mnt/sdcard/light_help.db";
    public static final int vesion = 1;

    public static DBHelper newInstance(Context context) {
        db_name = context.getApplicationContext().getCacheDir().getAbsolutePath() + "/light_help.db";
        return new DBHelper(context);
    }


    private DBHelper(Context context) {
        super(context, db_name, null, vesion);

    }

    /**
     * 废弃该方法
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    /**
     * 废弃该方法
     */
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

    @Override
    public String getDbPath() {
        return db_name;
    }
}
