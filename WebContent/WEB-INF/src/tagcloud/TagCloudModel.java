package tagcloud;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import processdata.*;
import tagcloudfunction.CuboidFunctions;
import util.*;
/**
 * This is the actual tag cloud model. It is
 * independent of the visual representation.
 * To simplify things, we can say that a 
 * TagCloudModel is a dimension and cuboid (see paper
 * for more elaborate discussion).
 * 
 * @author lemire
 *
 */
public class TagCloudModel {
	Cuboid mMyCuboid;
	CuboidFunctions mMyFunction;
	
	
	public TagCloudModel(Cuboid c, CuboidFunctions fnc) {
		mMyCuboid = c;
		mMyFunction=fnc;
	}
	
	public TagCloudView getView() {
		return new TagCloudView(this);
	}
	
	public Iterator<Tag> getTags() {
		return mMyFunction.getTags(mMyCuboid);
	}
	
	public String getDataSetName(){
		return mMyCuboid.getDataSetName();
	}
	
	public String getDataSetDescription(){
		return mMyCuboid.getDataSetDescription();
	}
	
	/**
	 * Hopefully, this computes the cosine similarity.
	 * 
	 * 
	 * @return
	 */
	public TagSimilarity getSimilarity(SimilarityMeasure sm) {
		// making sure the sim dims are sane when applied to this cuboid?
		// (this is needed because the function can be applied to different
		// cuboids and some of them may not have the desired 
		// similari ty dimensions?)
		boolean dimExist =mMyCuboid.getCuboidValues().containsKey(mMyFunction.getSimilarityDimensions()[0]);
    	if (dimExist==false) throw new RuntimeException("No such dimensions" + mMyFunction.getSimilarityDimensions()[0]);
    	for (int k=1; k< mMyFunction.getSimilarityDimensions().length; k++){
    		dimExist &= mMyCuboid.getCuboidValues().containsKey(mMyFunction.getSimilarityDimensions()[k]);
    		if (dimExist==false) throw new RuntimeException("No such dimensions" + mMyFunction.getSimilarityDimensions()[k]);
    	}
    	
		String[] measure = mMyCuboid.getCuboidValues().get("measure");
		// yes, we are going to recompute the combined dimensions, a bit of a wasted
		// effort, but it makes things safer and clearer... we can go back and cache this
		// later...
		// next line is a cheat, should not be called mCombinedDim
		
		String[] combinedDimValues = computeCombinedDimensions(mMyCuboid,mMyFunction.getTagDimensions());
		/**
		 * This hashtable stores each distinct value of the composite dimension
		 * and the corresponding indices where this value appears in the cuboid
		 */
		// Daniel: the tricky thing is that this mCombinedDim array can actually store
		// twice the same value, so it is not a set! (This not necessarily a bad design though.)
		//
    	Hashtable<String,Vector<Integer>> tempMap= new Hashtable<String,Vector<Integer>>();
    	for(int j=0; j< measure.length;j++){
    		if (tempMap.containsKey(combinedDimValues[j])){
    			Vector<Integer> tmp = tempMap.get(combinedDimValues[j]);
    			tmp.add(j);
    			tempMap.put(combinedDimValues[j], tmp);
    		}else{
    			Vector<Integer> tmp = new Vector<Integer>();
    			tmp.add(j);
    			tempMap.put(combinedDimValues[j], tmp);
    		}
    	}
    	
    	// Daniel saves some code on the next line by reusing code.
    	String[] combinedSimValues = computeCombinedDimensions(mMyCuboid,mMyFunction.getSimilarityDimensions()); 
    	Hashtable<Pair<String,String>,Double> simMap = new Hashtable<Pair<String,String>,Double>();
    	
    	String [] distinctCombinedDimValues =  (String[]) tempMap.keySet().toArray(new String[0]);
    	for(int k1=0; k1<distinctCombinedDimValues.length; ++k1)
    		for (int k2=0; k2< distinctCombinedDimValues.length; ++k2){
    			Pair<String,String> p = new Pair<String,String>(distinctCombinedDimValues[k1],distinctCombinedDimValues[k2]);
    			Pair<String,String> psym = new Pair<String,String>(distinctCombinedDimValues[k2],distinctCombinedDimValues[k1]);
    			if (simMap.containsKey(p)==false){
    				if(distinctCombinedDimValues[k1].equals(distinctCombinedDimValues[k2])){
    					// Daniel: next line is true only with sane similarity measures.
    					simMap.put(p, 1.0);
    					simMap.put(psym, 1.0);
    					//System.out.println("Similarity "+distinctCombinedDimValues[k1] + " "+ distinctCombinedDimValues[k2] +" = 1.0");
    				}else{
    					Vector<Integer> indice1 = tempMap.get(distinctCombinedDimValues[k1]);
    					Vector<Integer> indice2 = tempMap.get(distinctCombinedDimValues[k2]);
    					double sumsqWeight1=0;
    					double sumsqWeight2=0;
    					double sumweightproduct=0;
    					double sim=0;

    					Hashtable<String,Integer> tempindice1 = new Hashtable<String,Integer>();
    					for (int j=0; j<indice1.size(); ++j){
    						if (tempindice1.contains(combinedSimValues[indice1.elementAt(j)])){
    							tempindice1.put(combinedSimValues[indice1.elementAt(j)], tempindice1.get(combinedSimValues[indice1.elementAt(j)])+Integer.parseInt(measure[indice1.elementAt(j)]));
    						}else{
    							tempindice1.put(combinedSimValues[indice1.elementAt(j)], Integer.parseInt(measure[indice1.elementAt(j)]));
    						}
    					}

    					Hashtable<String,Integer> tempindice2 = new Hashtable<String,Integer>();
    					for (int j=0; j<indice2.size(); ++j){
    						if (tempindice2.contains(combinedSimValues[indice2.elementAt(j)])){
    							tempindice2.put(combinedSimValues[indice2.elementAt(j)], tempindice2.get(combinedSimValues[indice2.elementAt(j)])+Integer.parseInt(measure[indice2.elementAt(j)]));
    						}else{
    							tempindice2.put(combinedSimValues[indice2.elementAt(j)], Integer.parseInt(measure[indice2.elementAt(j)]));
    						}
    					}

    					Enumeration<String> en1=tempindice1.keys();
    					while (en1.hasMoreElements()){
    						String element1 = en1.nextElement();
    						Enumeration<String> en2=tempindice2.keys();
    						while (en2.hasMoreElements()){
    							String element2 = en2.nextElement();
    							if (element1.equals(element2)){
    								sumweightproduct+=((double)tempindice1.get(element1)*(double)tempindice2.get(element2));
    							}
    						}

    					}

    					if (sumweightproduct !=0.0) {
    						Enumeration<String> en=tempindice1.keys();
    						while (en.hasMoreElements()){
    							sumsqWeight1+=(double)Math.pow(tempindice1.get(en.nextElement()), 2);
    						}

    						en=tempindice2.keys();
    						while (en.hasMoreElements()){
    							sumsqWeight2+=(double)Math.pow(tempindice2.get(en.nextElement()), 2);
    						}
    						sim = sm.similarity(sumweightproduct, sumsqWeight1, sumsqWeight2);
    					}
    					simMap.put(p,sim);
    					simMap.put(psym, sim);
    					//System.out.println("Similarity "+distinctCombinedDimValues[k1] + " "+ distinctCombinedDimValues[k2] +" = " + sim);
    				}
    			}
    		}
    	return new TagSimilarityInstance(simMap);
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
}

