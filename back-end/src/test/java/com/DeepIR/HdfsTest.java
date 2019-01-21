package com.DeepIR;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class HdfsTest {

    FileSystem fs;

    @Before
    public void before() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.57.129:8020", "hadoop");
        fs = FileSystem.get(conf);
    }

    @Test
    public void upload() throws IOException {
        fs.copyFromLocalFile(new Path("test.txt"), new Path("hdfs://192.168.57.129:8020/user/christ/pagerank/test"));
    }

    @Test
    public void download() throws IOException {
        fs.copyToLocalFile(false, new Path("hdfs://192.168.57.129:8020/user/christ/test.txt"), new Path("test.txt"), true);
    }

    @Test
    public void listFiles() throws IOException {

        // listFile() 列出的是文件信息，并且提供递归遍历（参数为true时 能递归查看文件夹内的文件）
        RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path("/"), true);
        while (files.hasNext()) {
            LocatedFileStatus file     = files.next();
            Path              filePath = file.getPath();
            String            fileName = filePath.getName();
            System.out.println(fileName);
        }
        /**打印结果
         *  test.txt
         *  core-site.xml
         *  hadoop-core-1.2.1.jar
         *  jdk-8u161-linux-x64.tar.gz
         */

        System.out.println("-----------------------");

        // listStatus() 列出文件和文件夹的信息，但是不提供递归遍历（需要自行去做递归）
        FileStatus[] listStatus = fs.listStatus(new Path("/"));
        for (FileStatus status : listStatus) {
            String name = status.getPath().getName();
            System.out.println(name + (status.isDirectory() ? " - is a dir" : " - is a file"));
        }
    }

    @Test
    public void calcu() throws IOException {
        String             props    ="StanfordCoreNLP-Chinese.properties";
        AnnotationPipeline pipeline = new StanfordCoreNLP(props);
        Annotation         annotation;
        String content = "（通讯员 李高扬 陶瑾）11月26日晚6：30，由法学院2018级刑法及诉讼法专业研究生承办的第51期“鸣响”学术沙龙在法学院331顺利举办。本期聚焦2018年新修改的《刑事诉讼法》，由2018级法学硕士研究生曹頔繁、李倩、宋星衡、白鑫、赵伟辰主讲，邀请法学院杨文革教授、朱桐辉副教授作为嘉宾进行点评，活动吸引了天津市南开区检察院检察官李红震、刘占勇、孙嘉毅、杨磊，天津公安警官职业学院教师杨鹏以及法学院部分研究生的积极参与。" +
                "曹頔繁首先对刑事速裁程序的内容进行了详细的介绍，赵伟辰对捕诉合一问题展开阐述，分析了捕诉权与公诉权的性质、价值观念、运作机制，捕诉关系的发展沿革以及实行捕诉合一的利弊。李倩、白鑫同学则从《刑事诉讼法》的修改背景入手，详细介绍了缺席审判程序、认罪认罚制度的适用条件、适用效果等。宋星衡分析了《刑事诉讼法》和《监察法》的衔接，并阐述了不同专家学者对此问题的看法。最后五位同学一致认为虽然这些制度已经在实践中实行过一段时间，但仍然存在很多缺陷，值得进一步研究和完善。" +
                "同学分享结束后，杨文革老师从修改的特点、背景、主要内容等方面进行了专业点评。朱桐辉老师针对值班律师的定位、捕诉合一的现况与同学们进行探讨。南开区检察院检察官和天津公安警官职业学院老师则详细介绍了实践中的认罪认罚、速裁程序、值班律师、捕诉合一的运行状况，为同学们开启了实践视角。" +
                "沙龙结束后，同学们表示对新修改的《刑事诉讼法》有了更为深入的认识，提升了专业能力，收获颇丰。据悉，“鸣响”学术沙龙由法学院硕士研究生承办，从法学热点切入，与专业研究内容紧密结合，以研究生主讲与专业教师点评的方式进行，激发学生的主动性和创造性，实现第一课堂与第二课堂的有效互动，旨在培养符合新时代发展需要的法治人才。";
        annotation = new Annotation(content);


        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                System.out.println(word + " ");
            }
        }
    }

}
