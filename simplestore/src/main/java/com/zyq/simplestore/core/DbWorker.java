package com.zyq.simplestore.core;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zyq.simplestore.log.LightLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库执行者（可并发访问）
 */
public class DbWorker {
    public SQLiteOpenHelper dbHelper;
    public Application application;
    public SQLiteDatabase sqLiteDatabase;
    public static boolean openMaping=false;//开启主键外键多表查询为内连接（开启会影响数据库读写速度）
    public  DbWorker(SQLiteOpenHelper dbHelper,Application application){
        this.dbHelper=dbHelper;
        this.application=application;
    }
    /**
     * 打开可读写数据局
     */
    public synchronized boolean openDataBase() {
        if (dbHelper == null)
            dbHelper = new DBHelper(application);
        if(sqLiteDatabase!=null&&sqLiteDatabase.isOpen()){
            return  false;
        }
        sqLiteDatabase = dbHelper.getWritableDatabase();
        return  true;
    }

    /**
     * 打开只读数据库
     */
    public synchronized boolean openOnlyReadDataBase() {
        if (dbHelper == null)
            dbHelper = new DBHelper(application);
        if(sqLiteDatabase!=null&&sqLiteDatabase.isOpen()){
            return  false;
        }
        sqLiteDatabase = dbHelper.getReadableDatabase();
        return true;
    }

    /**
     * 获取数据库
     * @return
     */
    public SQLiteDatabase getSqLiteDatabase(){
        return sqLiteDatabase;
    }

    /**
     *
     * @return
     */
    public SQLiteOpenHelper getHelper() {
        return dbHelper == null ? dbHelper = new DBHelper(application) : dbHelper;
    }

    public synchronized boolean execSQL(String sql, String... value) {
        if (dbHelper == null) {
            LightLog.e(" save file error : sqLiteDatabase is null");
            return false;
        }
        openDataBase();
        return execSQL(getSqLiteDatabase(),sql,value);
    }
    public synchronized boolean execSQL(SQLiteOpenHelper dbHelper,String sql, String... value) {
        if (dbHelper == null) {
            LightLog.e(" save file error : sqLiteDatabase is null");
            return false;
        }
        try {
            return execSQL(  dbHelper.getReadableDatabase(),sql,value);
        }catch (Exception e){
            LightLog.E(e.getMessage());
        }
       return  false;
    }
    /**
     * 兼容模式执行自定义Sql语句
     * @param sql
     * @param value
     */
    public synchronized boolean execSQL(SQLiteDatabase sqLiteDatabase,String sql, String... value) {
        LightLog.i(sql);
        try {
            if (value != null && value.length > 0) {
                sqLiteDatabase.execSQL(sql, value);
            } else {
                sqLiteDatabase.execSQL(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null)
                sqLiteDatabase.close();
        }
        return true;
    }
    /**
     * 判断当前表是否存在
     */
    public boolean havetable(String name, SQLiteDatabase mLiteDatabase) {
        String sql = "SELECT sql FROM  SQLITE_MASTER WHERE name =?";
        Cursor cursor = null;
        try {
            cursor = mLiteDatabase.rawQuery(sql, new String[]{name});
            if (cursor != null && cursor.moveToNext())
                return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    /**
     * 程序启动第一次操作该表检查和模型的一致性
     **/
    protected void checktable(Class clazz, SQLiteDatabase mysqLiteDatabase) {
        OrmTableBean ormTableBean = DbPraseClazz.getInstent().getTableMsg(clazz);
        if (!ormTableBean.isCheckColumn()) {
            LightLog.i("auto check table name" + ormTableBean.getTableName());
            List<String> coumns = getTableCoumn(ormTableBean, mysqLiteDatabase);
            if (coumns != null && coumns.size() > 0) {
                upDataTableColumns(coumns, ormTableBean, mysqLiteDatabase);
            }
        }
    }
    /**
     * 获取当前表的所有列
     */
    protected List<String> getTableCoumn(OrmTableBean ormTableBean, SQLiteDatabase mysqLiteDatabase) {
        String sql = "pragma table_info([" + ormTableBean.getTableName() + "])";
        Cursor cursor = null;
        List<String> mColumns = new ArrayList<>();
        try {
            cursor = mysqLiteDatabase.rawQuery(sql, new String[]{});
            if (cursor == null)
                return mColumns;
            while (cursor.moveToNext()) {
                mColumns.add(cursor.getString(cursor.getColumnIndex("name")));
            }
            return mColumns;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            ormTableBean.setCheckColumn(true);
        }
        return mColumns;
    }
    /**
     * 自动更新字段（只支持拓展增加）
     * 该方法只会check模型与数据库字段名并不会检查字段类型不可更改主键
     */
    private void upDataTableColumns(List<String> columns, OrmTableBean ormTableBean, SQLiteDatabase mysqLiteDatabase) {
        if (ormTableBean == null)
            return;
        List<Field> addcolumns = new ArrayList<>();
        int size = ormTableBean.getFields().length;
        for (int i = 0; i < size; i++) {
            if (!haveColumn(ormTableBean.getFields()[i], columns)) {
                addcolumns.add(ormTableBean.getFields()[i]);
            }
        }
        if (addcolumns.size() > 0) {
            LightLog.e("auto update " + ormTableBean.getTableName() + " TableColumns");
            for (Field item : addcolumns) {
                String sql = "alter table " + ormTableBean.getTableName() + " add " + DbPraseClazz.getInstent().getColumn(item);
                mysqLiteDatabase.execSQL(sql);
                LightLog.e(sql);
            }
        }
    }
    /**
     * 探测模型字段是否在表中
     */
    public boolean haveColumn(Field field, List<String> columns) {
        String name = field.getName();
        for (String item : columns) {
            if (name.equals(item))
                return true;
        }
        return false;
    }


    /**
     * 获取游标数据
     */
    public Object getvalue(Cursor cursor, Class clazz, int index) throws Exception {
        if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
            return cursor.getInt(index);
        } else if (long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
            return cursor.getLong(index);
        } else if (String.class.isAssignableFrom(clazz)) {
            return cursor.getString(index);
        } else {
            return SerializeManager.getInstance().praseReferenceClass(cursor.getBlob(index), clazz);
        }
    }
    /**
     * 用于兼容模式以数据库表字段为蓝本过滤模型字段
     */
    public Field[] getFieldFromCoumns(List<String> coumns, Class clazz) {
        List<Field> fields = new ArrayList<>();
        try {
            for (String item : coumns) {
                Field field = clazz.getDeclaredField(item);
                if (field != null)
                    fields.add(field);
            }
        } catch (Exception e) {
            LightLog.e(e.getMessage());
        }
        return fields.toArray(new Field[fields.size()]);

    }
}
