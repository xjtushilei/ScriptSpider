package com.github.xjtushilei.core.saver;

import com.github.xjtushilei.model.Page;

import java.util.Map;

/**
 * Created by shilei on 2017/4/11.
 */
public class ConsoleSaver implements Saver {
    public Page save(Page page) {
        Map<Object, Object> map = page.getItems();

        map.forEach((k, v) -> System.out.println(k + " : " + v));
        return page;
    }

}
