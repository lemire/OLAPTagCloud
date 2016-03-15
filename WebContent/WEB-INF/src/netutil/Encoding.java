package netutil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Encoding {
	static Pattern querypattern = Pattern.compile("(.*?)=(.*?)($|&)");

	
	/**
	 *  This is Daniel's hack to workaround Sun's shaky implementation
	 *  of the request.getParameter method.
	 * @param query
	 * @return HashTable
	 */
	public static Hashtable<String,String> getParameters(String query) {
		   	Hashtable<String,String> a = new Hashtable<String,String>();
			Matcher match = querypattern.matcher(query);
			while (match.find()) {
				String key = null,value = null;
				try {
				key = URLDecoder.decode(match.group(1), "UTF-8");
				value = URLDecoder.decode(match.group(2), "UTF-8");
				} catch (UnsupportedEncodingException uee) {
				   uee.printStackTrace();
				}
				//System.out.println(key+" "+value);
				a.put(key, value);
			}
			return a;

	    }
}

