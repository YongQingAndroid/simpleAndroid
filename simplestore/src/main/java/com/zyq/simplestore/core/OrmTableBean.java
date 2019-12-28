package com.zyq.simplestore.core;
import com.zyq.simplestore.imp.DbColumn;
import com.zyq.simplestore.imp.DbMaping;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * package kotlinTest:com.qing.orm.OrmTableBean.class
 * 作者：zyq on 2017/7/3 14:37
 * 邮箱：zyq@posun.com
 */
public class OrmTableBean {
    private String tableName;
    private Field[] fields; //已经过滤多表连接
    private Field primaryKey;
    private Map<String,Field> maping;
    private Map<String,String> mapSql=new HashMap<>();
    private boolean isCheckColumn = false;
    public OrmTableBean(String tableName, Field[] fields) {
        this.tableName = tableName;
        this.fields = fields;
    }
    public OrmTableBean() {
    }

    /**
     * 获取类表名
     * @param field
     * @return
     */
    public String getDbColumnName(Field field){
        Object o = field.getAnnotation(DbColumn.class);
        if(o!=null){
            DbColumn mDbColumn= (DbColumn) o;
            return mDbColumn.value();
        }
        return  field.getName();
    }
    public Map<String, Field> getMaping() {
        return maping;
    }

    public void setMaping(Map<String, Field> maping) {
        this.maping = maping;
    }

    public boolean haveMaping(){
        if(!DbWorker.openMaping){
            return false;
        }
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
    public void praseMapingTask() throws Exception{
        if(haveMaping()){
            for(String key:maping.keySet()){
                OrmTableBean ormTableBean=  DbPraseClazz.getInstent().getTableMsg(maping.get(key).getType());
                if(ormTableBean!=null&&ormTableBean.fields!=null&&ormTableBean.fields.length>0){
                  String sql= DbPraseClazz.getInstent().getsaveSql(ormTableBean);
                    mapSql.put(key,sql);
                }
            }
        }
    }
    public void excuteMaping(Object obj){

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
