# ScriptSpider

ScriptSpider（以下简称SS），做一个好用的爬虫框架。

目前的功能已经够大多数情况下使用，ScriptSpider会朝着易用、高度、最新技术的方向发展！

欢迎 **Star** 和 **Fork** 我的项目！


# 项目主页

* 国外：[github](https://github.com/xjtushilei/ScriptSpider)
* 国内：[coding.net](https://coding.net/u/xjtushilei/p/ScriptSpider/git)




# 特点
- Java开发（学习java的良方）
- 易理解（中文注释，多样例代码）
- 易用性（最短一行代码就可以开始爬虫）
- 代码少（已经默认实现了大部分功能）
- 基于Jsoup（个人化解析网页方便）
- 高度扩展性（热插拔组件，可定制每一个流程）
- 速度快（多线程爬虫，线程池管理，线程池下载，分布式）
- 分布式（基于redis，mq等，部署简单，速度很快）【部分待完成】
- 反爬虫（模拟浏览器，随机userAgent）【部分待完成】
- 代理（自动切换代理）【待完成】
- 监控（分布式监控）【待完成】
- 故障重爬（断电，宕机后继续爬取）【待完成】
- 未完待续

# 安装

### 使用maven


```xml
<dependency>
    <groupId>com.github.xjtushilei</groupId>
    <artifactId>scriptspider</artifactId>
    <version>0.2</version>
    <!--请尽量使用最新版本-->
</dependency>
```

关于版本

请尽量使用最新版本，[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cscriptspider),中央仓库搜索最新版本

因为文档都是根据最新版本来及时更新的。

### 离线使用jar包

在项目主页的 [releases目录](https://github.com/xjtushilei/ScriptSpider/releases)

在最新的release下面，下载相应的所有的依赖包集合zip：`dependency.zip`。

打开自己的工程，导入即可！


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


### 更多的示范

请移步[How to Start](https://github.com/xjtushilei/ScriptSpider/blob/master/HowToStart.md)


# 感受

1. 设计一个框架需要考虑的东西需要很多，自己能力有限，第一次设计，不妥之处欢迎大家提issue。
2. 多线程调bug好忧伤。
3. 开源项目，需要花费很多的精力，自己有时候也挺疯狂的，各种折腾。回首一看，还是挺开心的。
4. 如果你有兴趣，可以加入ScriptSpider，我们一起构建更美好的JAVA爬虫框架！

# 背景

因背景有失大雅，故放在后面。

无意之中看到了一个软件设计大赛，看到一个题目有兴趣，结果工作人员迟迟不给示例文件密码，破解失败，无奈就随手选了个题目，那就爬虫吧。

# 欢迎加入

联系[个人主页](http://xjtushilei.com/about/)的邮箱、QQ等即可。

# 版本更新记录

- V-0.2
    - 完成基本的说明文档和样例程序。修复已知bug。
- V-0.1.1 
    - 完成基于redis的分布式调度
- V-0.0.1 
    - 基本的爬虫功能