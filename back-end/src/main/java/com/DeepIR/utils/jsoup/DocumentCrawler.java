package com.DeepIR.utils.jsoup;

import com.DeepIR.utils.Lucene.CoreNLPAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;

public class DocumentCrawler {

    private String url;
    private String content;
    private String title;
    private String website;
    private String time;

    private static String dirPath;
    private static String indexPath;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void getfile() throws IOException {

        File   fileList = new File(dirPath);
        File[] files    = fileList.listFiles();

        for (File file : files) {
            String filepath = file.getPath();
            website = file.getName().split("\\.")[0];
            indexPath = "docs/res_index/" + website + "_index";
            processFile(file);
        }

    }

    public void processFile(File file) throws IOException {

        String filepath = file.getPath();
        website = file.getName().split("\\.")[0];

        BufferedReader br      = new BufferedReader(new FileReader(filepath));
        String         strLine = br.readLine();

        while (strLine != null) {

            try {

                if (strLine.startsWith("#")) {
                    strLine = br.readLine();
                    continue;
                }

                url = strLine.split("\t")[1];
                strLine = br.readLine();
                title = strLine.split("\t")[1];
                strLine = br.readLine();
                time = strLine.split("：")[1];

                if (!url.endsWith("htm") && !url.endsWith("shtml") && !url.endsWith("html")) {
                    strLine = br.readLine();
                    continue;
                }

                content = law_spider(url);
                if (content.equals("-1")) {
                    strLine = br.readLine();
                    continue;
                }

                ExecutorService executor = Executors.newSingleThreadExecutor();
                FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        return writeIndex();
                    }
                });

                System.out.println("开始写入索引：" + title);
                long startTime = System.currentTimeMillis();
                executor.execute(futureTask);
                try {
                    futureTask.get(30000, TimeUnit.MILLISECONDS);
                    long endTime = System.currentTimeMillis();
                    System.out.println("耗时：" + (endTime - startTime) * 1.0 / 1000 + "s" + "\n");
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    //e.printStackTrace();
                    System.err.println("超时错误\n");

//                    BufferedWriter bw = new BufferedWriter(new FileWriter("docs\\log" + File.separator + website + ".log", true));
//                    bw.write("URL:\t" + url + "\nTITLE:\t" + title + "\n" + "ERROR:\t" + e.getMessage() + "\n");
//                    bw.flush();
//                    bw.close();
                    futureTask.cancel(true);
                }
            } catch (Exception e) {
                strLine = br.readLine();
                BufferedWriter bw = new BufferedWriter(new FileWriter("docs\\log" + File.separator + website + ".log", true));
                bw.write("URL:\t" + url + "\nTITLE:\t" + title + "\n" + e.getMessage() + "\n");
                bw.flush();
                bw.close();
                continue;
            }

            strLine = br.readLine();
        }
    }

    public String law_spider(String url) throws IOException {

        Connection con      = Jsoup.connect(url);

        Document   document = null;
        try {
            document = con.userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)").timeout(20000).get();
        } catch (SocketTimeoutException e) {
            System.err.println("访问超时");
            BufferedWriter bw = new BufferedWriter(new FileWriter("docs\\log" + File.separator + website + ".log", true));
            bw.write("URL:\t" + url + "\nTITLE:\t" + title + "\n" + e.getMessage() + "\n");
            bw.flush();
            bw.close();
            return "-1";
        }

        Elements selects = document.getElementsByClass("wp_articlecontent");

        String content = "";
        for (Element p : selects) {
            content += p.text();
        }

        if (content.replaceAll("\\u00A0+", "").trim().equals("")) {
            return "";
        }

        return content;

    }

    public String writeIndex() throws IOException {

        File indexdir = new File(indexPath);

        if (!indexdir.exists()) {
            indexdir.mkdir();
        }

        //指定索引库的存放位置Directory对象
        Directory directory = FSDirectory.open(indexdir);
        //索引库还可以存放到内存中
        //Directory directory = new RAMDirectory();

        //指定一个标准分析器，对文档内容进行分析
        Analyzer analyzer = new CoreNLPAnalyzer();

        //创建indexwriterCofig对象
        //第一个参数： Lucene的版本信息，可以选择对应的lucene版本也可以使用LATEST
        //第二根参数：分析器对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

        //创建一个indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory, config);


        //创建document对象
        org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();



        //url域
        Field urlField = new StringField("url", url, Field.Store.YES);

        //文件内容域
        Field contentField = null;
        //避免content为空的情况
        if (!content.equals("")) {
            contentField = new TextField("content", content, Field.Store.YES);
        }

        //日期域
        StringField dateField = new StringField("time", time, Field.Store.YES);

        //标题域
        TextField titleField = new TextField("title", title, Field.Store.YES);

        //来源
        Field websiteField = new StringField("website", this.website, Field.Store.YES);

        document.add(urlField);
        if (contentField != null) {
            document.add(contentField);
        }
        document.add(dateField);
        document.add(titleField);
        document.add(websiteField);
        //使用indexwriter对象将document对象写入索引库，此过程进行索引创建。并将索引和document对象写入索引库。
        indexWriter.addDocument(document);
        //关闭IndexWriter对象。
        indexWriter.close();
        System.out.println("成功写入索引：" + title);
        return "a";
    }

    public static void main(String[] args) throws IOException {
        DocumentCrawler documentCrawler = new DocumentCrawler();
        dirPath = "docs/input";
        documentCrawler.getfile();
//        documentCrawler.processFile("C:\\tmp\\input\\law.nankai.edu.cn.txt");
        System.exit(0);
    }

}
