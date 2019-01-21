package com.DeepIR;

import com.DeepIR.domain.po.Result;
import com.DeepIR.utils.Lucene.LuceneQuery;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneTest {

    static File dir;

    @Test
    public void test() throws Exception {

        ArrayList<Result> results = new ArrayList<>();

        int query = new LuceneQuery(1).getBoolQuery("张守文教授做客我院主讲“改革开放与中国经济法的四十年”", results);
        System.out.println(query);

//        long startTime = System.currentTimeMillis();
//        dir = new File("docs/index");
//        queryParse("嘉园停业公告");
//        System.out.println("总耗时：" + (System.currentTimeMillis() - startTime) * 1.0 / 1000 + "s" + "\n");
//        File dir = new File("docs/res_index/");
//        for (File file:dir.listFiles()) {
//            mergeIndex(file, new File("docs/index/"), new CoreNLPAnalyzer());
//        }
    }

    public List<String> tokenize(String content) {
        String     props    ="StanfordCoreNLP-Chinese.properties";
        AnnotationPipeline pipeline = new StanfordCoreNLP(props);
        Annotation         annotation;
        annotation = new Annotation(content);


        pipeline.annotate(annotation);

        List<CoreLabel> word = annotation.get(CoreAnnotations.TokensAnnotation.class);
        List<String> query = new ArrayList<>();
        for (CoreLabel label:word) {
            query.add(label.get(CoreAnnotations.TextAnnotation.class));
        }
        return query;
    }

    public void mergeIndex(File from, File to,Analyzer analyzer) throws IOException {

        Directory directory = FSDirectory.open(to);

        IndexWriter indexWriter = null;
        try {
            System.out.println("正在合并索引文件!\t ");
            IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
            indexWriter = new IndexWriter(directory, config);
            indexWriter.addIndexes(FSDirectory.open(from));
            indexWriter.close();
            System.out.println("已完成合并!\t ");
        } catch (Exception e) {
            System.out.println("合并索引出错！");
            e.printStackTrace();
        } finally {
            try {
                if (indexWriter != null)
                    indexWriter.close();
            } catch (Exception e) {

            }

        }

    }

    public void writeIndex() throws IOException {

        //指定索引库的存放位置Directory对象
        Directory directory = FSDirectory.open(new File("C:\\tmp\\test"));
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

        //原始文档的路径
        File   file     = new File("C:\\tmp\\t");
        File[] fileList = file.listFiles();
        for (File file2 : fileList) {
            //创建document对象
            Document document = new Document();

            //创建field对象，将field添加到document对象中

            //文件名称
            String fileName = file2.getName();
            //创建文件名域
            //第一个参数：域的名称
            //第二个参数：域的内容
            //第三个参数：是否存储
            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);

            //文件的大小
            long fileSize = FileUtils.sizeOf(file2);
            //文件大小域
            Field fileSizeField = new LongField("fileSize", fileSize, Field.Store.YES);

            //文件路径
            String filePath = file2.getPath();
            //文件路径域（不分析、不索引、只存储）
            Field filePathField = new StoredField("filePath", filePath);

            //文件内容
//            String fileContent = FileUtils.readFileToString(file2);
            String fileContent = FileUtils.readFileToString(file2, "utf-8");
            //文件内容域
            Field fileContentField = new TextField("fileContent", fileContent, Field.Store.YES);

            document.add(fileNameField);
            document.add(fileSizeField);
            document.add(filePathField);
            document.add(fileContentField);
            //使用indexwriter对象将document对象写入索引库，此过程进行索引创建。并将索引和document对象写入索引库。
            indexWriter.addDocument(document);
        }
        //关闭IndexWriter对象。
        indexWriter.close();

    }

    public void queryAllDocs(File dir) throws Exception {
        //创建一个Directory对象，指定索引库存放的路径
        Directory directory = FSDirectory.open(dir);
        //创建IndexReader对象，需要指定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建Indexsearcher对象，需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //创建查询条件
        //使用MatchAllDocsQuery查询索引目录中的所有文档
        Query query = new MatchAllDocsQuery();
        //执行查询
        //第一个参数是查询对象，第二个参数是查询结果返回的最大值
        DuplicateFilter filter  = new DuplicateFilter("url");
        TopDocs         topDocs = indexSearcher.search(query, filter, 10);

        //查询结果的总条数
        System.out.println("查询结果的总条数："+ topDocs.totalHits);
        //遍历查询结果
        //topDocs.scoreDocs存储了document对象的id
        //ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //scoreDoc.doc属性就是document对象的id
            //int doc = scoreDoc.doc;
            //根据document的id找到document对象
            Document document = indexSearcher.doc(scoreDoc.doc);

            System.out.println("url: " + document.get("url"));
            System.out.println("title: " + document.get("title"));
            System.out.println("web: " + document.get("website"));
            System.out.println("----------------------------------");
        }
        //关闭indexreader对象
        indexReader.close();
    }

    public void queryParse(String token) throws IOException, ParseException {
        String[] fields = {"title", "content"};
        MultiFieldQueryParser mp = new MultiFieldQueryParser(Version.LATEST, fields, new CoreNLPAnalyzer());
        long startTime = System.currentTimeMillis();
        Query parse = mp.parse(token);
        System.out.println("parse耗时：" + (System.currentTimeMillis() - startTime) * 1.0 / 1000 + "s" + "\n");
        execQuery(parse);
    }

    public void execQuery(Query query) throws IOException {
        IndexReader reader = getIndexReader();

        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs topDocs = searcher.search(query, 10);

        System.out.println("查询结果的总条数："+ topDocs.totalHits);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);

            System.out.println("url: " + document.get("url"));
            System.out.println("title: " + document.get("title"));
            System.out.println("website: " + document.get("website"));
            System.out.println("----------------------------------\n");
        }
        reader.close();
    }

    public IndexReader getIndexReader() throws IOException {
        Directory open = FSDirectory.open(dir);
        return IndexReader.open(open);
    }

}
