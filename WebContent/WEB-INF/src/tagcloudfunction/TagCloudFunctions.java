package tagcloudfunction;

import java.util.List;

import tagcloud.Tag;

/**
 * Interface allowing to apply functions on tag clouds
 * @author kamel
 *
 */
public interface TagCloudFunctions {
	List<Tag> apply(List<Tag> inTag);
}
