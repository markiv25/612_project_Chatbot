import java.io.*;
//package ChatBot;
import java.util.*;
import java.nio.file.*;
import java.util.Map.Entry;
 import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
//import ChatBot;
public class p2 {
   //attributes
   private String[] myDocs;
   private ArrayList<String> termAList;
   private ArrayList<ArrayList<Doc>> docLists;
   private double[] docLength;                     
   	ChatBot cb;
   /**
	 * Construct an inverted index using tf-idf weighting
    * @Tuheena Singh
	 * @param docs List of input strings or file names
	 * 
	 */
   public void p24(String[] docs) {
      //instanciate the attributes
      myDocs = docs;
      termAList = new ArrayList<String>();
      
      docLists = new ArrayList<ArrayList<Doc>>();
      ArrayList<Doc> docList;
      for(int i=0;i<myDocs.length;i++) {
         String[] words = myDocs[i].split(" ");
         String word;
         
         for(int j=0;j<words.length;j++) {
            boolean match = false;
            word = words[j];
            if(!termAList.contains(word)) {
               termAList.add(word);
               docList = new ArrayList<Doc>();
               Doc doc = new Doc(i, 1);         //raw term frequence is one
               docList.add(doc);
               docLists.add(docList);
            }
            else {
               int index = termAList.indexOf(word);
               docList = docLists.get(index);
                              
               for(Doc did:docList) {
                  if(did.docId == i) {
                     did.tw++;
                     match = true;
                     break;
               }
            }
            if(!match) {
               Doc doc = new Doc(i,1);
               docList.add(doc);
               System.out.println(docLists);

            }
         }
      }
      
     }
     // compute the tf.idf
     int N = myDocs.length;
     docLength = new double[N];
     
     for(int i=0;i<termAList.size();i++) {
      docList = docLists.get(i);
      int df = docList.size();
      Doc doc;
      for(int j=0;j<docList.size();j++) {
         doc = docList.get(j);
         double tfidf = (1+Math.log10(doc.tw)) * Math.log10(N/df*1.0);
         docLength[doc.docId] += Math.pow(tfidf,2);
         doc.tw = tfidf;
         docList.set(j,doc);
      }
     }
     for(int i=0;i<N;i++) {
      docLength[i] = Math.sqrt(docLength[i]);
     }
   }
    public  String readFileAsString(String fileName)throws Exception 
    { 
      String data = ""; 
      data = new String(Files.readAllBytes(Paths.get(fileName))); 
      return data; 
    } 
 
     //Binary search for a stop word
     public int searchStopWord(String key,String[] stopWords) {
        Arrays.sort(stopWords);
        int lo = 0;
        int hi = stopWords.length -1;
        while(lo <= hi) {
           int mid = lo + (hi-lo)/2;
           int result = key.compareTo(stopWords[mid]);
           if (result < 0) hi = mid-1;
           else if(result > 0) lo = mid +1;
           else return mid;
        }
        return -1;
     }  

   /**
	 * Compute matching score for documents by using cosine similarity with a value in [0,1]
	 * @param query user query in free form text
	 */
   public void rankSearch(String[] query) {
      //resultset that contains the matching documents, each of which has an id and a matching score
		HashMap<Integer, Double> docs = new HashMap<Integer, Double>();

		//TO BE COMPLETED
				ArrayList<Doc> docList;
		for(String phrase : query)
		{
			int index = termAList.indexOf(phrase);
			/* if phrase is not found*/
			if(index == -1)
				continue;
			docList = docLists.get(index);
			double termWeight = Math.log(myDocs.length*1.0/docList.size());
			Doc doc;
			for(int i = 0; i< docList.size(); i++)
			{
				doc = docList.get(i);
				double scoreVal = termWeight * doc.tw;
				if(!docs.containsKey(doc.docId))
				{
					docs.put(doc.docId, scoreVal);
				}
				else
				{
					scoreVal += docs.get(doc.docId);
					docs.put(doc.docId, scoreVal);
				}
			}
		}
		
      
      
      
		/*Normalization of values */
		double word = 0;                                                              
		for(Entry<Integer, Double> entry : docs.entrySet())
		{
			word += entry.getValue();
		}
		
		HashMap<Integer, Double> nzDocs = new HashMap<Integer, Double>();
		for(Entry<Integer, Double> entry : docs.entrySet())
		{
			nzDocs.put(entry.getKey(), entry.getValue()/word);
		}
		System.out.println("Original Value   :  "+docs);
      System.out.println("Nomalized Value   :  "+ nzDocs);
      Set<Map.Entry<Integer, Double>> mapSet = nzDocs.entrySet();
        Map.Entry<Integer, Double> elementAt5 = (Map.Entry<Integer, Double>) mapSet.toArray()[0];
        
     //   System.out.println(elementAt5.getKey());      //This is giving the best match of Symptom Acc to user input.
        System.out.println("The Best Match Symption is : " + myDocs[elementAt5.getKey()]);
   }
   
   /**
	 * Return the string representation of the index
	 */
   public String toString() {
      String outString = new String();
      ArrayList<Doc> docList;
      for(int i=0;i<termAList.size();i++) {
         outString += String.format("%-15s", termAList.get(i));
         docList = docLists.get(i);
         for(int j=0;j<docList.size();j++) {
            outString += docList.get(j) + "\t";
         }
         outString += "\n";
      }
      return outString;
   }
   
   
     public static void main(String[] args) throws Exception {
      //Creating the Frame
        	ChatBot cb=new ChatBot("Chat Bot");
		cb.setSize(800,605);
		cb.setLocation(50,50);
		          
           p2 p2 = new p2();
     String data = p2.readFileAsString("stopwords.txt");
     String datase  = p2.readFileAsString("dis_sym_dataset_norm.csv");    

      String[] sub;
     sub=datase.split("\n");  
     String[] search=sub[0].split(",");
     p2.p24(search);
             System.out.println(p2);
      String[] query1 = { "fever"};
      String[] query2 = { "ache"};
      String[] query3 = { "neck","pain"};
      String[] query4 = { "stomach","head"};
      
      
      p2.rankSearch(query1);
      p2.rankSearch(query2);
      p2.rankSearch(query3);
      p2.rankSearch(query4);
      
   }
}

/**
 * 
 * Document class that contains the document id and the term weight in tf-idf
 */
class Doc {
   int docId;
   double tw;
   //ArrayList<Integer> positionList;
   
   public Doc(int did, double tw) {
      docId = did;
      this.tw = tw;
   }
   
   public String toString() {
      String docIdString = docId + ":" + tw;
      return docIdString;
      
   }
    }
//© 2021 GitHub, Inc.