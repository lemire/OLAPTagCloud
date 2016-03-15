package tagcloudfunction;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import tagcloud.Tag;

/**
 * This class retrieves the top-N tags
 * This function may be not compatible with the optimization function.
 * @author kamel
 * 
 */
public class TopN implements TagCloudFunctions{
	int mN;
	public TopN(int n){
		mN=n;
	}

	public List<Tag> apply(List<Tag> inTag) {
		if (mN >= inTag.size()) return inTag;
		Collections.sort(inTag, new CompareTags("weight","desc"));
		List<Tag> outTag = new Vector<Tag>();
		int i=0;
		Iterator<Tag> it = inTag.iterator();
		while (it.hasNext() && i< mN){
			outTag.add(it.next());
			++i;
		}
		return outTag;
	}	
}
