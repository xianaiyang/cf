import java.util.*;
import java.io.*;
class valueCompare implements Comparator<String>{
	Map<String,Double>base;
	public valueCompare(Map<String,Double>ref){
		base = ref;
	}
	public int compare(String a, String b){
		if( base.get(a) - base.get(b) >= 0) {
			return -1;
		}
		else{
			return 1;
		}
	}
	
	
}
class valueCompare2 implements Comparator<pair>{
	Map< pair,Double> base;
	public valueCompare2(Map<pair,Double>ref){
		base = ref;
	}
	public int compare(pair a, pair b){
		if(base.get(a) - base.get(b) >= 0){
			return -1;
		}
		else{
			return 1;
		}
	}
	
}
public class predict {
	private HashMap<String,Set<String> > recList = new HashMap<String,Set<String>>();
	private Map<pair, Double> uiPrefer = new HashMap<pair, Double>();
	private String userItemRecPath = null;
	public predict(){
		userItemRecPath = "C:\\Users\\mburec\\Documents\\Tencent Files\\642339199\\FileRecv\\pred.txt";
	}
	public void preferLearn(Integer topk,Map<pair,Double>weight,Map<String,Set<String>>userItemList,
			Set<String>userList, Set<String>itemList){
		for(String user : userList){
			HashMap<String,Double>  map = new HashMap<String,Double>();
			for(pair userUser : weight.keySet()){
				if( user.equals(userUser.first)){
					map.put(userUser.second,weight.get(userUser));
				}
				else if( user.equals(userUser.second)){
					map.put(userUser.first,weight.get(userUser));
				}
			}
			valueCompare bvc = new valueCompare(map);
			TreeMap<String,Double> userWeight = new TreeMap<String,Double>(bvc);
			userWeight.putAll(map);
			Map<String,Double> topkUser = new TreeMap<String,Double>();
			Object[] topkUserId = userWeight.keySet().toArray();
			Object[] topkUserW = userWeight.values().toArray();
			int simUserlen = topkUserId.length;
			for(int i = 0; i < topk && i < simUserlen; i++){
				topkUser.put((String)topkUserId[i], (Double)topkUserW[i]);
			}
			for(String item: itemList){
				if(userItemList.get(user).contains(item)){
					continue;
				}
				else{
					pair uiPair = new pair(user,item);
					double prob = 0.0;
					for(String simUser: topkUser.keySet()){
						if(userItemList.get(simUser).contains(item)){
								prob += topkUser.get(simUser);
							}
						}
					if(prob > 0){
						uiPrefer.put(uiPair,prob);
					}
				}	
			}//calculate user i's preference for item j
		}
	}//end pred
	public void itemBasedpreferLearn(Map<pair,Double>itemBasedweight,Map<String,Set<String>>userItemList,
			Set<String>userList, Set<String>itemList){
		System.out.println(itemBasedweight.size());
		for(String user : userList){
			for(String item: itemList){
				if(userItemList.get(user).contains(item)){
					continue;
				}
				else{
					pair uiPair = new pair(user,item);
					double prob = 0.0;
					for(String purchaseItem: userItemList.get(user)){
						pair itemItem = new pair(item,purchaseItem);
						pair itemItem2 = new pair(purchaseItem,item);
						if(itemBasedweight.containsKey(itemItem)){
							prob += itemBasedweight.get(itemItem);
						}
						else if(itemBasedweight.containsKey(itemItem2)){
							prob += itemBasedweight.get(itemItem2);
						}
					}
					if(prob > 0){
						uiPrefer.put(uiPair,prob);
						System.out.println("prefer prob get");
					}
				}	
			}//calculate user i's preference for item j
		}
	}//end pred
	public void pred(int recAmount,Set<String>userList){
		for(String user : userList){
			Map<String,Double>userRecCanad = new HashMap<String,Double>();
			for(pair ui : uiPrefer.keySet()){
				if(user.equals(ui.first)){
					userRecCanad.put(ui.second, uiPrefer.get(ui));
				}
			}
			valueCompare vc = new valueCompare(userRecCanad);
			TreeMap<String,Double>sortList = new TreeMap<String,Double>(vc);
			sortList.putAll(userRecCanad);
			Object[] movieRec = sortList.keySet().toArray();
			Object[] prefer = sortList.values().toArray(); 
			int i = 0;
			for(; i < recAmount; i++){
				if(i < sortList.keySet().size() && recList.containsKey(user) && ((Double)prefer[i] > 0.18)){
					recList.get(user).add((String)movieRec[i]);
				}
				else{
					Set<String>mList = new HashSet<String>();
					if(i < sortList.keySet().size()){
						mList.add((String)movieRec[i]);
						recList.put(user, mList);
					}
				}
			}
		}
	}
	public void outPutPred(){
		File output = new File(userItemRecPath);
		BufferedWriter bfw = null;
		try{
			bfw = new BufferedWriter(new FileWriter(output));
			for(String user : recList.keySet()){
				bfw.write(user.toString());
				bfw.write(":\t");
				for(String movie : recList.get(user)){
					bfw.write(movie.toString());
					bfw.write("\t");
				}
				bfw.write("\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(bfw != null){
				try{
					bfw.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}//close bufferedWriter
		}
	}
	public Map<String,Set<String>> getRecList(){
		return recList;
	}
}
