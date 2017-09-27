# 如何开始

在开始之前，你应该先了解该框架是如何工作的。

### 流程图
![流程图](http://xjtushilei.com/images/github/ScriptSpider/流程图.png)

基本上，你只需要提供“解析器”，“下载器”两个模块就好啦。

因为SS也不知道您想要哪一部分内容，不知道您想存到哪里~

如果您对上图很了解，那么可以直接开始编程了。或者您可以先看一下下面的简单用法介绍。

在[src/main/java/com/github/xjtushilei/example](https://github.com/xjtushilei/ScriptSpider/tree/master/src/main/java/com/github/xjtushilei/example)中可以查看所有的样例程序

###   最小Spider

```
    //爬取《交大新闻网》开始的所有页面信息，并将信息打印到控制台！

    Spider.build().addUrlSeed("http://news.xjtu.edu.cn").run();

```

一句话，就能实现一个爬虫！

因为，我们给您默认提供了好多组件。

###   最小多线程Spider

```
    //爬取《交大新闻网》开始的所有页面信息，并将信息打印到控制台！
    Spider.build()
          .thread(10)   //设置多少个线程
          .addUrlSeed("http://news.xjtu.edu.cn")
          .run();

```

如果您没有设置thread选项，默认是5个线程

当然了，您可以使用`.thread(1)`来使用单线程。虽然我们不建议您这么做。

甚至您可以使用`.thread(-100)`来启动线程（呵呵，默认是5）

正常的机器，我们推荐您使用10个以上的线程进行尝试！


###   最小正则Spider
```
    //爬取《交大新闻网》站内所有页面信息，并将信息打印到控制台！
    Spider.build()
          .addUrlSeed("http://news.xjtu.edu.cn")
          .addRegexRule("+http://news.xjtu.edu.cn/.*") //限制爬取《交大新闻网》以外的其他站点的信息
          .run();

```

上面的代码中，我们添加了一个正则信息，只有符合该正则过滤规则的才能进行爬取。避免了我们的爬虫链接到了更广阔的天地。

您可以添加多条正则规则，我们提供更加丰富的正则过滤器。

- 其中“+”为积极正则，“-”为消极正则。
- 只要任意满足一条“积极正则”，就进入待爬取队列。
- 只要满足任何一条“消极正则”，就被排除在外。
- 没有加减号的，默认为“积极正则”
例如：

```java
    //爬取《交大新闻网》站内所有页面信息，并将信息打印到控制台！
    Spider.build()
          .addUrlSeed("http://news.xjtu.edu.cn")
          .addRegexRule("+http://news.xjtu.edu.cn/.*") //限制爬取《交大新闻网》以外的其他站点的信息
          .addRegexRule("+http://news.xjtu.edu.cn/info/1001/.*") //可以爬虫1001类别的信息
          .addRegexRule("-http://news.xjtu.edu.cn/info/1002/.*") //只要url是1002类别的，就不爬取。
          .run();
```
### 基本spider

上面的两个爬虫都是使用了我们的默认组件，可能不是您想要的，接下来，我们开始写一个“最基本的爬虫”

请您再看一遍上面提到的框架 [流程图](#流程图)

在学会写自己个性化的爬虫代码之前，我希望您知道`Page`里存了几个您会用到的东西。

因为，大多数情况下，为了便于理解，该框架中，我们都是跟page打交道。

```
    //该页面的url信息，里面包含了一个url和一个优先级number。
    private UrlSeed urlSeed;
    
    //该页面的jsoup文档，您可以解析该页面。
    private Document document;
    
    //新种子，要放到url队列里的，让爬虫源源不断的进行
    private List<UrlSeed> newUrlSeed;
    
    //待存储的json。需要您在PageProcessor里把有用的信息丢到这里，然后在Saver里存到数据库里
    private Map<Object, Object> items;
```
接下来，我们要实现一个自己的解析器，自己的存储器。

- 解析器（ScriptSpider推荐的做法如下）：
   - 完成process函数
        - 判断该页面是否是您想要的
        - 把您想要的东西解析出来，丢进去Page的items中（`page.setItems(items)`）
        - map可以用多层嵌套，可以用任何的类型，满足您所有的需求。
   - 完成processNewUrlSeeds函数
        - 取出Page中的newurlseeds如果您觉得该页面的某些url是想要优先爬取得，设置高的优先级。
- 存储器
    - 将page中的items存放到您想要存放的地方。

下面，我们看代码，代码中的注释已经尽量的详尽！

为了能在一个类中写完这个爬虫，我们两个组件直接在一个类里写了，如果您不喜欢，可能看起来会有点乱。我们推荐您用一个新的类，并继承 `PageProcessor`和`Saver`两个类。

```java
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
        public Page process(Page page) {

            //如果不匹配，则不进行解析！
            if (!Pattern.matches("http://news.xjtu.edu.cn/info/.*htm", page.getUrlSeed().getUrl())) {
                return page;
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


            return page;
        }

        /**
         * 推荐在这里做优先级处理的东西。或者您可以做任何其他的事情。关于优先级的使用，我们在接下来的“优先级Spider”中会讲解
         */
        @Override
        public Page processNewUrlSeeds(Page page) {
            return page;
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
        public Page save(Page page) {
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
            return page;
        }
    };
}

```
### 优先级spider

ScriptSpider的组件中，已经实现了优先级调度器，所以您只需要在爬虫启动时，设置优先级调度器即可。（当然了，分布式调度器例如redis调度器等，都是支持优先级调度的）

然后在解析器中，ScriptSpider可以设置优先级处理的功能。

```java

import com.github.xjtushilei.core.Spider;
import com.github.xjtushilei.core.pageprocesser.PageProcessor;
import com.github.xjtushilei.core.saver.Saver;
import com.github.xjtushilei.core.scheduler.PreDefine.PriorityQueueScheduler;
import com.github.xjtushilei.core.scheduler.PreDefine.RedisScheduler;
import com.github.xjtushilei.model.Page;
import org.jsoup.nodes.Document;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;



public class PriorityQueueSpider {

    public static void main(String[] args) {

        Spider.build()
                .setScheduler(new PriorityQueueScheduler()) //优先级队列调度器
                .setSaver(mySaver)
                .setProcessor(myPageProcessor)
                .thread(10)
                .addUrlSeed("http://news.xjtu.edu.cn/zsjy.htm")
                .addRegexRule("+http://news.xjtu.edu.cn/.*") //限制爬取《交大新闻网》以外的其他站点的信息
                .run();
    }



    /**
     * 一个输出到控制台的 存储器。
     * 您可以替换这段代码，将爬取到的结果放入到mongodb，mysql等等中！
     */
    static Saver mySaver = new Saver() {
        @Override
        public Page save(Page page) {
            Map<Object, Object> map = page.getItems();
            System.out.print(page.getUrlSeed().getPriority());
            map.forEach((k, v) -> System.out.println(k + " : " + v));
            return page;
        }
    };

    /**
     * 实现自己逻辑的页面解析功能！将标题打印出来。
     * <p>
     * 这里是在一个文件里实现的，若果你的功能比较多，完全可以用新的class文件来生成，并在上面setPageProcessor即可！
     */
    static PageProcessor myPageProcessor = new PageProcessor() {

        @Override
        public Page process(Page page) {

            //如果不匹配，则不进行解析！
            if (!Pattern.matches("http://news.xjtu.edu.cn/info/.*htm", page.getUrlSeed().getUrl())) {
                return page;
            }

            Document htmldoc = page.getDocument();
            //select返回的是一个数组，所以需要first，相关语法请google“jsoup select语法”和“cssquery”
            try {
                String title = htmldoc.select(".d_title").first().text();

                //用来存放爬取到的信息，供之后存储！map类型的即可，可以自定义各种嵌套！
                Map<String, String> items = new HashMap<String, String>();
                items.put("url", page.getUrlSeed().getUrl());
                items.put("title", title);
                //放入items中，之后会自动保存（如果你自己实现了下载器，请自己操作它。如下我自定义了自己的下载器，并将它保存到了文本中！）！
                page.setItems(items);
            } catch (NullPointerException e) {
                System.out.println("该页面没有解析到相关东西！跳过");
            }
            return page;
        }

        /**
         * 这里进行了优先级的用法示范。
         *
         * 比如，你想在“交大新闻网”尽快或者优先爬取“招生就业”类型的新闻。
         * 然后我们发现，这一类新闻的url符合 “http://newsxq.xjtu.edu.cn/info/1006/.*”
         *
         */
        @Override
        public Page processNewUrlSeeds(Page page) {

            //招生就业模块，符合 “http://newsxq.xjtu.edu.cn/info/1006/.*htm” 或者“http://news.xjtu.edu.cn/zsjy/.*htm”（翻页时候的url），进行设置高优先级设置
            page.getNewUrlSeed().forEach(urlSeed -> {
                if (Pattern.matches("http://news.xjtu.edu.cn/zsjy.*htm", urlSeed.getUrl())||Pattern.matches("http://news.xjtu.edu.cn/info/1006/.*htm", urlSeed.getUrl())) {
                    urlSeed.setPriority(8);
                }
            });

            //综合新闻 板块 ，符合“http://newsxq.xjtu.edu.cn/info/1002/.*htm” 或者"http://news.xjtu.edu.cn/zhxw.htm"(翻页时候的url），进行设置低优先级设置
            page.getNewUrlSeed().forEach(urlSeed -> {
                if (Pattern.matches("http://news.xjtu.edu.cn/zhxw.*htm", urlSeed.getUrl())||Pattern.matches("http://news.xjtu.edu.cn/info/1002/.*htm", urlSeed.getUrl())) {
                    urlSeed.setPriority(2);
                }
            });

            //接下来还能进行其他的优先级设置。

            //优先级设置完之后，该页面新的url种子再进入待爬取队列的时候，就会自动进入优先队列！

            return page;
        }
    };

}

```

如果您不需要进行优先级的处理，ScriptSpider建议您使用默认的调度器（在Spider启动的时候不设置调度器），因为默认的普通的调度器会更快。


### 分布式spider ( 基于redis )

分布式调度器，目前只实现了基于redis的，之后会提供基于多种消息队列的，方便没有安装redis的用户方便使用。

当然了，我们推荐您使用redis，因为他的速度真的很快!(谁用谁知道)

值得注意的是，redis的优先级调度，仅仅只有三个优先级（高优先级，默认优先级，低优先级）。因为大多数情况下，2个优先级已经足够你使用（而我们提供了三个）！

多台部署：同样的代码，多台机器一个接一个启动就好了，不分先后。保证所有机器可以连接到redis的IP。


```java
package com.github.xjtushilei.example;

import com.github.xjtushilei.core.Spider;
import com.github.xjtushilei.core.pageprocesser.PageProcessor;
import com.github.xjtushilei.core.saver.Saver;
import com.github.xjtushilei.core.scheduler.PreDefine.RedisScheduler;
import com.github.xjtushilei.model.Page;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by shilei on 2017/4/12.
 */
public class RedisSpider {

    public static void main(String[] args) {

        Spider.build()
                .setScheduler(new RedisScheduler()) //redis默认的ip和端口是127.0.0.1:6379.配置个性化的ip等请使用 RedisScheduler(String ip, int port, int MaxActive(建议100))
                .setSaver(mySaver)
                .setProcessor(myPageProcessor)
                .thread(5)
                .addUrlSeed("http://news.xjtu.edu.cn")
                .addRegexRule("+http://news.xjtu.edu.cn/.*") //限制爬取《交大新闻网》以外的其他站点的信息
                .run();
    }


    /**
     * 一个输出到控制台的 存储器。
     * 您可以替换这段代码，将爬取到的结果放入到mongodb，mysql等等中！
     */
    static Saver mySaver = new Saver() {
        @Override
        public Page save(Page page) {
            Map<Object, Object> map = page.getItems();

            map.forEach((k, v) -> System.out.println(k + " : " + v));
            return page;
        }
    };

    /**
     * 实现自己逻辑的页面解析功能！将标题打印出来。
     * <p>
     * 这里是在一个文件里实现的，若果你的功能比较多，完全可以用新的class文件来生成，并在上面setPageProcessor即可！
     */
    static PageProcessor myPageProcessor = new PageProcessor() {

        @Override
        public Page process(Page page) {

            //如果不匹配，则不进行解析！
            if (!Pattern.matches("http://news.xjtu.edu.cn/info/.*htm", page.getUrlSeed().getUrl())) {
                return page;
            }

            Document htmldoc = page.getDocument();
            //select返回的是一个数组，所以需要first，相关语法请google“jsoup select语法”和“cssquery”
            try {
                String title = htmldoc.select(".d_title").first().text();

                //用来存放爬取到的信息，供之后存储！map类型的即可，可以自定义各种嵌套！
                Map<String, String> items = new HashMap<String, String>();
                items.put("title", title);
                items.put("url", page.getUrlSeed().getUrl());
                //放入items中，之后会自动保存（如果你自己实现了下载器，请自己操作它。如下我自定义了自己的下载器，并将它保存到了文本中！）！
                page.setItems(items);
            } catch (NullPointerException e) {
                System.out.println("该页面没有解析到相关东西！跳过");
            }
            return page;
        }

        /**
         * 新url种子进行额外的处理！（先进行了默认提供的正则处理！之后才进行这步。建议功能：在这里进行优先级的调整！）
         * <p>
         * redis实现了优先级的。这里我们不做展示。优先级的用法请到示范：PriorityQueueScheduler.java 中查看。
         * redis的默认优先级是5.并且只有三个优先级，高于5的，低于5的，等于5的。
         * @param page
         * @return 自己
         */
        @Override
        public Page processNewUrlSeeds(Page page) {
            return page;
        }
    };


}

```