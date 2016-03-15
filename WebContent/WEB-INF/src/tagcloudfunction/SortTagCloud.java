package tagcloudfunction;

import java.util.Collections;
import java.util.List;

import tagcloud.Tag;

/**
 * This class sorts a tag cloud by text or weight (desc or asc).
 * This function is not compatible with the optimization function.
 * @author kamel
 * 
 */

public class SortTagCloud implements TagCloudFunctions{
	CompareTags mCompareFunction;
	public SortTagCloud(String att, String type){
		mCompareFunction = new CompareTags(att,type);
	}
	
	public SortTagCloud(String att){
		mCompareFunction = new CompareTags(att);
	}
	
	public SortTagCloud(){
		mCompareFunction = new CompareTags();
	}
	public List<Tag> apply(List<Tag> inTag) {
		Collections.sort(inTag, mCompareFunction);
		return inTag;
	}
}
