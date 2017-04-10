package com.xjtushilei.test;

/**
 * Created by shilei on 2017/4/10.
 */
public class test1 {


    static class SpiderWork implements Runnable {


        public void run() {
            for (int i = 1; i <= 10; i++) {
                try {
                    Thread.sleep(1000);
                    System.out.println(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new SpiderWork().run();
        System.out.println(2);

    }
}
