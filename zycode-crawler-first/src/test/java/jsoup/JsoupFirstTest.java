package jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.net.URL;

public class JsoupFirstTest {
    @Test
    public void testUrl() throws Exception{
        //解析url地址，第一个参数是访问的url，第二个参数，是我们访问时候的超时时间
        Document doc = Jsoup.parse(new URL("http://www.itcast.cn"), 2000);
        //使用标签选择器，获取title标签中的内容
        String title = doc.getElementsByTag("title").first().text();
        //print
        System.out.println(title);

    }
}
