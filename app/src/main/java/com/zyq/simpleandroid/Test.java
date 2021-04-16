package com.zyq.simpleandroid;

public class Test {
    public static void main(String[] args) {
        System.out.println("------循环测试--------");
         LooperManager.getInstance()
                .register(() -> {
                   System.out.println("一秒钟循环测试");
        },1000).commit();
        LooperManager.getInstance() .register(() -> {
                    System.out.println("五秒钟循环测试");
                },5000).commit();
    }
}
