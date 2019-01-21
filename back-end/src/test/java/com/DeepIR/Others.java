package com.DeepIR;

import com.DeepIR.utils.Lucene.LuceneQuery;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Others {

    @Test
    public void test() throws ParseException, IOException {

        List<String> list = new LuceneQuery(1).queryTitle("今晚");
        System.out.println(list.toString());


        System.exit(0);
    }

}
