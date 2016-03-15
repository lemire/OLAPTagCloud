package tagcloudfunction;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import processdata.Cuboid;
import tagcloud.*;

/**
 * This class applies a slicing on cuboids, given the dimension to slice 
 * and the corresponding attribute values. 
 * @author kamel
 */
public class Slice implements CuboidFunctions{
	String mDimToSlice;
	String[] mCurrentDims;
	String[] mNewDims;
	String[] mAttributes;
	String[] mSimDimensions;
	
	/**
	 * Constructor without similarity dimensions
	 * @param current tag-support dimensions
	 * @param dimtoslice
	 * @param attributevalues
	 */
	public Slice(String[] dimensions, String dimtoslice, String[] attributevalues){
		mDimToSlice=dimtoslice;
		mCurrentDims=dimensions;
		mAttributes=attributevalues;
		mSimDimensions=null;
	}
	
	/**
	 * Constructor with similarity dimensions
	 * @param dimtoslice
	 * @param current tag-support dimensions
	 * @param attributevalues
	 * @param simdimensions
	 */
	public Slice(String[] dimensions, String dimtoslice, String[] attributevalues, String[] simdimensions){
		mDimToSlice=dimtoslice;
		mCurrentDims=dimensions;
		mAttributes=attributevalues;
		mSimDimensions=simdimensions;
	}
	
	public Iterator<Tag> getTags(Cuboid cuboid) {
		return generateTags(cuboid);
	}
	
	private static String[] computeCombinedDimensions(Cuboid cuboid, String[] dimensions) {
    	//String[] measure = cuboid.getCuboidValues().get("measure");
    	String[] combinedDim= new String[cuboid.getCuboidValues().get("measure").length];
    	for(int j=0; j<combinedDim.length;j++){
    		combinedDim[j]=cuboid.getCuboidValues().get(dimensions[0])[j];
    		for(int k=1; k<dimensions.length; k++){
    			combinedDim[j]+="--"+cuboid.getCuboidValues().get(dimensions[k])[j];
    		}
    	}
		return combinedDim;
    			
	}
	
	protected Iterator<Tag> generateTags(Cuboid cuboid){
		if (! cuboid.getCuboidValues().containsKey(mDimToSlice)) throw new RuntimeException("Dimension "+ mDimToSlice +" not found");
		//Dimension exists
		String[] measure = cuboid.getCuboidValues().get("measure");
		String[] dimToSliceValues= cuboid.getCuboidValues().get(mDimToSlice);
		Vector<Integer> indiceOfRemainingCells=new Vector<Integer>();
		for(int i=0; i< dimToSliceValues.length; ++i)
			indiceOfRemainingCells.addElement(i);
		for(int k=0; k< mAttributes.length; ++k){
			for(int j=0; j< dimToSliceValues.length;++j){
				if (mAttributes[k].equals(dimToSliceValues[j])){
					indiceOfRemainingCells.remove(new Integer(j));
				}
			}
		}
		
		if (mCurrentDims.length-1==0){
			int all=0;
			for(int j=0;j<indiceOfRemainingCells.size();++j){
				all +=Integer.parseInt(measure[indiceOfRemainingCells.elementAt(j)]);
			}
			Vector<Tag> out = new Vector<Tag>();
			out.addElement(new Tag("ALL", all));
			return out.iterator();
		}
		int i=0;
		mNewDims = new String[mCurrentDims.length-1];
		for(int k=0; k < mCurrentDims.length ; ++k){
			if (!mCurrentDims[k].equals(mDimToSlice)){
				mNewDims[i]=mCurrentDims[k];
				++i;
			}	
		}
		String [] combindedDims=computeCombinedDimensions(cuboid,mNewDims);
		Hashtable<String,Integer> tempMap= new Hashtable<String,Integer>();
		
		for(int j=0;j<indiceOfRemainingCells.size();++j){
			String dimvalue = combindedDims[indiceOfRemainingCells.elementAt(j)];
			int measurevalue = Integer.parseInt(measure[indiceOfRemainingCells.elementAt(j)]);
			if(tempMap.contains(dimvalue)){
				tempMap.put(dimvalue, tempMap.get(dimvalue)+measurevalue);
			}else{
				tempMap.put(dimvalue,measurevalue);
			}
		}
		Enumeration<String> en=tempMap.keys();
		String element;
		List<Tag> tempTags= new Vector<Tag>();
		while (en.hasMoreElements()){
			element = en.nextElement();
			tempTags.add(new Tag(element,tempMap.get(element)));
		}
		return tempTags.iterator();
	}
	
	public String[] getTagDimensions() {
		return mNewDims;
	}

	public String[] getSimilarityDimensions() {
		return mSimDimensions;
	}
}
