package com.inforetrieval.utils;

import com.inforetrieval.domain.DocList;
import com.inforetrieval.domain.TokenInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Search {

    private String                         word;
    private int                            tokenID;
    private int                            count;
    private List<Integer>                  docIDs;
    private Map<Integer, String>           mapDoc;
    private List<InversedList>             inversedLists;
    private String                         pathBin;
    private List<Integer>                  tokeninDocCount;
    private Map<String, ArrayList<String>> mapK_gram;

    public Search(String word, String path) throws IOException {
        inversedLists = new ArrayList<InversedList>();
        docIDs = new ArrayList<Integer>();
        mapDoc = new TreeMap<Integer, String>();
        tokeninDocCount = new ArrayList<Integer>();
        mapK_gram = new TreeMap<String, ArrayList<String>>();
        this.pathBin = path + "/bin";
        this.word = word.toLowerCase();
        System.out.println("【stage 0】".toUpperCase());
        this.getDocMapping();
    }

    public List<DocList> searchProcess() throws IOException {

        long startTime = System.currentTimeMillis();

        System.out.println("Start searching for [".toUpperCase() + word + "]\n\n");

        System.out.println("【stage 1】".toUpperCase());
        int pos = this.getTokenID();

        /*如果第一次查询木有找到token，那么将根据2-gram再次查询*/
        if (pos == -1) {
            return k_gram_search();
        } else {
            System.out.println("【stage 2】".toUpperCase());
            List<Integer> tokeninDocs = this.getTokeninDocs(pos);
            List<String>  docs        = new ArrayList<String>();
            int           i           = 0;
            List<TokenInfo> tokenInfos = new ArrayList<TokenInfo>();
            for (int id : tokeninDocs) {

                tokenInfos.add(new TokenInfo(mapDoc.get(id), tokeninDocCount.get(i++)));

            }

            long endTime = System.currentTimeMillis();
            System.out.println("Time:" + (endTime - startTime) * 1.0 / 1000 + "s");

            List<DocList> docLists = new ArrayList<DocList>();
            docLists.add(new DocList(word, tokenInfos));


            return docLists;
        }

    }

    public List<DocList> k_gram_search() throws IOException {


        /*先进行word拆分*/
        ArrayList<String> grams = new ArrayList<String>();
        word = "$" + word;
        for (int i = 0; i < word.length() - 1; i++) {
            String gram = word.substring(i, i + 2);
            /*如果含有*，意味着模糊查询*/
            if (gram.contains("*")) {
                continue;
            }
            grams.add(gram);
        }

        this.getK_gram();
        ArrayList<String> words = this.bool_K_gram_search(grams);

        List<DocList> docLists = new ArrayList<DocList>();


        for (String word : words) {

            this.word = word;
            int           pos         = getTokenID();
            List<Integer> tokeninDocs = this.getTokeninDocs(pos);

            int             i          = 0;
            List<TokenInfo> tokenInfos = new ArrayList<TokenInfo>();
            for (int id : tokeninDocs) {
                tokenInfos.add(new TokenInfo(mapDoc.get(id), tokeninDocCount.get(i++)));
            }
            docLists.add(new DocList(word, tokenInfos));

        }

        return docLists;
    }

    private int getTotalLines(File file) throws IOException {
        FileReader       in     = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        String           line   = reader.readLine();
        int              lines  = 0;
        while (line != null) {
            lines++;
            line = reader.readLine();
        }
        reader.close();
        in.close();
        return lines;
    }

    public int getTokenID() throws IOException {

        if (inversedLists.size() == 0) {
            FileReader     fr      = new FileReader(pathBin + "/InversedList.txt");
            BufferedReader br      = new BufferedReader(fr);
            String         strLine = br.readLine();

            while (strLine != null) {

                String       str[]        = strLine.split(" ");
                InversedList inversedList = new InversedList();
                inversedList.setToken(str[0]);
                inversedList.setTokenID(Integer.parseInt(str[1]));
                inversedList.setCount(Integer.parseInt(str[2]));
                inversedLists.add(inversedList);

                strLine = br.readLine();

            }

            br.close();
            fr.close();
        }

        int     start = 0;
        int     end   = inversedLists.size() - 1;
        int     pos   = 0;
        boolean found = false;

        while (start <= end) {
            pos = (start + end) / 2;
            String token = inversedLists.get(pos).getToken();
            if (token.compareTo(word) > 0) {
                end = pos - 1;
                continue;
            } else if (token.compareTo(word) < 0) {
                start = pos + 1;
                continue;
            } else {
                System.out.println("Word found in index, retrieving Inverse List");
                System.out.println("\n\n");
                tokenID = inversedLists.get(pos).getTokenID();
                count = inversedLists.get(pos).getCount();
                found = true;
                break;
            }
        }
        if (found) {
            int length = 0;

            FileReader     _fr      = new FileReader(pathBin + "/InversedList.txt");
            BufferedReader _br      = new BufferedReader(_fr);
            String         _strLine = _br.readLine();

            while (length++ < pos) {

                _strLine = _br.readLine();

            }

            String[] str = _strLine.split(" ");
            for (int i = 3; i < str.length; i++) {
                tokeninDocCount.add(Integer.valueOf(str[i]));
            }

            _br.close();
            _fr.close();
            return pos;

        }
        System.out.println("Word failed to be located in index, please check your word or re-index");
        return -1;
    }

    public void getDocMapping() throws IOException {

        System.out.println("Retrieving documents information\n\n");
        FileReader     fr      = new FileReader(pathBin + "/Doc-Mapping.txt");
        BufferedReader br      = new BufferedReader(fr);
        String         strLine = br.readLine();

        while (strLine != null) {

            String str[] = strLine.split(" ");
            mapDoc.put(Integer.valueOf(str[0]), str[1]);
            strLine = br.readLine();

        }
        br.close();
        fr.close();

    }

    public List<Integer> getTokeninDocs(int pos) throws IOException {

        System.out.println("Retrieving tokens' information\n\n");
        FileReader     fr = new FileReader(pathBin + "/DocIDs.txt");
        BufferedReader br = new BufferedReader(fr);

        String strLine = br.readLine();
        int    length  = 0;


        while (length++ < pos) {

            strLine = br.readLine();

        }

        String str[] = strLine.trim().split(" ");

        for (int i = 1; i < str.length; i++) {

            docIDs.add(Integer.valueOf(str[i]));

        }

        System.out.println("【finished】".toUpperCase());
        System.out.println("Search done!\n\n".toUpperCase());
        System.out.println("[" + word + "] FOUND IN: ");
        br.close();
        return docIDs;

    }

    private void getK_gram() throws IOException {

        FileReader     fr = new FileReader(pathBin + "/2-grams.txt");
        BufferedReader br = new BufferedReader(fr);

        String strLine = br.readLine();

        while (strLine != null) {

            String[] str = strLine.split(" ");

            ArrayList<String> tokenGram = new ArrayList<String>();
            for (String s : str) {

                tokenGram.add(s);

            }
            String gram = tokenGram.get(0);
            tokenGram.remove(0);
            mapK_gram.put(gram, tokenGram);

            strLine = br.readLine();

        }

    }

    private ArrayList<String> bool_K_gram_search(List<String> k_grams) throws IOException {

        ArrayList<String> tokenGram = mapK_gram.get(k_grams.get(0));
        ArrayList<String> temp      = new ArrayList<String>();
        temp.addAll(tokenGram);

        for (int i = 1; i < k_grams.size(); i++) {

            temp.retainAll(tokenGram);

            String k_gram = k_grams.get(i);
            ArrayList<String> t = mapK_gram.get(k_gram);
            if (t == null) {
                continue;
            }
            tokenGram.retainAll(t);
            if (tokenGram.size() == 1) {
                break;
            } else if (tokenGram.size() == 0) {
                if (temp.size() >= 10) {
                    tokenGram.addAll(temp);
                    continue;
                }
                return temp;
            }

        }

        return tokenGram;

    }

}
