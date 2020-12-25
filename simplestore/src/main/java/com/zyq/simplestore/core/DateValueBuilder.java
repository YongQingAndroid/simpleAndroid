package com.zyq.simplestore.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DateValueBuilder {
    LinkedHashMap<String, List<Object>> Objects = new LinkedHashMap<>();

    public static DateValueBuilder newInstance() {
        return new DateValueBuilder();
    }

    public DateValueBuilder addObj(String fileName, Object obj) {
        return addObj(fileName, obj, isList(obj));
    }

    public DateValueBuilder addObj(String fileName, Object obj, boolean isList) {
        if (obj == null)
            return this;
        if (isList) {
            Objects.put(fileName, (List<Object>) obj);
        } else {
            List list = new ArrayList();
            list.add(obj);
            Objects.put(fileName, list);
        }
        return this;
    }

    public boolean isList(Object o) {
        if (o == null)
            return false;
        return o instanceof List;

    }
}
