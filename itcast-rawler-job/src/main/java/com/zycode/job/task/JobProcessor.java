package com.zycode.job.task;

import com.zycode.job.pojo.JobInfo;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class JobProcessor implements PageProcessor {

    private String url = "https://search.51job.com/list/170600,000000,0000,00,9,99,%2B,2,1.html?lang=c&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&ord_field=0&dibiaoid=0&line=&welfare=";

    @Override
    public void process(Page page) {
        //解析页面，获取招聘信息详情的url地址
        List<Selectable> list = page.getHtml().css("div#resultList div.el").nodes();
        //判断获取到的集合是否为空，
        if(list.size()==0) {
            //如果为空，表示这是招聘详情页,解析页面，获取招聘详情，保存数据
            this.saveJobInfo(page);
        }else {
            //如果不为空，表示这是列表页,解析出详情页的url地址，放到队列中
            for (Selectable selectable : list) {
                //获取url地址
                String jobInfoUrl = selectable.links().toString();
                //把获取到的url地址放入任务队列中
                page.addTargetRequest(jobInfoUrl);
            }
            //获取下一页的url
            String bkUrl = page.getHtml().css("div.p_in li.bk").nodes().get(1).links().toString();
            //把url放入任务队列中
            page.addTargetRequest(bkUrl);
        }
        String html = page.getHtml().toString();
    }
    //解析页面，获取招聘详情,保存数据
    private void saveJobInfo(Page page) {
        //创建招聘详情对象
        JobInfo jobInfo = new JobInfo();

        //解析页面
        Html html = page.getHtml();

        //获取数据，封装到对象中
        String text = Jsoup.parse(html.css("p.msg").toString()).text();
        String companyAddr = text.substring(0,text.indexOf("|"));
        jobInfo.setCompanyName(html.css("div.cn p.cname a","text").toString());
        jobInfo.setCompanyAddr(companyAddr.trim());
        jobInfo.setCompanyInfo(Jsoup.parse(html.css("div.tmsg").toString()).text());
        jobInfo.setJobName(html.css("div.cn h1","text").toString());
        jobInfo.setJobAddr(Jsoup.parse(html.css("div.bmsg").nodes().get(1).toString()).text());
        jobInfo.setJobInfo(Jsoup.parse(html.css("div.job_msg").toString()).text());
        jobInfo.setUrl(page.getUrl().toString());
        jobInfo.setSalary(html.css("div.cn strong","text").toString());
//        jobInfo.setSalaryMin();
//        jobInfo.setSalaryMax();
//        String time = text.substring(text.lastIndexOf("|"));
        String[] strings = text.split("[|]");
        for (String string : strings) {
            if(string.contains("发布")){
                jobInfo.setTime(string.replace("发布",""));
//                System.out.println(string.charAt(0));
            }
        }
        //把结果保存起来
        page.putField("jobInfo",jobInfo);
    }

    private Site site = Site.me()
            .setCharset("gbk")//设置编码
            .setTimeOut(10*1000)//设置超时时间
            .setRetrySleepTime(3000)//设置重试的间隔时间
            .setRetryTimes(3)//设置重试次数
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");//设置浏览器代理

    @Override
    public Site getSite() {
        return site;
    }

    @Autowired
    private SpringDataPipeline springDataPipeline;

    //initialDelay：任务启动后等待多久启动方法
    //fixedDelay：每隔多久执行方法
    @Scheduled(initialDelay = 2000,fixedDelay = 10*1000)
    public void process(){
        Spider.create(new JobProcessor())
                .addUrl(url)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(10)
                .addPipeline(springDataPipeline)
                .run();

    }
}
