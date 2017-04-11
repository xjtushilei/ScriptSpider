package com.github.xjtushilei.test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by shilei on 2017/4/10.
 */
public class test1 {


    static class SpiderWork implements Runnable {

        String name;

        SpiderWork(String name) {
            this.name = name;
        }

        public void run() {
            //            for (int i = 1; i <= 10; i++) {
            try {
                Thread.sleep(1000);
                System.out.println(name);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //            }
        }
    }

    public static void main(String[] args) {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        for (int i = 1; i < 10; i++) {
            threadPoolExecutor.execute(new SpiderWork(i + ""));
            System.out.println("个数:" + threadPoolExecutor.getActiveCount());
        }

        //        while (true){
        //
        //            try {
        //                if ( pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)) {
        //                    System.out.println("done");
        //                    break;
        //                }
        //            } catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //        }
        //        try {
        //            threadPoolExecutor.awaitTermination(Long.MAX_VALUE,TimeUnit.DAYS);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        System.out.println("kaishi ");
        while (true) {
            try {
                threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //            threadPoolExecutor.allowsCoreThreadTimeOut();
            //            TimeSleep.sleep(1000);
            System.out.println("完成数:" + threadPoolExecutor.getCompletedTaskCount());
            if (threadPoolExecutor.getActiveCount() == 0) {
                threadPoolExecutor.shutdown();
                break;

            }
        }
        System.out.println("!");

    }
}
