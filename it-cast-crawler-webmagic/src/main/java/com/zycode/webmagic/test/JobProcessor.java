package com.zycode.webmagic.test;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

public class JobProcessor implements PageProcessor {

    /**
     * 负责解析页面
     * @param page page 发起请求组获取到的数据
     */
    @Override
    public void process(Page page) {
        //解析返回数据Page，并把解析的结果放到ResultItem中
        page.putField("title",page.getHtml().css("title"));
    }

    private Site site = Site.me()
            .setCharset("utf8") //设置编码格式
            .setTimeOut(10*1000) //设置超时时间，单位是ms
            .setRetrySleepTime(3000) //设置重试的间隔时间
            .setSleepTime(3) //设置重试次数
            ;
    @Override
    public Site getSite() {
        return site;
    }
//主函数，执行爬虫
    public static void main(String[] args) {
        Spider spider = Spider.create(new JobProcessor())
                .addUrl("https://www.jd.com/")//设置爬取数据的页面
                .thread(5)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)));

        Scheduler scheduler = spider.getScheduler();

        //执行爬虫
        spider.run();
    }
}
