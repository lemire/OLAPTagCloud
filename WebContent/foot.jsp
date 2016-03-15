<%@ page language="Java" import="java.util.*"  pageEncoding="UTF-8" %>
<!-- mainframe end -->

</div>
<!-- Pages'foot -->
<div id="foot_frame">
<!--
<jsp:include page="todo.jsp" />
-->
<!--  borrowed from Daniel's tag sizing code  
<p id="testingarea" style="width:20cm;text-align:center;padding:0px;margin-left:
auto;margin-right:auto;border: solid black 1px"></p>
-->
<p>Time is : <%= new Date().toGMTString() %></p>

<p>
	<a href="http://validator.w3.org/check/referer">
	<img src="figures/w3c_xhtml_1.gif" alt="Valid XHTML 1.1!" /></a>
	<a href="http://jigsaw.w3.org/css-validator/check/referer">
	<img src="figures/valid-css.gif" alt="Valid CSS!" /></a>
</p>
<%
	String currentPage=request.getParameter("page");
	if ("plink".equals(currentPage)){%>
	<script type="text/javascript" charset="UTF-8">
	Element.hide('foot_frame');
	</script>
<%	}
%>
</div>
</body>
</html>
