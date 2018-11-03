package com.VSMRetrieval;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ComputeModel {
	
	/**
	 * 倒排文件索引
	 */
	InvertedIndex invertedIndex;
	
	/**
	 * 文档总数
	 */
	long lDocs;
	
	/**
	 * 文档平均长度
	 */
	long lDocAvgWords;
	
	/**
	 * BM25模型参数，调整词频用
	 */
	static final double k=2.0;
	
	/**
	 * BM25模型参数，调整文档长度用
	 */
	static final double b =0.75;
	
	
	/**
	 * 有参构造方法，参数为InvertedIndex，传入初始化后的倒排索引类
	 * @param index
	 */
	ComputeModel(InvertedIndex index){
		
		invertedIndex= index;
		lDocs = invertedIndex.lDocuments ;//文档数目
		
		long lDocsWords =0;//记录每篇文档的字数
		
		for (Integer WordNumber : invertedIndex.tmapDocWordNumber.values()) {
			lDocsWords =lDocsWords + WordNumber;
        }
		lDocAvgWords = lDocsWords / lDocs;
		
	}
	
	/**
	 * 二进制计算模型
	 * @param tfmap
	 * @return
	 */
	TreeMap<String , Double> modelBit(String keyWord){
		
		TreeMap<String , Double> tmapWeight = new TreeMap<String, Double>();
				
		TreeMap<String,Integer > tmapKeyWordTF = invertedIndex.tmapTF.get(keyWord); //获取KeyWord的文档频率Map

		Iterator<Map.Entry<String,Integer>> it = tmapKeyWordTF.entrySet().iterator();//遍历文档频率Map
				
		while(it.hasNext()) {
			Map.Entry<String,Integer> em = it.next();
			String DocId = em.getKey();//获取文档编号
			Integer dDocIdTF = em.getValue();//获取词KeyWord在DocId文档中的词频
			
			double dWeight = (dDocIdTF > 0) ? 1:0; //只要出现过，就记为1，不区分出现次数			
			tmapWeight.put(DocId, dWeight); //把文档编号和相似度权重值存入tmapWeight
		}
		
		return tmapWeight;
					
		}
	
	/**
	 * 词频反文档频率计算模型
	 * @param keyWord
	 * @return
	 */
	TreeMap<String , Double> modelTFIDF(String keyWord) {
		
		TreeMap<String , Double> tmapWeight = new TreeMap<String, Double>();

		Integer iDF = invertedIndex.tmapDF.get(keyWord);//文档频率
		double dIDF = Math.log((double)lDocs/iDF);//反文档频率IDF计算
				
		TreeMap<String,Integer > tmapKeyWordTF = invertedIndex.tmapTF.get(keyWord); //获取KeyWord的文档频率Map

		Iterator<Map.Entry<String,Integer>> it = tmapKeyWordTF.entrySet().iterator();//遍历文档频率Map
				
		while(it.hasNext()) {
			Map.Entry<String,Integer> em = it.next();
			String DocId = em.getKey();//获取文档编号
			Integer dDocIdTF = em.getValue();//获取词KeyWord在DocId文档中的词频
			double dWeight = dDocIdTF * dIDF; //文档权重计算公式
			tmapWeight.put(DocId, dWeight); //把文档编号和相似度权重值存入tmapWeight
		}
		
		return tmapWeight;
		
	}
	
	/**
	 * BM25计算模型
	 * @param tfmap
	 * @param dIDF
	 * @return
	 */
	TreeMap<String , Double> modelBM25(String keyWord){
		
		TreeMap<String , Double> tmapWeight = new TreeMap<String, Double>();
		
		lDocs = invertedIndex.lDocuments ;//文档数目
		Integer iDF = invertedIndex.tmapDF.get(keyWord);//文档频率
		double dIDF = Math.log((double)(lDocs + 1)/iDF);//反文档频率IDF计算
				
		TreeMap<String,Integer > tmapKeyWordTF = invertedIndex.tmapTF.get(keyWord); //获取KeyWord的文档频率Map

		Iterator<Map.Entry<String,Integer>> it = tmapKeyWordTF.entrySet().iterator();//遍历文档频率Map
				
		while(it.hasNext()) {
			Map.Entry<String,Integer> em = it.next();
			String DocId = em.getKey();//获取文档编号
			Integer DocIdTF = em.getValue();//获取词KeyWord在DocId文档中的词频
			Integer DocIdWords = invertedIndex.tmapDocWordNumber.get(DocId);//获取DocId文档的词数
			
			//BM25权重计算公式,需要转换每一个变量为double，否则可能出现截位的情况
			double dWeight =((double)(DocIdTF) * (k + 1)) / (((double)DocIdTF + k * (1 - b + b * (double)DocIdWords /(double)lDocAvgWords)))  * dIDF; 
			
			tmapWeight.put(DocId, dWeight); //把文档编号和相似度权重值存入tmapWeight
		}
		
		return tmapWeight;
			
	}

}
