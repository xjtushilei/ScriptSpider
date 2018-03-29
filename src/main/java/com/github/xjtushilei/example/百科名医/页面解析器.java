package com.github.xjtushilei.example.百科名医;

import com.github.xjtushilei.core.pageprocesser.PageProcessor;
import com.github.xjtushilei.model.Page;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author scriptshi
 * 2018/3/29
 */
public class 页面解析器 implements PageProcessor {

    @Override
    public void process(Page page) {
//http://www.baikemy.com/disease/list/1/34
        //如果匹配到具体的页面，则可以解析出我们关注的这个疾病的具体信息。
        if (Pattern.matches("http://www.baikemy.com/disease/detail/\\d*/1", page.getUrlSeed().getUrl())) {

            //解析想要的东西
            Document d = page.getDocument();
            String type = d.selectFirst("div.jb-mb > a:nth-child(5)").text();
            String name = d.selectFirst(" div.jb-head-left-text").text();
            String content = d.select(".lemma-main").text();

            //用来存放爬取到的信息，供之后存储！map类型的即可，可以自定义各种嵌套！
            Map<String, String> items = new HashMap<String, String>();
            items.put("type", type);
            items.put("name", name);
            items.put("content", content);
            items.put("url", page.getUrlSeed().getUrl());

            //放入items中，之后会自动保存（如果你自己实现了下载器，请自己操作它。如下我自定义了自己的下载器，并将它保存到了文本中！）！
            page.setItems(items);

        }
    }

    /**
     * 给需要存储的页面设置高的优先级，这样可以快速保存
     *
     * @param page page
     */
    @Override
    public void processNewUrlSeeds(Page page) {

        page.getNewUrlSeed().forEach(urlSeed -> {
            if (Pattern.matches("http://www.baikemy.com/disease/detail/\\d*/\\d*", urlSeed.getUrl())) {
                urlSeed.setPriority(10);
            }
        });
    }

}
