/***************
* WARNING: MAKE SURE YOU EDIT THIS FILE IN UTF-8 MODE.
* IF YOU ARE NOT SURE, ASK!
* (right click on the file, go to properties)
*****************/

package tagcloud;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * PROBABLY OBSELETE.
 * 
 * This class is just meant to generate a fake
 * tag cloud, to test the code.
 * 
 * @author lemire
 *
 */
public class FakeTagCloud extends TagCloud {
	
	Vector<Tag> V = new Vector<Tag>();
	String operation;
	public FakeTagCloud() {
		this("");
	}
	
	public FakeTagCloud(String op) {
		operation = op;
		Random r = new Random();
		V.add(new Tag("共Kamel Aouiche", r.nextInt(10)));
		V.add(new Tag("共Daniel Lemire", r.nextInt(10)));
		V.add(new Tag("共UQAM", r.nextInt(10)));
		V.add(new Tag("共Lyon", r.nextInt(10)));
		V.add(new Tag("共France", r.nextInt(10)));
		V.add(new Tag("共Canada", r.nextInt(10)));
		V.add(new Tag("共Data Mining", r.nextInt(10)));
		V.add(new Tag("共Entrepôts de données", r.nextInt(10)));
		V.add(new Tag("共Données complexes", r.nextInt(10)));
		V.add(new Tag("共Nuages", r.nextInt(10)));
		V.add(new Tag("共Données non structurées", r.nextInt(10)));
		V.add(new Tag("共if you get funny chars, check font encoding", r.nextInt(10)));
		Collections.shuffle(V);
	}
	
	public String getName() {
		return "fake";
	}

	public Iterator<Tag> getTags() {
		return V.iterator();
	}

	public TagCloud selectTag(Tag t) {
		return new FakeTagCloud();
	}

	public  TagCloud apply(String operation) {
		System.out.println("operation received by tag cloud is : ");
		System.out.println(operation);
		return new FakeTagCloud(operation);
	}

	  public Document getXML() {
			Document doc = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder parser = factory.newDocumentBuilder();
				doc = parser.newDocument();
				Element root = doc.createElement("cloud");
				root.setAttribute("cloud","fake");
				root.setAttribute("operation",operation);
				Iterator<Tag> i = getTags(); 
				while(i.hasNext()) {
			    	Element e = i.next().getElement(doc);
			    	//e.setAttribute("id", Integer.toString(++counter));
					root.appendChild(e);
				}
				doc.appendChild(root);
			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
				// will not happen?
				// if so, we will return a null document... 
			}
			return doc;

		  }
		  


}
