package tagcloud;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.*;

public class Tag {
    private String text;
    private double weight;
    private double normalizedweight;
    //private Tag() {}

    public Tag(String mtext, double mweight) {
    	text = mtext;
    	weight = mweight;
    	normalizedweight = mweight;
    }

    public double getWeight() { return weight;}
    
    public double getNormalizedWeight() {return normalizedweight;}
    
    public void setNormalizedWeight(double newnw) {
    	normalizedweight = newnw;
    }

    public String getText() { return text;}
    
    public String getFormattedText() { return text.replaceAll("\\s+","\u00A0");}
   
    public String toString() {
        return " text='"+text+"' weight="+weight;
    }
    
    public Element getElement(Document doc) {
    	Element e = doc.createElement("tag");
    	if(normalizedweight - Math.round(normalizedweight)< 0.00001)
    		e.setAttribute("weight", Integer.toString((int)normalizedweight));
    	else
    		e.setAttribute("weight", Double.toString(normalizedweight));
    	e.setAttribute("text",getFormattedText());
        e.setAttribute("trueweight",Double.toString(weight));
    	return e;
    }
    
    public JSONObject getJSON() throws JSONException {
    	JSONObject myjson=new JSONObject();
    	if(normalizedweight - Math.round(normalizedweight)< 0.00001)
    		myjson.put("weight",Integer.toString((int)normalizedweight));
    	myjson.put("text",getFormattedText());
    	myjson.put("trueweight", Double.toString(weight));
    	return myjson;
    }
    
    public String getHTML() {
        StringBuffer sb = new StringBuffer();
        // we should probably not use the tags' name as an id...
        // we should not use the onclick property, registering
        // javascript event handler is better!
        sb.append("<span class=\"tag"+ weight+"\">");
        		/*+" onclick=\"javascript:clickedTag('"+text+"')\" >"); */
        sb.append(getText());
        sb.append("</span>");// the <wbr /> is a soft line break
        return sb.toString();
    }
}