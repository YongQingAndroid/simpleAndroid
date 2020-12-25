package com.zyq.simplestore.core;

import com.zyq.simplestore.imp.DbColumn;
import com.zyq.simplestore.imp.DbToMany;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * package kotlinTest:com.qing.orm.OrmTableBean.class
 * 作者：zyq on 2017/7/3 14:37
 * 邮箱：zyq@posun.com
 */
public class OrmTableBean {
    private Class tableClass;
    private String tableName;
    private Field[] fields; //已经过滤多表连接
    private Field primaryKey;
    private Map<String, OrmTableBean> maping;
    private boolean isCheckColumn = false;
    private Field mField;//当前成员变量（开启关系表格时启用）
    private OrmTableBean parentOrm;

    public OrmTableBean setTableClass(Class tableClass) {
        this.tableClass = tableClass;
        return this;
    }

    public OrmTableBean(Class tableClass, String tableName, Field[] fields) {
        this.tableName = tableName;
        this.fields = fields;
        this.tableClass = tableClass;
    }

    public boolean isList() {
        if (mField == null) {
            return true;
        }
        return List.class.isAssignableFrom(mField.getType());
    }

    public OrmTableBean getParentOrm() {
        return parentOrm;
    }

    public void setParentOrm(OrmTableBean parentOrm) {
        this.parentOrm = parentOrm;
    }

    public OrmTableBean() {
    }

    public Object getValue(Object obj, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(obj);
    }

    public Field getOwerField() {
        return mField;
    }

    public void setOwerField(Field mField) {
        this.mField = mField;
    }

    public Class getTableClass() {
        return tableClass;
    }

    /**
     * 获取类表名
     *
     * @param field
     * @return
     */
    public String getDbColumnName(Field field) {
        Object o = field.getAnnotation(DbColumn.class);
        if (o != null) {
            DbColumn mDbColumn = (DbColumn) o;
            return mDbColumn.value();
        }
        return field.getName();
    }


    public void setMaping(Map<String, OrmTableBean> maping) {
        this.maping = maping;
    }

    public boolean haveMaping() {
        if (!DbWorker.openMaping) {
            return false;
        }
        if (maping != null && maping.size() > 0)
            return true;
        return false;
    }

    public Map<String, OrmTableBean> getMaping() {
        return maping;
    }

    public Field getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Field primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isCheckColumn() {
        return isCheckColumn;
    }

    public void setCheckColumn(boolean checkColumn) {
        isCheckColumn = checkColumn;
    }

    public Field[] getFields() {
        return fields;
    }

//    public void praseMapingTask() throws Exception {
//        if (haveMaping()) {
//            for (String key : maping.keySet()) {
//                OrmTableBean ormTableBean = DbPraseClazz.getInstent().getTableMsg(maping.get(key).getType());
//                if (ormTableBean != null && ormTableBean.fields != null && ormTableBean.fields.length > 0) {
//                    String sql = DbPraseClazz.getInstent().getsaveSql(ormTableBean);
//                    mapSql.put(key, sql);
//                }
//            }
//        }
//    }


    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public String getTableName() {
        return tableName;
    }

    public OrmTableBean setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }
}
