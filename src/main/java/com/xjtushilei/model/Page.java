package com.xjtushilei.model;

import com.xjtushilei.utils.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by shilei on 2017/4/10.
 */
public class Page {


    private Logger logger = LoggerFactory.getLogger(getClass());

    //该页面的url信息
    private UrlSeed urlSeed;
    //该页面的jsoup文档，设置了baseUrl
    private Document document;
    //新种子
    private List<UrlSeed> newUrlSeed;
    //待存储的json
    private Map<Object, Object> items;


    public static Page create() {
        return new Page();
    }

    public Page(UrlSeed urlSeed, String html) {
        this.urlSeed = urlSeed;
        this.document = Jsoup.parse(html, urlSeed.getUrl());
    }

    public Page() {
        newUrlSeed = new ArrayList<>();
    }

    public List<UrlSeed> links() {
        List<UrlSeed> result = new ArrayList<>();
        Elements elements = document.select("a");
        List<String> links = new ArrayList<String>(elements.size());
        for (Element element0 : elements) {
            //            logger.debug(element0.attr("abs:href"));
            if (!StringUtil.isBlank(element0.baseUri())) {
                links.add(element0.attr("abs:href"));
            } else {
                links.add(element0.attr("href"));

            }
        }
        links.forEach(str -> {
            result.add(new UrlSeed(5, str));

        });
        return result;
    }

    public Page setNewUrlSeed(List<UrlSeed> newUrlSeed) {
        this.newUrlSeed = newUrlSeed;
        return this;
    }

    public void addNewUrlSeed(String url, long priority) {
        newUrlSeed.add(new UrlSeed(priority, url));
    }

    public void addNewUrlSeed(UrlSeed urlSeed) {
        newUrlSeed.add(urlSeed);
    }

    public void addNewUrlSeed(String url) {
        newUrlSeed.add(new UrlSeed(5, url));
    }

    public UrlSeed getUrlSeed() {
        return urlSeed;
    }

    public Page setUrlSeed(UrlSeed urlSeed) {
        this.urlSeed = urlSeed;
        return this;
    }

    public Document getDocument() {
        return document;
    }

    public Page setDocument(Document document) {
        this.document = document;
        return this;
    }

    public Map<Object, Object> getItems() {
        return items;
    }

    public Page setItems(Map items) {
        this.items = items;
        return this;
    }

    public List<UrlSeed> getNewUrlSeed() {
        return newUrlSeed;
    }

    public static void main(String[] args) throws IOException {
        String url = "http://news.xjtu.edu.cn/index.htm";
        Document doc = Jsoup.connect(url).get();
        //        System.out.println(doc.html());
        Page.create().setUrlSeed(new UrlSeed(url)).setDocument(Jsoup.parse(HttpUtils.getInstance().get(url), url)).links();
    }
}
