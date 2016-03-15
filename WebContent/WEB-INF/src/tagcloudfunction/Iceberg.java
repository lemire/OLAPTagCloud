package tagcloudfunction;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import tagcloud.Tag;

/**
 * This class is meant to compute an iceberg of tags.
 * The tags having their weight less than a given value are removed. 
 * @author kamel
 *
 */
public class Iceberg implements TagCloudFunctions{
	double mLowerBound;
	public Iceberg(double value){
		mLowerBound = value;
	}
	public List<Tag> apply(List<Tag> inTag) {
		List<Tag> outTag=new Vector<Tag>();
		Iterator<Tag>it=inTag.iterator();
		while (it.hasNext()){
			Tag currentTag= it.next();
			if (currentTag.getWeight() >= mLowerBound){
				outTag.add(currentTag);
			}
		}
		return outTag;
	}
}
