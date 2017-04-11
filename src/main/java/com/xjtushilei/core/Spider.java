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

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private ThreadPoolExecutor pool;

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
        pool = new ThreadPoolExecutor(threadNum, threadNum,
                1500L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
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

    public Spider addUrlSeed(String url) {
        scheduler.push(new UrlSeed(url));
        return this;
    }

    public void run() {

        logger.info("爬虫启动!");


        UrlSeed urlSeed = null;
        while (true) {
            logger.info("当前线程池" + "已完成:" + pool.getCompletedTaskCount() + "   运行中：" + pool.getActiveCount() + "  最大:" + pool.getPoolSize());
            urlSeed = scheduler.poll();
            if (urlSeed == null && pool.getActiveCount() == 0) {
                pool.shutdown();
                try {
                    pool.awaitTermination(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    logger.error("关闭线程池失败！", e);
                }
                logger.info("爬虫结束！");
                break;
            } else if (urlSeed == null) {
                //没有取到种子就等待!
                TimeSleep.sleep(1000);
            } else {
                logger.info("正在处理:" + urlSeed.getUrl() + "  优先级(默认:5):" + urlSeed.getPriority());
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


            Page nowPage = downloader.download(urlSeed);

            pageProcessor.process(nowPage);

            nowPage.getNewUrlSeed().forEach(seed -> scheduler.push(seed));

            saver.save(nowPage);


        }
    }
}

