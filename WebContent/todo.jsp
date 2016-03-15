<div style="font-size:0.5em">
<h1>Deadline</h1>
<ol>
<li>WEBIST 2008 <a href="http://www.webist.org/cfp.htm">call for papers</a>
has <span style="color:red">October 13</span> as a deadline. See
<a href="http://www.webist.org/CFP.htm#area2">area 2</a>.</li>
</ol>

<h1>TODO</h1>

<ol>
<li><del>Make demo clouds clickable again</del></li>
<li>Reimplement Owen's hierarchical tag clouds</li>
<li>Clicking on a tag ought to do something useful (slice?)</li>
<li><del>Support different kinds of tag cloud optimization (&amp;clusteralgo=NN,&amp;clusteralgo=MC)</del></li>
<li>Add OLAP tag cloud operations
	<ul>
		<li><del>Slice(Cuboid, one dimension, attribute values, [similarity dimensions]): List of tags (Slice can be viewed as a projection followed by StripTags)</del></li>
		<li><del>TopN(list of tags, value of N):List of tags</del></li>
		<li><del>Sort(list of tags, tag attribute {text|weight}, type{asc,desc}):List of tags</del></li>
		<li><del>Rollup(cuboid, dimensions, [similarity dimensions]):List of tags</del> and partial rollup (I think that this function is similar to the projection one)</li>
		<li><del>Project(cuboid, dimensions, [similarity dimensions]):List of tags</del></li>
		<li><del>StripTags(list of tags, attribute values):List of tags</del></li>
		<li><del>Dice(Cuboid, HashMap(dimension,attribute values), [similarity dimensions]):List of tags</del></li>
		<li><del>Iceberg(list of tags, measure value):List of tags</del></li>
		<li>...</li>
	</ul>
</li>
<li><del>Give the user more control over tag cloud optimization and dimensions</del></li>
<li><del>Fully implement the menu bar for each cloud</del></li>
<li><del>Document the abbreviated REST syntax</del></li>
</ol>

<p>This to-do list will be removed once the application is in beta.</p>
</div>
