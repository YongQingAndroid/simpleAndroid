package com.zyq.simplestore.core;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

public class SerializeManager {
    private static SerializeManager self = null;

    public static synchronized SerializeManager getInstance() {
        if (self == null) {
            new SerializeManager();
        }
        return self;
    }

    public byte[] object2btye(Object o) {
        return JSON.toJSONBytes(o);
    }

    public Object byte2object(byte[] bytes, Type clazz) {
        return JSON.parseObject(bytes, clazz);
    }


    /**
     * 解析引用对象
     */
    public Object praseReferenceClass(byte[] bytes, Class clazz) {
        return byte2object(bytes, clazz);
    }
//    /**
//     * 序列化对象
//     * 对象 转为 byte[]
//     */
//    public byte[] objectToByte(Object obj) throws IOException {
//        ObjectOutputStream oos = null;
//        try {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            oos = new ObjectOutputStream(bos);
//            oos.writeObject(obj);
//            return bos.toByteArray();
//        } finally {
//            if (oos != null)
//                oos.close();
//        }
//    }
//    public Object byteToObject(byte[] bytes) throws Exception {
//        ObjectInputStream ois = null;
//        if (bytes == null || bytes.length < 0)
//            return null;
//        try {
//            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//            ois = new ObjectInputStream(bais);
//            return ois.readObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            if (ois != null)
//                ois.close();
//        }
//    }

}
