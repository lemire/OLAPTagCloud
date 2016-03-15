package tagcloud;
import java.util.*;


/**
 *  PROBABLY OBSELETE.
 *  
 * A straight-forward application of the Factory pattern.
 * For convenience, this class has only static methods.
 * The assumption being that you will never have several
 * such objects.
 * 
 * @author lemire
 *
 */
public final class TagCloudFactory {
	
  private static Hashtable<String,TagCloud> mContainer 
  = new Hashtable<String,TagCloud>();
  
  /**
   * TODO: add support for operation
   * @param name
   * @param operation
   * @return A tag cloud
   * @throws NoSuchTagCloud
   */
  public static TagCloud getTagCloudByName(String name, String operation) throws NoSuchTagCloud {
	 if(name == null) throw new NoSuchTagCloud("name is null");
	 if(! mContainer.containsKey(name)) {
		 //TagCloud tc = construct(name);
		 //mContainer.put(name, tc);	 
	 }
	 return mContainer.get(name).apply(operation);
  }

  /**
   * TODO: add support for operation, should probably 
   * call the TagCloud object and do something...
   * @param name
   * @param operation
   * @return
   * @throws NoSuchTagCloud
   */
  /*private static TagCloud  (String name) throws NoSuchTagCloud{
	  if(name.equals("fake"))
		  return new FakeTagCloud();
	  else 
		  if (name.equals("hier")) { 
			  return new HierarchicalCloud();
		  }
		  else
		  throw new NoSuchTagCloud(name);
  }*/
}
