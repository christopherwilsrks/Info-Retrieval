package com.DeepIR.controller;

import com.DeepIR.domain.po.Result;
import com.DeepIR.domain.po.TotalResults;
import com.DeepIR.utils.Lucene.LuceneQuery;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/3")
public class SearchController {

    @RequestMapping("/search")
    @ResponseBody
    public TotalResults search(String query, int pageNow) throws IOException, ParseException, InvalidTokenOffsetsException {

        if (query.equals("")) {
            return null;
        }
        List<Result> results = new ArrayList<>();

        int total = new LuceneQuery(pageNow).getBoolQuery(query, results);

        TotalResults totalResults = new TotalResults(total, results);

        return totalResults;

    }

    @RequestMapping("/fetch")
    @ResponseBody
    public List<String> fetch(String query) throws IOException {

        return new LuceneQuery(0).queryTitle(query);

    }

    @RequestMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        String dir  = "/home/Nookle/download/README.zip";
        File   file = new File(dir);

        if (file.exists()) {
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            try {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-disposition", "attachment; filename=" + new String("README.zip".getBytes("utf-8"), "ISO8859-1")); // 指定下载的文件名
                os.write(FileUtils.readFileToByteArray(file));
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    os.close();
                }
            }
        }
    }


}
