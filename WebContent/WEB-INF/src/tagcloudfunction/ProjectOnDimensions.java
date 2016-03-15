package tagcloudfunction;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import processdata.Cuboid;
import tagcloud.Tag;
/**
 * This class applies a projection on cuboids given a set of dimensions.
 * Only tags corresponding to those dimensions are retrieved.
 * Tags are obtained by combining those dimensions.
 * Tags may be clustered with respect of similarity dimensions. 
 * 
 * Here's what Daniel thinks this code ought to be doing.
 * Given the following cuboid:
 * 
 * Location | Age | Univerity | measure
 * Montreal | 31  | UQAM     | 13
 * Paris | 36  | Paris VII   | 15
 * Montreal | 31  | Concordia     | 12
 * 
 * Then applying a ProjectOnDimensions using Location and Age on this
 * will give you
 * 
 * Montreal-31 with measure 13 + 12
 * Paris-36 with measure 15
 * 
 * In other words, it is really the Rollup operation
 * on all dimensions but the ones we are projecting on.
 * It is assumed here that the operation applied to the
 * measure is the addition. (Presumably, this could be
 * made a parameter.)
 * 
 * 
 * @author kamel
 */
public class ProjectOnDimensions implements CuboidFunctions{
	String[] mDimensions;
	String[] mSimDimensions;
	// caching mCombinedDim without checking that we are applying it to the same Cuboid
	// object is bad. Caching is good though, but coding it this way makes the code
	// harder to grasp, I find.
	//String[] mCombinedDim;
	
	/**
	 * Constructor with similarity dimensions
	 * @param dimensions
	 * @param simdimensions
	 */
	public ProjectOnDimensions(String[] dimensions, String[] simdimensions){
		mDimensions=dimensions;
		mSimDimensions=simdimensions;
	}
	
	/**
	 * Constructor without similarity dimensions
	 * @param dimensions
	 */
	public ProjectOnDimensions(String[] dimensions){
		mDimensions=dimensions;
		mSimDimensions=null;
	}
	
	public Iterator<Tag> getTags(Cuboid cuboid) {
		//if (mSimDimensions ==null) 
		return generateTags(cuboid,mDimensions);
		//else return generateTags(cuboid,mDimensions,mSimDimensions);
	}
	
	private static String[] computeCombinedDimensions(Cuboid cuboid, String[] dimensions) {
    	String[] measure = cuboid.getCuboidValues().get("measure");
    	String[] combinedDim= new String[measure.length];
    	for(int j=0; j<measure.length;j++){
    		combinedDim[j]=cuboid.getCuboidValues().get(dimensions[0])[j];
    		for(int k=1; k<dimensions.length; k++){
    			combinedDim[j]+="--"+cuboid.getCuboidValues().get(dimensions[k])[j];
    		}
    	}
		return combinedDim;
    			
	}
	
	/**
     * Generates Tags from the current Cuboid given a set of dimensions 
     * @param dimensions
     * @return
     */
    private static Iterator<Tag> generateTags(Cuboid cuboid,String [] dimensions){
    	boolean dimExist=cuboid.getCuboidValues().containsKey(dimensions[0]);
    	if (dimExist==false) throw new RuntimeException("No such dimensions" + dimensions[0]);
    	for (int k=1; k< dimensions.length; k++){
    		dimExist &= cuboid.getCuboidValues().containsKey(dimensions[k]);
    		if (dimExist==false) throw new RuntimeException("No such dimensions" + dimensions[k]);
    	}
    	//dimExist is set to true continue
    	String[] measure = cuboid.getCuboidValues().get("measure");
    	String[] combinedDim = computeCombinedDimensions(cuboid, dimensions);
    	List<Tag> tempTags=new Vector<Tag>();
    	Hashtable<String,Integer> tempMap= new Hashtable<String,Integer>();
    	for (int k=0; k< combinedDim.length; k++){
    		if (tempMap.containsKey(combinedDim[k])){
    			tempMap.put(combinedDim[k], tempMap.get(combinedDim[k])+Integer.parseInt(measure[k]));
    		}else{
    			tempMap.put(combinedDim[k],Integer.parseInt(measure[k]));
    		}
    	}
    	Enumeration<String> en=tempMap.keys();
    	String element;
    	while (en.hasMoreElements()){
    		element = en.nextElement();
    		tempTags.add(new Tag(element,tempMap.get(element)));
    	}
    	return tempTags.iterator();
    }
    
	public String[] getTagDimensions() {
		return mDimensions;
	}

	public String[] getSimilarityDimensions() {
		return mSimDimensions;
	}
}
