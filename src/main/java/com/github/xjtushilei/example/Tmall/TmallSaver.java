package com.github.xjtushilei.example.Tmall;

import com.github.xjtushilei.core.saver.Saver;
import com.github.xjtushilei.model.Page;

import java.util.Map;

public class TmallSaver implements Saver {

    @Override
    public Page save(Page page) {
        Map<Object, Object> map = page.getItems();
        System.out.print(page.getUrlSeed().getPriority());
        map.forEach((k, v) -> System.out.println(k + " : " + v));
        return page;
    }
}
