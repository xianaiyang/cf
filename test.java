import java.util.*;
import java.io.*;
public class test {
	public static void main(String[]args){
		predict pred = new predict();
		train dataTrain = new train(false);
		Map<pair, Double > weight = dataTrain.getItemWeight();
		Map<String,Set<String>> userItemList = dataTrain.getUserItemList();
		Set<String> userList = dataTrain.getUserList();
		Set<String> itemList = dataTrain.getItemList();
		Map<String,Set<String>> recList = pred.getRecList();
		Map<String,Integer> itemRank = dataTrain.getItemPopRank();
		pred.itemBasedpreferLearn( weight, userItemList, userList, itemList);
		pred.pred(30,userList);
		pred.outPutPred();
		Set<String>recMovie = new HashSet<String>();
		for(Set<String> usermovie : recList.values()){
			for(String movie : usermovie){
				recMovie.add(movie);
			}
		}
		evaluate eva = new evaluate();
		System.out.println("precision"+eva.getPrecision()*100 + "%");
		System.out.println("recall:"+eva.getRecall()*100 + "%");
		System.out.println("avarage sells rank"+eva.getAvaPop(recMovie, itemRank));
		//System.out.println("coverage:"+recMovie.size()/(double)1682);
		//evaluate.trainTestGen("C:\\Users\\mburec\\Documents\\Tencent Files\\642339199\\FileRecv\\topUser.txt");
	}

}
