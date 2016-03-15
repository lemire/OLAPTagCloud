/**
* This JavaScript file is used to build tag clouds
* List of operations supported to create clouds (their get URL encodings are given bellow)
* Project: pr
* Rollup: ru
* Drill down: dd
* Slice: sl
* dice: di
* sort: so
* StripTags: st
* TopN: tn
* Iceberg: ic
* 
//Operations' parameters endcoding (they must be as short and meaningful as possible 
* Rollup dimensions: rud
* All dimensions: dim
* Tag-support dimensions: tag
* Similarity dimensions: sim
* Dimension to slice: sld
* Dimension values to slice: slv
* Dimensions to dice: did
* Dimension values to dice: div
* Sort attribute: att
* Sort type: t
* TopN value: n
* Iceberg minimum meausure value: m
* Tag text values to strip: stv
* 
*/
/*---------------------------Tag clouds--------------------------------*/

var offset =0

/**
* This is should be viewed as an abstract class
**/
function TagCloudArea(datasource, operation, tagcloudarea){
	this.datasource= datasource;
	this.operation= operation;
	this.tagcloudarea=tagcloudarea;
	this.data=null;//computed by getData
	this.tagcloudele=document.createElement("div");
	this.format="json"; //default value TODO turn this variable to a parameter
	this.menubar=document.createElement("p"); 
	this.undolist = new Array();//undo list (one undolist per cuboid function and tag cloud area) 
	
	//Methods
	this.addMenuBar=addMenuBar;
	this.addTags=addTags;
	this.getData=getData;
	this.getURL=getURL;
	this.getPermanentLink=getPermanentLink;
	this.getXMLURL=getXMLURL;
	this.addStatusBar=addStatusBar;
}

function TagCloudByRollup(datasource, operation, tagcloudarea){
	this.parent =TagCloudArea;
	this.parent(datasource, operation, tagcloudarea);//super
	this.url = this.getURL();
	this.permanentlink= this.getPermanentLink();
	this.xmlurl=this.getXMLURL();
	var rollup= arrayToQueryString(this.operation.getParameter('rud'),'rollupdims');
	this.url += rollup;
}
TagCloudByRollup.prototype = new TagCloudArea;

function TagCloudBySlice(datasource, operation, tagcloudarea){
	this.parent =TagCloudArea;
	this.parent(datasource, operation, tagcloudarea);//super
	
	this.url = this.getURL();
	this.permanentlink= this.getPermanentLink();
	this.xmlurl=this.getXMLURL();
	
	var slicedim= this.operation.getParameter('sld');
	var values = arrayToQueryString(this.operation.getParameter('slv'),'valuestoslice');
	this.url += '&slicedim='+slicedim+values;
}
TagCloudBySlice.prototype = new TagCloudArea;

function TagCloudByDice(datasource, operation, tagcloudarea){
	this.parent =TagCloudArea;
	this.parent(datasource, operation, tagcloudarea);//super
	
	this.url = this.getURL();
	this.permanentlink= this.getPermanentLink();
	this.xmlurl=this.getXMLURL();
	
	var dice = arrayToQueryString(this.operation.getParameter('did'),'dicedims');
	var values = arrayToQueryString(this.operation.getParameter('div'),'valuestodice');
	this.url += dice+values;
}

TagCloudByDice.prototype = new TagCloudArea;

function TagCloudByProject(datasource, operation, tagcloudarea){
	this.parent =TagCloudArea;
	this.parent(datasource, operation, tagcloudarea);//super
	this.url = this.getURL();
	this.permanentlink= this.getPermanentLink();
	this.xmlurl=this.getXMLURL();
}
TagCloudByProject.prototype = new TagCloudArea;

function getURL(){
	var dim = arrayToQueryString(this.operation.getParameter('dim'),'dim'); 
	var tagdim = arrayToQueryString(this.operation.getParameter('tag'),'tagdims');
	var simdim ='';
	if (arrayToQueryString(this.operation.getParameter('sim'),'simdims') !=null)
		simdims = arrayToQueryString(this.operation.getParameter('sim'),'simdims');
	return "cs?id="+this.datasource+"&as="+this.format+'&allop='+allOperations.toJSON();
}

function getXMLURL(){
	var dim = arrayToQueryString(this.operation.getParameter('dim'),'dim'); 
	var tagdim = arrayToQueryString(this.operation.getParameter('tag'),'tagdims');
	var simdim ='';
	if (arrayToQueryString(this.operation.getParameter('sim'),'simdims') !=null)
		simdims = arrayToQueryString(this.operation.getParameter('sim'),'simdims');
	return "cs?id="+this.datasource+"&as=xml"+'&allop='+allOperations.toJSON();
}

function getBaseURI() {
   var myuri = location.href;
   return myuri.substring(0,myuri.lastIndexOf("/")+1)
}

function getPermanentLink(){
	return getBaseURI()+"?page=plink&id="+this.datasource+'&allop='+allOperations.toJSON();
}

function addStatusBar(txt){
	var statusbar = document.createElement("p");
	statusbar.className="statusbar";
	statusbar.appendChild(document.createTextNode(txt));
	this.tagcloudarea.appendChild(statusbar);
}

function addTags(){
	if (this.data==null) {
		alert('before adding tags call getData() method');
		return false;
	}
	this.tagcloudarea.appendChild(this.tagcloudele);
	
	//Methods
	this.addTagsFromXML = addTagsFromXML;
	this.addTagsFromJSON = addTagsFromJSON;
	if (this.format=="xml") {
		this.addTagsFromXML();
	}else{ 
		if (this.format=="json"){
			this.addTagsFromJSON();
		} else {
			alert ("format" +this.format +" is not supported");
			return false;
		}
	}
}

function addTagsFromXML(){
	this.convertToHTML=convertToHTML;
	this.convertToElementWhenNeeded=convertToElementWhenNeeded;
	this.tagcloudele = convertToElementWhenNeeded(this.tagcloudele);
	this.tagcloudele.className = "tagcloud";
	
	for(var i = 0; i < this.data.childNodes.length; ++i) 
      		if(this.data.childNodes[i].tagName == "cloud") {
        		var ts = this.data.childNodes[i].childNodes;
        		break;
      		}
	var counter = 0;
    	if(ts.length > 0) {
      		computeRollupBoxes(ts[0]);  // for non-hierarchical, will toss some attributes onto the first tag
      		this.tagcloudele.appendChild(this.convertToHTML(ts[0]));
    	}else alert("empty cloud!");
    	
    	for(counter = 1; counter < ts.length; ++counter) {
       		this.tagcloudele.appendChild(document.createTextNode(" "));
       		this.tagcloudele.appendChild(this.convertToHTML(ts[counter]));
    	}
}

function addTagsFromJSON(){
	this.convertJSONToHTML=convertJSONToHTML;
	//this.convertToElementWhenNeeded=convertToElementWhenNeeded;
	//this.tagcloudele = convertToElementWhenNeeded(this.tagcloudele);
	this.tagcloudele.className = "tagcloud";
    if(this.data['tag'].length > 0) {
    	var counter = 0;
      	//computeRollupBoxes(this.data.tag[0]);  // for non-hierarchical, will toss some attributes onto the first tag
      	this.tagcloudele.appendChild(this.convertJSONToHTML(this.data.tag[0]));
      	
      	for(counter = 1; counter < this.data.tag.length; ++counter) {
       		this.tagcloudele.appendChild(document.createTextNode(" "));
       		this.tagcloudele.appendChild(this.convertJSONToHTML(this.data.tag[counter]));
    	}
    }else{
    	if (this.data.tag){
    		this.tagcloudele.appendChild(this.convertJSONToHTML(this.data.tag));
    	}else //This should not happen because if a tag cloud is actually empry an empty-tag is sent back
    		alert("empty cloud!");
    }
}

function addMenuBar(){
	if (this.url==null) {
		alert('before adding menubar call getData() method');
		return false;
	}
	//Methods
	this.addCloseButton = addCloseButton;
	this.addMinMaxButton = addMinMaxButton;
	this.addGetXML= addGetXML;
	this.addPermanentLink=addPermanentLink;
	this.addSortButton=addSortButton;
	this.addIcebergButton=addIcebergButton;
	this.addTopNButton=addTopNButton;
	this.addStripTagsButton=addStripTagsButton;
	
	this.menubar.className = "menubar";
	this.tagcloudarea.appendChild(this.menubar);
	
    this.addCloseButton();
    this.addMinMaxButton();
    this.addGetXML();
    this.addPermanentLink();
	this.addSortButton();
	this.addIcebergButton();
	this.addTopNButton();
	this.addStripTagsButton();
}

function addMinMaxButton(){
	//Minimize and maximize button
	var menubarelement = this.menubar;
	var tagcloudele = this.tagcloudele;
    var minmax=document.createElement("a");
    minmax.setAttribute("href","javascript:void(0);");
    minmax.setAttribute("title","Minimize this tag cloud");
    var minmaxfigure=document.createElement("img");
    minmaxfigure.setAttribute("src","figures/minus.png");
    minmaxfigure.setAttribute("alt","Minimize this tag cloud");
    var ismax=true;
    minmax.appendChild(minmaxfigure);
    minmax.className = "alignright";
    menubarelement.appendChild(minmax);
    Event.observe(minmax, 'click', function(event) {
		if (ismax){
			Element.hide(tagcloudele);
			minmaxfigure.setAttribute("src","figures/plus.png");
	    	minmaxfigure.setAttribute("alt","Maximize this tag cloud");
	    	ismax=false;
	    	minmax.setAttribute("title","Maximize this tag cloud");
	    }else{
	    	Element.show(tagcloudele);
			minmaxfigure.setAttribute("src","figures/minus.png");
	    	minmaxfigure.setAttribute("alt","Minimize this tag cloud");
	    	ismax=true;
	    	minmax.setAttribute("title","Minimize this tag cloud");
	    }
	});
}

function addCloseButton(){
	//Close button
	var tagcloudarea=this.tagcloudarea;
	var menubarelement = this.menubar;
	var closebutton=document.createElement("a");
	closebutton.setAttribute("href","javascript:void(0);");
	closebutton.setAttribute("title","Close this tag cloud");
	var closefigure=document.createElement("img");
	closefigure.setAttribute("src","figures/close.png");
	closefigure.setAttribute("alt","Close this tag cloud");
	closebutton.appendChild(closefigure);
	closebutton.className = "alignright";
	menubarelement.appendChild(closebutton);
	Event.observe(closebutton, 'click', function(event) {
		if (window.confirm('Really close this tag cloud?')){
			//tagcloudarea.previousSibling.nodeName returns LEGEND  for IE and #text for FF
			if (tagcloudarea.previousSibling){
				if (tagcloudarea.previousSibling.nodeName  != "#text" &&  tagcloudarea.previousSibling.nodeName  != "LEGEND"  && tagcloudarea.previousSibling.nodeName  != "legend"){
					Element.remove(tagcloudarea.previousSibling);
				}else{
					if (tagcloudarea.nextSibling)
						Element.remove(tagcloudarea.nextSibling);
				}
			}
			Element.remove(tagcloudarea);
		}
	});
}

function addGetXML(){
	var menubarelement = this.menubar;
	var axml = document.createElement("a");
    axml.setAttribute("href",this.xmlurl);
    axml.setAttribute("title","right click and save as...");
    axml.appendChild (document.createTextNode("Download as XML"));
    menubarelement.appendChild(document.createTextNode(" "));
    menubarelement.appendChild(axml);
}

function addPermanentLink(){
	var menubarelement = this.menubar;
	var id = 'id'+getRandomID();
    var divelement =document.createElement("div");
    divelement.setAttribute("id",id);
    divelement.appendChild (document.createTextNode("Paste link in email or IM"));
    divelement.appendChild (document.createElement("br"));
    var inputlink = document.createElement("input");
    inputlink.setAttribute("type","text");
    inputlink.setAttribute("size",50);
    inputlink.readOnly=true;
    inputlink.setAttribute("value",this.getPermanentLink());
    divelement.appendChild(inputlink);
    divelement.appendChild (document.createElement("br"));
    divelement.appendChild (document.createTextNode("Paste HTML to embed in website"));
    divelement.appendChild (document.createElement("br"));
    var inputiframe=document.createElement("input");
    inputiframe.setAttribute("size",50);
    inputiframe.setAttribute("type","text");
    inputiframe.readOnly=true;
    var iframe ="<iframe width=\"670\" height=\"300\" frameborder=\"0\" scrolling=\"auto\" marginheight=\"0\" marginwidth=\"0\" src='"+this.getPermanentLink()+"&iframe=true'></iframe>";   
    inputiframe.setAttribute("value",iframe);
    divelement.appendChild(inputiframe);
    
    menubarelement.appendChild(divelement);
    Element.hide(divelement);
    
    var auri = document.createElement("a");
    auri.setAttribute("href","javascript:void(0);");
    auri.appendChild(document.createTextNode(" Permanent link "));
    menubarelement.appendChild (document.createTextNode("|"));
    menubarelement.appendChild(auri);
    
    auri.onmouseover = function (e) {
		TagToTip(id,CENTERMOUSE,true,COPYCONTENT,false, STICKY, true, SHADOW, true, ABOVE,true,OFFSETY,offset, CLOSEBTN, true, TITLE, 'Permanent link to this tag cloud');
	};
	Event.observe(inputlink, 'click', function(event) {
    	inputlink.select();
	});
	Event.observe(inputiframe, 'click', function(event) {
    	inputiframe.select();
	});
}

function addSortButton(){
	var menubarelement = this.menubar;
	var datasource=this.datasource;
	var operation=this.operation;
	var tagcloudarea=this.tagcloudarea;
	var undolist = this.undolist;
	
	var sortelement=document.createElement("a");
    sortelement.appendChild (document.createTextNode(" Sort "));
    sortelement.setAttribute("href","javascript:void(0);");
    sortelement.setAttribute("title","sort");
    menubarelement.appendChild (document.createTextNode("|"));
    menubarelement.appendChild(sortelement);
    
    var id = 'id'+getRandomID();
    var divelement =document.createElement("div");
    divelement.setAttribute("id",id);
    
	divelement.appendChild (document.createTextNode("By tag attribute: "));
	var attelement = document.createElement("select");
	
	//first option
	var option = new Option("text","text");
	Try.these(
	     function() {attelement.add(option, null);}, // FF
	     function() {attelement.add(option, -1);}    // IE
	 );
	//second option
	var option = new Option("weight","weight");
	Try.these(
	 	function() {attelement.add(option, null);}, // FF
	 	function() {attelement.add(option, -1);}    // IE
	 );
	divelement.appendChild(attelement);
	
	divelement.appendChild (document.createElement("br"));
	divelement.appendChild (document.createTextNode("Type: "));
	
	var typeelement = document.createElement("select");
	//first option
	var option = new Option("asc","asc");
	Try.these(
	     function() {typeelement.add(option, null);}, // FF
	     function() {typeelement.add(option, -1);}    // IE
	 );
	//second option
	var option = new Option("desc","desc");
	Try.these(
	     function() {typeelement.add(option, null);}, // FF
	     function() {typeelement.add(option, -1);}    // IE
	 );
	 
	divelement.appendChild(typeelement);
		
	divelement.appendChild (document.createElement("br"));
	var sortbutton = document.createElement("input");
	sortbutton.setAttribute("type","button");
	sortbutton.setAttribute("value","Sort");
	sortbutton.className="alignright";
	divelement.appendChild(sortbutton);
	menubarelement.appendChild(divelement);
	Element.hide(divelement);
	
	Event.observe(sortbutton, 'click', function(event) {
    	var op = new Sort(typeelement.options[typeelement.selectedIndex].value, attelement.options[attelement.selectedIndex].value);
		allOperations.push(op);
		undolist.push(allOperations.length-1);
		updateCloud(datasource,operation,undolist,tagcloudarea);
	});
	
	
	sortelement.onmouseover = function (e) {
		TagToTip(id,CENTERMOUSE,true,COPYCONTENT,false, STICKY, true,  ABOVE,true,  OFFSETY,offset, SHADOW, true, CLOSEBTN, true, TITLE, 'Sort');
	};
}

function addIcebergButton(){
	var menubarelement = this.menubar;
	var datasource=this.datasource;
	var operation=this.operation;
	var tagcloudarea=this.tagcloudarea;
	var undolist = this.undolist;
	
    var icebergelement=document.createElement("a");
    icebergelement.appendChild (document.createTextNode(" Iceberg "));
    icebergelement.setAttribute("href","javascript:void(0);");
    icebergelement.setAttribute("title","iceberg");
    menubarelement.appendChild (document.createTextNode("|"));
    menubarelement.appendChild(icebergelement);
    
    var id = 'id'+getRandomID();
    var divelement =document.createElement("div");
    divelement.setAttribute("id",id);
    
    divelement.appendChild (document.createTextNode("Measure value: "));
    
    var valueelement = document.createElement("input");
	valueelement.setAttribute("type","input");
	divelement.appendChild(valueelement);	
	var icebergbutton = document.createElement("input");
	icebergbutton.setAttribute("type","button");
	icebergbutton.setAttribute("value","Iceberg");
	icebergbutton.disabled=true;
	divelement.appendChild(icebergbutton);
	
	var spanelement = document.createElement("span");
	var brelement = document.createElement("br");
	divelement.appendChild(brelement);
	divelement.appendChild(spanelement);
	
	menubarelement.appendChild(divelement);
	Element.hide(spanelement);
	Element.hide(brelement);
	Element.hide(divelement);
	
	Event.observe(valueelement, 'keyup', function(event) {
		if (valueelement.value.match("^\\s*\\d+\\s*$")){ 
			icebergbutton.disabled=false;
			spanelement.innerHTML="";
			Element.hide(spanelement);
			Element.hide(brelement);
		}else{
			icebergbutton.disabled=true;
			spanelement.innerHTML="measure value must be a positive integer";
			spanelement.className="error";
			Element.show(spanelement);
			Element.show(brelement);
		}	
	});
	
	Event.observe(icebergbutton, 'click', function(event) {
		var op = new Iceberg(valueelement.value);
		allOperations.push(op);
		undolist.push(allOperations.length-1);
		updateCloud(datasource,operation,undolist,tagcloudarea);
	});
	
	icebergelement.onmouseover = function (e) {
		TagToTip(id,CENTERMOUSE,true,COPYCONTENT,false, STICKY, true,  ABOVE,true, OFFSETY,offset, SHADOW, true, CLOSEBTN, true, TITLE, 'Iceberg');
	};
}

function addTopNButton(){
	var menubarelement = this.menubar;
	var datasource=this.datasource;
	var operation=this.operation;
	var tagcloudarea=this.tagcloudarea;
	var undolist = this.undolist;
	
    var topnelement=document.createElement("a");
    topnelement.appendChild (document.createTextNode(" TopN "));
    topnelement.setAttribute("href","javascript:void(0);");
    topnelement.setAttribute("title","Top N");
    menubarelement.appendChild (document.createTextNode("|"));
    menubarelement.appendChild(topnelement);
    
    var id = 'id'+getRandomID();
    var divelement =document.createElement("div");
    divelement.setAttribute("id",id);
    
    divelement.appendChild (document.createTextNode("N value: "));
    
    var valueelement = document.createElement("input");
	valueelement.setAttribute("type","input");
	divelement.appendChild(valueelement);
	
	var topnbutton = document.createElement("input");
	topnbutton.setAttribute("type","button");
	topnbutton.setAttribute("value","Top N");
	topnbutton.disabled=true;
	divelement.appendChild(topnbutton);
	
	var spanelement = document.createElement("span");
	var brelement = document.createElement("br");
	spanelement.appendChild(document.createTextNode("N must be a positive integer"));
	spanelement.className="error";
	divelement.appendChild(brelement);
	divelement.appendChild(spanelement);
	
	menubarelement.appendChild(divelement);
	
	Element.hide(spanelement);
	Element.hide(brelement);
	Element.hide(divelement);
	
	Event.observe(valueelement, 'keyup', function(event) {
		if (valueelement.value.match("^\\s*\\d+\\s*$")){ 
			topnbutton.disabled=false;
			Element.hide(spanelement);
			Element.hide(brelement);
		}else{
			topnbutton.disabled=true;
			Element.show(spanelement);
			Element.show(brelement);
		}
	});
	
	Event.observe(topnbutton, 'click', function(event) {
    	var op = new TopN(valueelement.value);
		allOperations.push(op);
		undolist.push(allOperations.length-1);
		updateCloud(datasource,operation,undolist,tagcloudarea);
	});

	topnelement.onmouseover = function (e) {
		TagToTip(id,CENTERMOUSE,true,COPYCONTENT,false, STICKY, true,  ABOVE,true, OFFSETY,offset, SHADOW, true, CLOSEBTN, true, TITLE, 'Top N');
	};
}

function addStripTagsButton(){
	var menubarelement = this.menubar;
	var datasource=this.datasource;
	var operation=this.operation;
	var tagcloudarea=this.tagcloudarea;
	var undolist = this.undolist;
	
    var striptagselement=document.createElement("a");
    striptagselement.appendChild (document.createTextNode(" Strip tags "));
    striptagselement.setAttribute("href","javascript:void(0);");
    striptagselement.setAttribute("title","Strip tags");
    menubarelement.appendChild (document.createTextNode("|"));
    menubarelement.appendChild(striptagselement);
    
    var id = 'id'+getRandomID();
    var divelement =document.createElement("div");
    divelement.setAttribute("id",id);
    var img = document.createElement("img");
    img.setAttribute("src","figures/about_on.png");
    img.setAttribute("alt","");
    var tiplink =document.createElement("a");
    tiplink.setAttribute("href","javascript:void(0);");
    tiplink.appendChild(img);
    divelement.appendChild(tiplink);
    
    divelement.appendChild (document.createTextNode(" List of current tags"));
    divelement.appendChild (document.createElement("br"));
    
    var about = document.createElement("span");
    about.appendChild(document.createTextNode('Use CTRL to select more than one item'));
    about.className="tipselect";
    divelement.appendChild(about);
    Element.hide(about);
    var state=0;
    tiplink.onclick = function (e) {
		if (state==0){
			Element.show(about);
			state=1;
		}else {
			Element.hide(about);
			state=0;
		}
	};
	
	tiplink.onblur = function (e) {
		Element.hide(about);
	};
    
    var selectelement = document.createElement("select");
	selectelement.setAttribute("size",5);
	selectelement.setAttribute("multiple",true);
	selectelement.className="tipselect";
	divelement.appendChild(selectelement);
	
	divelement.appendChild (document.createElement("br"));
	
	var striptagsbutton = document.createElement("input");
	striptagsbutton.setAttribute("type","button");
	striptagsbutton.setAttribute("value","Strip tags");
	striptagsbutton.className="tipselect";
	divelement.appendChild(striptagsbutton);
	menubarelement.appendChild(divelement);
	Element.hide(divelement);
	
	Event.observe(striptagsbutton, 'click', function(event) {
    	var op = new StripTags(selectedOptionsToArray(selectelement));
    	allOperations.push(op);
    	undolist.push(allOperations.length-1);
		updateCloud(datasource,operation,undolist,tagcloudarea);
	});
	
	Event.observe(selectelement, 'change', function(event) {
    	if (selectedOptionsToArray(selectelement).length ==0) striptagsbutton.disabled=true;
		else striptagsbutton.disabled=false;
	});
	
	var currenttags = this.data['tag'];
	if(currenttags.length > 0) {
      	for(var i=0; i < currenttags.length; ++i) {
      		var option = new Option(currenttags[i]['text']+' ('+currenttags[i]['trueweight']+')',currenttags[i]['text']);
	        Try.these(
	            function() {selectelement.add(option, null);}, // FF
	            function() {selectelement.add(option, -1);}    // IE
	       	);
    	}
    }else{
    	if (this.data.tag){
    		var option = new Option(this.data.tag['text']+' ('+this.data.tag['trueweight']+')',this.data.tag['text']);
	        Try.these(
	            function() {selectelement.add(option, null);}, // FF
	            function() {selectelement.add(option, -1);}    // IE
	       	);
    	}else{//This should not happen
    		striptagsbutton.disabled=true;
    		alert("empty cloud!");
    	}
    }
	if (selectedOptionsToArray(selectelement).length ==0) striptagsbutton.disabled=true;
	else striptagsbutton.disabled=false;
	
	striptagselement.onmouseover = function (e) {
		TagToTip(id,CENTERMOUSE,true,COPYCONTENT,false, STICKY, true,  ABOVE,true, OFFSETY,offset,SHADOW, true, CLOSEBTN, true, TITLE, 'Strip some tags');
	};
}

function getData(){
	new Ajax.Request(this.url,{
    	method: 'get', 
    	onFailure: function(transport) {
      		// our failure report mechanism is terrible, we need to be friendlier than this to the users!
      		var results = eval('(' + transport.responseText + ')');
			if (results){
				var rows  = results['results']['row'];
				var error  = results['results']['error'];
				if (error){
					if(document.getElementById('error')){
						Element.show("error");
						if(document.getElementById('error')) $('error').innerHTML = error;
						if(document.getElementById('tagCloud')) $('tagCloud').disabled=false;
					}
					if(document.getElementById('status')) $('status').innerHTML='';
					return;
				}
			}else{//Should not happen
				alert("We are in trouble (failure to load XML -- response from server was "+ transport.status+")\n TODO: do something smarter than an alert (FIXME)");
     	 		return;
			}
     	 },
   		 onComplete: function(transport, object) {
       		if((transport.status >= "200") && (transport.status < "300")){
       			succesfunc(transport);
       		}
     	}
  	});
}

//Operation
function Operation(id){
	this.param= new Hash();
	this.param["id"]=id;
	this.id=id;
	this.toJSON=operationToJSON;
	this.getParameter=getParameter;
}

function operationToJSON(){
	return this.param.toJSON();
}

function getParameter(name){
	return this.param[name];
}

function Project(dimensions, tagdims, simdims){
	this.parent=Operation;
	this.parent("pr");//super
    this.param["dim"]=dimensions;
    this.param["tag"]=tagdims;
    this.param["sim"]=simdims;
}
Project.prototype = new Operation;

function Rollup(dimensions, tagdims, simdims, rollupdims){
	this.parent=Operation;
	this.parent("ru");//super
	this.param["dim"]=dimensions;
    this.param["tag"]=tagdims;
    this.param["sim"]=simdims;
    this.param["rud"]=rollupdims;
}
Rollup.prototype = new Operation;

function Slice(dimensions, tagdims, simdims, slicedim,slicevalues){
	this.parent =Operation;
	this.parent("sl");//super
    this.param["dim"]=dimensions;
    this.param["tag"]=tagdims;
    this.param["sim"]=simdims;
    this.param["sld"]=slicedim;
    this.param["slv"]=slicevalues;
}
Slice.prototype = new Operation;

function Dice(dimensions, tagdims, simdims, dicedims,dicevalues){
	this.parent =Operation;
	this.parent("di");//super
    this.param["dim"]=dimensions;
    this.param["tag"]=tagdims;
    this.param["sim"]=simdims;
    this.param["did"]=dicedims;
    this.param["div"]=dicevalues;
}
Dice.prototype = new Operation;

function Sort(type,attribute){
	this.parent =Operation;
	this.parent("so");//super
    this.param["t"]=type;
    this.param["att"]=attribute;
}
Sort.prototype = new Operation;

function TopN(n){
	this.parent=Operation;
	this.parent("tn");//super
    this.param["n"]=n;
}
TopN.prototype = new Operation;

function Iceberg(measure){
	this.parent =Operation;
	this.parent("ic");//super
    this.param["m"]=measure;	
}
Iceberg.prototype = new Operation;

function StripTags(tags){
	this.parent =Operation;
	this.parent("st");//super
    this.param["tags"]=tags;
}
StripTags.prototype = new Operation;

function loadCloud(datasource,operation,tagcloudarea){
	// if you remove the if clause on the next line and there is no navigation id, 
	// as it is the case on the demo page, then the script fails silently... 
	// prototype is a bit broken because $(...) returns an object even if there is no id!!!
	//
	// it is just not very robust to have the whole script fail (silently) because
	// you omitted ids in the HTML
	var mytagcloud;
	
	if(document.getElementById('navigation')) Element.hide('navigation');
	if(document.getElementById('mp')) {
	  $('mp').setAttribute("src","figures/plus.png");
	  $('mp').setAttribute("alt","+");
	}
	
	cleanElement(tagcloudarea);
	if(document.getElementById('error')) $('error').innerHTML='';
	if(document.getElementById('plus')) $('plus').setAttribute("title","Show details");
	
	switch (operation.id) {
		case 'pr':
			mytagcloud = new TagCloudByProject(datasource, operation, tagcloudarea);
			if(document.getElementById('img_project')) {
				$('img_project').setAttribute("src","figures/project_on.png");
				$('img_rollup').setAttribute("src","figures/rollup_off.png");
				$('img_slice').setAttribute("src","figures/slice_off.png");
				$('img_dice').setAttribute("src","figures/dice_off.png");
			}
			break;
		case 'ru':
			mytagcloud = new TagCloudByRollup(datasource, operation, tagcloudarea);
			if(document.getElementById('img_rollup')) {
				$('img_project').setAttribute("src","figures/project_off.png");
				$('img_rollup').setAttribute("src","figures/rollup_on.png");
				$('img_slice').setAttribute("src","figures/slice_off.png");
				$('img_dice').setAttribute("src","figures/dice_off.png");
			}
			break;
		case 'sl':
			mytagcloud = new TagCloudBySlice(datasource, operation, tagcloudarea);
			if(document.getElementById('img_slice')) {
				$('img_project').setAttribute("src","figures/project_off.png");
				$('img_rollup').setAttribute("src","figures/rollup_off.png");
				$('img_slice').setAttribute("src","figures/slice_on.png");
				$('img_dice').setAttribute("src","figures/dice_off.png");
			}
			break;
		case 'di':
			mytagcloud = new TagCloudByDice(datasource, operation, tagcloudarea);
			if(document.getElementById('img_dice')) {
				$('img_project').setAttribute("src","figures/project_off.png");
				$('img_rollup').setAttribute("src","figures/rollup_off.png");
				$('img_slice').setAttribute("src","figures/slice_off.png");
				$('img_dice').setAttribute("src","figures/dice_on.png");
			}
			break;
		default: 
			alert('not implemented');
			if(document.getElementById('status')) $('status').innerHTML='';
			if(document.getElementById('fyourTagCloud'))  Element.hide("fyourTagCloud");
	    	if(document.getElementById('tagCloud')) $('tagCloud').disabled = false;
			return;
	}
	var beforedate = new Date();
	
	if(document.getElementById('status')) $('status').innerHTML='Loading tag cloud... please wait. <img src="figures/loading.gif" height="20" width="20" alt="Loading" />';
	mytagcloud.getData();//getting data (of the tag cloud) by AJAX query
	succesfunc = function(transport) {
		if (mytagcloud.format=="json") {
			mytagcloud.data = eval('(' + transport.responseText + ')')['cloud'];
		}else
		if(mytagcloud.format=="xml"){
			mytagcloud.data = transport.responseXML;
		}else{
			alert('Expected tag cloud format is not supported');
		}	
		if(document.getElementById('status')) $('status').innerHTML='';
		mytagcloud.addMenuBar();
		mytagcloud.addTags();
		var afterdate = new Date();
	    var timeelapsed = (afterdate.getTime()-beforedate.getTime());
	    var msg = "time elapsed: "+timeelapsed+" ms";
        if(operation.getParameter("dim"))
          msg+=", aggregating over {"+arrayToString(operation.getParameter("dim"))+"}";
        if(operation.getParameter("tag"))
          msg+=", showing {"+arrayToString(operation.getParameter("tag"))+"}";
        if(operation.getParameter("sim"))
          msg+=", clustering tags by {"+arrayToString(operation.getParameter("sim"))+"}";
	    mytagcloud.addStatusBar(msg);
	    if(document.getElementById('fyourTagCloud'))  Element.show("fyourTagCloud");
	    if(document.getElementById('tagCloud')) $('tagCloud').disabled = false;
	}
}

function updateCloud(datasource,operation,undolist,tagcloudarea){
	var mytagcloud;
	if(document.getElementById('navigation')) Element.hide('navigation');
	if(document.getElementById('mp')) {
	  $('mp').setAttribute("src","figures/plus.png");
	  $('mp').setAttribute("alt","+");
	}
	if(document.getElementById('error')) $('error').innerHTML='';
	if(document.getElementById('plus')) $('plus').setAttribute("title","Show details");
	switch (operation.id) {
		case 'pr':
			mytagcloud = new TagCloudByProject(datasource, operation, tagcloudarea);
			break;
		case 'ru':
			mytagcloud = new TagCloudByRollup(datasource, operation, tagcloudarea);
			break;
		case 'sl':
			mytagcloud = new TagCloudBySlice(datasource, operation, tagcloudarea);
			break;
		case 'di':
			mytagcloud = new TagCloudByDice(datasource, operation, tagcloudarea);
			break;
		default: 
			alert('not implemented');
			return;
	}
	mytagcloud.undolist=undolist;
	var beforedate = new Date();
	cleanElement(tagcloudarea);
	if(document.getElementById('status')) $('status').innerHTML='Loading tag cloud... please wait. <img src="figures/loading.gif" height="20" width="20" alt="Loading" />';
	mytagcloud.getData();//getting data (of the tag cloud) by AJAX query
	succesfunc = function(transport) {
		if (mytagcloud.format=="json") {
			mytagcloud.data = eval('(' + transport.responseText + ')')['cloud'];
		}else
		if(mytagcloud.format=="xml"){
			mytagcloud.data = transport.responseXML;
		}else{
			alert('Expected tag cloud format is not supported');
		}	
		if(document.getElementById('status')) $('status').innerHTML='';
		mytagcloud.addMenuBar();
		mytagcloud.addTags();
		//Updating menubar
		if (undolist.length >0){
			mytagcloud.menubar.appendChild (document.createTextNode("|"));
			var undo=document.createElement("a");
			undo.appendChild (document.createTextNode(" Undo "));
			undo.setAttribute("href","javascript:void(0);");
			undo.setAttribute("title","Undo the last operation");
			mytagcloud.menubar.appendChild(undo);
			undo.onclick = function (e) {
		    	if (mytagcloud.undolist.length >0){
		    	 	allOperations.splice(mytagcloud.undolist[mytagcloud.undolist.length-1],1); 
		    	 	mytagcloud.undolist.pop(); 
		    	 	updateCloud(datasource,operation,undolist,tagcloudarea);
		    	 }
			};
			
			
			var afterdate = new Date();
		    var timeelapsed = (afterdate.getTime()-beforedate.getTime());
		    var msg = "time elapsed: "+timeelapsed+" ms";
	        if(operation.getParameter("dim"))
	          msg+=", aggregating over {"+arrayToString(operation.getParameter("dim"))+"}";
	        if(operation.getParameter("tag"))
	          msg+=", showing {"+arrayToString(operation.getParameter("tag"))+"}";
	        if(operation.getParameter("sim"))
	          msg+=", clustering tags by {"+arrayToString(operation.getParameter("sim"))+"}";
		    mytagcloud.addStatusBar(msg);
		    if(document.getElementById('fyourTagCloud'))  Element.show("fyourTagCloud");
		    if(document.getElementById('tagCloud')) $('tagCloud').disabled = false;
		}else{
			loadCloud(datasource,operation,tagcloudarea);
		}
	}
}

/**
* remove any element present in the element.
* This might not be needed anymore, but it is 
* still useful to keep the HTML sane.
*/
function cleanElement(e) {
	if(! e.firstChild) return;//alert ("e.firstChild is null!");
	if(! e.removeChild) alert ("e.removeChild is null!");
	while(e.firstChild) e.removeChild(e.firstChild);
}

function tagHTML(e) {
	var spanele = document.createElement("span");
	spanele.appendChild(document.createTextNode(e.getAttribute("text")));
	spanele.className = "tag"+e.getAttribute("weight");
	spanele.setAttribute("title",e.getAttribute("trueweight"));
	return spanele;
}

/**
 * this function is the part that converts the
 * the JSO? (from our servlets) to HTML
 */
 function convertJSONToHTML(thistag) {
 	this.addEventsToElement=addEventsToElement;
 	var spanele = document.createElement("span");
	spanele.appendChild(document.createTextNode(thistag.text));
	spanele.className = "tag"+thistag.weight;
	spanele.setAttribute("title",thistag.trueweight);
 	this.addEventsToElement(spanele);
 	return spanele;
}

/**
 * this function is the part that converts the
 * the XML (from our servlets) to HTML
 */
 function convertToHTML(thistag) {
 	this.addEventsToElement=addEventsToElement;
 	if(thistag.nodeName == "tag") {
 		var spanele = tagHTML(thistag);
 		this.addEventsToElement(spanele);
 		return spanele;
 	} else if (thistag.nodeName == "bundle") {
 		// maybe this is truncated here...
 		var hp = parseInt(thistag.getAttribute("hpad"));
        var wp = parseInt(thistag.getAttribute("wpad"));
        if (thistag.hasAttribute("goNoFurther")) {
             // want to use a rectangle whose size matches the rendered nested table
             // that is being omitted.  Want the rectangle to contain my rollup tag
             // with proper tag format.  To do this, I will use a 3x3 table with the tag
             // in the centre and padding images in the top left and bottom right
             // transparent images preferred.
             // let's play with images now
             
             if (hp < 0) hp = 0;
             if (wp < 0) wp = 0;
             
             var tabele = document.createElement("table");
             var rowele = document.createElement("tr");  // first row
             tabele.appendChild(rowele);       
             var cellele = document.createElement("td");  // top left
             cellele.appendChild(paddingPicture(hp/2, wp/2));
             rowele.appendChild(cellele);
             rowele = document.createElement("tr"); // middle row
             rowele.appendChild( document.createElement("td")); // blank cell
             cellele = document.createElement("td");  // cell with tag
             cellele.appendChild( convertToHTML(thistag.firstChild));
             // picked up Event handler in the recursive call.
             rowele.appendChild(cellele);
             tabele.appendChild(rowele); 
             rowele = document.createElement("tr"); // last row
             rowele.appendChild( document.createElement("td")); // blank cell 
             rowele.appendChild( document.createElement("td")); // blank cell 
             cellele = document.createElement("td");
             cellele.appendChild(paddingPicture(hp/2,wp/2));
             rowele.appendChild( cellele); // pad
             tabele.appendChild(rowele); 
             
             return tabele;
             // return convertToHTML(thistag.firstChild);  // use the rollup tag
         }
       
         // we build a table.  There is a remote chance that it will
         // have to be padded wider or higher, because its rollup tag is bigger.
 
         if (hp < 0) hp = -hp; else hp=0;
         if (wp < 0) wp = -wp; else wp=0; 
       
         var tableele = document.createElement("table");
         if(thistag.getAttribute("border")=="true")
           tableele.setAttribute("border","1");
         else 
           tableele.setAttribute("border","0");
           
         var children = thistag.childNodes;
         // child 0 is a "rollup tag"
         // first row is only for padding.
         
         // I did not account for the extra pixels from globbing on extra empty rows/ columns??
         var padRow = document.createElement("tr");
         var padCell =document.createElement("td");
         var rowLen = 0;
         padRow.appendChild(padCell);
         padCell.appendChild(paddingPicture(hp/2,wp/2));
         
         tableele.appendChild(padRow);
         for(var i = 1; i < children.length; ++i) {
           if(children[i].nodeName != "row")
             alert("warning: bundle contains element "+children[i].nodeName);
           var trele = document.createElement("tr");
           // empty column first
           trele.appendChild(document.createElement("td"));
           var kids = children[i].childNodes;
           rowLen = kids.length;
           for(var j = 0; j < kids.length; ++j) {
             if((kids[j].nodeName != "tag") && (kids[j].nodeName != "bundle"))
               alert("warning: row contains element "+kids[j].nodeName);
             var tdele = document.createElement("td");
             tdele.appendChild(convertToHTML(kids[j]));
             trele.appendChild(tdele); 
           }          
           tableele.appendChild(trele);
         }
         // another row of padding
         trele = document.createElement("tr");
         // rowlen empty cells; +1 for the new leftmost column.  
         // Then a new rightmost pad column
         for (var i=0; i < rowLen+1; ++i) 
            trele.appendChild(document.createElement("td"));
         var padCell = document.createElement("td");
         padCell.appendChild(paddingPicture(hp/2, wp/2));  // blank pix lower right
         trele.appendChild(padCell);
         tableele.appendChild(trele);
         return tableele;
       } else {
         alert("don't know how to handle element: "+thistag.nodeName);
       }
 }
 
/* returns the sizes of rollup boxes, annotating the doc tree */
function computeRollupBoxes(e) {
	if (e.nodeName == "tag") {
		var x = tagHTML(e);
		var workarea = $('testingarea');//Watch foot.jsp
		workarea.appendChild(x);
		//remove it someday...
		e.setAttribute("width",x.offsetWidth);
		e.setAttribute("height",x.offsetHeight);
		return;
	}else if (e.nodeName == "bundle") {
		var x = tagHTML(e.firstChild);  // must accommodate the rollup tag
		document.getElementById("testingarea").appendChild(x);
		var wid = x.offsetWidth;
		var hei = x.offsetHeight;
      	var expandedHgt = 0;
      	var expandedWid = 0;
      	for (var row = e.firstChild.nextSibling; row != null;  row = row.nextSibling) {
         if (row.nodeName != "row") alert("in row with tag " + row.nodeName);
         var rowWidth = 0;
         var rowHeight = 0;
         for (var item = row.firstChild; item != null; item = item.nextSibling) {
         	if (item.nodeName != "bundle" && item.nodeName != "tag") alert("see wrong item "+ item.nodeName);
            computeRollupBoxes(item);
            if (! item.hasAttribute("width")) alert("no width!!!");
            var childWidth = parseInt(item.getAttribute("width"));
            var childHeight = parseInt(item.getAttribute("height"));
            rowWidth += (2 + childWidth); // 2 is (I think) the spacing used by CSS?? How to read?
            if (childHeight > rowHeight) rowHeight = childHeight;
         }
         rowWidth -= 2;  // see above, compensate for n items having n-1 gaps
         if (rowWidth > expandedWid) expandedWid = rowWidth;
         expandedHgt += rowHeight; //? assume no vertical separation?
      }
      if (expandedHgt > hei) hei = expandedHgt;
      if (expandedWid > wid) wid = expandedWid;
      e.setAttribute("width",wid);
      e.setAttribute("height",hei);
      var wpad = expandedWid - x.offsetWidth;
      var hpad = expandedHgt - x.offsetHeight;
      e.setAttribute("wpad", wpad); // -ve if tag bigger than its kids
      e.setAttribute("hpad", hpad); // ditto
   } else {
   		alert("unknown nodename " + e.nodeName + " in computeRollupBoxes");
     	return;
   }
   return;
}


/**
* this is used for span elements containing our tags
*/
function addEventsToElement(ele) {
	var url =this.url;
	var tagcloudele=this.tagcloudele;
	var datasource=this.datasource;
	var tagcloudarea=this.tagcloudarea;
	var operation = this.operation;
	var dimensions = this.dimensions;
	var tagdims= this.tagdims;
	var simdims=this.simdims;
	
    ele.style.cursor="pointer";
    if (ele.addEventListener) {
      ele.addEventListener('click',clickedTag,false);
      ele.addEventListener('mouseover',mouseoverTag,false);
      ele.addEventListener('mouseout',mouseoutTag,false);
    } else if (ele.attachEvent) {
      //alert("You are using IE. I will register the events, but \n be warned that they fail.");
      ele.attachEvent('onclick',clickedTag);
      ele.attachEvent('onmouseover',mouseoverTag);
      ele.attachEvent('onmouseout',mouseoutTag);
    }
    
	function clickedTag(event) {
		//alert("clickedTag"+url);
		E = getEvent(event);
		var src =  e.srcElement || e.target;
		var action="s1"; /* default left click */
		/* both IE and Netscape agree that button code 2 is a right click
		although they disagree on the codes for left and centre */
		/* if (e.button == 2) action="s2"; my browser grabs this first!*/
		if (e.ctrlKey) action="s2";  // ctrl left click
		var newdivtagcloudarea = document.createElement("div");
		//var divcloudelement = document.createElement("div");
		//divcloudelement.className = "tagcloud";
		var clickedText = "Error--clicked element does not have tag";
		if (src.nodeName=="span" || src.nodeName=="SPAN") 
			clickedText = src.firstChild.data; /* ummm...don't put any right parentheses in tags...*/
			else if (src.nodeName=="IMG" || src.nodeName =="img") 
				clickedText = src.getAttribute("alt");
			else alert("you clicked on " + src.nodeName);
		
		clickedText = myencode(clickedText);
	    tagcloudarea.parentNode.insertBefore(newdivtagcloudarea,tagcloudarea.nextSibling);
	    /*
	    * we kill all following tag clouds
	    */
	    el = newdivtagcloudarea.nextSibling;
	    while(el) {
	      	var eln = el.nextSibling;
	      	el.parentNode.removeChild(el);
	      	el = eln;
	    }
	    // they should be gone now
		//
		// next, we add a little arrow for cuteness
		var parr = document.createElement("p");
		parr.className = "cloudarrow";
		var arrow = document.createElement("img");
		arrow.setAttribute("src","figures/go-jump.png");
		arrow.setAttribute("alt","go jump");
		//parr.appendChild(document.createTextNode("?"));
		parr.appendChild(arrow);
		//parr.appendChild(document.createTextNode("<img src='figures/go-jump.png' />");
		tagcloudarea.parentNode.insertBefore(parr,tagcloudarea.nextSibling);
		// we temporarily enter some text in case the network is slow
		newdivtagcloudarea.appendChild(document.createTextNode("loading tag cloud... please wait."));
		// we go load the stuff
		//operation+action+"("+clickedText+")"
		//alert(operation.toJSON());
		loadCloud(datasource,operation,newdivtagcloudarea);
	};
	    
}

/**
* this is a hack to be compatible with IE
*/
function getEvent(e){
  if(window.event != null) {
    return event;
  }
  return e;
}


/**
* when the user goes over a tag, it lights up
*/
function mouseoverTag(event) {
	e = getEvent(event);
	var src =  e.srcElement || e.target;
	if(src.mybackground == "yellow") return;
	src.mybackground = src.style.backgroundColor;
	src.style.backgroundColor="yellow";
}

/**
* when a user leaves the tag, we need to
* unlight it
*/
function mouseoutTag(event) {
   e = getEvent(event);
   var src =  e.srcElement || e.target;
  //if(! this.style) alert("no style to this?");
  //alert("mouse out, setting tag to "+this.mybackground);
  src.style.backgroundColor= src.mybackground;
}


function getCloudElement(cloudName, operationName) {
  var children = document.getElementsByTagName("div");
  for (var i = 0; i < children.length ;++i) {
    var child = children[i];
    var cloud = child.getAttribute("cloud");
    var operation = child.getAttribute("operation");
    if((cloud == cloudName ) && (operation == operationName))
      return child;
  }
  alert(cloudName+ " "+operationName+" not found on the page");
  return null;
}

/**
* because we use duck typing, sometimes you may want
* to pass the id of the element, sometimes you may
* want to pass the element itself. This function
* makes sure than in either case, you get back
* the element. May not be needed anymore.
*/
function convertToElementWhenNeeded(tagcloudid) {
	var container = tagcloudid;
  	if(! container.getElementsByTagName) container = document.getElementById(tagcloudid);
  	if( ! container)  alert("container is null!");
  	if( ! container.getElementsByTagName)  alert("could not retrieve element!");
  	return container;
}

function myencode(str) {
 	return encodeURIComponent(str); //encodeURIComponent
}

function paddingPicture(h,w) {
  var imgele = document.createElement("img");      
      imgele.setAttribute("src","http://pizza.unbsj.ca/~owen/blank.gif");  // transparent
      // imgele.setAttribute("src","http://pizza.unbsj.ca/~owen/blank.jpg");  // opaque, for debug
      imgele.setAttribute("alt","not kamel");
      imgele.setAttribute("height", ""+h);  // to be dynamic; match omitted tbl's hgt
      imgele.setAttribute("width", ""+w);
      return imgele;
}

function loadCloudFromOp(datasource,tagcloudarea){
	var opid = allOperations[0].id;
	var mytagcloud;
	var op;
	switch (opid) {
		case 'pr':
			op = new Project(allOperations[0].dim, allOperations[0].tag, allOperations[0].sim);
			mytagcloud = new TagCloudByProject(datasource, op, tagcloudarea);
			break;
		case 'ru':
			op = new Rollup(allOperations[0].dim, allOperations[0].tag, allOperations[0].sim, allOperations[0].rud);
			mytagcloud = new TagCloudByRollup(datasource, op, tagcloudarea);
			break;
		case 'sl':
			op = new Slice(allOperations[0].dim, allOperations[0].tag, allOperations[0].sim,allOperations[0].sld,allOperations[0].slv);
			mytagcloud = new TagCloudBySlice(datasource, op, tagcloudarea);
			break;
		case 'di':
			op = new Dice(allOperations[0].dim, allOperations[0].tag, allOperations[0].sim,allOperations[0].did,allOperations[0].div);
			mytagcloud = new TagCloudByDice(datasource, op, tagcloudarea);
			break;
		default: 
			alert('not implemented');
			return;
	}
	var status=document.createElement("div");
	status.innerHTML='Loading tag cloud... please wait. <img src="figures/loading.gif" height="20" width="20" alt="Loading" />';
   	tagcloudarea.appendChild(status);
		
	//if(document.getElementById('status')) $('status').innerHTML='Loading tag cloud... please wait. <img src="figures/loading.gif" height="20" width="20" alt="Loading" />';
	//cleanElement(tagcloudarea);
	if(document.getElementById('error')) Element.hide('error');
	var beforedate = new Date();
	mytagcloud.getData();//getting data (of the tag cloud) by AJAX query
	succesfunc = function(transport) {
		if (mytagcloud.format=="json") {
			mytagcloud.data = eval('(' + transport.responseText + ')')['cloud'];
		}else if(mytagcloud.format=="xml"){
			mytagcloud.data = transport.responseXML;
		}else{
			alert('tag cloud format is not supported');
		}
		//if(document.getElementById('status')) $('status').innerHTML='';
		status.innerHTML='';;
		mytagcloud.addMenuBar();
		mytagcloud.addTags();	
		var afterdate = new Date();
	    	var timeelapsed = (afterdate.getTime()-beforedate.getTime());
	   	var msg = "time elapsed: "+timeelapsed+" ms";
	   
        if(op.getParameter("dim"))
         	msg+=", aggregating over {"+arrayToString(op.getParameter("dim"))+"}";
       	if(op.getParameter("tag"))
          	msg+=", showing {"+arrayToString(op.getParameter("tag"))+"}";
        if(op.getParameter("sim"))
        	msg+=", clustering tags by {"+arrayToString(op.getParameter("sim"))+"}";
	    mytagcloud.addStatusBar(msg);
	}
}