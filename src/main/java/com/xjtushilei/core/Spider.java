package com.xjtushilei.core;


import com.xjtushilei.core.downloader.Downloader;
import com.xjtushilei.core.pageprocesser.PageProcessor;
import com.xjtushilei.core.saver.Saver;
import com.xjtushilei.core.scheduler.Scheduler;
import com.xjtushilei.model.Page;
import com.xjtushilei.model.UrlSeed;
import com.xjtushilei.utils.TimeSleep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shilei on 2017/4/10.
 */
public class Spider {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Scheduler scheduler;
    private Downloader downloader;
    private PageProcessor pageProcessor;
    private Saver saver;

    private int threadNum = 4;//线程池大小。默认4个爬虫在进行。
    private ExecutorService pool;

    /**
     * 最多几个爬虫在进行。
     * 默认4个。
     *
     * @param threadNum
     * @return
     */
    public Spider thread(int threadNum) {
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            this.threadNum = 4;
        }
        return this;
    }

    public static Spider build() {
        return new Spider();
    }

    public Spider setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public Spider setDownloader(Downloader d) {
        this.downloader = d;
        return this;
    }

    public Spider setProcessor(PageProcessor p) {
        this.pageProcessor = p;
        return this;
    }

    public Spider setSaver(Saver s) {
        this.saver = s;
        return this;
    }


    public void run() {

        logger.info("爬虫启动!");
        pool = Executors.newFixedThreadPool(threadNum);

        UrlSeed urlSeed = null;
        if ((urlSeed = scheduler.poll()) == null) {
            logger.error("获取第一个种子失败，请检查相关队列或集合！");
            return;
        }
        Thread initThread = new Thread(new SpiderWork(urlSeed));
        initThread.start();
        try {
            initThread.join();
        } catch (InterruptedException e) {
            logger.error("种子线程初始化失败，请检查种子！", e);
        }

        int loopCout = 0;
        //五次取种子，取失败则认为不存在种子
        while (loopCout < 5) {

            if ((urlSeed = scheduler.poll()) == null) {
                loopCout++;
                //暂停1.5秒，继续取种子
                TimeSleep.sleep(1500);
            } else {
                pool.execute(new SpiderWork(urlSeed.clone()));
            }
        }
    }

    class SpiderWork implements Runnable {

        private UrlSeed urlSeed;

        SpiderWork(UrlSeed urlSeed) {

            this.urlSeed = urlSeed;
        }

        public void run() {

            //整个流程为:
            // (download下载) ->  (pageProcessor解析处理) ->  (save存储)


            Page nowPage = downloader.download(urlSeed.getUrl());


            pageProcessor.process(nowPage);


            saver.save(nowPage);


        }
    }
}

