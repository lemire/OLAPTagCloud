package tagcloud;

import java.util.Hashtable;

import util.Pair;


public class TagSimilarityInstance implements TagSimilarity {
	Hashtable<Pair<String,String>,Double> simMap;
	public TagSimilarityInstance(Hashtable<Pair<String,String>,Double> simMap) {
		this.simMap = simMap;
	}
	public double similarity(Tag t, Tag s) {
		return simMap.get(new Pair<String,String>(t.getText(),s.getText())).doubleValue();
	}
}