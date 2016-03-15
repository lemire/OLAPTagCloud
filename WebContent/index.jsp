<%@ page language="Java" pageEncoding="UTF-8" %>
<jsp:include page="head.jsp" />
<jsp:include page="menu.jsp" />
<%
	String currentPage=request.getParameter("page");
	if (currentPage == null){
		currentPage ="home";
	}
	String[] allpages={"home","cloud","upload","clouddemo","about","plink"};
	boolean isExist=false;
	for(String s:allpages){
		if (s.equals(currentPage)) {isExist=true;break;}
	}
	if(isExist==false) currentPage="error";
	currentPage += ".jsp";
%>
<jsp:include page="<%= currentPage %>" />
<jsp:include page="foot.jsp" />