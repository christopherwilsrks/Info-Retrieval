package com.VSMRetrieval.controller;


import com.VSMRetrieval.utils.vsm.MakeIndex;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@CrossOrigin
public class IndexController {

    @RequestMapping("/buildindex")
    @ResponseBody
    public String buildindex() {

        try {
            new MakeIndex("/home/VSM/news", "/home/VSM/bin").process();
            return "{\"message\": \"success!\"}";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{\"message\": \"failed!\"}";
    }

    @RequestMapping("/download")
    @ResponseBody
    public void download(HttpServletResponse response) throws IOException {

        String dir  = "/home/VSM/download/README.zip";
        File   file = new File(dir);

//        CompactAlgorithm compactAlgorithm = new CompactAlgorithm(new File("C:/tmp/download/Readme.zip"));
//        compactAlgorithm.zipFiles(file);

        if (file.exists()) {
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            try {
                response.setContentType("application/octet-stream");
//                if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {//IE浏览器
//
//                } else {
//                    fileName = URLDecoder.decode(fileName + ".xls");//其他浏览器
//                     	}
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
