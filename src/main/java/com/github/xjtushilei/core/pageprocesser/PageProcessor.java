package com.github.xjtushilei.core.pageprocesser;

import com.github.xjtushilei.model.Page;

/**
 * 解析页面使用
 * process函数需要完成的有：
 * 1.解析有用的信息，丢进去Page的List items中。之后save会进行存储！
 * 2.解析新的 url ，丢进去Page的List  newUrlSeed中。
 *
 */
public interface PageProcessor {

    /**
     * 解析页面
     * process函数需要完成的有：
     * 1.解析有用的信息，丢进去Page的List items中。之后save会进行存储！
     *
     * @param page
     * @return 自己
     */
    void process(Page page);

    /**
     * 新url种子进行额外的处理！（先进行了默认提供的正则处理！之后才进行这步）
     * <p>
     * 建议功能：在这里进行优先级的调整！或者你想做的任何关于新种子的处理！
     *
     * @param page
     * @return 自己
     */
    default void processNewUrlSeeds(Page page) {

    }
}
