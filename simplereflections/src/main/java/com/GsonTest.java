package com;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GsonTest {

    public static void main(String[] args) {
        Gson.setStrictMode(false);
        String msg = "{\"name\":\"lisa\",\"sex\":\"name\",\"testArray\":{},\"address\":\"lisa\",\"test\":\"测试66666\"}";
        System.out.println(new Gson().toJson(new TestBean()));
        System.out.println("before====" + msg);
        TestBean testBean = new Gson().fromJson(msg, TestBean.class);
        System.out.println("after====" + new Gson().toJson(testBean));
    }

    static class TestBean {
        String name = "lisa";
        String sex = "name";
        Address address = new Address();
        List<Address> testArray = new ArrayList<>();
        String test = "测试";
    }

    static class Address {
        String msg = "地球村";
    }
}
