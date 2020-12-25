package com.zyq.simplestore.core;

import android.text.TextUtils;

import com.zyq.simplestore.imp.DbTableName;
import com.zyq.simplestore.imp.DbToMany;
import com.zyq.simplestore.imp.DbToOne;

import java.lang.reflect.Field;

public class SqlBuilder {
    OrmTableBean ormTableBean;


    public static SqlBuilder newInstance() {
        return new SqlBuilder();
    }

    public SqlBuilder setTableName() {
        return this;
    }

    public SqlBuilder setTableMsg(OrmTableBean ormTableBean) {
        this.ormTableBean = ormTableBean;
        return this;
    }

    public String getSqlString() {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ");
        sql.append(this.ormTableBean.getTableName());
        return sql.toString();
    }

    public WhereBulider getWhereBulider(Object obj) {
        if (ormTableBean.getParentOrm() == null || obj == null) {
            return WhereBulider.creat();
        }
        Field field = ormTableBean.getOwerField();
        String c1 = "", c2 = "";
        Object o = field.getAnnotation(DbToMany.class);
        Object o2 = field.getAnnotation(DbToOne.class);
        if (o != null) {
            DbToMany mDbToMany = (DbToMany) o;
            c1 = mDbToMany.c1();
            c2 = mDbToMany.c1();
        }

        if (o2 != null || TextUtils.isEmpty(c1) || TextUtils.isEmpty(c2)) {
            c1 = ormTableBean.getParentOrm().getPrimaryKey().getName();
            c2 = ormTableBean.getPrimaryKey().getName();
        }
        if (TextUtils.isEmpty(c1) || TextUtils.isEmpty(c2)) {
            throw new RuntimeException(ormTableBean.getParentOrm().getTableName() + "和" + ormTableBean.getTableName() + "数据表格映射关系不存在");
        }
        Object primaryValue = null;
        try {
            primaryValue = obj.getClass().getField(c1).get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return WhereBulider.creat();
        }
        return WhereBulider.creat().where(c2 + "=?", primaryValue.toString());
    }
}
