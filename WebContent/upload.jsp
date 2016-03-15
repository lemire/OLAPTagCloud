<%@ page language="Java"  pageEncoding="UTF-8" %>
<!-- page content -->
<div id="content_frame">
<!-- the following script is necessary to display tips (must be included within the body  content) -->
<script type="text/javascript" src="scripts/wz_tooltip.js"></script>
<h1>
<div class="alignright">
<a href="javascript:void(0);" onmouseover="TagToTip('about',COPYCONTENT,false, ABOVE, true, STICKY, true, SHADOW, true);"><img src="figures/about_on.png" alt="" /></a>
</div>
Uploading data</h1>

<div id ="about" class="longtext">
	<h1>About this page</h1>
    <p>
        This form is where you upload
        your own <a href="http://en.wikipedia.org/wiki/CSV">CSV</a> or <a href="http://en.wikipedia.org/wiki/XML">XML</a> files.
        First pick the right file type (XML or CSV), then name your data set (such as my_dog_heights) so that
        you can later pull it back from the system. You can also enter a brief description of your data
        set.
    </p>
</div>  

<!-- This iframe is used as a place for the post to load -->
	<!--  This use of iframe is not XHTML compliant--> 
	<!--  name n'est pas un attribut de form en XHTML 1.1 
	target n'est pas un attribute de form en XHTML 1.1 -->
<form enctype="multipart/form-data" id="formUpload" method="post" action="up" target="target_upload">
	<fieldset><legend>Data source</legend>
		<div class="lineformright"><input type="text" name="sourceID" id="sourceID" /><span id="checkID"></span></div>
		<div class="lineform">Source ID</div>
		
		<div class="lineformright"><input type="file" id="dataFile" name="dataFile" /></div>
		<div class="lineform">Your data file</div>
		
		<div class="lineformright"><span id="labelatt">Attribute delimiter </span><input type="text" value="," name="attDelimiter" id="attDelimiter" size="2" maxlength="5" /></div>
		<div class="lineform"><input type="radio" name="fileFormat" id="fileFormatFlat" value="flat" checked="checked" /> <a href="http://en.wikipedia.org/wiki/CSV">CSV</a> file</div>
		
		<!-- Corresponding DTD <input type="file" id="dtdFile" name="dtdFile" />-->
		<div class="lineformright">&nbsp;</div>
		<div class="lineform"><input type="radio" name="fileFormat" id="fileFormatXML" value="xml" /> <a href="http://en.wikipedia.org/wiki/XML">XML</a> file</div>
		
		<div class="lineformrighttextarea"><textarea name="description" id="description" rows="3" cols="50"></textarea></div>
		<div class="lineformtextarea">Data description</div>
		
		<div class="lineformright"><input id="submitButton" type="submit" value="Upload your file"/></div>
		<div class="lineform">&nbsp;</div>
	</fieldset>
</form>
<!-- This is the upload status area -->
<div id="status"></div>
    <!--  I propose to leave it visible, for debugging purposes? -->
    <iframe id='target_upload' name='target_upload' src='' frameborder='0' height='30'  style='display: none; overflow:auto'></iframe>
</div>
