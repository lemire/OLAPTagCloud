package tagcloud.optimization;

import tagcloud.*;
import java.util.*;

/**
 * Abstract class that will serve as the super class of all
 * "optimal linear arrangement" tag cloud optimizers. See
 * NearestNeighbour as an example.
 * 
 * @author lemire
 *
 */
public abstract class LinearArrangement {
  protected TagSimilarity mTagSimilarity;
  public LinearArrangement(TagSimilarity ts) {
	  mTagSimilarity = ts;
  }
  
  /**
  * Given a list of tags, generate a new (optimized) list
  * of tags. The provided list may be empty at the end of the run.
  */
  public abstract List<Tag> optimize(List<Tag> tc);
  
  
  /**
   * this is the linear arrangement cost function we
   * seek to minimize (its computation can be a tag expensive)
   * @return Double similarity
   */
  public double cost(List<Tag> tc) {
	  int s = tc.size();
	  double answer = 0;
	  for(int k1 = 0; k1 < s; ++k1) {
		  Tag t1 = tc.get(k1);
		  for(int k2 = 0; k2 < s; ++k2)
			  answer += Math.abs(k1-k2)* mTagSimilarity.similarity(t1, tc.get(k2));
	  }
	  return answer;
  }

  public double similarityMatrixDensity(List<Tag> tc) {
	  int s = tc.size();
	  int counter = 0;
	  for(int k1 = 0; k1 < s; ++k1) {
		  Tag t1 = tc.get(k1);
		  for(int k2 = 0; k2 < s; ++k2)
			  if(Math.abs(mTagSimilarity.similarity(t1, tc.get(k2)))> 0.0001)
				  ++counter;
	  }
	  return  counter /(double)(s*s) ;
  }
  public void printSimilarityMatrix(List<Tag> tc) {
	  int s = tc.size();
	  for(int k1 = 0; k1 < s; ++k1) {
		  Tag t1 = tc.get(k1);
		  String line = "";
		  for(int k2 = 0; k2 < s; ++k2)
			  line+=  mTagSimilarity.similarity(t1, tc.get(k2))+ " ";
		  System.out.println(line);
	  }
	  
  }
  
}
