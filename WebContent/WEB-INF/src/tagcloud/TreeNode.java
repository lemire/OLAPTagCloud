
package tagcloud;

import java.util.*;

/**
 * PROBABLY OBSELETE.
 * 
 * TreeNode will eventually move out of package tagcloud
 * It provides for nested 2-d tables, where any table can have arbitrary
 * dimensions.  To reduce coupling, "Tag" should become a type parameter
 * and this code should consider it as opaque
 * 
 * @author owen
 *
 */
public class TreeNode {
	private Tag tag;
    private List<List<TreeNode>> kids;	
    

   public TreeNode( Tag t) {
	 tag = t;
	 kids = new ArrayList<List<TreeNode>>();
   }
	 
   // can be used for slicing trees 
   public TreeNode( Tag t, List<TreeNode> kidList, boolean isVertical) {
	   tag = t; // "rollup" tag
	   kids = new ArrayList<List<TreeNode>>();
	   
	   if (isVertical) { // all one row
		   kids.add(kidList);
	   }
	   else {
		 for (TreeNode tn : kidList) {
			 List<TreeNode> temp = new ArrayList<TreeNode>();
			 temp.add(tn);
			 kids.add(temp);
		 }	   
	   }
	}
	
   public List<List<TreeNode>> getKids() {
	   return kids;
   }
   
   // some of the Chinese characters permitted breaks, so let's remove them
   private static String [] sometags = new String [] {"Owen","Kaser","hierarchical","tag clouds",
	   "are","strange","beasts","I","am not","running out","of ideas", 
	   "for", "good tags"};
   
	   // this knows about tags, which is bad.  However, it's just for testing.
	   // makes a slicing tree
   public static TreeNode mkRandTree(int n) {
	  assert n > 0;
	   
	   // increase our chance of not having duplicate tags by appending randoms
	  String rString = sometags[(int) (Math.random()*sometags.length)]+(int)(Math.random() * 1000);
	  int    rWeight = (int) (Math.random()*10);
	  
	  Tag t = new Tag(rString, rWeight);
	  if (n == 1) return new TreeNode(t);   
	  
	  int numOnLeft = 1 + (int) (Math.random() * (n-1));
	  int numOnRight = n - numOnLeft;
	  
	  List<TreeNode> klist = new ArrayList<TreeNode>();
	  klist.add(mkRandTree(numOnLeft));
	  klist.add(mkRandTree(numOnRight));
	  t = new Tag(rString+"*",rWeight); // ensure all rollups have a name ending with *
	  return new TreeNode(t,klist, Math.random() > 0.7);
   }
   
   public boolean isLeaf() { return kids.size() == 0;}
   
   public Tag getLeafVal(){ return tag;}
   
   // could behave differently for leaves and internal nodes, if desired
   public Tag getVal() { return isLeaf() ?  getLeafVal() : tag;}
	
}
