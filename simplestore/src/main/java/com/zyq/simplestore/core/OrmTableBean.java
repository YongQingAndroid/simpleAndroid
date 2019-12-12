package com.zyq.simplestore.core;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * package kotlinTest:com.qing.orm.OrmTableBean.class
 * 作者：zyq on 2017/7/3 14:37
 * 邮箱：zyq@posun.com
 */
public class OrmTableBean {
    private String tableName;
    private Field[] fields;
    private Field primaryKey;
    private Map<String,Field> maping;
    private boolean isCheckColumn = false;
    public OrmTableBean(String tableName, Field[] fields) {
        this.tableName = tableName;
        this.fields = fields;
    }
    public OrmTableBean() {
    }
    public Map<String, Field> getMaping() {
        return maping;
    }

    public void setMaping(Map<String, Field> maping) {
        this.maping = maping;
    }

    public boolean haveMaping(){
        if(maping!=null&&maping.size()>0)
            return true;
        return false;
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

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
