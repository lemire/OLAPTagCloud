package tagcloud;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.*;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import tagcloud.optimization.*;
import tagcloudfunction.TagCloudFunctions;

/**
 * This class represents the visual model of a 
 * tag cloud. A given TagCloudModel may generate
 * different TagCloudView objects, especially if
 * we take into account layout optimization. Ultimately,
 * we would want to support different views.
 * 
 * @author lemire
 *
 */
public class TagCloudView {
	TagCloudModel myTCM;
	List<Tag> mMyTagList;
	
	/**
	 * protected model, go to the TagCloudModel and
	 * call getView instead.
	 * 
	 * @param tcm
	 */
	protected TagCloudView(TagCloudModel tcm) {
		myTCM = tcm;
		mMyTagList = new Vector<Tag>();
		Iterator<Tag> i = tcm.getTags();
		while(i.hasNext())
			mMyTagList.add(i.next());
	}
	
	/**
	 * Optimize the layout of the tags.
	 * @param la
	 */
	public void optimize(LinearArrangement la){
		mMyTagList = la.optimize(mMyTagList);
		//la.printSimilarityMatrix(mMyTagList);
	}
	
	public double clusteringBadness(util.SimilarityMeasure sm) {
		NearestNeighbour nn = new NearestNeighbour(myTCM.getSimilarity(sm));
		return nn.cost(mMyTagList);
	}
	
	public void apply(TagCloudFunctions fnc){
		mMyTagList = fnc.apply(mMyTagList);
	}
	

	public void optimize(String method, util.SimilarityMeasure sm) {
		if(method.equals("NN"))
		  //optimize(new NearestNeighbour(myTCM.mMyCuboid.getSimilarity(myTCM.mMyDimensions)));
			optimize(new NearestNeighbour(myTCM.getSimilarity(sm)));
		else if(method.startsWith("PWMC")) {
			int times = Integer.parseInt(method.substring(4));
			optimize(new PointWiseMonteCarlo(myTCM.getSimilarity(sm),times));
		}		
		else if(method.equals("MC"))
			optimize(new MonteCarlo(myTCM.getSimilarity(sm)));
		else if(method.startsWith("MC")) {
			int times = Integer.parseInt(method.substring(2));
			optimize(new MonteCarlo(myTCM.getSimilarity(sm),times));
		} else if(method.startsWith("EC")) {
			int times = Integer.parseInt(method.substring(2));
			optimize(new ExhaustiveCut(myTCM.getSimilarity(sm),times));			
		}
		else throw new Error("Unsupported method");
	}
	
	
	
	public void normalizeWeights() {
		// by default the weights are set as integers between 0 and 9
		double minval = Double.MAX_VALUE;
	    double maxval = Double.MIN_VALUE;
		Iterator<Tag> i = mMyTagList.iterator(); 
		while(i.hasNext()) {
			Tag t = i.next();
			if(t.getWeight()>maxval) maxval = t.getWeight();
			if(t.getWeight()<minval) minval = t.getWeight();
		}
		i = mMyTagList.iterator(); 
		while(i.hasNext()) {
			Tag t = i.next();
			t.setNormalizedWeight(Math.round((t.getWeight()-minval)/(maxval-minval)*9));
		}
	    
	}
	
	/**
	 * get an XML representation suitable for the
	 * browser.
	 * @return Tag Cloud as an XML document
	 */
	public Document getXML(){
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.newDocument();
			Element root = doc.createElement("cloud");
			root.setAttribute("cloud",myTCM.getDataSetName());
			root.setAttribute("description",myTCM.getDataSetDescription());
			//root.setAttribute("operation",operation);
			Iterator<Tag> i = mMyTagList.iterator(); 
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

	/**
	 * get a JSON representation suitable for the
	 * browser.
	 * @return Tag cloud as a JSON
	 */
	public JSONObject getJSON() {
		JSONObject json=new JSONObject();
		try {
			if(mMyTagList.size()==0) {
				json.accumulate("tag", new Tag("no tag",1).getJSON());
				json.put("description","empty tag cloud");
				json.put("cloud","empy");
				return new JSONObject().put("cloud",json);
			}
			Iterator<Tag> i = mMyTagList.iterator(); 
			while(i.hasNext()) {
					json.accumulate("tag", i.next().getJSON());
			}
			json.put("description",myTCM.getDataSetDescription());
			json.put("cloud", myTCM.getDataSetName());
			return new JSONObject().put("cloud",json);
		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}
		// will not happen?
		// if so, we will return a null json... 
		return null;
	}

	/**
	 * get an HTML representation suitable for the
	 * browser.
	 * @return Tag cloud as an HTML string
	 */
	public String getHTML() {
		throw new Error("not implemented");
	}
	
	public TagCloudModel getTagCloudModel() { return myTCM;}
	public List<Tag> getTags() {return mMyTagList;}
	
	public int getSize() {return mMyTagList.size();}

}
