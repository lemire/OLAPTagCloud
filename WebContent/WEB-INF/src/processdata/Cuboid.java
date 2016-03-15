package processdata;

import java.util.Hashtable;
import java.util.Iterator;
//import util.*;
import tagcloud.Tag;
import tagcloud.TagCloudModel;
//import tagcloud.TagSimilarity;
import tagcloudfunction.CuboidFunctions;

/**
 * (Daniel wonders why an in-memory model was chosen. One could have
 * created temporary tables in the database instead?)
 * 
 * 
 * Cuboid is a logical model of the stored data.
 * It's composed of a set of dimensions and the corresponding aggregated measure. 
 * Cuboid is meant to compute the top-n grouping set from a given a data set
 * Cuboid adopts the logical model of tag cloud defined in the paper
 * Cuboid (dataset, dimensions, measure) measure is not necessary given, 
 * by default counting is used
 * @author kamel
 *
 */
public abstract class Cuboid {
	
	protected String mDatasetID;
	protected String mDescription;
	protected String[] mDimensions;
	protected static int LIMIT =150;
	public Hashtable<String,String[]>  mCuboidValues;
	
	/**
	 * Protected constructor so that FakeCuboid and other
	 * derived classes can work.
	 *
	 */
	protected Cuboid() {}
	
	/**
	 * Getting tags 
	 * function may be rollup, project on dimensions, slice, dice
	 * default function is project on dimensions 
	 */
	public Iterator<Tag> getTags(CuboidFunctions fnc) {
    	return fnc.getTags(this);
	}
	
	/**
	 * Getting a tag cloud model by applying 
	 * a function on the current cuboid.
	 * @param fnc
	 * @return A tag cloud model
	 */
    public TagCloudModel  getTagCloudModel(CuboidFunctions fnc) {
    	return new TagCloudModel(this,fnc);
	}
	
	/**
	 * Getting the values of each cuboid's cell 
	 * @return Cells of the cuboid as a Hashtable
	 */
	public Hashtable<String,String[]> getCuboidValues(){
		return mCuboidValues;
	}
	
	/**
	 * Getting the cuboid's dimensions 
	 * @return all dimensions as an array of strings
	 */
	public String[] getDimensions(){
		return mDimensions;
	}
	
	/**
	 * Getting data source name
	 * It identifies each cube stored in our database
	 * @return String name of the data set
	 */
    public String getDataSetName(){
    	return mDatasetID;
    }
    
	/**
	 * Description of the corresponding cube
	 * @return String Data description
	 */
	public String getDataSetDescription(){
		return mDescription;
	}
    
    /**
     * To set max number of tags we should compute from the data base.
     * This number corresponds to the limit parameter of SQL queries.
     * Default value is set to 150. 
     * So, we can have up to 150 tags per tag clouds.
     * @param value
     */
    public static void setLIMIT(int value){
    	LIMIT=value;
    }

    public int getNumberOfDimensions(){
    	return mCuboidValues.size();
    }
    public int getSize(){
    	return mCuboidValues.values().iterator().next().length;
    }
}
