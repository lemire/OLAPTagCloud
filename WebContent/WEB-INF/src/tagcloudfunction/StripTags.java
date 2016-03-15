package tagcloudfunction;

import java.util.Iterator;
import java.util.List;
import tagcloud.Tag;

/**
 * This class removes tags from a given tag cloud.
 * The tags to remove are retrieved by their texts corresponding to the constructor parameter.  
 * @author kamel
 *
 */
public class StripTags implements TagCloudFunctions{
	String[] mAttributeValues;
	
	public StripTags(String[] att){
		mAttributeValues=att;
	}
	
	public List<Tag> apply(List<Tag> inTag) {
		for(int k=0; k< mAttributeValues.length; ++k){
			Iterator<Tag>it=inTag.iterator();
			while (it.hasNext()){
				Tag currentTag=it.next();
				 if((mAttributeValues[k].equals(currentTag.getFormattedText()))){
					 inTag.remove(currentTag);
					 break;
				 }
			}
		}
		return inTag;
	}
}
