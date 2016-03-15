package tagcloud;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
//import java.util.*;
import processdata.*;
import tagcloudfunction.ProjectOnDimensions;

public class TagCloudServlet extends HttpServlet {
	static final long serialVersionUID= 1;	

    
	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) 
	throws ServletException,IOException {
		//request.setCharacterEncoding("UTF-8");
		System.out.println("URI: '"+request.getQueryString()+ " "+  request.getRequestURI());
		//Hashtable<String,String> params = netutil.Encoding.getParameters(request.getQueryString());
		//String cloud = params.get("c");//request.getParameter("c");
		//String operation = params.get("o");//request.getParameter("o");
		//String as = params.get("as");//request.getParameter("as");
		String mimetype =  "text/xml" ;
	    //if(as != null)
	    //	if(as.equals("html"))
	    //		mimetype = "text/html" ;
		response.setCharacterEncoding("UTF-8");	    
		response.setContentType(mimetype);
		//response.getOutputStream().print("<html><head><script type='text/javascript'>function killUpdate() { window.parent.killUpdate(''); }</script></head><body onload='killUpdate()'></body></html>");
	    
		//HttpSession session = request.getSession();
	//	try { 
	      Cuboid c = new FakeCuboid();
		  //TagCloudView tc = c.getTagCloudModel("country").getView();//TagCloudFactory.getTagCloudByName(cloud, operation);
	      TagCloudView tc = c.getTagCloudModel(new ProjectOnDimensions(new String[]{"country"},new String[]{"continent"})).getView();//TagCloudFactory.getTagCloudByName(cloud, operation);
		  
	      tc.optimize("NN",util.SimilarityMeasure.COSINE);
		  tc.normalizeWeights();
		  //if(mimetype.equals("text/html")) {
		    //response.getWriter().println(tc.getHTML());
		  //} else 
		  {// must be XML
			  try {
			    TransformerFactory tfact = TransformerFactory.newInstance();
			    Transformer transformer = tfact.newTransformer();
			    StreamResult result = new StreamResult(response.getWriter());
			    transformer.transform(new DOMSource(tc.getXML()), result);
			  } catch (TransformerException te) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, te.getMessage());
			  }	
		  }
		//} catch(NoSuchTagCloud e) {
		  //response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
	//	}
	}
	
	protected void doPost(HttpServletRequest request, 
			HttpServletResponse response) 
	throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "post not implemented");
	}
	
}