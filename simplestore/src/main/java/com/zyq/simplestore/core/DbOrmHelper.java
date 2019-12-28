package com.zyq.simplestore.core;

import android.app.Application;

import android.database.Cursor;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.zyq.simplestore.log.LightLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象级数据库，面向对象的数据库存储
 * 自动侦测数据库表字段变化自动升级数据库
 * 查询数据时带有映射关系类型必须为ArrayList或List
 * 优化映射关系数据为异步查询，返回集合对象使用到映射数据时才会触发数据库条件搜索
 * 映射子查询不可用于兼容老数据库表
 * 作者：zyq
 * @author zyq
 */
public class DbOrmHelper {
    private static DbOrmHelper self;
    private static Application context;
    private DbWorker dbWorker;
    public DbOrmHelper() {
        dbWorker=new DbWorker(null,getContext());
    }

    /**
     * @param helper 需要兼容到老数据库是初始化SQLiteOpenHelper
     */
    public void initSQLiteOpenHelper(SQLiteOpenHelper helper) {
        this.dbWorker.dbHelper = helper;
    }

    /**
     * 获取单例对象
     */
    public static synchronized DbOrmHelper getInstent() {
        if (self == null)
            self = new DbOrmHelper();
        return self;
    }




    /**
     * @param appcontext 上下文
     *                   初始化ORM
     */
    public static void initSdk(Application appcontext) {
        context = appcontext;
    }

    /**
     * 基于反射的初始化ORM
     */
    public static void initSdk() throws Exception {
        context = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
    }



    private Application getContext() {
        if (context == null) {
            try {
                initSdk();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return context;
    }

    /**
     * 更新对象表单（必须含有主键）
     */
    public void updata(Object obj) {
        save(obj);
    }

    /**
     * @param mClass 表单对象
     *               查询数据库
     */
    public <M> List<M> query(Class<M> mClass) {
        return this.query(mClass, null);
    }

    /**
     * @param whereBulider 查询条件
     * @param mClass       表单对象
     *                     查询数据库
     */
    public <M> List<M> query(Class<M> mClass, WhereBulider whereBulider) {
        return this.query(mClass, DbPraseClazz.getInstent().getTableName(mClass), whereBulider);
    }

    /**
     * 查询数据库字段
     *
     * @param mClass       数据模型 即返回数据集合的模型
     * @param tableName    表名
     * @param whereBulider 查找条件
     *                     多表联合查询优化为子查询
     *                     优化重写映射关系集合多表联合查询的时间复杂度理论趋近于零
     */
    public <M> List<M> query(Class<M> mClass, String tableName, WhereBulider whereBulider) {
        OrmTableBean ormTableBean = DbPraseClazz.getInstent().getTableMsg(mClass);
        Field[] fields = ormTableBean.getFields();
        List list = new ArrayList<>();
        dbWorker.openOnlyReadDataBase();
        verification_tab(mClass);
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ");
        sql.append(tableName);
        if (whereBulider != null) {
            sql.append(whereBulider.toString());
        } else {
            whereBulider = WhereBulider.creat();
        }
        LightLog.i(sql.toString());
        int size = fields.length;
        Cursor cursor = dbWorker.getSqLiteDatabase().rawQuery(sql.toString(), whereBulider.getvalue());
        try {
            long sart = System.currentTimeMillis();
            while (cursor.moveToNext()) {
                Object item = newInstance(mClass);
                for (int i = 0; i < size; i++) {
                    Field field = fields[i];
//                    field.setAccessible(true);//跳过安全检查可以提高速度
                    String name = field.getName();
                    int index = cursor.getColumnIndex(name);
                    if (index == -1)
                        continue;
                    Object myobj =dbWorker.getvalue(cursor, field.getType(), index);

                    if (myobj != null)
                        field.set(item, myobj);
                }
                list.add(item);
            }
            LightLog.i("耗时" + (System.currentTimeMillis() - sart) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            dbWorker.getSqLiteDatabase().close();
        }
        return list;
    }

    /**
     * 实例化对象
     */
    private <M> M newInstance(Class<M> mClass) throws InstantiationException, IllegalAccessException {
        return mClass.newInstance();
    }

    /**
     * @param table_name   表格名字
     * @param whereBulider 条件
     *                     清空表
     */
    private void remove(String table_name, WhereBulider whereBulider) {
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ");
        sql.append(table_name);
        sql.append(whereBulider.toString());
        executeSql(sql.toString(), whereBulider.getvalue());
    }

    /**
     * @param calzz        表格对象
     * @param whereBulider 条件
     *                     清空表
     */
    public void remove(Class calzz, WhereBulider whereBulider) {
        OrmTableBean ormTableBean = DbPraseClazz.getInstent().getTableMsg(calzz);
        remove(ormTableBean.getTableName(), whereBulider);
    }

    /**
     * 删除数据库数据
     *
     * @param object 数据实体对象
     */
    public void remove(Object object) {
        OrmTableBean ormTableBean = DbPraseClazz.getInstent().getTableMsg(object.getClass());
        if (ormTableBean.getPrimaryKey() == null) {
            LightLog.e(ormTableBean.getTableName() + " no primarykey not limit auto delet");
            return;
        }
        WhereBulider whereBulider = WhereBulider.creat();
        try {
//          ormTableBean.getPrimaryKey().setAccessible(true);
            whereBulider.where(ormTableBean.getPrimaryKey().getName() + "=?", String.valueOf(ormTableBean.getPrimaryKey().get(object)));
            this.remove(object.getClass(), whereBulider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空表格
     *
     * @param clazz 表格类
     */
    public void remove(Class clazz) {
        OrmTableBean ormTableBean = DbPraseClazz.getInstent().getTableMsg(clazz);
        if (ormTableBean == null) {
            LightLog.e("not find Table mapping " + clazz.getName());
        }
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ");
        sql.append(ormTableBean.getTableName());
        executeSql(sql.toString());
    }

    public void removeAllTable() {

    }

    /**
     * 批量删除
     * 优化删除语句
     * 开启批量事务
     * 预编译删除
     *
     * @param list 数据实体集合
     */
    public void remove(List list) {
        Object testobject = list.get(0);
        OrmTableBean ormTableBean = DbPraseClazz.getInstent().getTableMsg(testobject.getClass());
        if (ormTableBean.getPrimaryKey() == null) {
            LightLog.e(ormTableBean.getTableName() + " no primarykey not limit auto delet");
        }
        SQLiteStatement statement = null;
        try {
            dbWorker.openDataBase();
            dbWorker.getSqLiteDatabase().beginTransaction();
            StringBuffer sql = new StringBuffer();
            sql.append("delete from ");
            sql.append(ormTableBean.getTableName());
            sql.append(" where");
            sql.append(ormTableBean.getPrimaryKey().getName());
            sql.append("=?");
            statement = dbWorker.getSqLiteDatabase().compileStatement(sql.toString());
            for (Object object : list) {
                DbPraseClazz.getInstent().bindStatement(statement, 0, ormTableBean.getPrimaryKey().getType(), ormTableBean.getPrimaryKey(), object);
                statement.executeUpdateDelete();
            }
            dbWorker.getSqLiteDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (dbWorker.getSqLiteDatabase() != null) {
                dbWorker.getSqLiteDatabase().endTransaction();
                dbWorker.getSqLiteDatabase().close();
            }
        }
    }

    /**
     * 执行sql语句
     *
     * @param sql
     * @param value
     */
    public void executeSql(String sql, String... value) {
        dbWorker.execSQL(sql,value);
    }


    /**
     * 需要保存到数据库的所有字段除基础数据类型必须是可序列化
     *
     * @param object 需要保存到数据库的对象
     */
    public void save(Object object) {
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
        OrmTableBean tab_msg = DbPraseClazz.getInstent().getTableMsg(clazz);
        if (tab_msg.getPrimaryKey() == null) {
            throw new RuntimeException(tab_msg.getTableName() + "no primarykey not limit save");
        }
        SQLiteStatement  statement =null;
        try {
            dbWorker.openDataBase();
            verification_tab(clazz);
            dbWorker.getSqLiteDatabase().beginTransaction();
            statement =   dbWorker.getSqLiteDatabase().compileStatement(DbPraseClazz.getInstent().getsaveSql(clazz));
            if (islist) {
                List list = (List) object;
                for (Object item : list) {
                    DbPraseClazz.getInstent().saveData(statement, item, clazz);
                }
            } else {
                DbPraseClazz.getInstent().saveData(statement, object, clazz);
            }
            dbWorker.getSqLiteDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if ( dbWorker.getSqLiteDatabase() != null) {
                dbWorker.getSqLiteDatabase().endTransaction();
                dbWorker.getSqLiteDatabase().close();
            }
        }
    }

    /**
     * 校验字段
     * 检查表格字段对应关系
     * 检查表格是否存在
     *
     * @param clazz 关联表的对象
     */
    private void verification_tab(Class clazz) {
        OrmTableBean mOrmTableBean = DbPraseClazz.getInstent().getTableMsg(clazz);
        if (mOrmTableBean == null) {
            return;
        }
        if (!mOrmTableBean.isCheckColumn()) {
            if (dbWorker.havetable(mOrmTableBean.getTableName(), dbWorker.getSqLiteDatabase())) {
                dbWorker.checktable(clazz, dbWorker.getSqLiteDatabase());
            } else {
                dbWorker.getSqLiteDatabase().execSQL(DbPraseClazz.getInstent().getCreaTabSql(clazz));
                mOrmTableBean.setCheckColumn(true);
            }
        }
    }

    /**
     * 拓展兼容原有数据库
     * 兼容其他第三方数据库框架以及原生android数据库接口
     *
     * @param sqLiteOpenHelper
     */
//    public static 666 with(SQLiteOpenHelper sqLiteOpenHelper) {
//        return 666.getInstent().setSQLiteOpenHelper(sqLiteOpenHelper);
//    }
}
