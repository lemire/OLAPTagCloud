package tagcloud.optimization;

import java.util.List;

import tagcloud.Tag;
import tagcloud.TagSimilarity;

public class PointWiseMonteCarlo  extends NearestNeighbour {

	  int mSwitchTimes = 100;
	  java.util.Random generator = new java.util.Random();
	  public PointWiseMonteCarlo(TagSimilarity ts) {
		  super(ts);
	  }
	  public PointWiseMonteCarlo(TagSimilarity ts, int cuttimes) {
		  super(ts);
		  mSwitchTimes = cuttimes;
	  }
	  

	  /**
	   * Given a list of tags, generate a new (optimized) list
	   * of tags. The provided list will be empty at the end of the run.
	   */
	  public List<Tag> optimize(List<Tag> tc) {
		  if(tc.size()==0) return tc;
		  List<Tag> answer = super.optimize(tc);
		  if(mSwitchTimes == 0) return answer;
		  for(int k = 0 ; k <mSwitchTimes;++k){
			  int p1 = generator.nextInt(answer.size());
			  int p2 = generator.nextInt(answer.size());
			  if(cost(answer,p1,p2)>0) {
				  Tag t1 = answer.get(p1);
				  Tag t2 = answer.get(p2);
				  answer.set(p1, t2);
				  answer.set(p2, t1);
			  }
		  }
		  return answer;
	  }
	  
	  /**
	   * cost contribution
	   * @param tc
	   * @param k1
	   * @return
	   */
	  public double cost(List<Tag> tc, int k1, int k2) {
		  int s = tc.size();
		  double before = 0;
		  double after = 0;
		  Tag t1 = tc.get(k1);
		  Tag t2 = tc.get(k2);
		  for(int k3 = 0; k3 < s; ++k3) {
			  if( (k3 == k1) || (k3 == k2) ) continue;
			  Tag t3 = tc.get(k3);
			  double s13 = mTagSimilarity.similarity(t1, t3);
			  double s23 = mTagSimilarity.similarity(t2, t3);
			  before += Math.abs(k1-k3)* s13;
			  before += Math.abs(k2-k3)* s23;
			  after += Math.abs(k1-k3)* s23;
			  after += Math.abs(k2-k3)* s13;
		  }
		  return before-after;
	  }

	  
	  
}
