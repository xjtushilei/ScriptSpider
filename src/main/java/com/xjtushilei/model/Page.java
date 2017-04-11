package com.xjtushilei.model;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by shilei on 2017/4/10.
 */
public class Page {


    private Logger logger = LoggerFactory.getLogger(getClass());

    private UrlSeed urlSeed;
    private Document document;


    private List<UrlSeed> newUrlSeed = new ArrayList<>();
    private Map<Object, Object> items;

    public static Page create() {
        return new Page();
    }

    public Page(UrlSeed urlSeed, Document document) {
        this.urlSeed = urlSeed;
        this.document = document;
    }

    public Page() {
    }

    public List<UrlSeed> links() {
        List<UrlSeed> result = new ArrayList<>();
        Elements elements = document.select("a");
        List<String> links = new ArrayList<String>(elements.size());
        for (Element element0 : elements) {
            if (!StringUtil.isBlank(element0.baseUri())) {
                links.add(element0.attr("abs:href"));
            } else {
                links.add(element0.attr("href"));
            }
        }
        links.forEach(str -> result.add(new UrlSeed(5, str)));
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

    public void setUrlSeed(UrlSeed urlSeed) {
        this.urlSeed = urlSeed;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Map<Object, Object> getItems() {
        return items;
    }

    public void setItems(Map items) {
        this.items = items;
    }

    public List<UrlSeed> getNewUrlSeed() {
        return newUrlSeed;
    }
}
