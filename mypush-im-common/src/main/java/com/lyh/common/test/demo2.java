package com.lyh.common.test;

import java.util.Vector;

public class demo2 {

    private static Vector<Integer> vector;

    public static void main(String[] args) {
        vector = new Vector<>();

        singleThreadIterator();
//        multiThreadIterator();
    }

    //单线程Iterator迭代时, 修改容器元素会抛出异常
    private static void singleThreadIterator() {
        for (int i = 0; i < 5; i++) {
            vector.add(i);
        }

        for (Integer integer : vector) {
            vector.remove(1);  //这里会抛出异常
        }
    }
    
    //多线程,如果一个线程在使用迭代器,这时候修改了容器元素,也会抛出异常
    private static void multiThreadIterator() {
       for (int i = 0; i < 5; i++) {
            vector.add(i);
        }
        //修改
        new Thread(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            vector.remove(3);
        }).start();

        //读
        new Thread(() -> {
            for (Integer integer : vector) {
                System.out.println(integer);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}