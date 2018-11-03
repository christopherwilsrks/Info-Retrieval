package com.VSMRetrieval;

import java.io.*;
import java.util.*;

/**
为文件建立倒排索引
step1:为所有的文件建立索引号 FileID_Number，首先查找到所有的文件目录 file.list[]，将所有的文件写到一个文件索引文件中 fileIndex.txt
step2:根据文件的路径将文件加载到程序中，将其中的单词分词统计在各个文件中出现的频率，统计信息写到结果文件wordIndex.txt中
*/
public class InvertedIndex
{
	/**
	 * 词频数据结构，使用TreeMap同时实现了词排序
	 */
	 TreeMap<String,TreeMap<String,Integer>>  tmapTF ;
	
	/**
	 * 词文档频率数据结构，使用TreeMap同时实现了排序
	 */
	 TreeMap<String,Integer> tmapDF; 
	
	/**
	 * 记录文档总数，计算IDF用
	 */
	 Integer lDocuments;
	
	/**
	 * 记录文档长度的数据结构，长度用包含词数表示，使用TreeMap同时实现了排序
	 */
	 TreeMap<String,Integer> tmapDocWordNumber; 
	
	/**
	 * 给所有文件建立统一名称索引
	 * @param filePath 存放预处理文件的目录
	 * @param docIndex 文件名，保存每个文件的统一编号
	 * @throws Exception 
	 */
	public void getFileIndex(String filePath , String docIndex) throws Exception
	{
		//通过传入的filePath找到文件所在，并将该文件下所有文件信息写到docIndex.txt中
		File file = new File(filePath);
		File[] fileList = file.listFiles();
		
		lDocuments =fileList.length;//文件数量
		tmapDocWordNumber = new TreeMap<String,Integer>();
		
		BufferedWriter bufw = null;
        try 
        {   //将所有filePath下的文件路径写到docIndex文件中
		    bufw = new BufferedWriter(new FileWriter(docIndex));
			for(int x = 0 ; x <fileList.length ; x++ )
			{
				String docPath = fileList[x].getAbsolutePath();	
				long ldocWorws = this.docLength(fileList[x]);//计算文档词数
				
				tmapDocWordNumber.put("DocID_" + x, (int) ldocWorws);//存放文档编号和文档词数
				
				bufw.write("DocID_" + x + "\t" + docPath + "\t" + ldocWorws);
				bufw.newLine();
				bufw.flush();//刷新写入
			}
        }
        catch (IOException e)
        {
			System.out.println("打开文件失败" + e);
        }
	    finally
		{
			try
			{
				if(bufw != null)
				bufw.close();
			}
			catch (IOException ex)
			{
				System.out.println("关闭文件失败" + ex);
			}
		}
	}
	
	/**
	 * 计算每个文件中每个词的词频；计算每个词的文档频率
	 * @param docIndex 文件名，保存每个文件的统一编号
	 * @param wordIndex 文件名，保存每个词在各个文件中的出现次数
	 * @param wordDocFreq 文件名，保存每个词出现在多少个文件中出现
	 * @throws IOException
	 */
	public void wordsComputing(String docIndex , String wordIndex,String wordDocFreq) throws IOException
	{ 
		//通过docIndex文件中的内容找到每个文件，并将文件中的内容做单词统计
       tmapTF = new TreeMap<String,TreeMap<String,Integer>>();//统计词频
       tmapDF = new TreeMap<String,Integer>();//统计词文档频率
       
	   BufferedReader bfReaderDoc = new BufferedReader(new FileReader(docIndex));//读取docIndex
	   BufferedWriter bufwritewordIndex = new BufferedWriter(new FileWriter(wordIndex));//写入到wordIndex
	   BufferedWriter bufwritewordDocFreq = new BufferedWriter(new FileWriter(wordDocFreq));//写入到wordDocFreq
	   
	   BufferedReader bufrDoc = null;
	   String docIDandPath = null;
	   
	   while( (docIDandPath = bfReaderDoc.readLine()) != null)
		{
		      String[] docInfo = docIDandPath.split("\t");
			  String docID = docInfo[0]; String docPath = docInfo[1];//获取到docID和文件的路径
			  bufrDoc = new BufferedReader(new FileReader(docPath));
			  String  wordLine = null;	 
			  while( (wordLine = bufrDoc.readLine()) != null)
				{
				  String[] words = wordLine.split("\\W");//分词
				  for(String wordOfDoc : words)
					  if(!wordOfDoc.equals(""))
						  wordDealing(wordOfDoc,docID,tmapTF);//将从docIndex读取到对应文件内容对做统计处理			    		       
				}
		} 
        //将处理后的结果写入到wordIndex.txt文件中		
		String wordFreInfo = null;
		String wordFreDocInfo = null;
		
		Set<Map.Entry<String,TreeMap<String,Integer>>> entrySet = tmapTF.entrySet();
		
		Iterator<Map.Entry<String,TreeMap<String,Integer>>> it = entrySet.iterator();
		
		while(it.hasNext())
		{
			Map.Entry<String,TreeMap<String,Integer>> em = it.next();
			
			wordFreInfo = em.getKey() +"\t" + em.getValue();
			
			bufwritewordIndex.write(wordFreInfo);
			bufwritewordIndex.newLine();
			bufwritewordIndex.flush();
			
			TreeMap<String , Integer> tmapTempDF = new TreeMap<String , Integer>(); 
			
			tmapTempDF = em.getValue();
			
			int docFreq =tmapTempDF.size();
			
			wordFreDocInfo = em.getKey() +"\t" + docFreq;
			
			tmapDF.put(em.getKey(), docFreq);
			
			bufwritewordDocFreq.write(wordFreDocInfo);
			bufwritewordDocFreq.newLine();
			bufwritewordDocFreq.flush();
			
		}
		
		bufwritewordIndex.close();
		bufwritewordDocFreq.close();
		bfReaderDoc.close();
		bufrDoc.close();
	}
	
	void wordDealing(String wordOfDoc,String docID,TreeMap<String,TreeMap<String,Integer>> tmapTF)
	{
        wordOfDoc = wordOfDoc.toLowerCase();
        if(!tmapTF.containsKey(wordOfDoc))
		{	
		  //单词在统计中是首次出现	
			TreeMap<String , Integer> tmapTempTF = new TreeMap<String , Integer>();
			tmapTempTF.put(docID,1);
			tmapTF.put(wordOfDoc,tmapTempTF);
		}        
		else
		{
          //单词在tmapTF中已存在,获取该单词在对应docID中出现次数，若是首次出现，则将（docID ,1)加入到tmapST中；否则将count++后，再将信息回写到tmpST中。
			TreeMap<String ,Integer> tmapTempTF = tmapTF.get(wordOfDoc);
			Integer count = tmapTempTF.get(docID);
			
			if (count == null) {
				count=1;
			} else{
				count=count+1;
			}
			tmapTempTF.put(docID,count);				
			tmapTF.put(wordOfDoc,tmapTempTF);	//将最新结果回写到tmp中	 
		}
	}
	
	/**
	 * 统计文档里有多少个词，即文档长度
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	long docLength(File filename) throws Exception {
		
		BufferedReader bfReaderDoc;
		long ldocWords =0;//记录文档词数
		
		try {
			bfReaderDoc = new BufferedReader(new FileReader(filename));
			String strLine = null;//记录行文本
			
			while ( (strLine = bfReaderDoc.readLine()) != null) {
				
				String[] words = strLine.split("\\W");//
				
				long lLineWorws= words.length;//记录行词数
				
				ldocWords = ldocWords + lLineWorws;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}		
		return ldocWords;
	}
}
