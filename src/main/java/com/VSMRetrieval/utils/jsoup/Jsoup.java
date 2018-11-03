package com.VSMRetrieval.utils.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Jsoup {

    public void spiderBBC() throws IOException {

        int count = 0;
        for (int i = 1; i < 10; i++) {

            Document document = null;

            if (i == 1) {
                document = org.jsoup.Jsoup.connect("http://www.en8848.com.cn/tingli/news/bbc2016")
                        //模拟火狐浏览器
                        .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                        .get();
            }
            else {

                document = org.jsoup.Jsoup.connect("http://www.en8848.com.cn/tingli/news/bbc2016/index_" + i + ".html")
                        //模拟火狐浏览器
                        .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                        .get();
            }
            Elements main = document.getElementsByClass("ch_lii_left");
            Elements url  = main.select("a");


            for (Element question : url) {
                //输出href后的值，即主页上每个关注问题的链接
                String URL = question.attr("abs:href");
                //下载问题链接指向的页面
                Document document2 = org.jsoup.Jsoup.connect(URL)
                        .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                        .get();
                //问题
                Elements title = document2.getElementById("articlebody").getAllElements();

                FileWriter     fr = new FileWriter("C:\\Users\\18711\\Desktop\\news\\" + count++ + ".txt");
                BufferedWriter bw = new BufferedWriter(fr);

                for (Element p : title) {

                    try {
                        if (p.child(0) != null) {
                            if (p.child(0).tagName() == "img") {
                                continue;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        if (p.text() == "&nbsp;") {
                            continue;
                        }
                        String strLine = p.text();
                        strLine += "\n";
                        bw.write(strLine);
                    }

                }

                bw.flush();
                bw.close();
                fr.close();

            }
        }
    }

}
