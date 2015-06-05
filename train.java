import java.util.*;
import java.io.*;
class valCompare implements Comparator<String>{
	Map<String,Integer> base;
	valCompare(Map<String,Integer>base){
		this.base = base;
	}
	public int compare(String a, String b){
		if(base.get(a) >= base.get(b)){
			return -1;
		}
		else{
			return 1;
		}
	}
}
public class train {
	//userItemlist map user to rated item list
	private Map< String, Set<String> > userItemList= null;
	private Map<String,Set<String>> itemUserList = null;
	private String trainDataPath = null;
	private Map<pair, Double > weight = null;
	private Map<pair, Double> itemBasedWeight = null;
	private Set<String> userList = null;
	private Set<String> itemList = null;
	private Map<String,Integer> itemPopRank = null;
	private Map<String,Integer> userPopRank = null;
	private boolean userBased = true;
	train(boolean userBased){
		trainDataPath = "C:\\Users\\mburec\\Documents\\Tencent Files\\642339199\\FileRecv\\train.txt";
		this.userBased = userBased;
		userItemList = new HashMap<String,Set<String>>();
		itemUserList = new HashMap<String,Set<String>>();
		itemPopRank = new HashMap<String,Integer>();
		weight  = new HashMap<pair,Double>();
		itemBasedWeight = new HashMap<pair,Double>();
		userList = new HashSet<String>();
		itemList = new HashSet<String>();
		userPopRank = new HashMap<String,Integer>();
		dataInput2();
		if(userBased){
			learning();
		}
		else{
			itemBasedLearning();
		}
		itemPopRankCal();
	}
	private void dataInput2(){
		File trainFile = new File(trainDataPath);
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(trainFile));
			String line = null;
			while((line = reader.readLine()) != null){
				String[] field = line.split(":\t");
				userList.add(field[0]);
				String[]items = field[1].split("\t");
				for(String item : items){
					itemList.add(item);
					if(itemUserList.containsKey(item)){
						itemUserList.get(item).add(field[0]);
					}
					else{
						Set<String>users = new HashSet<String>();
						users.add(field[0]);
						itemUserList.put(item, users);
					}
				}
				Set<String>itemset = new HashSet<String>(Arrays.asList(items));
				userItemList.put(field[0], itemset);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(reader != null){
				try{
					reader.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	private void dataInput(){
		File trainFile = new File(trainDataPath);
		BufferedReader reader = null;
		try{
			reader = new BufferedReader( new FileReader(trainFile));
			String line = null;
			while((line = reader.readLine()) != null){
				String[] field =null;
				field = line.split(",");
				userList.add(field[0]);
				itemList.add(field[1]);
				if(userItemList.containsKey(field[0])){
					userItemList.get(field[0]).add(field[1]);
				}
				else{
					Set<String> initItem = new HashSet<String>();
					initItem.add(field[1]);
					userItemList.put(field[0], initItem);
				}
				if(itemUserList.containsKey(field[1])){
					itemUserList.get(field[1]).add(field[0]);
				}
				else{
					Set<String> initUser = new HashSet<String>();
					initUser.add(field[0]);
					itemUserList.put(field[1], initUser);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		finally{
			if(reader != null){
				try{
					reader.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}//close the buffered reader
	}
	private void learning(){
		for(String user1 : userList){
			for (String user2 : userList){
				if(user1.compareTo(user2) > 0){
					pair userUser = new pair(user1,user2);
					Integer common = interSection(userItemList.get(user1),userItemList.get(user2));
					Double w = (common + 0.0)/(Math.sqrt(userItemList.get(user1).size()*
							userItemList.get(user2).size()));
					if(w > 0){
						weight.put(userUser, w);
					}
				}
			}
		}
	}
	private void itemBasedLearning(){
		int weightcount = 0;
		for(String item1 : itemList){
			for(String item2: itemList){
				if(item1.compareTo(item2) > 0){
					pair itemItem = new pair(item1,item2);
					Integer common = interSection(itemUserList.get(item1),itemUserList.get(item2));
					Double w = (common + 0.0)/(Math.sqrt(itemUserList.get(item1).size()*
							itemUserList.get(item2).size()));
					if(w > 0){
						itemBasedWeight.put(itemItem, w);
						weightcount++;
					}
				}
			}
		}
		System.out.println(weightcount);
	}
	private void revertLearning(){
		Map<String,Integer>userLength = new HashMap<String,Integer>();
		for(String movie : itemUserList.keySet()){
			for(String user : itemUserList.get(movie)){
				if(userLength.containsKey(user)){
					userLength.put(user, userLength.get(user) + 1);
				}
				else{
					userLength.put(user, (Integer)1);
				}
				for(String user2: itemUserList.get(movie)){
					if(user2.compareTo(user) > 0){
						pair ui = new pair(user,user2);
						if(weight.containsKey(ui)){
							System.out.println("enter ui");
							weight.put(ui,weight.get(ui) + 1.0);
						}
						else{
							weight.put(ui,(Double)1.0);
						}
					}
				}
			}
		}
		for(pair ui : weight.keySet()){
			weight.put(ui, weight.get(ui)/Math.sqrt(userLength.get(ui.first)* userLength.get(ui.second)));
		}
	}
	private Integer interSection(Set<String> a, Set<String> b){
		Set<String> result = new HashSet<String>();
		result.addAll(a);
		result.retainAll(b);
		return result.size();
	}
	private void itemPopRankCal(){
		Map<String,Integer>map = new HashMap<String,Integer>();
		for(String item : itemList){
			if(map.containsKey(item)){
				map.put(item, map.get(item) + itemUserList.get(item).size());
			}
			else{
				map.put(item, itemUserList.get(item).size());
			}
		}
		valCompare vc = new valCompare(map);
		TreeMap<String,Integer>itemRank = new TreeMap<String,Integer>(vc);
		itemRank.putAll(map);
		Object[] items = itemRank.keySet().toArray();
		int itemSize = itemRank.size();
		for(int i = 0; i < itemSize; i++){
			itemPopRank.put((String)(items[i]), (Integer)i);
		}
	}
	private void userPopRankCal(){
		Map<String,Integer>map = new HashMap<String,Integer>();
		for(String user: userList){
			if(map.containsKey(user)){
				map.put(user, map.get(user) + userItemList.get(user).size());
			}
			else{
				map.put(user, itemUserList.get(user).size());
			}
		}
		valCompare vc = new valCompare(map);
		TreeMap<String,Integer>userRank = new TreeMap<String,Integer>(vc);
		userRank.putAll(map);
		Object[] users = userRank.keySet().toArray();
		int userSize = userRank.size();
		for(int i = 0; i <userSize; i++){
			userPopRank.put((String)(users[i]), (Integer)i);
		}
		
	}
	public Map<pair, Double > getWeight(){
		return weight;
	}
	public Map<pair,Double> getItemWeight(){
		return itemBasedWeight;
	}
	public Map<String,Set<String> > getUserItemList(){
		return userItemList;
	}
	public Set<String> getUserList(){
		return userList;
	}
	public Set<String> getItemList(){
		return itemList;
	}
	public Map<String,Set<String>>getItemUserList(){
		return itemUserList;
	}

	public Map<String,Integer> getItemPopRank(){
		return itemPopRank;
	}

}
