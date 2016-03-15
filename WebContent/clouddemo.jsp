<%@ page language="java" pageEncoding="UTF-8" import="java.util.*;import netutil.*"%>
<!-- page content -->

<div id="content_frame">

<div id ="about" class="longtext">
	<h1>Tag Cloud demo page</h1>
    <p>
        This page is a testing ground for the developers. Some of the features on this page
        may not work.
    </p>
</div>  

<!-- the following script is necessary to display tips (must be included within the body  content) -->
<script type="text/javascript" src="scripts/wz_tooltip.js"></script>
<script type="text/javascript" src="scripts/tip_balloon.js"></script>
<% 
Hashtable<String,String> params = netutil.Encoding.getParameters(request.getQueryString());
String currentCloud = params.get("c");
if(currentCloud == null) currentCloud ="fake";  
String currentOperation = params.get("o");
if(currentOperation == null) currentOperation ="pr";
%>

<h1>
<div class="alignright">
<a href="javascript:void(0);" onmouseover="TagToTip('about',COPYCONTENT,false, ABOVE, true, STICKY, true, SHADOW, true);"><img src="figures/about_on.png" alt="" /></a>
</div>
TagCloud your data cube (<%= currentCloud %>)
</h1>
<p><a href="?page=clouddemo&amp;c=fake">Fake</a> / <a href="?page=clouddemo&amp;c=hier">hierarchical</a></p>

<% if(currentOperation.length() > 0) { %>
<p>Base operation: '<%= currentOperation %>'.</p>
<% } %>
<div id="tagcloudbundle">
	<div id="tagcloudarea"> cloud="<%= currentCloud %>" operation="<%= currentOperation %>" 
		<script type="text/javascript" charset="UTF-8">
			var dimensions=['country','continent'];
			var tagdims=['country'];
			var simdims=['continent'];
			var sourceid='<%= currentCloud %>';
			var currentOperation='<%= currentOperation %>';
			var tagcloudarea=$('tagcloudarea');
			var op = new Project(dimensions,tagdims, simdims);
			allOperations=new Array();
			allOperations.push(op);
			loadCloud(sourceid, op, tagcloudarea);
		</script>
	</div>
</div>
<div id="status"></div>
<div class="error" id="error"></div>
</div>