package tagcloud;

import javax.servlet.http.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import processdata.*;
import tagcloudfunction.CuboidFunctions;
import tagcloudfunction.Dice;
import tagcloudfunction.Iceberg;
import tagcloudfunction.ProjectOnDimensions;
import tagcloudfunction.Rollup;
import tagcloudfunction.Slice;
import tagcloudfunction.SortTagCloud;
import tagcloudfunction.StripTags;
import tagcloudfunction.TagCloudFunctions;
import tagcloudfunction.TopN;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Vector;

import config.*;
import javax.servlet.*;

import org.json.JSONArray;
import org.json.JSONException;
import olapdbtools.*;


/**
 * This servlet returns tag clouds according to input parameters
 * @author kamel
 *
 */
public class CuboidTagCloudServlet extends HttpServlet implements ConfigFileLocations {
	static final long serialVersionUID= 1L;	
	DbToolsFactory dbfact;
	CuboidFactory cfact;
	
	public void init(ServletConfig  sc)  {
		try {
			System.out.println(sc.getServletContext().getResource(tagcloudproperties));
		}catch(java.net.MalformedURLException murl) {murl.printStackTrace();}
		try {
			dbfact = new DbToolsFactory(sc.getServletContext().getResourceAsStream(tagcloudproperties));
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	    cfact = new CuboidFactory(dbfact);
	};
	
	public void destroy() {
		ConnectionPool.closeAll();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		CuboidFunctions cuboidFNC = null;
		TagCloudFunctions tagcloudFNC = null;
		Vector<TagCloudFunctions> tagcloudFNCs=new Vector<TagCloudFunctions>();
		TagCloudView tc;
		Hashtable<String,String> params =netutil.Encoding.getParameters(request.getQueryString());
		String id = params.get("id").toLowerCase();
		if (id ==null) throw new RuntimeException("empty cube ID");
		if (params.get("allop")!=null){
			try{
				JSONArray js = new JSONArray(params.get("allop"));//All operations being applied
				String[] dims=null;
				String[] simdims=null;
				String[] tagdims=null;
				for(int i=0; i<js.length(); ++i){
					String opid =js.getJSONObject(i).optString("id").toUpperCase();
					if(opid==null) throw new RuntimeException("empty operation ID");
					JSONArray tempjsonarray;
					switch(EnumCuboidOperations.valueOf(opid)){
					case PR : 
						//doProject
						tempjsonarray= (JSONArray) js.getJSONObject(i).optJSONArray("dim");
						if (tempjsonarray== null) throw new RuntimeException("no dimensions");
						dims= JSONtoArray(tempjsonarray);

						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("tag");
						if (tempjsonarray== null) throw new RuntimeException("no support_tag dimensions");
						tagdims= JSONtoArray(tempjsonarray);

						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("sim");
						if (tempjsonarray != null){ 
							simdims= JSONtoArray(tempjsonarray);
						}
						if (simdims == null){
							cuboidFNC = new ProjectOnDimensions(tagdims);
						}else{
							cuboidFNC = new ProjectOnDimensions(tagdims,simdims);
						}
						break;
					case RU :
						//doRollup
						tempjsonarray= (JSONArray) js.getJSONObject(i).optJSONArray("dim");
						if (tempjsonarray== null) throw new RuntimeException("no dimensions");
						dims= JSONtoArray(tempjsonarray);

						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("tag");
						if (tempjsonarray== null) throw new RuntimeException("no support_tag dimensions");
						tagdims= JSONtoArray(tempjsonarray);

						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("sim");
						if (tempjsonarray != null){ 
							simdims= JSONtoArray(tempjsonarray);
						}
						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("rud");
						if (tempjsonarray== null) throw new RuntimeException("no roll-up dimensions");
						String[] rollupdims= JSONtoArray(tempjsonarray);

						if (simdims == null){
							cuboidFNC = new Rollup(rollupdims);
						}else{
							cuboidFNC = new Rollup(rollupdims,simdims);
						}
						break;
					case SL :
						//doSlice
						tempjsonarray= (JSONArray) js.getJSONObject(i).optJSONArray("dim");
						if (tempjsonarray== null) throw new RuntimeException("no dimensions");
						dims= JSONtoArray(tempjsonarray);

						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("tag");
						if (tempjsonarray== null) throw new RuntimeException("no support_tag dimensions");
						tagdims= JSONtoArray(tempjsonarray);

						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("sim");
						if (tempjsonarray != null){ 
							simdims= JSONtoArray(tempjsonarray);
						}
						String slicedim = (String) js.getJSONObject(i).opt("sld");
						if (slicedim == null) throw new RuntimeException("no dimension to slice");
						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("slv");
						if (tempjsonarray == null) throw new RuntimeException("no values to slice");
						String[] slicevalues= JSONtoArray(tempjsonarray);
						if (simdims == null){
							cuboidFNC = new Slice(tagdims,slicedim,slicevalues);
						}else{
							cuboidFNC = new Slice(tagdims,slicedim,slicevalues,simdims);
						}
						break;
					case DI:
						//doDice
						tempjsonarray= (JSONArray) js.getJSONObject(i).optJSONArray("dim");
						if (tempjsonarray== null) throw new RuntimeException("no dimensions");
						dims= JSONtoArray(tempjsonarray);

						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("tag");
						if (tempjsonarray== null) throw new RuntimeException("no support_tag dimensions");
						tagdims= JSONtoArray(tempjsonarray);

						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("sim");
						if (tempjsonarray != null){ 
							simdims= JSONtoArray(tempjsonarray);
						}
						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("did");
						if (tempjsonarray == null) throw new RuntimeException("no dimensions to dice");
						String[] dicedims= JSONtoArray(tempjsonarray);
						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("div");
						if (tempjsonarray == null) throw new RuntimeException("no  values to dice");
						String[] dicevalues= JSONtoArray(tempjsonarray);
						if (simdims == null){
							cuboidFNC = new Dice(dicedims,dicevalues);
						}else{
							cuboidFNC = new Dice(dicedims,dicevalues,simdims);
						}
						break;
					case ST:
						//doStripTags
						tempjsonarray = (JSONArray) js.getJSONObject(i).optJSONArray("tags");
						if (tempjsonarray == null) throw new RuntimeException("no tags to strip");
						String[] tagstostrip= JSONtoArray(tempjsonarray);
						tagcloudFNC= new StripTags(tagstostrip);
						tagcloudFNCs.add(tagcloudFNC);
						break;
					case SO:
						//doSortTagCloud
						String attribute = (String) js.getJSONObject(i).opt("att");
						if (attribute == null) throw new RuntimeException("no attribute to sort");
						String type = (String) js.getJSONObject(i).opt("t");
						if (type == null) throw new RuntimeException("no sort type");
						tagcloudFNC= new SortTagCloud(attribute,type);
						tagcloudFNCs.add(tagcloudFNC);
						break;
					case TN:
						//doTopN
						Integer N = (Integer) js.getJSONObject(i).optInt("n");
						if (N == null) throw new RuntimeException("no N value");
						tagcloudFNC= new TopN(N);
						tagcloudFNCs.add(tagcloudFNC);
						break;

					case IC:
						//doIceberg
						Integer measure = (Integer) js.getJSONObject(i).optInt("m");
						if (measure == null) throw new RuntimeException("no min measure value");
						tagcloudFNC= new Iceberg((double)measure);
						tagcloudFNCs.add(tagcloudFNC);
						break;
					}
				}
				tc = cfact.getCuboid(id, dims).getTagCloudModel(cuboidFNC).getView();
				tc.normalizeWeights();
				if (simdims != null) tc.optimize("NN",util.SimilarityMeasure.COSINE);
				for(int fnc=0; fnc<tagcloudFNCs.size(); ++fnc)
					tc.apply(tagcloudFNCs.elementAt(fnc));
				if ("xml".equals(request.getParameter("as").toLowerCase())){	    
					response.setContentType("text/xml");
					TransformerFactory tfact = TransformerFactory.newInstance();
					Transformer transformer = tfact.newTransformer();
					StreamResult result = new StreamResult(response.getWriter());
					transformer.transform(new DOMSource(tc.getXML()), result);
				}else{
					if  ("json".equals(request.getParameter("as").toLowerCase())){
						response.setContentType("application/json");
						response.getWriter().println(tc.getJSON());
					}else{
						response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
					}
				}
			} catch (Exception e){
				response.setCharacterEncoding("UTF-8");	    
				response.setContentType("text/html");
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(stackTraceToJSON(e));
			}
			
		}
		
		if ("v".equals(params.get("q")))
			doGetTag(request,response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Unsupported POST method");
	}
	
	/*public Map<String,String[]> getMap(JSONArray js) throws JSONException{
		//JSONArray temp = json.getJSONArray();
		for(int i=0; i<js.length(); ++i){
			HashMap jsonmap = js.getJSONObject(i).getMap();
			Iterator it = jsonmap.keySet().iterator();
			while(it.hasNext()){
				String key=(String)it.next();
				JSONArray tempjsonnarray = (JSONArray) js.getJSONObject(i).optJSONArray(key);
				if(tempjsonnarray !=null){
					System.out.println();
					System.out.print(key + " ");
					for (int j=0; j < tempjsonnarray.getArrayList().size(); ++j)
						System.out.print(tempjsonnarray.getArrayList().get(j)+" ");
					System.out.println();
				}
				
			}
		}
		return null;
	}*/
	
	/*private void doAddHeader(HttpServletResponse response){
		//Make sure the status response is not cached by the browser
		response.setCharacterEncoding("UTF-8");	
		response.addHeader("Expires", "0");
	    response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	    response.addHeader("Pragma", "no-cache");
	}*/
	
	private String[] JSONtoArray(JSONArray json) throws JSONException{
		String[] answer= new String[json.length()];
		for (int j=0; j < json.getArrayList().size(); ++j)
			answer[j]=json.getString(j);
		return answer;
	}
	
	private void doGetTag(HttpServletRequest request, HttpServletResponse response) throws IOException{
		if (request.getParameter("id") ==null) throw new RuntimeException("empty cube ID");
		if (request.getParameterValues("cdim") ==null) throw new RuntimeException("empty dimension name");
		
		TagCloudView tc;
		ProjectOnDimensions project = new ProjectOnDimensions(request.getParameterValues("cdim"));
		try {
			tc = cfact.getCuboid(request.getParameter("id").toLowerCase(), request.getParameterValues("dim")).getTagCloudModel(project).getView();
			response.setContentType("application/json");
			//doAddHeader(response);
			response.getWriter().println(tc.getJSON());
		} catch (Exception e){
			response.setCharacterEncoding("UTF-8");	    
			response.setContentType("text/html");
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(stackTraceToJSON(e));
		}
	}
	
	private String stackTraceToJSON(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stack = sw.toString();
		stack = stack.replaceAll("&", "&amp;");
		stack = stack.replaceAll("<", "&lt;");
		stack = stack.replaceAll("(\r\n|\n|\r|\u0085|\u2028|\u2029)", "<br />");
		stack = stack.replaceAll("\'","\\\\'");
		stack = stack.replaceAll("\"","\\\\\"");
		stack= "{\"results\":{\"error\":\""+stack+"\"}}";
		return stack;
	}
}
