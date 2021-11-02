package com.github.xjtushilei.example;

import com.github.xjtushilei.core.Spider;
import com.github.xjtushilei.core.pageprocesser.PageProcessor;
import com.github.xjtushilei.core.saver.Saver;
import com.github.xjtushilei.core.scheduler.PreDefine.PriorityQueueScheduler;
import com.github.xjtushilei.model.Page;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by shilei on 2017/4/12.
 */
public class PriorityQueueSpider {

    public static void main(String[] args) {

        Spider.build()
                .setScheduler(new PriorityQueueScheduler()) //优先级队列调度器
                .setSaver(mySaver)
                .setProcessor(myPageProcessor)
                .thread(10)
                .addUrlSeed("http://news.xjtu.edu.cn/")
                .addRegexRule("+http://news.xjtu.edu.cn/.*") //限制爬取《交大新闻网》以外的其他站点的信息
                .run();
    }


    /**
     * 一个输出到控制台的 存储器。
     * 您可以替换这段代码，将爬取到的结果放入到mongodb，mysql等等中！
     */
    static Saver mySaver = new Saver() {
        @Override
        public void save(Page page) {
            Map<Object, Object> map = page.getItems();
            System.out.print(page.getUrlSeed().getPriority());
            map.forEach((k, v) -> System.out.println(k + " : " + v));
        }
    };

    /**
     * 实现自己逻辑的页面解析功能！将标题打印出来。
     * <p>
     * 这里是在一个文件里实现的，若果你的功能比较多，完全可以用新的class文件来生成，并在上面setPageProcessor即可！
     */
    static PageProcessor myPageProcessor = new PageProcessor() {

        @Override
        public void process(Page page) {

            //如果不匹配，则不进行解析！
            if (!Pattern.matches("http://news.xjtu.edu.cn/info/.*htm", page.getUrlSeed().getUrl())) {
                return;
            }

            Document htmldoc = page.getDocument();
            //select返回的是一个数组，所以需要first，相关语法请google“jsoup select语法”和“cssquery”
            try {
                String title = htmldoc.select(".ssd").first().text();

                //用来存放爬取到的信息，供之后存储！map类型的即可，可以自定义各种嵌套！
                Map<String, String> items = new HashMap<String, String>();
                items.put("url", page.getUrlSeed().getUrl());
                items.put("title", title);
                //放入items中，之后会自动保存（如果你自己实现了下载器，请自己操作它。如下我自定义了自己的下载器，并将它保存到了文本中！）！
                page.setItems(items);
            } catch (NullPointerException e) {
                System.out.println("该页面没有解析到相关东西！跳过");
            }
        }

        /**
         * 这里进行了优先级的用法示范。
         *
         * 比如，你想在“交大新闻网”尽快或者优先爬取“人才培养”类型的新闻。
         * 然后我们发现，这一类新闻的url符合 “http://news.xjtu.edu.cn/info/1003/.*”
         *
         */
        @Override
        public void processNewUrlSeeds(Page page) {

            //人才培养模块，符合 “http://news.xjtu.edu.cn/info/1006/.*htm” 或者“http://news.xjtu.edu.cn/rcpy/.*htm”（翻页时候的url），进行设置高优先级设置
            page.getNewUrlSeed().forEach(urlSeed -> {
                if (Pattern.matches("http://news.xjtu.edu.cn/rcpy.*htm", urlSeed.getUrl()) || Pattern.matches("http://news.xjtu.edu.cn/info/1003/.*htm", urlSeed.getUrl())) {
                    urlSeed.setPriority(8);
                }
            });

            //要闻聚焦 板块 ，符合“http://news.xjtu.edu.cn/info/1002/.*htm” 或者"http://news.xjtu.edu.cn/ywjj.htm"(翻页时候的url），进行设置低优先级设置
            page.getNewUrlSeed().forEach(urlSeed -> {
                if (Pattern.matches("http://news.xjtu.edu.cn/ywjj.*htm", urlSeed.getUrl()) || Pattern.matches("http://news.xjtu.edu.cn/info/1002/.*htm", urlSeed.getUrl())) {
                    urlSeed.setPriority(2);
                }
            });

            //接下来还能进行其他的优先级设置。

            //优先级设置完之后，该页面新的url种子再进入待爬取队列的时候，就会自动进入优先队列！

        }
    };


}
