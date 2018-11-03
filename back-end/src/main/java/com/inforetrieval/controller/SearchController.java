package com.inforetrieval.controller;

import com.inforetrieval.domain.DocList;
import com.inforetrieval.utils.Search;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin
public class SearchController {

    @RequestMapping(value = "/search")
    @ResponseBody
    public List<DocList> search(String word) {

        String path = "/home/Bool/tmp/";

//        String path = "C:/tmp";

        try {
            Search        search = new Search(word, path);
            List<DocList> docLists;
            if (word.contains("*")) {
                docLists = search.k_gram_search();
            } else {
                docLists = search.searchProcess();
            }
            return docLists;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
