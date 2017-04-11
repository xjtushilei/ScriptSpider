package com.xjtushilei.example;

import com.xjtushilei.core.Spider;
import com.xjtushilei.core.downloader.HttpClientPoolDownloader;
import com.xjtushilei.core.pageprocesser.TextPageProcessor;
import com.xjtushilei.core.saver.ConsoleSaver;
import com.xjtushilei.core.scheduler.QueueScheduler;

/**
 * Created by shilei on 2017/4/11.
 */
public class SimpleSpider {
    public static void main(String[] args) {
        Spider.build()
                .setScheduler(new QueueScheduler())
                .setDownloader(new HttpClientPoolDownloader())
                .setProcessor(new TextPageProcessor())
                .setSaver(new ConsoleSaver())
                .thread(5)
                .addUrlSeed("http://newsxq.xjtu.edu.cn/")
                .run();
    }
}
