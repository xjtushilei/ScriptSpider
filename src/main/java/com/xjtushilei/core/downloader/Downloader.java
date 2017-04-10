package com.xjtushilei.core.downloader;

import com.xjtushilei.model.Page;
import com.xjtushilei.model.UrlSeed;

/**
 * Created by shilei on 2017/4/10.
 * 下载器
 */
public interface Downloader {

    /**
     * @param urlSeed 下载url页面
     */
    public Page download(UrlSeed urlSeed);
}
