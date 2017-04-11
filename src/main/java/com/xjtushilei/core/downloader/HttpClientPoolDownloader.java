package com.xjtushilei.core.downloader;

import com.xjtushilei.model.Page;
import com.xjtushilei.model.UrlSeed;
import com.xjtushilei.utils.HttpUtils;

/**
 * Created by shilei on 2017/4/10.
 */
public class HttpClientPoolDownloader implements Downloader {

    public Page download(UrlSeed urlSeed) {
        String html = HttpUtils.getInstance().get(urlSeed.getUrl());
        Page page = new Page(urlSeed, html);
        return page;
    }
}
