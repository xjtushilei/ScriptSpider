package com.xjtushilei.model;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by shilei on 2017/4/11.
 */
public class RegexRule {
    public RegexRule() {

    }

    private ArrayList<String> positiveRule = new ArrayList<String>();
    private ArrayList<String> negativerule = new ArrayList<String>();


    /**
     * 添加一个正则规则 正则规则有两种，正正则和反正则
     * URL符合正则规则需要满足下面条件： 1.至少能匹配一条正正则 2.不能和任何反正则匹配
     * 正正则示例：+a.*c是一条正正则，正则的内容为a.*c，起始加号表示正正则
     * 反正则示例：-a.*c时一条反正则，正则的内容为a.*c，起始减号表示反正则
     * 如果一个规则的起始字符不为加号且不为减号，则该正则为正正则，正则的内容为自身
     * 例如a.*c是一条正正则，正则的内容为a.*c
     *
     * @param rule 正则规则
     * @return
     */
    public RegexRule addRule(String rule) {
        if (rule.length() == 0) {
            return this;
        }
        char pn = rule.charAt(0);
        String realrule = rule.substring(1);
        if (pn == '+') {
            addPositive(realrule);
        } else if (pn == '-') {
            addNegative(realrule);
        } else {
            addPositive(rule);
        }
        return this;
    }


    /**
     * 添加一个正正则规则
     *
     * @param positiveregex
     * @return
     */
    public RegexRule addPositive(String positiveregex) {
        positiveRule.add(positiveregex);
        return this;
    }


    /**
     * 添加一个反正则规则
     *
     * @param negativeregex
     * @return
     */
    public RegexRule addNegative(String negativeregex) {
        negativerule.add(negativeregex);
        return this;
    }


    /**
     * 判断输入字符串是否符合正则规则
     *
     * @param str 输入的字符串
     * @return 输入字符串是否符合正则规则
     */
    public boolean regex(String str) {

        int state = 0;
        for (String nregex : negativerule) {
            if (Pattern.matches(nregex, str)) {
                return false;
            }
        }

        int count = 0;
        for (String pregex : positiveRule) {
            if (Pattern.matches(pregex, str)) {
                count++;
            }
        }
        if (count == 0) {
            return false;
        } else {
            return true;
        }

    }
}
