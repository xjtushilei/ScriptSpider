package com.github.xjtushilei.core.downloader;

import com.github.xjtushilei.model.Page;
import com.github.xjtushilei.model.UrlSeed;

/**
 * Created by shilei on 2017/4/10.
 * 下载器
 */
public interface Downloader {

    /**
     * @param urlSeed 下载url页面
     * @return Page page
     */
    Page download(UrlSeed urlSeed);
}
