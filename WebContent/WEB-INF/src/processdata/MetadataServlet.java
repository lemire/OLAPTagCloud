package processdata;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import olapdbtools.DbTools;
import olapdbtools.DbToolsFactory;
import util.StripString;
import config.*;


/**
 * MetadataServlet is meant to retrieve some metadata 
 * @author kamel
 *
 */
public class MetadataServlet extends HttpServlet implements ConfigFileLocations{
	private static final long serialVersionUID = 1L;
	DbToolsFactory dbfact;
	
	public void init(ServletConfig  sc)  {
		try {
			dbfact = new DbToolsFactory(sc.getServletContext().getResourceAsStream(tagcloudproperties));
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	};
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,"Post mehod is not implemented");	
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//HttpSession session = request.getSession();
		response.setContentType("application/json");
		if("c".equals(request.getParameter("q"))){//cubes
			doGetALLCubeNames(response);
		}else if ("a".equals(request.getParameter("q"))){//dimensions
			doGetDimensions(request,response);
		}else if ("v".equals(request.getParameter("q"))){//attribute values
			doGetAttributeValues(request,response);
		}else{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Check your query");
		}
	}
	
	private void doGetALLCubeNames(HttpServletResponse response) throws IOException{
		DbTools myDB=dbfact.newInstance();
		String sqlString="Select id,description from cubes order by id";
		try{
			myDB.openConnection();
			myDB.queryDataBase(sqlString);
			String json = myDB.getResultsAsJSON();
			response.setStatus(HttpServletResponse.SC_OK);
			//response.setContentType("application/json");
			response.getWriter().println(json);
			myDB.closeConnection();
		}catch (Exception e){
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(stackTraceToJSON(e));
		}
	}
	
	private void doGetDimensions(HttpServletRequest request, HttpServletResponse response) throws IOException{
		/**
		 * TODO: this is somewhat bad because all the logic is down
		 * in the servlet, this should move up to CuboidFactory.getAllDimensions()
		 * or something.
		 */
		if (request.getParameter("id") ==null) throw new RuntimeException("empty cube ID");
		DbTools myDB=dbfact.newInstance();
			try{
				myDB.openConnection();
				String [] select ={"id","isxml"};
				String [] where ={"id"};
				String [] values ={StripString.toCleanUp(request.getParameter("id").toLowerCase())};
				boolean isIDExist = myDB.hasResults("cubes",select,where,values);
				if (!isIDExist){
					throw new RuntimeException("can't find cube "+StripString.toCleanUp(request.getParameter("id").toLowerCase()));
				}else{
					String[] results = myDB.getResultsAsString();
					int isXML = Integer.parseInt(results[0].split(",")[1]);
					if (isXML ==0){//Flat file
						String sqlString="SHOW COLUMNS FROM "+results[0].split(",")[0];
						myDB.queryDataBase(sqlString);
						//response.setContentType("application/json");
						response.getWriter().println(myDB.getResultsAsJSON());
					}else{//XML file
						String sqlString="SELECT DISTINCT label as Field FROM "+ results[0].split(",")[0]+"_edge WHERE flag='cda'";
						myDB.queryDataBase(sqlString);
						response.setStatus(HttpServletResponse.SC_OK);
						//response.setContentType("application/json");
						response.getWriter().println(myDB.getResultsAsJSON());
					}			
				}
				myDB.closeConnection();
			}catch (Exception e){
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(stackTraceToJSON(e));
			}
	}
	
	private void doGetAttributeValues(HttpServletRequest request, HttpServletResponse response) throws IOException{
		if (request.getParameter("id") ==null) throw new RuntimeException("empty cube ID");
		if (request.getParameter("cdim") ==null) throw new RuntimeException("empty dimension name");
		DbTools myDB=dbfact.newInstance();
		try{
			myDB.openConnection();
			String id = StripString.toCleanUp(request.getParameter("id").toLowerCase());
			String attribute = StripString.toCleanUp(request.getParameter("cdim").toLowerCase());
			String [] select ={"id","isxml"};
			String [] where ={"id"};
			String [] values ={StripString.toCleanUp(id.toLowerCase())};
			boolean isIDExist = myDB.hasResults("cubes",select,where,values);
			if (!isIDExist){
				throw new RuntimeException("can't find cube "+id);
			}else{
				String[] results = myDB.getResultsAsString();
				int isXML = Integer.parseInt(results[0].split(",")[1]);
				if (isXML ==0){
					String sqlString="select distinct "+ attribute +" from "+id+" order by "+attribute;
					//System.out.println(sqlString);
					myDB.queryDataBase(sqlString);
					response.getWriter().println(myDB.getResultsAsJSON());
				}else{
					//String sqlString="select distinct "+ attribute +" form "+id;
					throw new RuntimeException("not implemented ");	
				}
			}
			myDB.closeConnection();
		}catch(Exception e){
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
