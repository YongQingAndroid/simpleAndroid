package com.zyq.simplestore;

import com.alibaba.fastjson.JSON;
import com.zyq.simplestore.core.DbOrmHelper;
import com.zyq.simplestore.core.TableCatch;
import com.zyq.handler.ThreadPool;
import com.zyq.simplestore.core.WhereBulider;
import com.zyq.simplestore.imp.DbPrimaryKey;
import com.zyq.simplestore.log.LightLog;

import java.util.List;

public class SimpleStore {

    /**
     * 储存
     *
     * @param key
     * @param obj
     */
    public static void store(String key, Object obj) {
        TableCatch.mapCatch.put(key, JSON.toJSONString(obj));
        ThreadPool.execute(() -> {
            DbOrmHelper.getInstent().save(new SimpleTable(key, obj));
        });
    }

    /**
     * 开启磁盘映射
     */
    public static void openMmap() {
        //涉及底层待实现
    }


    /**
     * 删除数据
     *
     * @param key
     */
    public static void remove(String key) {
        if (TableCatch.mapCatch.containsKey(key)) {
            TableCatch.mapCatch.remove(key);
        }
        DbOrmHelper.getInstent().remove(SimpleTable.class, WhereBulider.creat().where("key=?", key));
    }

    /**
     * 清空数据
     */
    public static void clear() {
        TableCatch.mapCatch.clear();
        DbOrmHelper.getInstent().remove(SimpleTable.class);
    }

    /***
     * 获取数据
     * @param key
     * @return
     */
    public static Result praseKey(String key) {
        return new Result(key);
    }

    public static class Result {
        String key;

        protected Result(String key) {
            this.key = key;
        }

        /**
         * 同步获取数据的值
         *
         * @param type 数据类型
         * @param <T>
         * @return
         */
        public <T> T get(Class<T> type) {
            try {
                if (TableCatch.mapCatch.containsKey(key)) {
                    LightLog.I("From Memory");
                    return JSON.parseObject(TableCatch.mapCatch.get(key), type);
                } else {
                    List<SimpleTable> result = DbOrmHelper.getInstent().query(SimpleTable.class, WhereBulider.creat().where("key=?", key));
                    if (result != null && result.size() > 0) {
                        TableCatch.mapCatch.put(key, result.get(0).value);
                        return JSON.parseObject(result.get(0).value, type);
                    }
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        /**
         * 异步获取数据的值
         *
         * @param type
         * @param call
         * @param <T>
         */
        public <T> void get(Class<T> type, storeCall<T> call) {

            Object obj = get(type);
            if (obj != null) {
                call.call((T) obj);
            }
        }
    }

    public static class SimpleTable {
        @DbPrimaryKey
        String key;
        String value;

        public SimpleTable() {

        }

        SimpleTable(String key, Object value) {
            this(key, JSON.toJSONString(value));
        }

        SimpleTable(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }

    public interface storeCall<T> {
        void call(T obj);
    }
    //            Type[] interfacesTypes = call.getClass().getGenericInterfaces();
//            Type[] genericType2 = ((ParameterizedType) interfacesTypes[0]).getActualTypeArguments(); 表达式不能获取类型
}
