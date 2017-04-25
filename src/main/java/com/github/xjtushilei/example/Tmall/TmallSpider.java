package com.github.xjtushilei.example.Tmall;

import com.github.xjtushilei.core.Spider;

/**
 * Created by shilei on 2017/4/12.
 */
public class TmallSpider {

    public static void main(String[] args) {

        Spider.build()
                .setSaver(new TmallSaver())
                .setProcessor(new TmallPageProcessor())
                .thread(10)
                .addUrlSeed("https://www.tmall.com")
                .addRegexRule("+https://.*.tmall.com/.*") //限制爬取“天猫网站”以外的其他站点的信息
                .run();
    }


}
