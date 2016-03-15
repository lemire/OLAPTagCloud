<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="application/xhtml+xml; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
<title>Properties</title>
</head>
<body>
<ol>
	<%Properties p = System.getProperties();
	Enumeration e =  p.propertyNames();
	while(e.hasMoreElements()) {
	  String s = (String) e.nextElement();
	%><li><strong><%=s%></strong>: <%=p.get(s)%></li>
		<%}
	%>
</ol>
</body>
</html>