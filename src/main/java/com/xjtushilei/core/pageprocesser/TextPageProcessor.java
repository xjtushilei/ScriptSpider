package com.xjtushilei.core.pageprocesser;

import com.xjtushilei.model.Page;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shilei on 2017/4/11.
 *
 * 解析页面使用
 * process函数需要完成的有：
 * 1.解析有用的信息，丢进去Page的List items中。之后save会进行存储！
 * 2.解析新的 url ，丢进去Page的List  newUrlSeed中。
 *
 */
public class TextPageProcessor implements PageProcessor {

    /**
     * 解析页面
     * process函数需要完成的有：
     * 1.解析有用的信息，丢进去Page的List items中。之后save会进行存储！
     * 2.解析新的 url ，丢进去Page的List  newUrlSeed中。
     *
     * @param page
     * @return
     */
    public Page process(Page page) {
        Document doc = page.getDocument();

        String title = doc.title();
        String text = doc.text();
        Map<String, String> items = new HashMap<String, String>();
        items.put("title", title);
        items.put("text", text);
        items.put("url", page.getUrlSeed().getUrl());

        page.setItems(items);

        return page;
    }


    /**
     * url进行处理！
     * 这里没有进行任何特殊的处理！ 因为我们推荐你使用正则处理（默认实现，在spider初始化的时候进行添加）！
     *
     * @param page
     * @return
     */
    public Page regexNewUrlSeed(Page page) {


        return page;
    }
}
