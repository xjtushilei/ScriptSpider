package com.github.xjtushilei.test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shilei on 2017/4/10.
 */
public class test2 {


    public static void main(String[] args) {

        Map map = new HashMap();
        map.forEach((key, value) -> System.err.println(key.toString() + " : " + value.toString()));
    }
}
