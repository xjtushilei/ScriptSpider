package com.github.xjtushilei.example;

import com.github.xjtushilei.core.Spider;

/**
 * Created by shilei on 2017/4/11.
 */
public class MiniSpider {

    //爬取《交大新闻网》的所有信息，并将信息打印到控制台！
    public static void main(String[] args) {
        Spider.build()
                .addUrlSeed("http://news.xjtu.edu.cn")
                .run();
    }
}
