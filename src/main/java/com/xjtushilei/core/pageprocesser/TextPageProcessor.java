package com.xjtushilei.core.pageprocesser;

import com.xjtushilei.model.Page;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shilei on 2017/4/11.
 */
public class TextPageProcessor implements PageProcessor {

    public Page process(Page page) {
        Document doc = page.getDocument();
        String title = doc.title();
        String text = doc.text();
        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("title", title);
        map.put("text", text);
        items.add(map);

        page.setItems(items);

        return page;
    }
}
