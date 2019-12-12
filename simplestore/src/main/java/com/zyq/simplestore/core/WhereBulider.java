package com.zyq.simplestore.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by qing on 2017/6/11.
 * orm条件构造器
 * @author zyq
 */

public class WhereBulider {
    private StringBuffer sql = new StringBuffer();
    private List<String> values = new ArrayList<>();
    /**
     * @param arg 条件
     * @param value value
     * 生成条件
     * */
    public WhereBulider where(String arg, String... value) {
        sql.append(" where ");
        sql.append(arg);
        if (value != null && value.length > 0)
            values.addAll(Arrays.asList(value));
        return this;
    }
    /**
     * @param arg 条件
     * @param value value
     * */
    public WhereBulider AN(String arg, String... value) {
        sql.append(" and ");
        sql.append(arg);
        if (value != null && value.length > 0)
            values.addAll(Arrays.asList(value));
        return this;
    }

    public static WhereBulider creat() {
        return new WhereBulider();
    }
    /**
     * @param arg 条件
     * @param value value
     * */
    public WhereBulider OR(String arg, String... value) {
        sql.append(" or ");
        sql.append(arg);
        if (value != null && value.length > 0)
            values.addAll(Arrays.asList(value));
        return this;
    }
    /**
     * 导出语句
     * */
    public String toString() {
        return sql.toString();
    }
    /**
     * 获取value数组
     * */
    public String[] getvalue() {
        if (values.size() == 0)
            return new String[]{};
        return values.toArray(new String[]{});
    }
    /**
     * 分页条件
     * @param count 每页查询数量
     * @param page 页码
     * */
    public WhereBulider limit(int page, int count) {
        sql.append(String.format(" LIMIT %1$s OFFSET %2$s ", count, (page - 1)*count));
        return this;
    }
}
