package com.inforetrieval.controller;

import com.inforetrieval.utils.Documents;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@CrossOrigin(value = "*", maxAge = 3600)
public class IndexController {

    @RequestMapping(value = "/indexing", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String indexing(HttpServletRequest request) throws IOException {

        HttpSession session   = request.getSession();
        String      sessionId = session.getId();

        Documents doc = new Documents("/home/tmp/" + sessionId);

        int processDocID = doc.processDocID();
        if (processDocID != 1) {
            return "文档读取失败";
        }

        int docReading = doc.docReading();
        if (docReading != 1) {
            return "创建索引失败";
        }

        return "成功创建索引";

    }

    @RequestMapping(value = "/login")
    @ResponseBody
    public void doclist(HttpServletRequest request) {

        HttpSession session   = request.getSession();
        String      sessionId = session.getId();

        System.out.println(sessionId);

    }

}
