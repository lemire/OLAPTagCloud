package tagcloudfunction;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import processdata.Cuboid;
import tagcloud.*;

/**
 * This class applies a dicing operation over a set of dimensions, given their attribute values.
 * We can see dicing as a projection followed by stripping some tags (corresponding to
 * attribute values).
 * A similarity function can also be used to cluster the returned tags. 
 * @author kamel
 */
public class Dice implements CuboidFunctions{
	String[] mCellValues;
	String[] mDimToDice;
	String[] mSimDimensions;
	
	/**
	 * Constructor without similarity dimensions
	 * @param dimstodice
	 * @param cellvalues
	 */
	public Dice(String[] dimstodice, String[] cellvalues){
		mCellValues = cellvalues;
		mDimToDice=dimstodice;
		mSimDimensions=null;
	}
	
	/**
	 * Constructor with similarity dimensions
	 * @param dimstodice
	 * @param cellvalues
	 * @param simdimensions
	 */
	public Dice(String[] dimstodice, String[] cellvalues, String[] simdimensions){
		mCellValues = cellvalues;
		mDimToDice=dimstodice;
		mSimDimensions=simdimensions;
	}
	
	public Iterator<Tag> getTags(Cuboid cuboid) {
		List<Tag> listOfTags = new Vector<Tag>();
		Iterator<Tag> i;
		if(mSimDimensions ==null)
			i = cuboid.getTags(new ProjectOnDimensions(mDimToDice));
		else
			i = cuboid.getTags(new ProjectOnDimensions(mDimToDice,mSimDimensions));
		
		while(i.hasNext())
			listOfTags.add(i.next());
		return new StripTags(mCellValues).apply(listOfTags).iterator();
	}

	public String[] getTagDimensions() {
		return mDimToDice;
	}

	public String[] getSimilarityDimensions() {
		return mSimDimensions;
	}
}
