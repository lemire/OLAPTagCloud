package processdata;

import java.util.*;
import tagcloud.*;
//import util.*;

/**
 * A FakeCuboid is just something useful to create a demo.
 * 
 * @author lemire
 *
 */
public class FakeCuboid  extends Cuboid {
	//
	String[] dim1       = {"共", "Québec","Australia","Austria","Belgium","Canada","Czech Republic","Denmark","Finland","France","Germany","Greece","Hungary","Iceland","Ireland","Italy","Japan","Korea","Luxembourg","Mexico","Netherlands","New Zealand","Norway","Poland","Portugal","Slovak Republic","Spain","Sweden","Switzerland","Turkey","United Kingdom","United States","Slovenia"};
	String[] dim1rollup = {"Asia", "American", "Oceania","Europe","Europe","America","Europe","Europe","Europe","Europe","Europe","Europe","Europe","Europe","Europe","Europe","Asia","Asia","Europe","America","Europe","Oceania","Europe","Europe","Europe","Europe","Europe","Europe","Europe","Europe","Europe","America","Europe"};

	int[] measures = {31462,32843,31675,31751,18314,32232,30361,29287,28732,27412,15548,31897,31151,27586,29739,20771,53299,9989,34527,23205,42062,12511,19029,14708,25672,31007,37638,7186,32470,39590,21268};
	/**
	 * kamel : I need measure to be an array of strings because data retrieved from DB are all in string format
	 */
	String[] measure = {"1", "2", "31462","32843","31675","31751","18314","32232","30361","29287","28732","27412",
			"15548","31897","31151","27586","29739","20771","53299","9989","34527","23205","42062","12511",
			"19029","14708","25672","31007","37638","7186","32470","39590","21268"};
    
	Vector<Tag> temp;
    //Hashtable<String,String> mMap;
    public FakeCuboid() {
    	mDatasetID="fake";
    	//temp = new Vector<Tag>();
    	/*mMap = new Hashtable<Pair<String,String>,Double>();
    	for(int k = 0; k < dim1.length;++k){
    		for(int k2 = 0; k2 < dim1.length; ++k2) {
    			//temp.add(new Tag(dim1[k],measures[k]));1
    	    	//mMap.put(dim1[k],dim1rollup[k]);
    			//
    			double m = Double.parseDouble(measure[k]);
    			double m2 = Double.parseDouble(measure[k2]);
    			if(dim1rollup[k].equals(dim1rollup[k2])) {
    	        	if(Math.abs(m-m2)<0.0001)
    					mMap.put(new Pair<String,String>(dim1[k],dim1[k2]), 100000000.0);
    	        	else 
    	        		mMap.put(new Pair<String,String>(dim1[k],dim1[k2]), (m+m2)/Math.abs(m-m2) );
    			} else 
    	        	mMap.put(new Pair<String,String>(dim1[k],dim1[k2]), -1000.0);
    		}
    	}*/
    	mCuboidValues = new Hashtable<String,String[]>();
    	mCuboidValues.put("country", dim1);
    	mCuboidValues.put("continent", dim1rollup);
    	mCuboidValues.put("measure", measure);
    	mDimensions= new String[]{"country","continent"};
    }
    
    
		/*if(mMap.get(t.getText()).equals(mMap.get(s.getText()))) {
	if (Math.abs(t.getWeight() - s.getWeight())<0.00001)
		return 100000000;
	else return (t.getWeight() + s.getWeight()) / Math.abs(t.getWeight() - s.getWeight()) ; 
} else return -1000.0;*/

    public String getDataSetDescription(){
		return "Gross national income per capita, 2004, according to swivel.com";
	}
    
    /**
     * PROBABLY OBSELETE.
     * Compute the projection on a dimension.
     * @param dimension
     */
    /*public Iterator<Tag> getTags(String dimension) {
    	if(dimension.equals("country"))
    		return temp.iterator();
    	else throw new RuntimeException("No such dimension: "+dimension);
    }*/
    
    /**
     * PROBABLY OBSELETE.
     */
    /*public Iterator<Tag> getTags(String[] dimension) {
    	if(dimension[0].equals("country"))
    		return temp.iterator();
    	else throw new RuntimeException("No such dimension: "+dimension);
    }*/
}
