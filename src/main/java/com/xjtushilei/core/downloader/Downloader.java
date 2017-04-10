package com.xjtushilei.core.downloader;

import com.xjtushilei.model.Page;

/**
 * Created by shilei on 2017/4/10.
 * 下载器
 */
public interface Downloader {

    /**
     * @param Url 下载url页面
     */
    public Page download(String Url);
}
