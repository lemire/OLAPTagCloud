package tagcloud;

/**
 * PROBABLY OBSELETE.
 * 
 * This exception is thrown when it is really impossible
 * to create a given tag cloud by name.
 * 
 * @author lemire
 *
 */
public class NoSuchTagCloud extends Exception {
  static final long serialVersionUID= 1;
  public NoSuchTagCloud(String error){
	  super(error);
  }
}
