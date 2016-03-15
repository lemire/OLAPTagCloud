package processdata;

import olapdbtools.DbTools;
import olapdbtools.DbToolsFactory;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import util.StripString;

import java.io.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import com.missiondata.fileupload.MonitoredDiskFileItemFactory;
import config.*;

public class UploadServlet extends HttpServlet implements ConfigFileLocations{
	static final long serialVersionUID= 1;
	static final String STATUS="status";
	public static boolean verbose = false;
	DbToolsFactory dbfact;
	CuboidFactory cfact;
	ProcessDataFactory pdfcatory;
	ProcessData pd;
	
	public void init(ServletConfig  sc)  {
		try {
			dbfact = new DbToolsFactory(sc.getServletContext().getResourceAsStream(tagcloudproperties));
			cfact = new CuboidFactory(dbfact);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	};
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(STATUS.equals(request.getParameter("c"))){
			doStatus(session, response);
		} else if(STATUS.equals(request.getParameter("f"))){
			doCheck(request,response);
		}else
			response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException{
		HttpSession session = request.getSession();
		doFileUpload(session, request, response);
	}
	
	private void doFileUpload(HttpSession session, HttpServletRequest request, HttpServletResponse response) 
	throws IOException {
		InputStream dataStream=null;
		try {
			if(verbose) System.out.println("starting file upload...");
			FileUploadListener listener = new FileUploadListener(request.getContentLength());
			listener.getFileUploadStats().setBytesRead(0);
			session.setAttribute("FILE_UPLOAD_STATS", listener.getFileUploadStats());
			FileItemFactory factory = new MonitoredDiskFileItemFactory(listener);
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items = upload.parseRequest(request);
			String fieldName;
			String fieldValue;
			String fileFormat=null;
			String delimiter=",";
			boolean isXML=false;
			String sourceID=null;
			String description=null;
			
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					fieldName = item.getFieldName();
					fieldValue = item.getString();
					if (fieldName.equals("attDelimiter")){
						if (!fieldValue.equals("")){
							delimiter = fieldValue;
						}
					}
					if (fieldName.equals("sourceID")){
						if (!fieldValue.equals("")){
							sourceID = fieldValue.toLowerCase();
						}
					}
					if (fieldName.equals("description")){
						if (!fieldValue.equals("")){
							description = fieldValue.toLowerCase();
						}
					}
					
					if (fieldName.equals("fileFormat")){
						fileFormat=fieldValue;
						isXML = fileFormat.equals("xml");
					}
				}else{
					fieldName=item.getFieldName();
					if (fieldName.equals("dataFile")){
						//Validateur.validate(item.getInputStream());
						dataStream = item.getInputStream();
					}
				}
			}
			
			String stats;
			if(verbose) System.out.println("Processing Data...");
			pdfcatory = new ProcessDataFactory();
			pd= pdfcatory.getProcessData(dataStream,sourceID,description,delimiter,isXML);
			session.setAttribute("PROCESSDATA", pd);
			int numberofelements = pd.uploadData(dbfact);
			stats = numberofelements+" elements loaded";
			
			dataStream.close();
		    response.setStatus(HttpServletResponse.SC_CREATED);
		    sendCompleteResponse(response, "",stats);
			
		} catch (Exception e){
			try {
				pd.deleteCubeEntry(dbfact);
			} catch (Exception sql) {
				sql.printStackTrace();
			}
			e.printStackTrace();
		    String errorMessage="<p>Unable to upload a file.</p><pre>"+stackTraceToString(e)+"</pre>";
			sendCompleteResponse(response,errorMessage,"");				
		} finally {					
			if(dataStream!=null) dataStream.close();
		}
	}
	
	private void doStatus(HttpSession session, HttpServletResponse response) throws IOException {
	    // Make sure the status response is not cached by the browser
	    response.addHeader("Expires", "0");
	    response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	    response.addHeader("Pragma", "no-cache");
	    FileUploadListener.FileUploadStats fileUploadStats = (FileUploadListener.FileUploadStats) session.getAttribute("FILE_UPLOAD_STATS");
	    
	    if(fileUploadStats != null){
	    	long bytesProcessed = fileUploadStats.getBytesRead();
	    	long totalSize = fileUploadStats.getTotalSize();
	    	long percentComplete = (long)Math.floor(((double)bytesProcessed / (double)totalSize) * 100.0);
	    	long timeInSeconds = fileUploadStats.getElapsedTimeInSeconds();
	    	double uploadRate = bytesProcessed / (timeInSeconds + 0.00001);
	    	double estimatedRuntime = totalSize / (uploadRate + 0.00001);
	    	response.getWriter().println("<p>Upload Status: </p>");
	    	
	    	if(fileUploadStats.getBytesRead() != fileUploadStats.getTotalSize()){
	    		response.getWriter().println("<div class=\"prog_border\"><div class=\"prog_bar\" style=\"width: " + percentComplete + "%;\"></div></div>");
	    		response.getWriter().println("<p>Uploaded: " + bytesProcessed + " out of " + totalSize + " bytes (<span class=\"text_bar\">" + percentComplete + "%</span>) " + (long)Math.round(uploadRate / 1024) + " Kbs</p>");
	    		response.getWriter().println("<p>Runtime: " + formatTime(timeInSeconds) + " out of " + formatTime(estimatedRuntime) + " " + formatTime(estimatedRuntime - timeInSeconds) + " remaining</p>");
	    	}else{
	    		response.getWriter().println("<p>Uploaded: " + bytesProcessed + " out of " + totalSize + " bytes</p>");
	    		session.removeAttribute("FILE_UPLOAD_STATS");
	    		//response.getWriter().println("<p><strong>Upload complete.</strong></p>");
	    	}
	    }
	    if(fileUploadStats != null && fileUploadStats.getBytesRead() == fileUploadStats.getTotalSize()){
	    	session.removeAttribute("FILE_UPLOAD_STATS");
	    	response.getWriter().println("<p><strong>Upload complete.</strong></p>");
	    }
	    
	    ProcessData pd = (ProcessData) session.getAttribute("PROCESSDATA");
	    if (pd !=null){
		    response.getWriter().println("<p><strong>Processing status:</strong></p>");
		    int bytesProcessed =  pd.getProcessedBytes();
		    int totalSize = pd.getTotalSize();
		    long percentProcess = (long)Math.floor(((double)bytesProcessed / (double)totalSize) * 100.0);
		    if(pd.getProcessedBytes() != pd.getTotalSize()){
		    	response.getWriter().println("<div class=\"prog_border\"><div class=\"prog_bar\" style=\"width: " + percentProcess + "%;\"></div></div>");
		    	response.getWriter().println("<p>Uploaded: " + bytesProcessed + " out of " + totalSize + " bytes (<span class=\"text_bar\">" + percentProcess + "%</span>)</p>");
		    }else{
		    	response.getWriter().println("<p>Uploaded: " + bytesProcessed + " out of " + totalSize + " bytes</p>");
		    }
		    response.getWriter().println("<p>Number of elements already processed: "+pd.getNbOfElements()+ "</p>");
	    }
	}
	
	private void doCheck(HttpServletRequest request, HttpServletResponse response) throws IOException{
		if (request.getParameter("id") !=null){
			try{
				DbTools myDB=dbfact.newInstance();
				myDB.openConnection();
				String [] select ={"id"};
				String [] where ={"id"};
				String [] values ={StripString.toCleanUp(request.getParameter("id").toLowerCase())};
				boolean isIDExist = myDB.hasResults("cubes",select,where,values);
				if (isIDExist){
					response.getWriter().println("0");//means id is not available
				}else{
					response.getWriter().println("1");//means id is available
				}
				myDB.closeConnection();
			}catch (Exception e){
				e.printStackTrace();
				String errorMessage="<p>Unable to upload a file.</p><pre>"+stackTraceToString(e)+"</pre>";
				response.getWriter().println("<span class=\"error\">"+errorMessage+"</span>");
			}
		}else{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Check your query parameters");
		}
	}

	private void sendCompleteResponse(HttpServletResponse response, String message, String stats) throws IOException {
		String javascriptfunction = "killUpdate";
		//if(verbose) System.out.println("sending the following message: "+message);
	    response.getOutputStream().print("<html><head><script type='text/javascript'>function killUpdate() { window.parent."+javascriptfunction+"('" + message + "','"+stats+"'); }</script></head><body onload='killUpdate()'></body></html>");
	}
	
	
	private String formatTime(double timeInSeconds){
	    long seconds = (long)Math.floor(timeInSeconds);
	    long minutes = (long)Math.floor(timeInSeconds / 60.0);
	    long hours = (long)Math.floor(minutes / 60.0);
	    if(hours != 0){
	    	return hours + " hours " + (minutes % 60) + " minutes " + (seconds % 60) + " seconds";
	    }else if(minutes % 60 != 0){
	    	return (minutes % 60) + " minutes " + (seconds % 60) + " seconds";
	    }else{
	    	return (seconds % 60) + " seconds";
	    }
	}
	
	private String stackTraceToString(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stack = sw.toString();
		stack = stack.replaceAll("&", "&amp;");
		stack = stack.replaceAll("<", "&lt;");
		stack = stack.replaceAll("(\r\n|\n|\r|\u0085|\u2028|\u2029)", "<br />");
		stack = stack.replaceAll("\'","\\\\'");
		stack = stack.replaceAll("\"","\\\\\"");
		return stack;
	}
}
