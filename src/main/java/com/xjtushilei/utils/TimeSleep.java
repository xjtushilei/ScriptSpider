package com.xjtushilei.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by shilei on 2017/4/10.
 */
public class TimeSleep {
    private static Logger logger = LoggerFactory.getLogger(TimeSleep.class);

    /**
     * 睡眠等待
     *
     * @param milliseconds 毫秒
     */
    public static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(1500);
        } catch (InterruptedException e) {
            logger.error("该线程无法获取到种子了（意味着线程正常结束！或者出错！）", e);
        }
    }

}
