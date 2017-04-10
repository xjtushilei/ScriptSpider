package com.xjtushilei.model;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Created by shilei on 2017/4/10.
 */
public class Page {


    private Logger logger = LoggerFactory.getLogger(getClass());

    private UrlSeed urlSeed;
    private Document document;
    private List<?> items;

    public Page(UrlSeed urlSeed, Document document) {
        this.urlSeed = urlSeed;
        this.document = document;
    }

    public Page() {
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

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }


    public static Page create() {
        return new Page();
    }


}
