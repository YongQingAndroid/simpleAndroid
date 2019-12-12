package com.zyq.simplestore.core;

import android.database.sqlite.SQLiteDatabase;

public class DbWorker {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    public DbWorker(DBHelper dbHelper){
        this.dbHelper=dbHelper;
    }


}
