<%@ page language="Java"  pageEncoding="UTF-8" %>
<!-- left menu -->
<% String currentPage = request.getParameter("page"); %>
<%!
private String print_menu_item(String currentPage, String title, String page){
	String menuItem="";
	if(page.equals(currentPage))
		menuItem="<span class='menu_item'><img src=\"figures/"+page+"_off.png\" alt=\"icon\" /> "+ title + "</span>";
	else
		menuItem="<span class='menu_item'><img src=\"figures/"+page+"_on.png\" alt=\"icon\" /> <a href='./?page="+page+"'>"+ title +"</a></span>";
	return menuItem;
}
%>
<div id="menu_frame">
<%= print_menu_item(currentPage,"Home","home") %>
<%= print_menu_item(currentPage,"Upload","upload") %>
<%= print_menu_item(currentPage,"Tag cloud","cloud") %>
<%= print_menu_item(currentPage,"Tag cloud examples","clouddemo") %>
<%= print_menu_item(currentPage,"About OlapTagCloud","about") %>
<%
	if ("plink".equals(currentPage)){%>
	<script type="text/javascript" charset="UTF-8">
	Element.hide('menu_frame');
	</script>
<%	}
%>
</div>
<!-- end left menu -->