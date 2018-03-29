package com.github.xjtushilei.example;

import com.github.xjtushilei.core.Spider;
import com.github.xjtushilei.core.pageprocesser.PageProcessor;
import com.github.xjtushilei.core.saver.Saver;
import com.github.xjtushilei.model.Page;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by shilei on 2017/4/11.
 */
public class SimpleSpider {

    //爬取《交大新闻网》的所有信息，并将信息输出到文本文件！

    public static void main(String[] args) {
        Spider.build()
                .setProcessor(myPageProcessor)
                .setSaver(mySaver)
                .addUrlSeed("http://news.xjtu.edu.cn")
                .addRegexRule("http://news.xjtu.edu.cn/.*htm") //只爬取新闻类的页面
                .run();

    }


    /**
     * 实现自己逻辑的页面解析功能！
     * <p>
     * 这里是在一个文件里实现的，若果你的功能比较多，完全可以用新的class文件来生成，并在上面set即可！
     */
    static PageProcessor myPageProcessor = new PageProcessor() {

        @Override
        public void process(Page page) {

            //如果不匹配，则不进行解析！
            if (!Pattern.matches("http://news.xjtu.edu.cn/info/.*htm", page.getUrlSeed().getUrl())) {
                return;
            }

            Document htmldoc = page.getDocument();
            //select返回的是一个数组，所以需要first，相关语法请google“jsoup select语法”和“cssquery”
            try {
                String title = htmldoc.select(".d_title").first().text();
                String text = htmldoc.select(".d_detail").first().text();

                //用来存放爬取到的信息，供之后存储！map类型的即可，可以自定义各种嵌套！
                Map<String, String> items = new HashMap<String, String>();
                items.put("title", title);
                items.put("text", text);
                items.put("url", page.getUrlSeed().getUrl());

                //放入items中，之后会自动保存（如果你自己实现了下载器，请自己操作它。如下我自定义了自己的下载器，并将它保存到了文本中！）！
                page.setItems(items);
            } catch (NullPointerException e) {
                System.out.println("没有解析到相关东西！跳过");
            }

        }

    };

    /**
     * 实现自己的保存器！可以将爬取到的结果放入到mongodb，mysql等等中！这里保存到<当前用户>的“ScriptSpider”目录下。
     * <p>
     * 这里是在一个文件里实现的，若果你的功能比较多，完全可以用新的class文件来生成，并在上面set即可！
     *
     * 提醒：运行结束会产生大量的文件！建议运行几秒即可！
     */
    static Saver mySaver = new Saver() {

        @Override
        public void save(Page page) {
            //结果不为空就存储！
            if (page.getItems().size() != 0) {
                try {
                    String fileRoot = System.getProperty("user.home") + "/ScriptSpider/";
                    File file = new File(fileRoot + (new Date().getTime()) + ".txt");
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) {
                        fileParent.mkdirs();
                    }
                    file.createNewFile();
                    FileWriter fileWriter = new FileWriter(file);
                    page.getItems().forEach((key, value) -> {
                        try {
                            fileWriter.append(key.toString() + "\n").append(value.toString() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    };
}
