package tagcloudfunction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import processdata.Cuboid;
import tagcloud.Tag;

/**
 * This class is meant to roll up a cuboid over a set of dimensions.
 * An iterator on tags corresponding to the remaining dimensions 
 * (after rollup) is returned.
 * Rollup can be viewed as a projection of the cuboid on the remaining dimensions.
 * A similarity function can also be used to cluster the returned tags. 
 * @author kamel
 *
 */
public class Rollup implements CuboidFunctions{
	String[] mRollupDims;
	String[] mSimDimensions;
	String[] mTagDimensions;
	
	/**
	 * Constructor without similarity dimensions
	 * @param dimensions Dimensions over which the rollup is applied
	 */
	public Rollup(String[] dimensions){
		mRollupDims= dimensions;
		mSimDimensions=null;
	}
	
	/**
	 * Constructor with similarity dimensions
	 * @param dimensions Dimensions over which the rollup is applied
	 * @param simdimensions Similarity dimensions
	 */
	public Rollup(String[] dimensions, String[] simdimensions){
		mRollupDims= dimensions;
		mSimDimensions=simdimensions;
	}
	
	
	public Iterator<Tag> getTags(Cuboid cuboid){
		if (mRollupDims.length == cuboid.getDimensions().length){
			String[] copyOfNewDims = new String[mRollupDims.length];
			String[] copyOfmDimenions =new String[cuboid.getDimensions().length];
			System.arraycopy(mRollupDims, 0, copyOfNewDims, 0, mRollupDims.length);
			System.arraycopy(cuboid.getDimensions(), 0, copyOfmDimenions, 0, cuboid.getDimensions().length);
			Arrays.sort(copyOfNewDims);
			Arrays.sort(copyOfmDimenions);
			if (Arrays.equals(copyOfNewDims,copyOfmDimenions)){//All
				/**
				 * should return ALL, aggregated measure value over all dimensions
				 */
				//System.out.println("Aggregate over all the dimenions");
				String[] measure = cuboid.getCuboidValues().get("measure");
				int all=0;
				for(int k=0; k<measure.length;k++){
					all +=Integer.parseInt(measure[k]);
		    	}
				Vector<Tag> out = new Vector<Tag>();
				out.addElement(new Tag("ALL", all));
				return out.iterator();
			}else throw new RuntimeException("No matching between the cuboid's dimensions and the dimensions you wish to rollup over");
		}
		if (mRollupDims.length > cuboid.getDimensions().length) 
			throw new RuntimeException("Number of new dimensions is greater than the current number of dimensions");
		
		Boolean isExist=false;
		Vector<Integer> rollupDimIndices= new Vector<Integer>();
		for (int k=0; k< mRollupDims.length; ++k){
			for (int j=0; j< cuboid.getDimensions().length; ++j){
				if (mRollupDims[k].equals(cuboid.getDimensions()[j])){
					isExist = true;
					rollupDimIndices.add(j);
					break;
				}
			}
			if (isExist == false) throw new RuntimeException("Dimension "+ mRollupDims[k] +" is not found");
		}
		//isExist is set to true
		mTagDimensions = new String[cuboid.getDimensions().length-rollupDimIndices.size()];
		int i=0;
		for (int k=0; k< cuboid.getDimensions().length; ++k){
			if (!rollupDimIndices.contains(new Integer(k))){
				mTagDimensions[i]=cuboid.getDimensions()[k];
				++i;
			}
		}
		if (mSimDimensions == null)
			return cuboid.getTagCloudModel(new ProjectOnDimensions(mTagDimensions)).getTags();
		else 
			return cuboid.getTagCloudModel(new ProjectOnDimensions(mTagDimensions,mSimDimensions)).getTags();
	}

	public String[] getTagDimensions() {
		return mTagDimensions;
	}

	public String[] getSimilarityDimensions() {
		return mSimDimensions;
	}
}
