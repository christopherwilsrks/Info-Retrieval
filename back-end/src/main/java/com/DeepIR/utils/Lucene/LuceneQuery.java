package com.DeepIR.utils.Lucene;

import com.DeepIR.domain.po.Result;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class LuceneQuery {

    private File             dir      = new File("C:\\Users\\christ\\Documents\\Homework\\DeepIR\\docs\\index");
    private int              pageNow;
    private int              pageSize = 10;

    public LuceneQuery(int pageNow) {
        this.pageNow = pageNow;
    }

    public Query getQueryParser(String token) throws ParseException {
        String[]              fields    = {"title", "content"};
        MultiFieldQueryParser mp        = new MultiFieldQueryParser(Version.LATEST, fields, new CoreNLPAnalyzer());
        long                  startTime = System.currentTimeMillis();
        Query                 query     = mp.parse(token);
        System.out.println("parse耗时：" + (System.currentTimeMillis() - startTime) * 1.0 / 1000 + "s" + "\n");
        return query;
    }

    public int getBoolQuery(String token, List<Result> resultList) throws ParseException, IOException, InvalidTokenOffsetsException {

//        BooleanQuery booleanQuery = new BooleanQuery();
//
//        QueryParser parser1    = new QueryParser(Version.LATEST, "title", new CoreNLPAnalyzer());
//        Query       titleQuery = parser1.parse(token);
//        booleanQuery.add(titleQuery, BooleanClause.Occur.SHOULD);
//
//        QueryParser parser2      = new QueryParser(Version.LATEST, "content", new CoreNLPAnalyzer());
//        Query       contentQuery = parser2.parse(token);
//        booleanQuery.add(contentQuery, BooleanClause.Occur.SHOULD);


        String[]              fields = {"title", "content"};
        MultiFieldQueryParser mp     = new MultiFieldQueryParser(Version.LATEST, fields, new CoreNLPAnalyzer());
        Query                 query  = mp.parse(token);

        return execQuery(query, resultList);

    }

    public int execQuery(Query query, List<Result> resultList) throws IOException, InvalidTokenOffsetsException {

        Directory     open     = FSDirectory.open(dir);
        IndexReader   reader   = DirectoryReader.open(open);
        IndexSearcher searcher = new IndexSearcher(reader);

        DuplicateFilter filter  = new DuplicateFilter("title");
        TopDocs         topDocs = searcher.search(query, filter, pageSize * pageNow);

        System.out.println("查询结果的总条数：" + topDocs.totalHits);

        int start = (pageNow - 1) * pageSize;
        int end   = pageSize * pageNow;

        ScoreDoc[] scores = topDocs.scoreDocs;

        QueryScorer         qs = new QueryScorer(query);
        SimpleHTMLFormatter sh = new SimpleHTMLFormatter("<span style=\"color:#dd4b39;\">", "</span>");

        SimpleSpanFragmenter fragmenter  = new SimpleSpanFragmenter(qs);
        Highlighter          highlighter = new Highlighter(sh, qs);
        highlighter.setTextFragmenter(fragmenter);


        for (int i = start; i < end && i < scores.length; i++) {
            Document document = reader.document(scores[i].doc);
            String   content  = document.get("content");
            String   title    = document.get("title");
            TokenStream ts;
            if (content != null) {
                ts = new CoreNLPAnalyzer().tokenStream("content", new StringReader(content));
                content = highlighter.getBestFragment(ts, content);
            }
            if (title != null) {
                ts = new CoreNLPAnalyzer().tokenStream("title", new StringReader(title));
                title = highlighter.getBestFragment(ts, title);
            }
            String time = document.get("time");
            time = time.substring(0, 10).replaceFirst("-", "年").replaceFirst("-", "月") + "日";
            resultList.add(new Result(document.get("url"), title, content, time, document.get("website")));
        }

//        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
//            Document document = searcher.doc(scoreDoc.doc);
//
//            resultList.add(new Result(document.get("url"), document.get("title"), document.get("content")));
//
//            System.out.println("url: " + document.get("url"));
//            System.out.println("title: " + document.get("title"));
//            System.out.println("website: " + document.get("website"));
//            System.out.println("----------------------------------\n");
//        }
        reader.close();

        return topDocs.totalHits;
    }

    public List<String> queryTitle(String word) throws IOException {
        String        text     = word + "*";
        WildcardQuery query    = new WildcardQuery(new Term("title", text));
        IndexSearcher searcher = getIndexSearch();
        TopDocs       topDocs  = searcher.search(query, 6);
        ScoreDoc[]    scores   = topDocs.scoreDocs;

        List<String> results = new ArrayList<>();

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);

            results.add(document.get("title"));

        }
        return results;
    }

    public IndexSearcher getIndexSearch() throws IOException {
        Directory     open          = FSDirectory.open(dir);
        IndexReader   reader        = DirectoryReader.open(open);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        return indexSearcher;
    }

}
