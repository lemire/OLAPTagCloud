package util;

public class StringArrays {
	public static String[] toArray(String s) {
		String[] answer = new String[1];
		answer[0] = s;
		return answer;
	}
	public static String toString(String[] s) {
		String ans="";
		for(int k = 0; k < s.length-1; ++k)
			ans += s[k]+",";
		ans += s[s.length-1];
		return ans;
	}

}