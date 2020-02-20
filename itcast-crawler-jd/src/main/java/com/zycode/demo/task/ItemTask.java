package com.zycode.demo.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zycode.demo.pojo.Item;
import com.zycode.demo.service.ItemService;
import com.zycode.demo.utils.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ItemTask {

    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private ItemService itemService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    //当下载任务完成后，间隔多长时间进行下一次的任务
    @Scheduled(fixedDelay = 100 * 1000)
    public void itemTask() throws Exception {
        //声明需要解析的初始地址
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=%E6%89%8B%E6%9C%BA&s=105&click=0&page=";
        //按照页码对手机的搜索结果进行遍历解析
        for (int i = 1; i < 10; i += 2) {
            String html = httpUtils.doGetHtml(url + i);
            //解析页面，获取商品数据并存储
            this.parse(html);
        }
        System.out.println("手机数据抓取完成!");
    }

    private void parse(String html) throws JsonProcessingException {
        //解析html获取DOM对象
        Document doc = Jsoup.parse(html);
        //获取spu
        Elements spuEles = doc.select("div#J_goodsList > ul > li");
        for (Element spuEle : spuEles) {
            long spu = Long.parseLong(spuEle.attr("data-spu"));
            //获取sku
            Elements skuEles = spuEle.select("li.ps-item");
            for (Element skuEle : skuEles) {
                long sku = Long.parseLong(skuEle.select("[data-sku]").attr("data-sku"));

                //根据sku查询商品数据
                Item item = new Item();
                item.setSku(sku);
                List<Item> list = this.itemService.findAll(item);

                if (list.size() > 0) {
                    //如果商品存在，就进行下一个循环，商品不保存
                    continue;
                }
                //设置商品的spu
                item.setSpu(spu);

                //获取商品详情的url
                String itemUrl = "http://item.jd.com/" + sku + ".html";
                item.setUrl(itemUrl);

                //获取商品图片
                String picUrl ="https:" + skuEle.select("img[data-sku]").first().attr("data-lazy-img");
                picUrl = picUrl.replace("/n9/","/n1/");
                String picName = this.httpUtils.doGetImage(picUrl);
                item.setPic(picName);

                //获取商品价格
                String priceJson = this.httpUtils.doGetHtml("http://p.3.cn/prices/mgets?skuIds=J_" + sku);
                double price = MAPPER.readTree(priceJson).get(0).get("p").asDouble();
                item.setPrice(price);

                //获取商品标题
                String itemInfo = this.httpUtils.doGetHtml(item.getUrl());
                String title = Jsoup.parse(itemInfo).select("div.sku-name").text();
                item.setTitle(title);

                //创建时间
                item.setCreated(new Date());


                //更新时间
                item.setUpdated(item.getCreated());

                //保存商品数据到数据库中
                this.itemService.save(item);
            }
        }
    }
}
