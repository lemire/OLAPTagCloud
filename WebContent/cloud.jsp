<%@ page language="java" pageEncoding="UTF-8"%>

<!-- page content -->

<!-- the following script is necessary to display tips (must be called within the body  content) -->
<script type="text/javascript" src="scripts/wz_tooltip.js"></script>
<script type="text/javascript" src="scripts/tip_balloon.js"></script>
<!-- <script type="text/javascript" src="scripts/wz_dragdrop.js"></script> -->

<div id="content_frame">
<h1>
<div class="alignright">
<a href="javascript:void(0);" onmouseover="TagToTip('about',COPYCONTENT,false, ABOVE, true, STICKY, true, SHADOW, true);"><img src="figures/about_on.png" alt="" /></a>
</div>
TagCloud your data cubes
</h1>
<div id="about" class="longtext">
	<h1>About this page</h1>
    <p>
        This form is where you can create your tag clouds from the available data set.
        First select a data set, then pick a few dimensions that are of interest to you.
        Finally, pick an operation: if you do not know what those are, simply pick "Project".
        Other applications are borrowed from traditional <a href="http://en.wikipedia.org/wiki/OLAP">OLAP</a> operations.
    </p>
</div>  


<form id="formTagCloud" method="get">
	<fieldset><legend>Tag Clouds</legend>
		<div class="lineformright">
			<select name="cubeID" id="cubeID"><option value=''>--- Select a data cube ---</option></select> 
			<a href="javascript:void(0);" id="des">description</a>
		</div>
		<div class="lineform">List of stored data cubes</div>
		<div style="clear:both" />
		<fieldset id="dimensions"><legend>List of dimensions</legend>
			<div id="navtitle">
				<div id="previoustitle">
					<!-- <input type="button" id="previousLink" value="Previous" /> -->
					<a href="javascript:void(0);" id="previousLink" title="Previous"><img src="figures/previous.png" alt="" /></a>
				</div>
				<div id="viewtitle">
					<span id="currentRec"></span>
				</div>
				<div id="nexttitle">
					<div class="alignright">
						<!-- <input type="button" id="plus" title="Hide details" value="-" /> -->
						<a href="javascript:void(0);" id="plus" title="Hide details"><img id="mp" src="figures/minus.png" alt="-" /></a>
					</div>
					<!-- <input type="button" id="nextLink" value="Next" />  -->
					<a href="javascript:void(0);" id="nextLink" title="next"><img src="figures/next.png" alt="" /></a> 
				</div>
			</div>
			<div id="navigation">
				<div id="previous"></div>
				<div id="view"></div>
				<div id="next"></div>
			</div>
			<!-- <div class="lineformright">&nbsp;&nbsp;</div><div class="lineform">&nbsp;</div>-->
		</fieldset>
		<fieldset id="operation"><legend>Tag cloud operations</legend>
			<div class="lineformright">
				<!-- onmouseover="TagToTip('selection',OFFSETY,-250,CENTERMOUSE,true,COPYCONTENT,false, CLOSEBTN, true, TITLE, 'Tag-support and similarity dimensions', STICKY, true)" -->
				<a id="projecttip" href="javascript:void(0);" ><img id="img_project" src="figures/project_off.png" alt="project" /></a>
				<a id="rolluptip" href="javascript:void(0);"><img id="img_rollup" src="figures/rollup_off.png" alt="rollup" /></a> 
				<a id="slicetip" href="javascript:void(0);"><img id="img_slice" src="figures/slice_off.png" alt="slice" /></a> 
				<a id="dicetip" href="javascript:void(0);"><img id="img_dice" src="figures/dice_off.png" alt="dice" /></a> 
				<a id="drilldowntip" href="javascript:void(0);"><img id="img_drilldown" src="figures/drilldown_off.png" alt="drill down" /></a>
			</div>
			<div class="lineform"><span>&nbsp;</span></div>	
		</fieldset>
	</fieldset>
	<fieldset id="fyourTagCloud">
		<legend>Your Tag Cloud</legend>
	</fieldset>
	<div id="status"></div>
</form>

<!--  
<img name="test" src="figures/project_on.png" alt="project" />

<div id="reldiv"><ilayer name="reldivn4" bgcolor="#eeeeee"><div>
                      To convert images or layers into draggable DHTML items, simply pass their names/IDs to the library's main function 'SET_DHTML()'.
                    </div></ilayer></div>
-->
<div id="tagdimtip">
<fieldset>
<legend>Select tag-support and similarity dimensions</legend>
<table>
<tr>
	<td colspan="3"><span style="color:red;">You have to select at least one tag-support dimension.</span></td>
</tr>
<tr>
	<td>Cuboid's dimensions</td>
	<td>Tag-support dimensions<sup>*</sup></td>
	<td>Clustering dimensions<sup>**</sup></td>
</tr>
<tr>
	<td>
		<select id="alldims" size="5" multiple="true" class="tipselect"> 
		</select>
	</td>
	<td>
		<select id="tagdims" size="5" multiple="true" class="tipselect">
		</select>
	</td>
	<td>
		<select id="simdims" size="5" multiple="true" class="tipselect">
		</select>
	</td>
</tr>
<tr>
	<td>--&gt;</td>
	<td><input id="addDim" type="button" value="Add" class="tipinput" /></td>
	<td><input id="addSim" type="button" value="Add" class="tipinput" /></td>
</tr>

<tr>
	<td>&lt;--</td>
	<td><input id="remDim" type="button" value="Remove" class="tipinput" /></td>
	<td><input id="remSim" type="button" value="Remove" class="tipinput" /></td>
</tr>
</table>
<table>
<tr>
<td id="slice">
	<fieldset>
	<legend>Slice</legend>
	<select id="dimtoslice" class="tipselect"><option value=''>--- Select a dimension ---</option></select><br />
	<select id="dimtoslicevalues" size="5" multiple="true" class="tipselect"></select>
	</fieldset>
</td>
<td id="dice">
	<fieldset>
	<legend>Dice</legend>
	<select id="currentdimtodice" class="tipselect"><option value=''>--- Selected dimensions ---</option></select><br />
	<select id="valuestodice" size="5" multiple="true" class="tipselect"></select><br />
	<input id="getValuesToDice" type="button" value="Get values" class="tipinput" /><br />
	<!--<input id="removeValuesToDice" type="button" value="Remove" class="tipinput" />-->
	</fieldset>
</td>
<td  id="rollup">
	<fieldset>
	<legend>Rollup</legend>
	<select id="dimtorollup" size="5" multiple="true" class="tipselect"></select><br />
	<input id="addRollupDim" type="button" value="Add" class="tipinput" /><br />
	<input id="removeRollupDim" type="button" value="Remove" class="tipinput" />
	</fieldset>
</td>
</tr>
</table>
</fieldset>
<div>
<div class="alignright"><input type="button" id="tagCloud" value="TagCloud" /></div>
<span id="selectedID"></span></div>
<p style="color:aaa;">
(*) The attribute values of the selected dimensions are combined to derive a tag cloud.<br />
(**) The selected dimensions are used to cluster the tags.
</p>
</div>
<div class="error" id="error"></div>
</div>