package tagcloudfunction;

import java.util.Iterator;
import processdata.Cuboid;
import tagcloud.Tag;

/**
 * Interface allowing to apply some functions on cuboids.
 * Those functions return an iterator on the resulting tags. 
 * @author kamel
 *
 */
public interface CuboidFunctions {
	Iterator<Tag> getTags(Cuboid c);
	String[] getSimilarityDimensions();
	String[] getTagDimensions();
}
