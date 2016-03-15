package tagcloudfunction;

import java.util.Iterator;
import processdata.Cuboid;
import tagcloud.Tag;

/**
 * This class is meant to drill down a cuboid over a set of dimensions.
 * An iterator on tags corresponding to the remaining dimensions 
 * (after drilling down) is returned.
 * Drill-down opeartion can be viewed as a projection of the cuboid on the dimensions obtainded by merging  
 * the dimensions being drilled down  and the current cuboid's one.
 * Drill down function needs to know its previous contexte to be correctly applied.
 * A similarity function can also be applied.
 * @author kamel
 *
 */
public class Drilldown implements CuboidFunctions{
	String[] mSimDimensions;
	String[] mDrillDownDims;
	String[] previousDims;
	String [] newDims;
	
	/**
	 * Constructor without similarity dimensions
	 * @param dimensions Dimensions over which we apply the rollup
	 * @param previousdimensions The previous tag-support dimenisons of the current cuboid
	 */
	public Drilldown(String[] dimensions, String[] previousdimensions){
		mSimDimensions=null;
		mDrillDownDims=dimensions;
		previousDims=previousdimensions;
	}
	
	/**
	 * Constructor with similarity dimensions
	 * @param dimensions Dimensions over which we apply the rollup function
	 * @param simdimensions Similarity dimensions
	 * @param previousdimensions The previous tag-support dimensions of the current cuboid
	 */
	public Drilldown(String[] dimensions, String[] previousdimensions, String[] simdimensions){
		mSimDimensions=simdimensions;
		mDrillDownDims=dimensions;
		previousDims=previousdimensions;
	}

	public Iterator<Tag> getTags(Cuboid cuboid) {
		if (mDrillDownDims.length==0) throw new RuntimeException("No dimension");
		if ((mDrillDownDims.length + previousDims.length) > cuboid.getDimensions().length)
			throw new RuntimeException("The number of dimensions is  greater than the cuboid's one");
		boolean dimExist=cuboid.getCuboidValues().containsKey(mDrillDownDims[0]);
    	if (dimExist==false) throw new RuntimeException("No such dimensions" + mDrillDownDims[0]);
    	for (int k=1; k< mDrillDownDims.length; k++){
    		dimExist &= cuboid.getCuboidValues().containsKey(mDrillDownDims[k]);
    		if (dimExist==false) throw new RuntimeException("No such dimensions" + mDrillDownDims[k]);
    	}
    	
    	//Merge drill dow dimensions with  the current active cuboid dimensions
    	newDims = new String[mDrillDownDims.length+previousDims.length]; 
    	System.arraycopy(previousDims, 0, newDims, 0, previousDims.length);
    	System.arraycopy(mDrillDownDims, previousDims.length, newDims, 0, mDrillDownDims.length);
		
    	if (mSimDimensions == null)
			return cuboid.getTagCloudModel(new ProjectOnDimensions(newDims)).getTags();
		else 
			return cuboid.getTagCloudModel(new ProjectOnDimensions(newDims,mSimDimensions)).getTags();
	}

	public String[] getTagDimensions() {
		return newDims;
	}

	public String[] getSimilarityDimensions() {
		return mSimDimensions;
	}
}
