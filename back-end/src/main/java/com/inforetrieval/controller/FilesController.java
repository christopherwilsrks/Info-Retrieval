package com.inforetrieval.controller;

import com.inforetrieval.domain.FileList;
import com.inforetrieval.utils.CompactAlgorithm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/files")
public class FilesController {

    /*@RequestMapping(value = "/list")
    @ResponseBody
    public List<FileList> list(HttpServletRequest request) {

        HttpSession session   = request.getSession();
        String      sessionId = session.getId();

        String dir = "/home/tmp/" + sessionId;

        File file = new File(dir);
        if (!file.exists()) {
            return null;
        }
        File[] listFiles = file.listFiles();

        List<FileList> fileList = new ArrayList<FileList>();


        int i = 0, j = 0;
        for (; i < listFiles.length; i++) {
            if (listFiles[i].isFile()) {
                fileList.add(new FileList(listFiles[i].getName()));
            }
        }

        return fileList;

    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String files(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) {

        HttpSession session   = request.getSession();
        String      sessionId = session.getId();

        if (files == null) {

            return "fail to upload.";

        }

        for (MultipartFile file : files) {

            try {
                // 文件存放服务端的位置
                String rootPath = "/home/tmp";
                File   dir      = new File(rootPath + File.separator + sessionId);
                if (!dir.exists())
                    dir.mkdirs();
                // 写文件到服务器
                File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
                file.transferTo(serverFile);
            } catch (Exception e) {
                return "You failed to upload " + file.getOriginalFilename() + " => " + e.getMessage();
            }

        }
        return "You successfully uploaded file";
    }*/

    @RequestMapping(value = "/download")
    public void download(HttpServletResponse response) throws IOException {

        String dir  = "/home/Bool/download/README.zip";
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

    @RequestMapping(value = "/downloadindex")
    @ResponseBody
    public void downloadindex(HttpServletResponse response) throws IOException {

        String dir = "/home/Bool/download/INDEX.zip";
        File   file = new File(dir);

        if (file.exists()) {
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            try {
                response.setContentType("application/octet-stream");
//                if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {//IE浏览器
//
//                } else {
//                    fileName = URLDecoder.decode(fileName + ".xls");//其他浏览器
//                     	}
                response.setHeader("Content-disposition", "attachment; filename=" + new String("INDEX_INFO.zip".getBytes("utf-8"), "ISO8859-1")); // 指定下载的文件名
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

    @RequestMapping("/compact")
    @ResponseBody
    public void compact() {

        String dir = "/home/Bool/download/INDEX.zip";
        File   file = new File(dir);

        CompactAlgorithm compactAlgorithm = new CompactAlgorithm(file);
        compactAlgorithm.zipFiles(new File("/home/Bool/bin"));

    }

    /*@RequestMapping(value = "/remove")
    @ResponseBody
    public String remove(String filename, HttpServletRequest request) {

        HttpSession session   = request.getSession();
        String      sessionId = session.getId();

        String dir = "/home/tmp/" + sessionId;

        File file = new File(dir + File.separator + filename);
        if (file.exists()) {
            file.delete();
            return "file deleted!";
        } else {
            return "file does not exists";
        }

    }

    private String encodeChineseDownloadFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        String resultFileName = "";
        String agent          = request.getHeader("User-Agent").toUpperCase();
        if (null == agent) {
            resultFileName = fileName;
        } else {
            if (agent.indexOf("FIREFOX") != -1 || agent.indexOf("CHROME") != -1) {//firefox, chrome
                resultFileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            } else {  //ie7+
                resultFileName = URLEncoder.encode(fileName, "UTF-8");
                resultFileName = StringUtils.replace(resultFileName, "+", "%20");  //替换空格
            }
        }
        return resultFileName;
    }*/


}
