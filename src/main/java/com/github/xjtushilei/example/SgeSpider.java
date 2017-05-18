package com.github.xjtushilei.example;

import com.github.xjtushilei.core.Spider;
import com.github.xjtushilei.core.pageprocesser.PageProcessor;
import com.github.xjtushilei.core.saver.Saver;
import com.github.xjtushilei.dao.KLineDao;
import com.github.xjtushilei.model.KLine;
import com.github.xjtushilei.model.Page;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Created by shilei on 2017/4/11.
 */
public class SgeSpider {


    public static void main(String[] args) {
        Spider spider = Spider.build().setProcessor(myPageProcessor);

        for(int i=1;i<=213;i++) {
            spider.addUrlSeed("http://www.sge.com.cn/sjzx/mrhqsj?p="+i);
        }

        spider.addRegexRule("+http://www.sge.com.cn/sjzx/mrhqsj/\\d*\\?top=\\d*")
                .setSaver(mySaver)
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
            if (!Pattern.matches("http://www.sge.com.cn/sjzx/mrhqsj/\\d*\\?top=\\d*", page.getUrlSeed().getUrl())) {
                return page;
            }

            Document htmldoc = page.getDocument();
            //select返回的是一个数组，所以需要first，相关语法请google“jsoup select语法”和“cssquery”
            try {
                String title = htmldoc.title();
                String text = htmldoc.text();
                Elements tDate = htmldoc.getElementsMatchingText("时间:");
                String tmp = tDate.text();
                int index = tmp.indexOf("时间:");
                String time = tmp.substring(index+3,index+13) + " 15:30:00";
                System.out.println("===>"+time);
                Elements es = htmldoc.getElementsByTag("table");
                Element e = es.get(0);
                Elements trs = e.getElementsByTag("tr");
                List<KLine> lineList = new ArrayList<>();
                for(Element tr:trs) {
                    Elements tds = tr.getElementsByTag("td");
                    String tdText = tds.text();
                    System.out.println("=====>" + tdText);
                    String[] texts = tdText.split(" ");
                    KLine line = new KLine();
                    try {
                        line.setOpen(Double.valueOf(texts[1].replaceAll(",","")));
                        line.setHigh(Double.valueOf(texts[2].replaceAll(",","")));
                        line.setLow(Double.valueOf(texts[3].replaceAll(",","")));
                        line.setClose(Double.valueOf(texts[4].replaceAll(",","")));
                        line.setAvg_price(Double.valueOf(texts[7].replaceAll(",","")));
                        line.setVolume(Double.valueOf(texts[8].replaceAll(",","")));
                        line.setStatus(1);
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime endTime = LocalDateTime.parse(time,df);
                        line.setStartTime(getStatTime(endTime));
                        line.setEndTime(endTime);
                        line.setCreateTime(LocalDateTime.now());
                        line.setLevel("1440");

                        if(!text.contains("涨跌幅")) {
                            line.setAvg_price(Double.valueOf(texts[6].replaceAll(",","")));
                            line.setVolume(Double.valueOf(texts[7].replaceAll(",","")));
                        }

                        if (tdText.contains("mAuT+D ") || tdText.contains("mAu(T+D) ") || tdText.contains("mAu（T+D) ")) {
                            line.setCode("MAUT+D");
                            line.setVolume(line.getVolume()*10);
                            lineList.add(line);
                            continue;
                        }
                        if (tdText.contains("AuT+D ") || tdText.contains("Au(T+D) ") || tdText.contains("Au（T+D) ")) {
                            String direction = texts[11];
                            if(!text.contains("涨跌幅")) {
                                direction = texts[10];
                            }
                            line.setDirection(getDirection(text,direction));
                            line.setCode("AUT+D");
                            lineList.add(line);
                            continue;
                        }
                        if (tdText.indexOf("AgT+D")!=-1 || tdText.indexOf("Ag(T+D)")!=-1 || tdText.indexOf("Ag（T+D)")!=-1) {
                            String direction = texts[11];
                            if(!text.contains("涨跌幅")) {
                                direction = texts[10];
                            }
                            line.setDirection(getDirection(text,direction));
                            line.setCode("AGT+D");
                            lineList.add(line);
                            continue;
                        }
                    }catch (NumberFormatException ex) {
                        continue;
                    }catch (ArrayIndexOutOfBoundsException ex){//针对2008年的特殊页面处理，不够11列
                        line.setAvg_price(Double.valueOf(texts[6].replaceAll(",","")));
                        line.setVolume(Double.valueOf(texts[7].replaceAll(",","")));
                        if (tdText.contains("mAuT+D ") || tdText.contains("mAu(T+D) ") || tdText.contains("mAu（T+D) ")) {
                            line.setCode("MAUT+D");
                            line.setVolume(line.getVolume()*10);
                            lineList.add(line);
                            continue;
                        }
                        if (tdText.contains("AuT+D ") || tdText.contains("Au(T+D) ") || tdText.contains("Au（T+D) ")) {
                            line.setCode("AUT+D");
                            lineList.add(line);
                            continue;
                        }
                        if (tdText.indexOf("AgT+D")!=-1 || tdText.indexOf("Ag(T+D)")!=-1 || tdText.indexOf("Ag（T+D)")!=-1) {
                            line.setCode("AGT+D");
                            lineList.add(line);
                            continue;
                        }
                    }
                }
                System.out.println(lineList.toString()+"size:" + lineList.size());

                //用来存放爬取到的信息，供之后存储！map类型的即可，可以自定义各种嵌套！
                Map<String, Object> items = new HashMap<String, Object>();
                items.put("title", title);
                items.put("text", text);
                items.put("url", page.getUrlSeed().getUrl());
                items.put("lines",lineList);

                //放入items中，之后会自动保存（如果你自己实现了下载器，请自己操作它。如下我自定义了自己的下载器，并将它保存到了文本中！）！
                page.setItems(items);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("没有解析到相关东西！跳过");
            }

            return page;
        }

        private LocalDateTime getStatTime(LocalDateTime endTime) {
            LocalDateTime startTime = endTime.minusDays(1).withHour(20).withMinute(0);
            if(startTime.getDayOfWeek().getValue() == 7) {
                startTime = startTime.minusDays(2);
            }
            return startTime;
        }

        private String getDirection(String text, String direction) {
            if(!direction.contains("付")){
                int in = text.indexOf("Au(T+D)--");
                if(in != -1) {
                    direction = text.substring(in + 9, in + 12);

                }else{
                    direction = "";
                }
            }
            direction = direction.replaceAll("支付","付")
                                 .replaceAll("付给","付")
                                 .replaceAll("支付给","付");
            return direction;
        }

        /**
         * 如果你自己想处理url(之前已经经过了正则过滤,或者初始化的时候不添加正则信息 !)，这里可以自己增加自己的方法（除了正则，因为已经默认实现了正则，除非你想在这里再次实现也没有关系）！
         * @param page
         * @return 自己
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
            String sql = "insert t_sge_1440 (open,high,low,close,volume,level,start_time,end_time,code,status,create_time,direction,avg_price) values(?,?,?,?,?,?,?,?,?,1,CURRENT_TIMESTAMP,?,?)";
            Map<Object,Object> map = page.getItems();
            List<KLine> lines = (List<KLine>)map.get("lines");

            KLineDao lineDao = KLineDao.getInstance();
            if(lines != null) {
                System.out.println("size=========>" + lines.size());
                lineDao.insert(sql, lines);
            }
            return page;
        }
    };
}
