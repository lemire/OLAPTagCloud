<%@ page language="java" pageEncoding="UTF-8" import="java.util.*;import netutil.*"%>
 <%@ page import="java.util.*"%> 
<!-- the following script is necessary to display tips (must be included within the body  content) -->
<script type="text/javascript" src="scripts/wz_tooltip.js" charset="UTF-8"></script>
<script type="text/javascript" src="scripts/tip_balloon.js" charset="UTF-8"></script>

<div>
<% 
Hashtable<String,String> params = netutil.Encoding.getParameters(request.getQueryString());
String sourceid = params.get("id");
if(sourceid == null) {
	throw new RuntimeException("empty operation ID");
}
String allop = params.get("allop");
if(allop == null) {
	throw new RuntimeException("operations not found");
}

%>
<h1>Your Tag Cloud</h1>
<div id="tagcloudbundle">
<div id="tagcloudarea">
<script type="text/javascript" charset="UTF-8">
		var sourceid='<%= sourceid %>';
		var tagcloudarea=$('tagcloudarea');
		allOperations=new Array();
		allOperations=<%=allop%>.toArray();
		loadCloudFromOp(sourceid, tagcloudarea);
	</script>
</div>
</div>
<div id="status"></div>
<div class="error" id="error"></div>
</div>