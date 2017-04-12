package com.github.xjtushilei.core.downloader.PreDefine;

import com.github.xjtushilei.core.downloader.Downloader;
import com.github.xjtushilei.model.Page;
import com.github.xjtushilei.model.UrlSeed;
import com.github.xjtushilei.utils.HttpUtils;

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
