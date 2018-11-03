package com.VSMRetrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class TermQuery {
	
    static InvertedIndex invertedIndex;
	static ComputeModel computeModel;
	
    /**
     * TreeMap数据结构，保存文档编号：查询词和文档的相似度累加值
     */
    static TreeMap<String , Double> tmapWeight ;
	
	/**
	 * 将文件名统一命名，计算词频，词文档频率预处理，并写入指定文件
	 * @throws Exception 
	 */
	static void preprocess() throws Exception{
		
		String filePath = "C:\\Users\\18711\\Desktop\\news";
		String docIndex = "docIndex.txt";
		String wordIndex = "wordIndex.txt";
		String wordDocFreq = "wordDocFreq.txt";
		
		invertedIndex = new InvertedIndex();
		
		invertedIndex.getFileIndex(filePath , docIndex);
		
		try {
			invertedIndex.wordsComputing(docIndex, wordIndex, wordDocFreq);
			computeModel =new ComputeModel(invertedIndex);
			System.out.println("预处理已经完成!");
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	 }
	
	/**
	 * 按值value排序Map对象,可返回排序过的Map对象
	 * @param originMap
	 */
	public static Map<String, Double> sortMapByValue(Map<String, Double> originMap) {
		
        if (originMap == null || originMap.isEmpty()) {
            return null;
        }
        
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        ArrayList<Map.Entry<String, Double>> entryList = new ArrayList <Map.Entry<String, Double>>(originMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());//使用类MapValueComparator，固定模式
        
       
        
        Iterator<Map.Entry<String, Double>> iter1 = entryList.iterator();
        Map.Entry<String, Double> tmpEntry = null;
        
        while (iter1.hasNext()) {
            tmpEntry = iter1.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        
       // 屏幕输出按值排序的文档号和相似度值的Map
        System.out.println("------");  
        System.out.println("文档ID"+"\t"+"相似度"); 
        
		Iterator<Entry<String, Double>> iter2 = sortedMap.entrySet().iterator();
		
		while(iter2.hasNext())
		{
			Map.Entry<String,Double> entry=iter2.next();  
            String key=entry.getKey().toString();  
            String value=entry.getValue().toString();  
            System.out.println(key+"\t"+value);  
		}
        
        return sortedMap;
    }
	
	/**
	 * 按照Entry<String, Double>的Double值类型Double的大小实现排序
	 */
	static class MapValueComparator implements Comparator<Map.Entry<String, Double>> {
        @Override
        public int compare(Entry<String, Double> me1, Entry<String, Double> me2) {

            return me2.getValue().compareTo(me1.getValue());
        }
    }
	 
	public static void main(String[] args) throws Exception
	{	
		
		
		System.out.println("正在进行预处理，为文档库中每个文件每个词计算词频和文档频率，并且保存在文件中！");
		preprocess();
		
		Scanner inputChar;//接受键盘输入
		do { 
		
			System.out.println();
			System.out.println("-----------------------------------------------------------------");
			System.out.println();
			System.out.println("请输入要查询英语句子或关键词！");
			
			//读入查询语句
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(System.in ));
			String strQuery  = buffReader.readLine();
			
			//将查询语句strQuery分词放入strKeyWord[]，逐词计算
			String strKeyWord[] = strQuery.toLowerCase().split("\\W");
			
			tmapWeight = new TreeMap<String, Double>();//记录总的文档权重结果
			
			TreeMap<String,Double> tmapKeyWordWeight = new TreeMap<String,Double>();//记录每个查询词的文档权重结果
			
			for (int i=0;i<strKeyWord.length;i++){//遍历输入的每个词，文档相似度权重值累加

				Integer iDF = invertedIndex.tmapDF.get(strKeyWord[i]);//iDF能检测是否此关键词在文档库中出现过

				
				if (iDF!=null){//当关键词在文档库中出现时计算权重值
					
					tmapKeyWordWeight = computeModel.modelBM25(strKeyWord[i]);//获取strKeyWord[i]的文档权重Map
					
					Iterator<Map.Entry<String,Double>> it = tmapKeyWordWeight.entrySet().iterator();//遍历文档频率Map
					
					while(it.hasNext()) {
						
						Map.Entry<String,Double> em = it.next();
						String DocId = em.getKey();//获取文档编号
						double dKeyWordWeight = em.getValue();//获取词strKeyWord[i]在DocId文档中的计算权重
						
						Double oldWeight =tmapWeight.get(DocId); //获取文档的权重旧值
						
						if (oldWeight!=null){
							dKeyWordWeight = oldWeight + dKeyWordWeight; //累加文档的新旧权重值
						}
						
						System.out.println(strKeyWord[i] +"的匹配文档及累加权重值：\t" +DocId +"\t"+ dKeyWordWeight);//测试每个查询词的输出 
						
						//把文档编号和相似度权重值存入tmapWeight
						tmapWeight.put(DocId, dKeyWordWeight); 
						
					}
				}
			}
			
			//按照tmapWeight的value的大小排序并输出对应的文档号
			sortMapByValue(tmapWeight);//调用排序方法
			
			System.out.println("继续查询吗？(Y or N)"); 
			
			inputChar = new Scanner(System.in); 
			
		} while( inputChar.next().equalsIgnoreCase("Y"));

		System.out.println("退出程序！"); 
	}
}
