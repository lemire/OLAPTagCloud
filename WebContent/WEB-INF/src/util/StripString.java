package util;

import java.util.regex.Pattern;
/**
 * Some useful methods to apply on strings to prevent javascript injection.
 * For example, tags <script></script>  are removed. 
 * @author kamel
 *
 */
public class StripString {
	private static final Pattern htmlTagPattern =
	    Pattern.compile("</?[a-zA-Z][^>]*>");
	
	/**
	   * Given a <code>String</code>, returns an equivalent <code>String</code> with
	   * all HTML tags stripped. Note that HTML entities, such as "&amp;amp;" will
	   * still be preserved.
	   */
	  public static String stripHtmlTags(String string) {
	    if ((string == null) || "".equals(string)) {
	      return string;
	    }
	    return htmlTagPattern.matcher(string).replaceAll("");
	  }
	  
	  /**
	   * Given a String with < >, returns an equivalent String with
	   * all <,> ' " charcters stripped.
	   */
	  public static String toCleanUp(String in){
		  String out;
		  out = in.replaceAll("&", "&amp;");
		  out = out.replaceAll("<", "&lt;");
		  out = out.replaceAll(">", "&lt;");
		  out = out.replaceAll("(\r\n|\n|\r|\u0085|\u2028|\u2029)", "\n");
		  out = out.replaceAll("\'","\\\\'");
		  out = out.replaceAll("\"","\\\\\"");
		  return out;
	  }
	  
	  public static String[] toCleanUp(String[] in){
		  String [] out = new String[in.length];
		  for (int k=0; k < in.length; k++){
			  out[k] = in[k].replaceAll("&", "&amp;");
			  out[k] = out[k].replaceAll("<", "&lt;");
			  out[k] = out[k].replaceAll(">", "&lt;");
			  out[k] = out[k].replaceAll("(\r\n|\n|\r|\u0085|\u2028|\u2029)", "\n");
			  out[k] = out[k].replaceAll("\'","\\\\'");
			  out[k] = out[k].replaceAll("\"","\\\\\"");
		  }
		  return out;
	  }

}
