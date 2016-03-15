package tagcloud.optimization;

import java.util.*;

import tagcloud.*;

/**
 * This is the simplest of the optimal-linear-arrangement tag cloud
 * optimizer. You need to provide a TagSimilarity object.
 * It runs in O(n^2) time so it might be expensive.
 * 
 * TODO implement Monte Carlo variant
 * TODO add placement hints support
 * @author lemire
 *
 */
public class NearestNeighbour extends LinearArrangement {
	  public NearestNeighbour(TagSimilarity ts) {
		  super(ts);
	  }
	  
	  /**
	   * Given a list of tags, generate a new (optimized) list
	   * of tags. The provided list will be empty at the end of the run.
	   */
	  public List<Tag> optimize(List<Tag> tc) {
		  if(tc.size() == 0 ) {
			  System.out.println("[NearestNeighour] Got an empty list of tags... bug?");
			  return tc;
		  }
		  if (tc.size() ==1) return tc;
		  List<Tag> out = new Vector<Tag>();
		  Tag current = tc.get(0);
		  out.add(current);
		  tc.remove(current);
		  while(tc.size() > 0) {
			  double besttag = Double.NEGATIVE_INFINITY;
			  Tag nexttag = null;
			  for(Tag t : tc) {
				  double s = mTagSimilarity.similarity(current,t);
				  if(s> besttag) {
					 nexttag = t; 
					 besttag = s; 
				  }
			  }
			  //System.out.println("going from "+current+" to "+ nexttag+ " sim="+besttag);
			  current = nexttag;
			  out.add(current);
			  tc.remove(current);
		  }		  
		  return out;
		  //return null;
	  }
}
