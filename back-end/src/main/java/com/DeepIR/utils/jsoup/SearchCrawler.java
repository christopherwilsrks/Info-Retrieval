package com.DeepIR.utils.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class SearchCrawler {

    static public void spider(String url, String output) throws IOException {

        int      i = 1;
        int      count;
        Document document;


        File file = new File("docs/input/" + File.separator + output);

        if (file.exists()) {
            BufferedReader br      = new BufferedReader(new FileReader("docs/input" + File.separator + output));
            String         strLine = "";
            String         tmp     = br.readLine();
            while (tmp != null) {
                strLine = tmp;
                tmp = br.readLine();
            }
            if (strLine.startsWith("##")) {
                i = Integer.parseInt(strLine.split("\t")[1]);
            } else if (strLine.startsWith("#")) {
                i = Integer.parseInt(strLine.split("\t")[1]) + 1;
            }
            br.close();
        }

        Connection con = Jsoup.connect(url);
        con.data("pageIndex", String.valueOf(1));
        con.data("searchType", "all");
        con.data("catalog", "0");
        con.data("advanced", "false");
        con.data("searchFilter", "1");
        con.data("orderType", "publishTime");
        con.data("columnPath", "false");
        con.data("keyword", " ");
        con.data("isShow", "1").timeout(30000);
        document = con.post();
        String result_count = document.getElementsByClass("items_num").get(0).text().trim();
        count = Integer.parseInt(result_count.substring(result_count.indexOf("约") + 1, result_count.indexOf("个"))) / 10 + 1;

        BufferedWriter bw = new BufferedWriter(new FileWriter("docs/input" + File.separator + output, true));

        for (; i <= count; i++) {

            con = Jsoup.connect(url);
            con.data("pageIndex", String.valueOf(i));
            con.data("searchType", "all");
            con.data("catalog", "0");
            con.data("advanced", "false");
            con.data("searchFilter", "1");
            con.data("orderType", "publishTime");
            con.data("columnPath", "false");
            con.data("keyword", " ");
            con.data("isShow", "1").timeout(15000);
            System.out.println("开始爬取第" + i + "页");
            long startTime = System.currentTimeMillis();

            try {
                document = con.post();
            } catch (Exception se) {
                System.err.println("爬取第" + i + "页异常");
                BufferedWriter _bw = new BufferedWriter(new FileWriter("docs\\log" + File.separator + url.split("\\.")[1] + ".log", true));
                _bw.write("PAGE:\t" + i + "\n" + se.getMessage() + "\n");
                _bw.flush();
                _bw.close();
                continue;
            }

            long endTime = System.currentTimeMillis();
            System.out.println("耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");

            Elements main = document.getElementsByClass("results_list").select(".result_item");
            Elements urls = main.select("a");

            for (Element item : main) {
                //输出href后的值，即主页上每个关注问题的链接
                String URL = item.getElementsByTag("a").get(0).attr("abs:href");
                //下载问题链接指向的页面
//            Document document2 = org.jsoup.Jsoup.connect(URL)
//                    .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
//                    .get();

                String title = item.getElementsByTag("h3").get(0).text();
                String meta_time = item.getElementsByClass("meta_time").get(0).text();
                meta_time = meta_time.substring(meta_time.indexOf("："), meta_time.length());
                bw.write("URL:" + '\t' + URL + '\n' + "title:" + '\t' + title + "\n" + "time:" + '\t' + meta_time + "\n");
            }
            bw.write("#" + '\t' + i + '\n');
            System.out.println("第" + i + "页爬取完成" + "\n");
            bw.flush();
        }
        bw.flush();
        bw.close();

    }

    public static void main(String[] args) throws IOException {
        spider("http://www.nankai.edu.cn/_web/search/doSearch.do?_p=YXM9MTMmdD02MyZkPTIyOCZwPTEmbT1TTiY_&locale=zh_CN&request_locale=zh_CN", "nankai.edu.cn.txt");
        spider("http://tas.nankai.edu.cn/_web/search/doSearch.do?_p=YXM9NTkmdD04OSZkPTI4NSZwPTEmbT1OJg__&locale=zh_CN&request_locale=zh_CN", "tas.nankai.edu.cn.txt");
        spider("http://law.nankai.edu.cn/_web/search/doSearch.do?_p=YXM9NzcmdD0xNzYmZD01NjUmcD0xJm09U04m&locale=zh_CN&request_locale=zh_CN", "law.nankai.edu.cn.txt");
        spider("http://physics.nankai.edu.cn/_web/search/doSearch.do?_p=YXM9MjEmdD0xMiZkPTMyJnA9MSZtPVNOJg__&locale=zh_CN&request_locale=zh_CN", "physics.nankai.edu.cn.txt");
        spider("http://sms.nankai.edu.cn/_web/search/doSearch.do?_p=YXM9OTEmdD0xOTAmZD01OTYmcD0xJm09TiY_&locale=zh_CN&request_locale=zh_CN", "sms.nankai.edu.cn.txt");
    }

}
