package com.github.xjtushilei.core.scheduler.PreDefine;

import com.github.xjtushilei.core.scheduler.Scheduler;
import com.github.xjtushilei.model.UrlSeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by shilei on 2017/4/11.
 */
public class PriorityQueueScheduler implements Scheduler {

    public static final int defaultPriority = 5;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private PriorityBlockingQueue<UrlSeed> priorityQueue = new PriorityBlockingQueue<UrlSeed>(defaultPriority, (o1, o2) -> -((o1.getPriority() < o2.getPriority()) ? -1 : ((o1.getPriority() == o2.getPriority()) ? 0 : 1)));
    private Set<UrlSeed> urlSet = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void push(UrlSeed urlSeed) {
        if (urlSeed.getUrl() == null
                || urlSeed.getUrl().trim().equals("")
                || urlSeed.getUrl().trim().equals("#")
                || urlSeed.getUrl().trim().toLowerCase().contains("javascript:"))
            return;
        if (urlSet.add(urlSeed)) {
            priorityQueue.add(urlSeed);
        } else {
            //            logger.info("UrlSeed重复:" + urlSeed.getUrl());

        }
    }

    @Override
    public UrlSeed poll() {
        return priorityQueue.poll();
    }

    //优先级队列测试！
    //    public static void main(String[] args){
    //        PriorityBlockingQueue<UrlSeed> priorityQueue = new PriorityBlockingQueue<UrlSeed>(defaultPriority, (o1, o2) -> -((o1.getPriority() < o2.getPriority()) ? -1 : ((o1.getPriority() == o2.getPriority()) ? 0 : 1)));
    //
    //        priorityQueue.add(new UrlSeed(1,"1"));
    //        priorityQueue.add(new UrlSeed(3,"3"));
    //        priorityQueue.add(new UrlSeed(5,"5"));
    //        priorityQueue.add(new UrlSeed(9,"9"));
    //        priorityQueue.add(new UrlSeed(10,"10"));
    //        System.out.println(priorityQueue.poll().getUrl());
    //        System.out.println(priorityQueue.poll().getUrl());
    //        System.out.println(priorityQueue.poll().getUrl());
    //
    //    }
}
