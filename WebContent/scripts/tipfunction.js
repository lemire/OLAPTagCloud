/**
* This JavaScript file is the companion to 
* wz_tooltip.js
*/

/*---------------------------TipTools----------------------------------*/

function rollupTip(parentnode,alldims){
	return;
}

function diceTip(parentnode,alldims){
	return;
}

function sliceTip(parentnode,alldims){
	return;
}

function projectTip(parentnode,alldims){
	return;
}

function drilldownTip(parentnode,alldims){
	return;
}

function projectLinkTip(parentnode){
	currentalldims = new Array();
	currentalldims[0]="one";
	currentalldims[1]="two";
	currentalldims[2]="three";
	var dimname='';
	for (var i=0; i< currentalldims.length; ++i)
		dimname+=currentalldims[i];
	
	var divelement =document.createElement("div");
	divelement.className="rowtip";
	divelement.setAttribute("id",dimname);
	var tableelement = document.createElement("div");
	
	var rowelement = document.createElement("div");
	var cellelement= document.createElement("div");
	var textelement =document.createTextNode("You have to select at least one tag-support dimension.");
	cellelement.style.color="red";
	//cellelement.setAttribute("colspan",3);
	cellelement.appendChild(textelement);
	rowelement.appendChild(cellelement);
	tableelement.appendChild(rowelement);
	
	
	rowelement = document.createElement("div");
	rowelement.className="clear";
	cellelement= document.createElement("div");
	cellelement.appendChild(document.createTextNode("Cuboid's dimensions"));
	cellelement.className="celltip";
	rowelement.appendChild(cellelement);
	
	cellelement= document.createElement("div");
	cellelement.appendChild(document.createTextNode("Tag support dimensions*"));
	cellelement.className="celltip";
	rowelement.appendChild(cellelement);
	
	cellelement= document.createElement("div");
	cellelement.appendChild(document.createTextNode("Clustering dimensions**"));
	cellelement.className="celltip";
	rowelement.appendChild(cellelement);
	tableelement.appendChild(rowelement);
	
	rowelement = document.createElement("div");
	
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	var alldims=document.createElement("select");
	alldims.setAttribute("size",5);
	alldims.setAttribute("multiple",true);
	alldims.className="tipselect";
	arrayToList(currentalldims,alldims);
	cellelement.appendChild(alldims);
	rowelement.appendChild(cellelement);
	
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	var tagdims=document.createElement("select");
	tagdims.setAttribute("multiple",true);
	tagdims.setAttribute("size",5);
	tagdims.className="tipselect";
	cellelement.appendChild(tagdims);
	rowelement.appendChild(cellelement);
	
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	var simdims=document.createElement("select");
	simdims.setAttribute("multiple",true);
	simdims.setAttribute("size",5);
	simdims.className="tipselect";
	cellelement.appendChild(simdims);
	rowelement.appendChild(cellelement);
	
	tableelement.appendChild(rowelement);
	
	rowelement = document.createElement("div");
	
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	cellelement.appendChild(document.createTextNode("-->"));
	rowelement.appendChild(cellelement);
	
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	var addtagdims = document.createElement("input");
	addtagdims.setAttribute("type","button");
	addtagdims.setAttribute("value","Add");
	addtagdims.className="tipinput";
	cellelement.appendChild(addtagdims);
	rowelement.appendChild(cellelement);
	
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	var addsimdims = document.createElement("input");
	addsimdims.setAttribute("type","button");
	addsimdims.setAttribute("value","Add");
	addsimdims.className="tipinput";
	cellelement.appendChild(addsimdims);
	rowelement.appendChild(cellelement);
	
	tableelement.appendChild(rowelement);
	
	
	rowelement = document.createElement("div");
	//rowelement.className="clear";
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	cellelement.appendChild(document.createTextNode("<--"));
	rowelement.appendChild(cellelement);
	
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	var removetagdims = document.createElement("input");
	removetagdims.setAttribute("type","button");
	removetagdims.setAttribute("value","Remove");
	removetagdims.className="tipinput";
	cellelement.appendChild(removetagdims);
	rowelement.appendChild(cellelement);
	
	cellelement= document.createElement("div");
	cellelement.className="celltip";
	var removesimdims = document.createElement("input");
	removesimdims.setAttribute("type","button");
	removesimdims.setAttribute("value","Remove");
	removesimdims.className="tipinput";
	cellelement.appendChild(removesimdims);
	rowelement.appendChild(cellelement);
	
	tableelement.appendChild(rowelement);
	
	
	rowelement = document.createElement("div");
	rowelement.className="clear";
	cellelement= document.createElement("div");
	textelement =document.createTextNode("(*) The attribute values of the selected dimensions are combined to derive a tag cloud.");
	//cellelement.setAttribute("colspan",3);
	cellelement.appendChild(textelement);
	rowelement.appendChild(cellelement);
	
	tableelement.appendChild(rowelement);
	
	rowelement = document.createElement("div");
	
	cellelement= document.createElement("div");
	textelement =document.createTextNode("(**) The selected dimensions are used to cluster the tags.");
	//cellelement.setAttribute("colspan",3);
	cellelement.appendChild(textelement);
	rowelement.appendChild(cellelement);
	
	tableelement.appendChild(rowelement);
	//fieldtip.appendChild(tableelement);	
	
	removetagdims.disabled="true";
	removesimdims.disabled="true";
	
	Event.observe(addtagdims, 'click', function(event) {
		multipleSwitch(alldims,tagdims);
		enableDisableButtons();
	});
	
	Event.observe(addsimdims, 'click', function(event) {
    	multipleSwitch(alldims,simdims);
    	enableDisableButtons();
	});
	
	Event.observe(removetagdims, 'click', function(event) {
    	multipleSwitch(tagdims,alldims);
    	enableDisableButtons();
	});
	
	Event.observe(removesimdims, 'click', function(event) {
    	multipleSwitch(simdims,alldims);
    	enableDisableButtons();
	});
	
	enableDisableButtons = (function() {
		if (alldims.length==0){
			addtagdims.disabled=true;
			addsimdims.disabled=true;
		}else{
			addtagdims.disabled=false;
			addsimdims.disabled=false;
		}
	
		if (simdims.length==0) {
			removesimdims.disabled=true;
		}else{
			removesimdims.disabled=false;
		}
		
		if (tagdims.length==0) {
			removetagdims.disabled=true;
		}else{
			removetagdims.disabled=false;
		}
	});
	
	divelement.appendChild(tableelement);
	parentnode.appendChild(divelement);
	Element.hide(divelement);
	var link = document.createElement("a");
	link.setAttribute("href","#");
	link.appendChild(document.createTextNode("Click here to select tag-support and similarity dimensions"));
	
	link.onmouseover = function (e) {
		//Element.hide(tagdims);
		TagToTip(divelement.id,CENTERMOUSE,true,COPYCONTENT,false, CLOSEBTN, true, STICKY, true)
	};
	parentnode.appendChild(link);
}
/**
* Creates a fieldset that will be tagged to tip 
* current dimensions as an array
**/
function createTipTools(parentnode,linknode){
	currentalldims = new Array();
	currentalldims[0]="one";
	currentalldims[1]="two";
	currentalldims[2]="three";
	var dimname='';
	for (var i=0; i< currentalldims.length; ++i)
		dimname+=currentalldims[i];
	
	var tableelement = document.createElement("table");//<table>
	var tbodyleelement = document.createElement("tbody");//<tbody>
	var rowelement = document.createElement("tr");//<tr>
	var cellelement= document.createElement("td");//<td colspan="3">
	cellelement.setAttribute("colspan",3);
	cellelement.setAttribute("style","color:red;");
	var textelement =document.createTextNode("You have to select at least one tag-support dimension.");
	cellelement.appendChild(textelement);
	rowelement.appendChild(cellelement);//</td>
	tbodyleelement.appendChild(rowelement);//</tr>
	
	rowelement = document.createElement("tr");//<tr>
	cellelement= document.createElement("td");//<td>
	cellelement.appendChild(document.createTextNode("Cuboid's dimensions"));
	rowelement.appendChild(cellelement);//</td>
	cellelement= document.createElement("td");//<td>
	cellelement.appendChild(document.createTextNode("Tag support dimensions*"));
	rowelement.appendChild(cellelement);//</td>
	cellelement= document.createElement("td");//<td>
	cellelement.appendChild(document.createTextNode("Clustering dimensions**"));
	rowelement.appendChild(cellelement);//</td>
	tbodyleelement.appendChild(rowelement);//</tr>
	
	rowelement = document.createElement("tr");//<tr>
	cellelement= document.createElement("td");//<td>
	var alldims=document.createElement("select");
	alldims.setAttribute("size",5);
	alldims.setAttribute("multiple",true);
	alldims.className="tipselect";
	arrayToList(currentalldims,alldims);
	cellelement.appendChild(alldims);
	rowelement.appendChild(cellelement);//</td>
	cellelement= document.createElement("td");//<td>
	var tagdims=document.createElement("select");
	tagdims.setAttribute("multiple",true);
	tagdims.setAttribute("size",5);
	tagdims.className="tipselect";
	cellelement.appendChild(tagdims);
	rowelement.appendChild(cellelement);//</td>
	cellelement= document.createElement("td");//<td>
	var simdims=document.createElement("select");
	simdims.setAttribute("multiple",true);
	simdims.setAttribute("size",5);
	simdims.className="tipselect";
	cellelement.appendChild(simdims);
	rowelement.appendChild(cellelement);//</td>
	tbodyleelement.appendChild(rowelement);//</tr>
	
	rowelement = document.createElement("tr");//<tr>
	cellelement= document.createElement("td");//<td>
	cellelement.appendChild(document.createTextNode("-->"));
	rowelement.appendChild(cellelement);//</td>
	cellelement= document.createElement("td");//<td>
	var addtagdims = document.createElement("input");
	addtagdims.setAttribute("type","button");
	addtagdims.setAttribute("value","Add");
	addtagdims.className="tipinput";
	cellelement.appendChild(addtagdims);
	rowelement.appendChild(cellelement);//</td>
	cellelement= document.createElement("td");//<td>
	var addsimdims = document.createElement("input");
	addsimdims.setAttribute("type","button");
	addsimdims.setAttribute("value","Add");
	addsimdims.className="tipinput";
	cellelement.appendChild(addsimdims);
	rowelement.appendChild(cellelement);//</td>
	tbodyleelement.appendChild(rowelement);//</tr>
	
	rowelement = document.createElement("tr");//<tr>
	cellelement= document.createElement("td");//<td>
	cellelement.appendChild(document.createTextNode("<--"));
	rowelement.appendChild(cellelement);//</td>
	cellelement= document.createElement("td");//<td>
	var removetagdims = document.createElement("input");
	removetagdims.setAttribute("type","button");
	removetagdims.setAttribute("value","Remove");
	removetagdims.className="tipinput";
	cellelement.appendChild(removetagdims);
	rowelement.appendChild(cellelement);//</td>
	cellelement= document.createElement("td");//<td>
	var removesimdims = document.createElement("input");
	removesimdims.setAttribute("type","button");
	removesimdims.setAttribute("value","Remove");
	removesimdims.className="tipinput";
	cellelement.appendChild(removesimdims);
	rowelement.appendChild(cellelement);//</td>
	tbodyleelement.appendChild(rowelement);//</tr>
	
	rowelement = document.createElement("tr");//<tr>
	cellelement= document.createElement("td");//<td colspan="3">
	cellelement.setAttribute("colspan",3);
	textelement =document.createTextNode("(*) The attribute values of the selected dimensions are combined to derive a tag cloud.");
	cellelement.appendChild(textelement);
	rowelement.appendChild(cellelement);//</td>
	tbodyleelement.appendChild(rowelement);//</tr>
	
	rowelement = document.createElement("tr");//<tr>
	cellelement= document.createElement("td");//<td colspan="3">
	cellelement.setAttribute("colspan",3);
	textelement =document.createTextNode("(**) The selected dimensions are used to cluster the tags.");
	cellelement.appendChild(textelement);
	rowelement.appendChild(cellelement);//</td>
	tbodyleelement.appendChild(rowelement);//</tr>
	
	tableelement.appendChild(tbodyleelement);//</tbody>
	
	//var divelement = document.createElement("div");//<div>
	//divelement.setAttribute("id",dimname);
	//divelement.appendChild(tableelement);//</table>
	parentnode.appendChild(tableelement);//</div>
	Element.hide(parentnode);
	
	removetagdims.disabled="true";
	removesimdims.disabled="true";
	
	Event.observe(addtagdims, 'click', function(event) {
		multipleSwitch(alldims,tagdims);
		enableDisableButtons();
	});
	
	Event.observe(addsimdims, 'click', function(event) {
    	multipleSwitch(alldims,simdims);
    	enableDisableButtons();
	});
	
	Event.observe(removetagdims, 'click', function(event) {
    	multipleSwitch(tagdims,alldims);
    	enableDisableButtons();
	});
	
	Event.observe(removesimdims, 'click', function(event) {
    	multipleSwitch(simdims,alldims);
    	enableDisableButtons();
	});
	
	enableDisableButtons = (function() {
		if (alldims.length==0){
			addtagdims.disabled=true;
			addsimdims.disabled=true;
		}else{
			addtagdims.disabled=false;
			addsimdims.disabled=false;
		}
	
		if (simdims.length==0) {
			removesimdims.disabled=true;
		}else{
			removesimdims.disabled=false;
		}
		
		if (tagdims.length==0) {
			removetagdims.disabled=true;
		}else{
			removetagdims.disabled=false;
		}
	});
	
	var link = document.createElement("a");
	link.setAttribute("href","#");
	link.appendChild(document.createTextNode("Click here to select tag-support and similarity dimensions"));
	linknode.appendChild(link);
	link.onmouseover = function (e) {
		//Element.hide(tagdims);
		TagToTip(parentnode.id,OFFSETY,-250,CENTERMOUSE,true,COPYCONTENT,false, CLOSEBTN, true, TITLE, 'Tag-support and similarity dimensions', STICKY, true);
		//TagToTip(divelement.id,OFFSETY,-250,CENTERMOUSE,true,COPYCONTENT,false, CLOSEBTN, true, TITLE, 'Tag-support and similarity dimensions', STICKY, true);
	};
}

/*var url='cs';
	var dim = arrayToQueryString(this.dimensions,'dim'); 
	var tagdims=arrayToQueryString(this.tagdims,'cdim');
	var currentcube=this.datasource;
	var pars= 'q=v&id='+currentcube+tagdims+dim;
	displayAttributeValues = function(response){
		var results = eval('(' + response.responseText + ')')['cloud'];
		var tags = results['tag'];
		if(tags.length > 0) {
			for(var i = 0; i < tags.length; ++i) {
				var option = new Option(tags[i]['text']+' ('+tags[i]['trueweight']+')',tags[i]['text']);
	        	Try.these(
	            	function() {selectelement.add(option, null);}, // FF
	            	function() {selectelement.add(option, -1);}    // IE
	       		);
	       	}
		}else{
			alert('FIXME');
		}	
		if (selectelement.length==0) striptagsbutton.disabled=true;
			else striptagsbutton.disabled=false;
	}
	
	var myAjax = new Ajax.Request(
		url, 
		{method: 'get', parameters: pars, onComplete: displayAttributeValues, onFailure: reportError}
	);*/
	
