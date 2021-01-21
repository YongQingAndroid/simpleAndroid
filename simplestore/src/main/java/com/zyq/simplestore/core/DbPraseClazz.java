package com.zyq.simplestore.core;

import android.database.sqlite.SQLiteStatement;


import com.zyq.simplestore.imp.DbIgnore;
import com.zyq.simplestore.imp.DbPrimaryKey;
import com.zyq.simplestore.imp.DbTableName;
import com.zyq.simplestore.imp.DbToMany;
import com.zyq.simplestore.imp.DbToOne;
import com.zyq.simplestore.log.LightLog;

import java.io.File;
import java.lang.annotation.Documented;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 字段反射处理类
 *
 * @author zyq
 */
public class DbPraseClazz {
    private static DbPraseClazz self;

    private DbPraseClazz() {
    }

    public static synchronized DbPraseClazz getInstent() {
        if (self == null)
            self = new DbPraseClazz();
        return self;
    }

    /**
     * 缓存表信息 避免反复反射浪费性能
     */
    private Map<Class, OrmTableBean> tables = new HashMap<>();

    /**
     * 根据模型创建表
     */
    public String getCreaTabSql(Class clazz) {
        OrmTableBean ormTableBean = getTableMsg(clazz);
        ormTableBean.setCheckColumn(true);
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(ormTableBean.getTableName());
        sql.append(" (");
        Field[] fields = ormTableBean.getFields();
        int size = fields.length;
        for (int i = 0; i < size; i++) {
            Field myfield = fields[i];
            String typeValue = getColumn(myfield);
            if (isPrimaryKey(myfield)) {
                if (Integer.class.isAssignableFrom(myfield.getType()) || int.class.isAssignableFrom(myfield.getType())) {
                    DbPrimaryKey myfieldAnnotation = myfield.getAnnotation(DbPrimaryKey.class);
                    if (DbPrimaryKey.AUTOINCREMENT.equalsIgnoreCase(myfieldAnnotation.value())) {
                        sql.append(OrmTableBean.getDbColumnName(myfield));
                        sql.append(" INTEGER ");
                        sql.append(" PRIMARY KEY");
                        sql.append(" AUTOINCREMENT ");
                    } else {
                        sql.append(typeValue);
                        sql.append(" PRIMARY KEY");
                    }
                } else {
                    sql.append(typeValue);
                    sql.append(" PRIMARY KEY");
                }
                ormTableBean.setPrimaryKey(myfield);
            } else {
                sql.append(typeValue);
            }
            if (i + 1 == size) {
                sql.append(")");
            } else {
                sql.append(",");
            }
        }
        LightLog.i(sql.toString());
        return sql.toString();
    }

    public OrmTableBean getTableMsg(Class clazz, Field... fieldArgs) {
        return getTableMsg(clazz, null, fieldArgs);
    }

    /**
     * 根据模型获取表信息
     */
    public OrmTableBean getTableMsg(Class clazz, OrmTableBean parentOrm, Field... fieldArgs) {
        OrmTableBean mOrmTableBean = new OrmTableBean();
        if (tables.containsKey(clazz)) {
            return tables.get(clazz);
        }
//        LightLog.i("getTableMsg from Class search");
        List<Field> table_field = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        Field primaryField = null;
        Field defult_primaryField = null;
        int size = fields.length;
        Map<String, OrmTableBean> mapping = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Field myfield = fields[i];
            myfield.setAccessible(true);//跳过安全检查调高响应速度
            if (myfield.getName().equalsIgnoreCase("$change") || (myfield.getAnnotation(DbIgnore.class) != null)) {
                continue;
            }
            if (isPrimaryKey(myfield)) {
                primaryField = myfield;
            }
            if (myfield.getName().equalsIgnoreCase("id"))
                defult_primaryField = myfield;
            myfield.setAccessible(true);
            if (praseMapping(mOrmTableBean, myfield, mapping)) {
                continue;
            }
            table_field.add(myfield);
        }
        if (table_field.size() < 0) {
            throw new RuntimeException("can not get " + getTableName(clazz) + "TableMsg");
        }
        Field[] myfields = table_field.toArray(new Field[table_field.size()]);
        mOrmTableBean.setTableClass(clazz).setTableName(getTableName(clazz)).setFields(myfields);
        if (fieldArgs != null && fieldArgs.length > 0) {
            mOrmTableBean.setOwerField(fieldArgs[0]);
            mOrmTableBean.setParentOrm(parentOrm);
        }
        /**解析关系**/
        if (mapping.size() > 0) {
            mOrmTableBean.setMaping(mapping);
        }
        if (primaryField != null) {
            mOrmTableBean.setPrimaryKey(primaryField);
        } else if (defult_primaryField != null) {
            mOrmTableBean.setPrimaryKey(defult_primaryField);
        }
        tables.put(clazz, mOrmTableBean);
        return mOrmTableBean;
    }

    /***
     * 兼容模式表信息字段获取
     */
    public OrmTableBean getExtendTableMsg(Class clazz) {
        if (tables.containsKey(clazz)) {
            return tables.get(clazz);
        }
        return null;
    }

    /**
     * 兼容模式下保存表信息到缓存
     */
    public void saveTableMsg(Class clazz, OrmTableBean arg) {
        tables.put(clazz, arg);
    }

    /**
     * 生成表名
     */
    public String getTableName(Class clazz) {
        Object obj = clazz.getAnnotation(DbTableName.class);
        if (obj != null) {
            DbTableName tableName = (DbTableName) obj;
            return tableName.value();
        }
        return clazz.getName().replaceAll("\\.", "_");
    }

    /**
     * 根据模型获取预编译保存sql语句
     */
    public String getsaveSql(Class clazz) throws Exception {
        OrmTableBean ormTableBean = getTableMsg(clazz);
        if (ormTableBean == null)
            throw new RuntimeException("Table does not exist the bean object is invalid");
        return this.getsaveSql(ormTableBean);
    }

    /**
     * 根据表信息获取预编译保存sql语句
     */
    public String getsaveSql(OrmTableBean ormTableBean) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("REPLACE INTO ");
        sql.append(ormTableBean.getTableName());
        sql.append("(");
        StringBuffer databulider = new StringBuffer();
        databulider.append(" values(");
        int size = ormTableBean.getFields().length;
        for (int i = 0; i < size; i++) {
            Field field = ormTableBean.getFields()[i];
            sql.append(OrmTableBean.getDbColumnName(field));
            field.setAccessible(true);
            databulider.append("?");
            if (i + 1 == size) {
                sql.append(")");
                databulider.append(")");
            } else {
                sql.append(",");
                databulider.append(",");
            }

        }
        sql.append(databulider);
        LightLog.i(sql.toString());
        return sql.toString();
    }

    /**
     * 根据表信息获取预编译保存sql语句
     //     */
//    public String getUpdateSql(OrmTableBean ormTableBean) throws Exception {
//        StringBuffer sql = new StringBuffer();
//        sql.append("UPDATE ");
//        sql.append(ormTableBean.getTableName());
//        sql.append(" SET ");
//        int size = ormTableBean.getFields().length;
//        for (int i = 0; i < size; i++) {
//            Field field = ormTableBean.getFields()[i];
//            sql.append(field.getName());
//            sql.append("= ?");
//        }
//        return sql.toString();
//    }

    /**
     * 绑定预编语句value 执行插入
     * 捕获异常并抛出关闭预编译
     */
    public void saveData(DbWorker dbWorker, Object object, Class TableClass) throws Exception {
        if (object == null) {
            return;
        }
        SQLiteStatement statement = dbWorker.getSqLiteDatabase().compileStatement(DbPraseClazz.getInstent().getsaveSql(TableClass));
        try {
            if (object instanceof List) {
                List list = (List) object;
                for (Object item : list) {
                    saveDataExe(dbWorker, statement, item, TableClass);
                }
                statement.close();
            } else {
                saveDataExe(dbWorker, statement, object, TableClass);
                statement.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            statement.close();
        }

    }

    /**
     * 执行数据插入更新
     *
     * @param dbWorker
     * @param statement
     * @param obj
     * @param TableClass
     * @throws Exception
     */
    public void saveDataExe(DbWorker dbWorker, SQLiteStatement statement, Object obj, Class TableClass) throws Exception {
        OrmTableBean ormTableBean = getTableMsg(TableClass);
        if (ormTableBean == null)
            throw new RuntimeException("Table does not exist the bean object is invalid");
        int size = ormTableBean.getFields().length;
        for (int i = 0; i < size; i++) {
            Field field = ormTableBean.getFields()[i];
            Class myclass = field.getType();
            bindStatement(statement, i, myclass, field, obj);
        }
        statement.executeInsert();
        if (ormTableBean.haveMaping()) {
            OrmTableBean itemtable = null;
            for (String key : ormTableBean.getMaping().keySet()) {
                itemtable = ormTableBean.getMaping().get(key);
                saveData(dbWorker, itemtable.getValue(obj, itemtable.getOwerField()), itemtable.getTableClass());
            }
        }
    }

    /**
     * @param field     当前字段描述
     * @param i         预编译的字段位置
     * @param myclass   当前需要绑定的数据类型
     * @param statement 预编译Sql
     * @param obj       当前字段的值
     */
    public void bindStatement(SQLiteStatement statement, int i, Class myclass, Field field, Object obj) throws Exception {
        if (Integer.class.isAssignableFrom(myclass) || int.class.isAssignableFrom(myclass)) {
            statement.bindString(i + 1, String.valueOf(field.get(obj)));
        } else if (Long.class.isAssignableFrom(myclass) || long.class.isAssignableFrom(myclass)) {
            statement.bindLong(i + 1, field.getLong(obj));
        } else if (String.class.isAssignableFrom(myclass)) {
            statement.bindString(i + 1, String.valueOf(field.get(obj)));
        } else {
            praseReferenceObject(field.get(obj), statement, i);
        }
    }

    /**
     * 解析引用对象
     */
    private void praseReferenceObject(Object obj, SQLiteStatement statement, int i) throws Exception {
        if (obj == null)
            return;
        statement.bindBlob(i + 1, SerializeManager.getInstance().object2btye(obj));
//         if (obj instanceof List) {
//            statement.bindBlob(i + 1, objectToByte((ArrayList) obj));/**必须指明类型否则会反序列化失败*/
//        } else if (obj instanceof Map) {
//            statement.bindBlob(i + 1, objectToByte((HashMap) obj));
//        } else if (obj instanceof Serializable) {
//            statement.bindBlob(i + 1, objectToByte(obj));
//        }
    }

    /**
     * 获取表字段名称及类型
     */
    public String getColumn(Field field) {
        Class type = field.getType();
        String sqltype;
        if (String.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type) || double.class.isAssignableFrom(type) || short.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            sqltype = "TEXT";
        } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            sqltype = "TEXT";
        } else if (Date.class.isAssignableFrom(type)) {
            sqltype = "TEXT";
        } else {
            sqltype = "BLOB";
        }
        return OrmTableBean.getDbColumnName(field) + " " + sqltype;
    }

    /**
     * 判断主键
     */
    private boolean isPrimaryKey(Field field) {
        Object o = field.getAnnotation(DbPrimaryKey.class);
        return o != null || "id".equalsIgnoreCase(OrmTableBean.getDbColumnName(field));
    }

    /**
     * 判断映射关系
     */
    private boolean praseMapping(OrmTableBean parentOrm, Field field, Map<String, OrmTableBean> map) {

        if (!DbWorker.openMaping) {
            return false;
        }
        Object o = field.getAnnotation(DbToMany.class);
        Object o2 = field.getAnnotation(DbToOne.class);
        if (o != null) {
            map.put(OrmTableBean.getDbColumnName(field), getTableMsg(getClassType(field), parentOrm, field));
            return true;
        } else if (o2 != null) {
            map.put(OrmTableBean.getDbColumnName(field), getTableMsg(getClassType(field), parentOrm, field));
            return true;
        }
        return false;
    }

    /***
     * 解析Field对应的表数据类型
     * @param field
     * @return
     */
    private Class getClassType(Field field) {
        if (List.class.isAssignableFrom(field.getType())) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            Class<?> actualTypeArgument = (Class<?>) type.getActualTypeArguments()[0];
            return actualTypeArgument;
        }
        return field.getType();
    }
}
