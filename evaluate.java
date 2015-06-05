import java.io.*;
import java.util.*;
public class evaluate {
	private Double precision = 0.0;
	private Double recall = 0.0;
	private Double coveragre;
	private Double popular;
	private String predPath;
	private String testPath;
	private Map<String,Set<String>>userRatedList = new HashMap<String,Set<String>>();
	private Map<String,Set<String>>userPredList = new HashMap<String,Set<String>>();
	public evaluate(){
		predPath = "C:\\Users\\mburec\\Documents\\Tencent Files\\642339199\\FileRecv\\pred.txt";
		testPath = "C:\\Users\\mburec\\Documents\\Tencent Files\\642339199\\FileRecv\\test.txt";
		testDataInput();
		calculate();
	}
	private void testDataInput(){
		File testFile= new File(testPath);
		File predFile = new File(predPath);
		BufferedReader bfrTest = null;
		BufferedReader bfrPred = null;
		try{
			bfrTest = new BufferedReader(new FileReader(testFile));
			bfrPred = new BufferedReader(new FileReader(predFile));
			String line = null;
			while((line = bfrTest.readLine())!= null){
				String field[] = line.split(":\t");
				String userId = field[0];
				if(field.length > 1){
					String[] items = field[1].split("\t");
					Set<String>itemList = new HashSet<String>(Arrays.asList(items));
					userRatedList.put(userId, itemList);
				}
			}
			while((line = bfrPred.readLine() )!= null){
				String field[] = line.split(":\t");
				String recList[] = field[1].split("\t");
				String userId = field[0];
				Set<String>movieList = new HashSet<String>(Arrays.asList(recList));
				userPredList.put(userId,movieList);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(bfrPred != null){
					bfrPred.close();
				}
				if(bfrTest != null){
					bfrTest.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	private void calculate(){
		Integer predNum = 0;
		Integer testNum = 0;
		Integer hit = 0;
		Set<String>userPred = userPredList.keySet();
		Set<String>userTest = userRatedList.keySet();
		for(String userp : userPred ){
			Set<String> userPMovieList = userPredList.get(userp);
			if(userTest.contains(userp)){
				Set<String> userTMovieList = userRatedList.get(userp);
				Set<String>section = new HashSet<String>(userPMovieList);
				section.retainAll(userTMovieList);
				hit += section.size();
			}	
		}
		for(String userp : userPred){
			predNum += userPredList.get(userp).size();
		}
		for(String usert : userTest){
			testNum += userRatedList.get(usert).size();
		}
		precision = (hit + 0.0)/predNum;
		recall = (hit + 0.0)/testNum;
		System.out.println("hitnum:"+hit);
		System.out.println("prednum:"+predNum);
		System.out.println("testNum:"+testNum);
	}
	public Double getPrecision(){
		return precision;
	}
	public Double getRecall(){
		return recall;
	}
	public Double getAvaPop(Set<String>recItem,Map<String,Integer>itemRank){
		Integer rankTotal = 0;
		for(String item : recItem){
			rankTotal += itemRank.get(item);
		}
		return (rankTotal + 0.0)/recItem.size();
	}
	public static void trainTestGen(String dataFile){
		File fdata = new File(dataFile);
		File ftrain = new File("C:\\Users\\mburec\\Documents\\Tencent Files\\642339199\\FileRecv\\train.txt");
		File ftest = new File("C:\\Users\\mburec\\Documents\\Tencent Files\\642339199\\FileRecv\\test.txt");
		BufferedReader bfrData = null;
		BufferedWriter bfrTrain = null;
		BufferedWriter bfrTest = null;
		try{
			bfrData = new BufferedReader(new FileReader(fdata));
			bfrTrain = new BufferedWriter(new FileWriter(ftrain));
			bfrTest = new BufferedWriter(new FileWriter(ftest));
			String line = null;
			while((line = bfrData.readLine()) != null){
				String[] field = line.split(":\t");
				String[] items = field[1].split("\t");
				bfrTrain.write(field[0]+":\t");
				bfrTest.write(field[0]+":\t");
				for(String item : items){
					if((int)(5*Math.random()) != 3){
						bfrTrain.write(item+"\t");
					}
					else{
						bfrTest.write(item+"\t");
					}
				}
				bfrTrain.write("\n");
				bfrTest.write("\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(bfrData != null){
				try{
					bfrData.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
				if(bfrTrain != null){
					try{
						bfrTrain.close();
					}catch(IOException e){
						e.printStackTrace();
					}
			}
				if(bfrTest != null){
					try{
						bfrTest.close();
					}catch(IOException e){
						e.printStackTrace();
					}
			}
		}
		
	}
}
