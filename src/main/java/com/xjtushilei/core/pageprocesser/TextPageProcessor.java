package com.xjtushilei.core.pageprocesser;

import com.xjtushilei.model.Page;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shilei on 2017/4/11.
 */
public class TextPageProcessor implements PageProcessor {

    public Page process(Page page) {
        Document doc = page.getDocument();

        String title = doc.title();
        String text = doc.text();
        Map<String, String> items = new HashMap<String, String>();
        items.put("title", title);
        items.put("text", text);
        items.put("url", page.getUrlSeed().getUrl());

        page.setItems(items);
        page.setNewUrlSeed(page.links());

        return page;
    }
}
