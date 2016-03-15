package tagcloud;

import java.util.*;

import org.w3c.dom.*;
//import javax.xml.parsers.*;

/**
 * PROBABLY OBSELETE.
 * 
 * What all tag clouds must implement.
 * 
 * @author lemire
 *
 */
public abstract class TagCloud {
  
  public abstract Iterator<Tag> getTags();
  
  public abstract TagCloud selectTag(Tag t);
  
  public abstract String getName();
  
  /**
   * apply some operation 
   * @param operation
   */
  public abstract TagCloud apply(String operation);
  
  public abstract Document getXML();
  /*public String getHTML() {
	  return CloudUtil.getHTML(this);
  }*/
 
}

