package com.github.xjtushilei.contentextractor;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Stack;

/**
 * 这是一个通用的新闻类型网页抽取正文的算法。特殊网页请自己定制！
 * author:SeaTomorrow
 * website:http://blog.csdn.net/seatomorrow/article/details/48393547
 */
public class ContentExtractor {


    private static String GetDocContent(Document doc) {
        Elements divs = doc.body().getElementsByTag("div");
        int max = -1;
        String content = null;
        for (int i = 0; i < divs.size(); i++) {
            Element div = (Element) divs.get(i);
            String divContent = GetDivContent(div);
            if (divContent.length() > max) {
                max = divContent.length();
                content = divContent;
            }
        }
        return content;
    }

    private static String GetDivContent(Element div) {
        StringBuilder sb = new StringBuilder();
        //考虑div里标签内容的顺序，对div子树进行深度优先搜索
        Stack<Element> sk = new Stack<Element>();
        sk.push(div);
        while (!sk.empty()) {
            //
            Element e = sk.pop();
            //对于div中的div过滤掉
            if (e != div && e.tagName().equals("div")) continue;
            //考虑正文被包含在p标签中的情况，并且p标签里不能含有a标签
            if (e.tagName().equals("p") && e.getElementsByTag("a").size() == 0) {
                String className = e.className();
                if (className.length() != 0 && className.equals("pictext")) continue;
                sb.append(e.text());
                sb.append("\n");
                continue;
            } else if (e.tagName().equals("td")) {
                //考虑正文被包含在td标签中的情况
                if (e.getElementsByTag("div").size() != 0) continue;
                sb.append(e.text());
                sb.append("\n");
                continue;

            }
            //将孩子节点加入栈中
            Elements children = e.children();
            for (int i = children.size() - 1; i >= 0; i--) {
                sk.push((Element) children.get(i));
            }
        }

        return sb.toString();
    }

}
