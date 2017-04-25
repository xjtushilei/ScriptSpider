package com.github.xjtushilei.example.Tmall;

import com.github.xjtushilei.core.pageprocesser.PageProcessor;
import com.github.xjtushilei.model.Page;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author shilei
 *         Date : 2017/4/17.
 */
public class TmallPageProcessor implements PageProcessor {

    @Override
    public Page process(Page page) {

        //如果匹配商品详情！
        if (!Pattern.matches("https://detail.tmall.com/.*html", page.getUrlSeed().getUrl())) {
            Document html = page.getDocument();

            try {
                //网页url
                String url = page.getUrlSeed().getUrl();
                //网页title
                String htmlTitle = html.title().trim();
                //商品名字
                String name = html.select(".tb-detail-hd h1").size() != 0 ? html.select(".tb-detail-hd h1").first().text().trim() : null;

                //商品详情
                Map<String, String> attributes = new HashMap<String, String>();
                for (Element element : html.select("#J_AttrUL li")) {
                    String key = element.text().split(":").length != 0 ? element.text().split(":")[0].trim() : element.text().trim();
                    String value = element.attr("title").trim();
                    attributes.put(key, value);
                }
                //商铺信息
                String a = html.select(".tb-detail-hd h1").size() != 0 ? html.select(".tb-detail-hd h1").first().text().trim() : null;


                //用来存放爬取到的信息，供之后存储！map类型的即可，可以自定义各种嵌套！
                Map<String, String> items = new HashMap<String, String>();
                items.put("url", page.getUrlSeed().getUrl());
                //放入items中，之后会自动保存（如果你自己实现了下载器，请自己操作它。如下我自定义了自己的下载器，并将它保存到了文本中！）！
                page.setItems(items);
            } catch (NullPointerException e) {
                System.out.println("该页面没有解析到相关东西！跳过");
            }
            return page;
        }


        return page;
    }

    /**
     * 这里进行了优先级的用法示范。
     * <p>
     * 比如，你想在“交大新闻网”尽快或者优先爬取“招生就业”类型的新闻。
     * 然后我们发现，这一类新闻的url符合 “http://newsxq.xjtu.edu.cn/info/1006/.*”
     */
    @Override
    public Page processNewUrlSeeds(Page page) {

        //招生就业模块，符合 “http://newsxq.xjtu.edu.cn/info/1006/.*htm” 或者“http://news.xjtu.edu.cn/zsjy/.*htm”（翻页时候的url），进行设置高优先级设置
        page.getNewUrlSeed().forEach(urlSeed -> {
            if (Pattern.matches("http://news.xjtu.edu.cn/zsjy.*htm", urlSeed.getUrl()) || Pattern.matches("http://news.xjtu.edu.cn/info/1006/.*htm", urlSeed.getUrl())) {
                urlSeed.setPriority(8);
            }
        });

        return page;
    }
}
