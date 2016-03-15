<%@ page language="java" contentType="application/xhtml+xml; charset=utf-8" pageEncoding="utf-8"%>
 <!-- Pages' head -->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
	<title>OlapTagClouds</title>
	<meta name="Author" content="Kamel Aouiche and Daniel Lemire" />
	<meta name="Description" content="Eclipse Tag Cloud Projects" />
	<% String currentPage = request.getParameter("page"); 
		if("upload".equals(currentPage)){
	%>
		<link rel="stylesheet" href="css/olaptagcloud.css" type="text/css" />
		<script src="scripts/prototype.js"  type="text/javascript" charset="UTF-8" ></script>
		<script src="scripts/progressbar.js"  type="text/javascript" charset="UTF-8"></script>
	<% } else if ("clouddemo".equals(currentPage)) { %>
		<link rel="stylesheet" href="css/olaptagcloud.css" type="text/css" />
		<script src="scripts/prototype.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/datacloud.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/usefulfunctions.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/tagcloud.js"  type="text/javascript" charset="UTF-8"></script>
	<% } else if ("cloud".equals(currentPage)) { %>
		<link rel="stylesheet" href="css/olaptagcloud.css" type="text/css" />
		<script src="scripts/prototype.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/tagcloud.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/tipfunction.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/datacloud.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/usefulfunctions.js"  type="text/javascript" charset="UTF-8"></script>
	<% } else if ("plink".equals(currentPage)) { %>
		<link rel="stylesheet" href="css/iframe.css" type="text/css" />
		<script src="scripts/prototype.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/tagcloud.js"  type="text/javascript" charset="UTF-8"></script>
		<script src="scripts/usefulfunctions.js"  type="text/javascript" charset="UTF-8"></script>
	<% } else { %>
		<link rel="stylesheet" href="css/olaptagcloud.css" type="text/css" />
	<% }%>
</head>

<body>

<div id="head">
		<!--<div class="left_head"><img src="figures/tagcloudexample.jpg" alt="" /></div>-->
		<div id="left_head"></div>
		<!--<p>Put here  a head banner</p>-->
		<%
	if ("plink".equals(currentPage)){%>
	<script type="text/javascript" charset="UTF-8">
	Element.hide('head');
	</script>
<%	}
%>
</div>

<div id="main_frame">
<!-- mainframe begin -->