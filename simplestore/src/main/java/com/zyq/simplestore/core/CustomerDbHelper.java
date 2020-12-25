package com.zyq.simplestore.core;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.zyq.simplestore.imp.BaseSQLiteOpenHelper;
import com.zyq.simplestore.log.LightLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomerDbHelper {
    DbWorker dbWorker;

    public CustomerDbHelper(BaseSQLiteOpenHelper helper, Application app) {
        dbWorker = new DbWorker(helper, app);
    }

    /**
     * 兼容模式模式数据保存
     * 预处理表字段和模型的对应关系
     *
     * @param object    数据实体或者集合
     * @param tableName 表格名字
     */
    public void save(Object object, String tableName) {
        updata(object, tableName, null);
    }

    /**
     * @param object
     * @param tableName
     * @param whereBulider
     */
    public void updata(Object object, String tableName, WhereBulider whereBulider) {

        Class clazz;
        if (object == null)
            return;
        boolean islist = object instanceof List;
        if (islist) {
            if (((List) object).size() < 1) {
                return;
            }
            clazz = ((List) object).get(0).getClass();
        } else {
            clazz = object.getClass();
        }
        SQLiteDatabase sqLiteDatabase = null;
        try {
            dbWorker.openDataBase();
            sqLiteDatabase = dbWorker.getSqLiteDatabase();
            saveExecute(object, tableName, islist, clazz, sqLiteDatabase, whereBulider);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null)
                sqLiteDatabase.close();
        }
    }

    /**
     * 用于兼容模式字段保存
     *
     * @param islist         是否为集合
     * @param clazz
     * @param sqLiteDatabase 数据库对象
     * @param object         数据实体或者集合
     * @param tableName      表格名字
     */
    private void saveExecute(Object object, String tableName, boolean islist, Class clazz, SQLiteDatabase sqLiteDatabase, WhereBulider whereBulider) throws Exception {

        if (!dbWorker.havetable(tableName, sqLiteDatabase)) {
            LightLog.e(tableName + " Table does not exist");
            return;
        }
        OrmTableBean ormTableBean = DbPraseClazz.getInstent().getExtendTableMsg(clazz);
        if (ormTableBean == null) {
            ormTableBean = new OrmTableBean();
            preseTableWithOrmTableBean(ormTableBean, clazz, sqLiteDatabase, tableName);
        }
        try {
            sqLiteDatabase.beginTransaction();

            StringBuilder sql = new StringBuilder();
            sql.append(DbPraseClazz.getInstent().getsaveSql(ormTableBean));
            if (whereBulider != null) {
                sql.append(" ");
                sql.append(whereBulider.toString());
            }
            SQLiteStatement statement = sqLiteDatabase.compileStatement(sql.toString());
//            if (islist) {
//                List list = (List) object;
//                for (Object item : list) {
//                    DbPraseClazz.getInstent().saveData(statement, item, clazz);
//                }
//            } else {
//                DbPraseClazz.getInstent().saveData(statement, object, clazz);
//            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sqLiteDatabase.endTransaction();
            } catch (Exception e) {
                sqLiteDatabase.close();
            }
        }
    }

    /**
     * 兼容模式查询数据库
     *
     * @param clazz     表格对象
     * @param tableName 表名
     */
    public synchronized <T> List<T> query(Class<T> clazz, String tableName) {
        return this.query(clazz, tableName, null);
    }

    /**
     * 兼容模式查询数据库
     *
     * @param clazz        表格对象
     * @param tableName    表名
     * @param whereBulider 条件
     */
    public synchronized <T> List<T> query(Class<T> clazz, String tableName, WhereBulider whereBulider) {
        dbWorker.openOnlyReadDataBase();
        SQLiteDatabase sqLiteDatabase = dbWorker.getSqLiteDatabase();
        if (!dbWorker.havetable(tableName, sqLiteDatabase)) {
            LightLog.e(tableName + " Table does not exist");
            return null;
        }
        OrmTableBean ormTableBean = DbPraseClazz.getInstent().getExtendTableMsg(clazz);
        if (ormTableBean == null) {
            ormTableBean = new OrmTableBean();
            preseTableWithOrmTableBean(ormTableBean, clazz, sqLiteDatabase, tableName);
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ");
        sql.append(tableName);
        if (whereBulider != null) {
            sql.append(whereBulider.toString());
        } else {
            whereBulider = WhereBulider.creat();
        }
        LightLog.i(sql.toString());
        int size = ormTableBean.getFields().length;
        List list = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(sql.toString(), whereBulider.getvalue());
        try {
            while (cursor.moveToNext()) {
                Object item = clazz.newInstance();
                for (int i = 0; i < size; i++) {
                    Field field = ormTableBean.getFields()[i];
                    String name = field.getName();
                    int index = cursor.getColumnIndex(name);
                    if (index == -1)
                        continue;
                    field.setAccessible(true);
                    Object myobj = dbWorker.getvalue(cursor, field.getType(), index);
                    if (myobj != null)
                        field.set(item, myobj);
                }
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return list;
    }

    /**
     * 用于兼容模式
     */
    private void preseTableWithOrmTableBean(OrmTableBean ormTableBean, Class clazz, SQLiteDatabase sqLiteDatabase, String tableName) {
        ormTableBean.setTableName(tableName);
        List<String> coumns = dbWorker.getTableCoumn(ormTableBean, sqLiteDatabase);
        if (coumns != null && coumns.size() > 0) {
            Field[] fields = dbWorker.getFieldFromCoumns(coumns, clazz);
            if (fields != null && fields.length > 0) {
                ormTableBean.setFields(fields);
                DbPraseClazz.getInstent().saveTableMsg(clazz, ormTableBean);
            }
        }

        if (ormTableBean.getFields() == null || ormTableBean.getFields().length < 1) {
            throw new RuntimeException(" Table can not get field");
        }

    }

}
