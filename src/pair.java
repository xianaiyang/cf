
public class pair {
	public String first;
	public String second;
	public pair(String first, String second){
		this.first = first;
		this.second = second;
	}
	public boolean equares(pair ref){
		if(this.first.equals(ref.first)){
			return this.second.equals(ref.second);
		}
		else{
			return false;
		}
	}
}
