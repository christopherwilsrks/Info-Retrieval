package com.VSMRetrieval.controller;

import com.VSMRetrieval.utils.nlp.StanfordNLP;
import com.VSMRetrieval.utils.vsm.LoadIndex;
import com.VSMRetrieval.utils.vsm.Query;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Controller
@CrossOrigin
public class SearchController {

    @RequestMapping(value = "/search")
    @ResponseBody
    public List<KeyValuePair> search(String s) {

        Query query = new Query(s, "/home/VSM/news", "/home/VSM/bin");
        List<Map.Entry<String, Double>> list = query.process();

        List<KeyValuePair> keyValuePairs = new ArrayList<>();

        for (Map.Entry<String, Double> entry:list) {

            KeyValuePair keyValuePair = new KeyValuePair(entry.getKey(), String.valueOf(entry.getValue()));
            keyValuePairs.add(keyValuePair);

        }

        return keyValuePairs;

    }

    @RequestMapping("/searchDoc")
    @ResponseBody
    public String searchDoc(String docName, String s) throws IOException {

        TreeMap<String, String> mapDocs = new LoadIndex("/home/VSM/news", "/home/VSM/bin").getMapDocs();

        String[] tokens = s.split("\\W");

        BufferedReader bw = new BufferedReader(new FileReader(mapDocs.get(docName)));

        String strLine = bw.readLine();
        String passage = "";

        while(strLine != null) {

            for (String token: tokens) {

                if (strLine.toLowerCase().contains(token)) {
                    strLine = strLine.replaceAll("(?i)" + token, "<font color=\"red\"><i>" + token.toUpperCase() + "</i></font>");
                }

            }

            passage += "<p>" + strLine + "</p>\n";
            strLine = bw.readLine();

        }

        return passage;
    }

}
