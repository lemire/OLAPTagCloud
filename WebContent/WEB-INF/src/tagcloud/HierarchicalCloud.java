package tagcloud;

import org.w3c.dom.*;
import java.util.regex.*;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * NEEDS TO BE PORTED BACK TO NEW VIEW-MODEL PARADIGM.
 * 
 * Hierarchical Cloud is very general.  Each node is an n x m table
 * whose elements may be sub-tables.  Its special cases include
 * slicing trees and normal 2-d tables.
 * 
 * @author owen
 *
 */
public class HierarchicalCloud extends TagCloud {
	List<Tag> myTags;
	TreeNode root;
	boolean showBorder = false;
	int numtags;  // temporary
	Pattern commandParser;
    String mOperation ="";
	Document myXML;

	public HierarchicalCloud(int n) {
		root = TreeNode.mkRandTree(n);
		commandParser = Pattern.compile("(.*\\))?(.*)\\((.*)\\)");
		numtags = n;
		myXML = buildXML();
		// adjust the root so it's text is "Root" and it is flagged as finest granularity
		// wimpy because it uses whatever the old strength was
		// navigate from bundle to tag (attributes text and weight)
		// do this better sometime
		try {
		Element bdl = (Element) myXML.getFirstChild().getFirstChild();
		Element tg = (Element) bdl.getFirstChild();
		tg.setAttribute("text","ClickMeRoᑦ");
		tg.setAttribute("weight","8");
		bdl.setAttribute("goNoFurther","1");
		} catch (DOMException de) { de.printStackTrace();
		                            throw new RuntimeException("de= "+de);
		}
	 
	}
	
	public HierarchicalCloud() { this(7);}
	
	public String getHTML() {
	// may no longer be used anywhere
	// form HTML as nested tables
       StringBuffer sb = new StringBuffer();
	   recGetHTML(root,sb);
	   return sb.toString();
	}
	
	public Document getXML() {
		return myXML;
	}
	
	private Document buildXML() {
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.newDocument();
			Element rootele = doc.createElement("cloud");
			rootele.setAttribute("cloud","hier");
			rootele.setAttribute("operation",mOperation);
			rootele.appendChild(recGetXML(root,doc));
			doc.appendChild(rootele);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			// will not happen?
			// if so, we will return a null document... 
		}
		return doc;
	}
	
	private Element recGetXML(TreeNode t, Document doc){
		   assert t != null;
		   if (t.isLeaf()) {
			 // leaf stores a tag for display
			 return t.getLeafVal().getElement(doc);
		   }
		   Element container = doc.createElement("bundle");
		   container.setAttribute("border",Boolean.toString(showBorder));
		   container.appendChild(t.getVal().getElement(doc));  // could've sworn I did this earlier??
		   for (List<TreeNode> row : t.getKids()) {
				   Element rowele = doc.createElement("row");
				   for (TreeNode kid : row) {
					   rowele.appendChild(recGetXML(kid,doc));
			        }
				    container.appendChild(rowele);
		   }
		   return container;
    }
	
	private void recGetHTML(TreeNode t, StringBuffer acc){
	   assert t != null;
	   if (t.isLeaf()) {
		 // leaf stores a tag for display
		 acc.append(t.getLeafVal().getHTML());
	   } else {
		  acc.append("<table "+ (showBorder ? "border" : "") +">");
		  for (List<TreeNode> row : t.getKids()) {
			 	acc.append("<tr>");
			  
		        for (TreeNode kid : row) {
		            acc.append("\n<td>");
			        recGetHTML(kid, acc);
			        acc.append("\n</td>");
		        }
			  
				acc.append("</tr>");
		   }
	       acc.append("</table>\n");
	   }
	}
	
	/* (non-Javadoc)
	 * @see tagcloud.TagCloud#apply(java.lang.String)
	 */
	@Override
	public TagCloud apply(String operation) {
		System.out.println("operation="+operation);
		if (operation.equals("")) {
			return this;
		}
		mOperation = operation;
		
		// operation maintains a history...current request at end. Parse it off.
		Matcher m = commandParser.matcher(operation);
		
		if (!m.matches()) {
			System.out.println("does not match??");
			throw new RuntimeException("does not match??");
			//return new HierarchicalCloud();
		}
		
		String opSpec = m.group(2);
		String arg = m.group(3);
		
		System.out.println("opspec ="+opSpec+" arg ="+ arg);
		
		// The client has enough information to do this all, in Javascript.
		// so this can be migrated to client if necessary.
		
			if (opSpec.equals("s1")){
			    /* s1 = drill down */
				drilldown((Element) myXML.getFirstChild().getFirstChild(),arg);
				return this;
			}
			else if (opSpec.equals("s2")){
			   rollup((Element) myXML.getFirstChild().getFirstChild(), null, arg);
			   return this;
			}
			else
				throw new RuntimeException("unknown opspec");
			
		//return new HierarchicalCloud(4);
	}
	
	private void drilldown(Element e, String expandOn) {
	  // e will be a Bundle or a Tag
		if (e.getTagName().equals("tag")) return;  
	  //System.out.println("To Be Done drill down on "+ expandOn);
		try {
			System.out.print("I see tag '"+e.getTagName()+"'");
			assert e.getTagName().equals("bundle");
			Element rollupTag = (Element) e.getFirstChild();
			System.out.println(" check '"+rollupTag.getAttribute("text")+"' vs '"+expandOn+"'");
			if (rollupTag.getAttribute("text").equals(expandOn)) {
				System.out.println("yes");
				e.removeAttribute("goNoFurther");
				// but block further progress on children
				// nd is "row", get "bundles" (or tags) beneath via nd1
				for (Node nd = rollupTag.getNextSibling(); nd != null ; nd = nd.getNextSibling()) {
					for (Node nd1 =  nd.getFirstChild(); nd1 != null; nd1 = nd1.getNextSibling()) {
				       Element kid = (Element) nd1;
				       kid.setAttribute("goNoFurther","1");
					}
				}
			}
			else {
				// recur over grandchildren
				for (Node nd = rollupTag.getNextSibling(); nd != null; nd = nd.getNextSibling())
					for (Node nd1 =  nd.getFirstChild(); nd1 != null; nd1 = nd1.getNextSibling()) {
					   drilldown( (Element) nd1, expandOn);
					}
			}
		} catch (DOMException de){
			de.printStackTrace();
			throw new RuntimeException("drilldown de="+de);
		}
	}
	
	
	private void rollup(Element e, Element grandpar, String collapseOn) {
		  // e will be a Bundle or a Tag.  Grandpar is the enclosing table in either case
			
		  System.out.println("rollup on "+ collapseOn);
			try {
				System.out.print("I see tag "+e.getTagName());
			    // if it's a tag, does it match?
				String tagtext="";
			    if (e.getTagName().equals("tag"))  tagtext = e.getAttribute("text");
			    else tagtext = ((Element) (e.getFirstChild())).getAttribute("text");
				
				if (tagtext.equals(collapseOn)) {
					if (grandpar == null) return; // cannot rollup past top
					grandpar.setAttribute("goNoFurther","1");
					System.out.println("gotcha");
					// should not need e.removeAttribute("goNoFurther");
				}

				if (e.getTagName().equals("tag")) return;
				
					// recur over grandchildren
					for (Node nd = e.getFirstChild().getNextSibling(); nd != null; nd = nd.getNextSibling())
						for (Node nd1 =  nd.getFirstChild(); nd1 != null; nd1 = nd1.getNextSibling()) {
						   rollup( (Element) nd1, e, collapseOn);
						}
			} catch (DOMException de){
				de.printStackTrace();
				throw new RuntimeException("rollup de="+de);
			}
		}

	/* (non-Javadoc)
	 * @see tagcloud.TagCloud#getName()
	 */
	@Override
	public String getName() {
		return "hierarchical";
	}

	/* (non-Javadoc)
	 * @see tagcloud.TagCloud#getTags()
	 */
	@Override
	public Iterator<Tag> getTags(){
		myTags = new ArrayList<Tag>();
	    flatten(root);
		return myTags.iterator();
	}

	private void flatten(TreeNode t) {
	  assert t != null;
	  // the internal nodes may have rollup names, but we won't see
	  if (t.isLeaf()) myTags.add(t.getLeafVal());
	  for (List<TreeNode> row : t.getKids() )
	      for (TreeNode tn : row) flatten(tn);  
	}

	/* (non-Javadoc)
	 * @see tagcloud.TagCloud#selectTag(tagcloud.Tag)
	 */
	@Override
	public TagCloud selectTag(Tag t) {
		// TODO Auto-generated method stub
		System.out.println("**** selectTag not expected to be called****");
		return new HierarchicalCloud(4);
	}
}
