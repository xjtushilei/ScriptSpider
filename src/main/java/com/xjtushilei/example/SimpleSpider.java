package com.xjtushilei.example;

import com.xjtushilei.core.Spider;

/**
 * Created by shilei on 2017/4/11.
 */
public class SimpleSpider {

    public static void main(String[] args) {
        Spider.build()
                .addUrlSeed("http://newsxq.xjtu.edu.cn/")
                .run();
    }
}
