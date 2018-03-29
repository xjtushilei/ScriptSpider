package com.github.xjtushilei.example.百科名医;

import com.github.xjtushilei.core.Spider;
import com.github.xjtushilei.core.scheduler.PreDefine.PriorityQueueScheduler;

/**
 * @author scriptshi
 * 2018/3/29
 */
public class 百科名医爬虫入口 {
    public static void main(String[] args) {
        Spider.build()
                .thread(20)
                .setScheduler(new PriorityQueueScheduler()) //使用具有优先级的调度器
                .setProcessor(new 页面解析器())
                .setSaver(new 结果保存器())
                .addUrlSeed("http://www.baikemy.com/disease/list/0/0")
                .addRegexRule("http://www.baikemy.com/disease/list/\\d*/\\d*") //添加科室list页面
                .addRegexRule("http://www.baikemy.com/disease/view/\\d*") //添加疾病概述页面
                .addRegexRule("http://www.baikemy.com/disease/detail/\\d*/1") //添加疾病具体内容页面
                .run();
    }
}
