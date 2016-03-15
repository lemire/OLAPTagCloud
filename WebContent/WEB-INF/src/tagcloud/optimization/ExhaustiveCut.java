package tagcloud.optimization;

import java.util.List;
import java.util.Vector;

import tagcloud.Tag;
import tagcloud.TagSimilarity;
import util.*;

public class ExhaustiveCut  extends NearestNeighbour {
	  private static final boolean verbose = false;

	  int mCutTimes = 10;
	  java.util.Random generator = new java.util.Random();
	  public ExhaustiveCut(TagSimilarity ts) {
		  super(ts);
	  }
	  public ExhaustiveCut(TagSimilarity ts, int cuttimes) {
		  super(ts);
		  mCutTimes = cuttimes;
	  }
	  

	  /**
	   * Given a list of tags, generate a new (optimized) list
	   * of tags. The provided list will be empty at the end of the run.
	   */
	  public List<Tag> optimize(List<Tag> tc) {
		  if(mCutTimes == 0) return tc;
		  List<Tag> answer = super.optimize(tc);
		  double costbefore = cost(answer);
		  Pair<List<Tag>,Double> tcpair = new Pair<List<Tag>,Double>(answer,new Double(costbefore));
		  for(int k = 0 ; k <mCutTimes;++k){
			  tcpair = cut(tcpair);
		  }
		  return tcpair.first();
	  }
	  

	  public Pair<List<Tag>,Double> cut(Pair<List<Tag>,Double> tcpair) {
		  List<Tag> tc = tcpair.first();
		  double costbefore = tcpair.second().doubleValue();
		  if(tc.size() < 1 ) //This should not happen 
			  throw new RuntimeException("[MonteCarlo -> cut] Got an empty list of tags... bug?");
		  for(int randcut = 1; randcut <tc.size()-1;++randcut) {
			  //System.out.println("random "+randcut);
			  List<Tag> shuffled = flip(tc,randcut);
			  double costafter = cost(shuffled);
			  assert(shuffled.size() == tc.size());
			  if(verbose) System.err.println("cut : "+randcut+" diff. = "+ (costbefore-costafter)+ " "+ (costbefore-costafter>0?"**yes**":""));
			  if(costafter < costbefore) {
				  return new Pair<List<Tag>,Double>(shuffled,new Double(costafter));
			  }
		  } 
		  return tcpair;
	  }
	  
	  
	  public static List<Tag> flip(List<Tag> tc, int cut) {
		  final List<Tag> firstpart = tc.subList(0, cut);
		  List<Tag> shuffled = new Vector<Tag>(tc.subList(cut, tc.size()));
		  shuffled.addAll(firstpart);
		  return shuffled;
	  }
	  
}