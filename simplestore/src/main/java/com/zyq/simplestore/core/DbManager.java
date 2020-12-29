package com.zyq.simplestore.core;

import java.util.List;

public class DbManager {


    public <T> WorkHandler query(Class<T> t, WhereBulider whereBulider) {

        return new WorkHandler(o -> DbOrmHelper.getInstent().query(t, whereBulider));
    }

    public <T> WorkHandler<List<T>> query(Class<T> t) {
        return query(t, null);
    }

    public void save(Object o) {

    }

    public void updata(Object o) {
        save(o);
    }

    public void remove(Object o) {

    }

    public void remove(Class o) {

    }


}
