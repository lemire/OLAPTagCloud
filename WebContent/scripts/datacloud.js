/**
* Tasks: retrieving  stored cubes, their dimensions and easily navigating over them
*/
/*-------------------------GLOBALS-------------------------*/
var currentRecord = 0;
var pagingSize 	= 5;
var recordCount;
var previousPage;
var currentPage;
var nextPage;
var records;
var nbDim=0;
var selectedDimensions;
var beforedate;
var afterdate;
var opid;//id of each operation @see tegcloud.js
var allOperations;
var offset =0;
/*---------------------------------------------------------------*/

Event.observe(window, 'load', init, false);

function init(){
	if(document.getElementById('cubeID')){
		Event.observe('cubeID', 'change', getAttributes, false);
		Event.observe('nextLink', 'click', getNextPage, false);
		Event.observe('previousLink', 'click', getPreviousPage, false);
		Event.observe('plus', 'click', showOrHide, false);
		$('tagCloud', 'navigation', 'fyourTagCloud','operation','dimensions','error').invoke('hide');
		$('selectedID').setStyle({color:'red'});
		Event.observe('tagCloud', 'click', displayTagCloud, false);
		getCubes();
		
		Event.observe('addDim', 'click', addDim, false);
		Event.observe('remDim', 'click', remDim, false);
		Event.observe('addSim', 'click', addSim, false);
		Event.observe('remSim', 'click', remSim, false);
		Event.observe('addRollupDim', 'click', addDimToRollup, false);
		Event.observe('removeRollupDim', 'click', removeDimFromRollup, false);
		Event.observe('getValuesToDice', 'click', getValuesToDice, false);
		Element.hide("des");//description
		Element.hide("tagdimtip");
		Event.observe($('projecttip'), 'mouseover', function(event) {
			$('rollup', 'dice', 'slice').invoke('hide');
			opid='pr';
			TagToTip('tagdimtip',COPYCONTENT,false,  ABOVE,true, OFFSETY,offset, CLOSEBTN, true, TITLE, 'Project, Tag-support and similarity dimensions', STICKY, true);
		});
		
		Event.observe($('slicetip'), 'mouseover', function(event) {
			Element.show("slice");
			$('rollup', 'dice').invoke('hide');
			opid='sl';
			TagToTip('tagdimtip',COPYCONTENT,false ,  ABOVE,true, OFFSETY,offset, CLOSEBTN, true, TITLE, 'Slice, Tag-support and similarity dimensions', STICKY, true);
		});
		
		Event.observe($('dicetip'), 'mouseover', function(event) {
			Element.show("dice");
			$('rollup', 'slice').invoke('hide');
			opid='di';
			TagToTip('tagdimtip',COPYCONTENT,false,  ABOVE,true, OFFSETY,offset, CLOSEBTN, true, TITLE, 'Dice, Tag-support and similarity dimensions', STICKY, true);
		});
		
		Event.observe($('rolluptip'), 'mouseover', function(event) {
			Element.show("rollup");
			$('dice', 'slice').invoke('hide');
			opid='ru';
			TagToTip('tagdimtip',COPYCONTENT,false,  ABOVE,true, OFFSETY,offset, CLOSEBTN, true, TITLE, 'Rollup, Tag-support and similarity dimensions', STICKY, true);
		});
		
		Event.observe($('dimtoslice'), 'change', function(event) {
			clearList($('dimtoslicevalues'));
			if($('dimtoslice').options[$('dimtoslice').selectedIndex].value ==''){
				$('tagCloud').disabled=true;
				return;
			}
			var url='cs';
			var currentdim=$('dimtoslice').options[$('dimtoslice').selectedIndex].value;
			var currentcube=$('cubeID').options[$('cubeID').selectedIndex].value;
			var dim= arrayToQueryString(selectedDimensions.keys(),'dim'); 
			var pars= 'q=v&id='+currentcube+'&cdim='+currentdim+dim;
			displayAttributeValues = function(response) {
				var results = eval('(' + response.responseText + ')')['cloud'];
				var tags = results['tag'];
				if(tags.length > 0) {
					for(var i = 0; i < tags.length; ++i) {
						var option = new Option(tags[i]['text']+' ('+tags[i]['trueweight']+')',tags[i]['text']);
	        			Try.these(
	            			function() {$('dimtoslicevalues').add(option, null);}, // FF
	            			function() {$('dimtoslicevalues').add(option, -1);}    // IE
	        			);
					}
				}else{
					var option = new Option(tags['text']+' ('+tags['trueweight']+')',tags['text']);
			        Try.these(
			            function() {$('valuestodice').add(option, null);}, // FF
			            function() {$('valuestodice').add(option, -1);}    // IE
			        );	
				}
			}
			
			var myAjax = new Ajax.Request(
				url, 
				{method: 'get', parameters: pars, onComplete: displayAttributeValues, onFailure: reportError}
			);
		});
	}
}
 /**
 *retrieves all the stored data sets
 */  
function getCubes(){
	Element.hide("error");
	var url='md';
	var pars= 'q=c';
	var myAjax = new Ajax.Request(
		url, 
		{method: 'get', parameters: pars, onComplete: displayCubes, onFailure: reportError}
	);
}

/**
*Displaying the retrieved data sets
*/
function displayCubes(response){
	var results = eval('(' + response.responseText + ')');
	if (results){
		var rows  = results['results']['row'];
		var error  = results['results']['error'];
		if (error){
			Element.show("error");
			$('error').innerHTML = error;
			return;
		}
		if( rows) {
		  if (rows.length)  {
			for (var i=0 ; i < rows.length ; i++){
				var option = new Option(rows[i].id,rows[i].id);
				if (rows[i].description !="null") option.title=rows[i].description;
				else option.title="no description is availible";
	        	Try.these(
	            	function() {$('cubeID').add(option, null);}, // FF
	            	function() {$('cubeID').add(option, -1);}    // IE
	        	);
	        }
			return;
		  }else{
		  // Daniel is not sure what this next bit does:
		  // Kamel If we have one data cube the variable rows is not an array 
		  //(because of the returned JSON)
	      	var option = new Option(rows.id,rows.id);
	      	Try.these(
	      		function() {$('cubeID').add(option, null);}, // FF
	      		function() {$('cubeID').add(option, -1);}    // IE
	      	);
	      	return;
	      }
		}
		var sb = $("statusbar");
        cleanElement(sb);
        sb.appendChild(document.createTextNode("no data, upload some "));
	}
}

/**
* retrieves the attributes of a given data set
*/
function getAttributes(){
	$('view').innerHTML = '';
	$('next').innerHTML = '';
	$('previous').innerHTML = '';
	$('currentRec').innerHTML = '';
	$('selectedID').innerHTML = '';
	$('fyourTagCloud', 'tagCloud', 'error','operation','dimensions','currentRec').invoke('hide');
	$('status').innerHTML='Loading dimensions... please wait. <img src="figures/loading.gif" height="20" width="20" alt="" />';
	var url='md';
	var id=$('cubeID').options[$('cubeID').selectedIndex].value;
	if (id != '' && id !='null'){
		var pars= 'q=a&id='+id;
		var myAjax = new Ajax.Request(
					url, 
					{method: 'get', parameters: pars, onComplete: displayAttributes, onFailure: reportError}
					);
	}else{
		Element.hide("des");
		$('status').innerHTML='';
	}
}

/**
*Displaying those attributes
*/
function displayAttributes(response){
	if ($('cubeID').options[$('cubeID').selectedIndex].title){
		Element.show("des");
		$('img_project').setAttribute("src","figures/project_off.png");
		$('img_rollup').setAttribute("src","figures/rollup_off.png");
		$('img_slice').setAttribute("src","figures/slice_off.png");
		$('img_dice').setAttribute("src","figures/dice_off.png");
		$('des').onmouseover = function (e) {
			Tip($('cubeID').options[$('cubeID').selectedIndex].title.replace(/\n/g,"<br \>"),BALLOON,true,ABOVE,true);
		};
	}else{
		Element.hide("des");
	}
	records = response.responseText;
	var results = eval('(' + response.responseText + ')');
	$('status').innerHTML='';
	clearList($('alldims'));
	clearList($('tagdims'));
	clearList($('simdims'));
	selectedDimensions= new Hash();
	previousPage =  new Array();
	currentPage =  new Array();
	nextPage =  new Array();
	currentRecord = 0;
	nbDim=0;
	if (results){
		var rows  = results['results']['row'];
		var error  = results['results']['error'];
		if (error){
			Element.show("error");
			$('error').innerHTML = error;
			return;
		}
		if (rows.length){
			Element.show('navigation');
			$('plus').setAttribute("value","-");
			$('plus').setAttribute("title","Hide details");
			recordCount = rows.length;
			if (recordCount < pagingSize){
				pagingSize=recordCount;
				Element.show("dimensions");
				Element.show("operation");
				getTableData();
				showNavigation();
				Element.hide('nextLink');
				Element.hide('currentRec');
			}else{
				Element.show("dimensions");
				Element.show("operation");
				getTableData();
				getNextData();
				nbDim=currentPage.length;
				showNavigation();
			}
			heightvalue = (pagingSize*20)+'px';
			$('previous').setStyle({height:heightvalue});
			$('next').setStyle({height:heightvalue});
			$('view').setStyle({height:heightvalue});
			$('navigation').setStyle({height:heightvalue});
		}else{
			/**
			* TODO
			* show the single dimension as a checkbox 
			**/
			//recordCount=1;
			
			/*$('selectedID').innerHTML='<input type="checkbox" name="dimensions" value="'+rows.Field+'" id="'+rows.Field+'"'+ checked + ' /> '+rows.Field;
			Event.observe(rows.Field, 'click', function(event) {
				var elt = Event.element(event);
				($(elt).checked == true)
					? $('selectedID').innerHTML += '<br />You have selected one dimension'
					: ;
			});*/
		}
	}
}

/**
* Displaying tag clouds upon request
*/	
function displayTagCloud(){
	//Cleaning up if there was tag clouds displayed before
	cleanElement($("testingarea"));
    if ($('fyourTagCloud').getElementsByClassName('tagcloud')!=""){
    	var array= $('fyourTagCloud').getElementsByClassName("tagcloud");
    	for (i=0; i< array.length; i++){
    		Element.remove(array[i].parentNode);
    	}
    }
    
    if ($('fyourTagCloud').getElementsByClassName('cloudarrow')!=""){
    	var array= $('fyourTagCloud').getElementsByClassName("cloudarrow");
    	for (i=0; i< array.length; i++){
    		Element.remove(array[i]);
    	}
    }
    
    var tagcloudarea=document.createElement("div");
    $('fyourTagCloud').appendChild(tagcloudarea);
    Element.hide('fyourTagCloud');
	$('tagCloud').disabled = true;
	
	var op;
	var tagdims = listToArray($('tagdims'));
	var simdims = listToArray($('simdims'));
	var dimensions = selectedDimensions.keys();
	var sourceid = $('cubeID').options[$('cubeID').selectedIndex].value;
	allOperations = new Array();
	switch (opid) {
		case 'pr':
			op = new Project(dimensions, tagdims, simdims);
			allOperations.push(op);
			if(document.getElementById('img_project')) {
				$('img_project').setAttribute("src","figures/project_on.png");
				$('img_rollup').setAttribute("src","figures/rollup_off.png");
				$('img_slice').setAttribute("src","figures/slice_off.png");
				$('img_dice').setAttribute("src","figures/dice_off.png");
			}
			break;
		case 'ru':
			op = new Rollup(dimensions, tagdims, simdims, listToArray($('dimtorollup')));
			allOperations.push(op);
			$('img_project').setAttribute("src","figures/project_off.png");
			$('img_rollup').setAttribute("src","figures/rollup_on.png");
			$('img_slice').setAttribute("src","figures/slice_off.png");
			$('img_dice').setAttribute("src","figures/dice_off.png");
			break;
		case 'sl':
			op = new Slice(dimensions, tagdims, simdims, $('dimtoslice').options[$('dimtoslice').selectedIndex].value, selectedOptionsToArray($('dimtoslicevalues')));
			allOperations.push(op);
			$('img_project').setAttribute("src","figures/project_off.png");
			$('img_rollup').setAttribute("src","figures/rollup_off.png");
			$('img_slice').setAttribute("src","figures/slice_on.png");
			$('img_dice').setAttribute("src","figures/dice_off.png");
			break;
		case 'di':
			op = new Dice(dimensions, tagdims, simdims, listToArrayWhenNotNull($('currentdimtodice')), selectedOptionsToArray($('valuestodice')));
			allOperations.push(op);
			$('img_project').setAttribute("src","figures/project_off.png");
			$('img_rollup').setAttribute("src","figures/rollup_off.png");
			$('img_slice').setAttribute("src","figures/slice_off.png");
			$('img_dice').setAttribute("src","figures/dice_on.png");
			break;
		default: 
			alert('not implemented');
			if(document.getElementById('status')) $('status').innerHTML='';
			if(document.getElementById('fyourTagCloud'))  Element.hide("fyourTagCloud");
	    	if(document.getElementById('tagCloud')) $('tagCloud').disabled = false;
			return;
	}
	
	/**
	*@see tagcloud.js
	*/
	loadCloud(sourceid, op, tagcloudarea);
}

function reportError(response){
    $('status').innerHTML='';
    var results = eval('(' + response.responseText + ')');
	var error  = results['results']['error'];
	if (error){
		Element.show("error");
		$("error").innerHTML = error;
	}else{
		Element.show("error");
		$("error").innerHTML = 'Error communicating with the server.';
	}
}

/*---------------------------Paging--------------------------------*/

function getTableData() {
	var tmp = eval('(' + records + ')');
	var data = tmp['results']['row'];
	for (var i=0; i< pagingSize;i++){
		currentPage[i]=data[i+currentRecord].Field;
	}
	drawTable(currentPage, $('view'));
}

function getNextData() {
	if (currentRecord == 0) currentRecord = pagingSize;
	nextPage = new Array();
	var tmp = eval('(' + records + ')');
	var data = tmp['results']['row'];
	if ((recordCount - currentRecord) < pagingSize){
		for (var i=0; i< (recordCount - currentRecord);i++){
			nextPage[i]=data[i+currentRecord].Field;
		}
	}else{
		for (var i=0; i< pagingSize;i++){
			nextPage[i]=data[i+currentRecord].Field;
		}
	}
	drawTable(nextPage, $('next'));
}

function getPreviousData() {
	previousPage = new Array();
	if((currentRecord - pagingSize) >= pagingSize) {
		$('previous').innerHTML = '';
		var tmp = eval('(' + records + ')');
		var data = tmp['results']['row'];
		for (var i=0; i< pagingSize;i++){
			previousPage[i]=data[i+currentRecord-2*pagingSize].Field;
		}
		drawTable(previousPage,$('previous'));
	}else {
		$('previous').innerHTML = '';
	}
}

function drawTable(data, contain) {
	table='';
	alt = 'alt2';
	for(i = 0; i < data.length; i++) {
		(selectedDimensions[data[i]])
			? checked='checked'
			: checked='';
		
		
		table +=		'<div class="'+alt+'">' +
						'<input type="checkbox" name="dimensions" value="'+data[i]+'" id="'+data[i]+'"'+ checked + ' /> '+data[i] +
						'</div>';
						
		(alt == 'alt1')
			?	alt = 'alt2'
			:	alt = 'alt1';
	}
	contain.innerHTML = table;
	for(i = 0; i < data.length; i++){
		Event.observe(data[i], 'click', function(event) {
			var elt = Event.element(event);
			if ($(elt).checked == true){
				selectedDimensions[$(elt).readAttribute('value')]=true;
				if ($('tagdims').length == 0 ) {
					addOption($('tagdims'),$(elt).readAttribute('value'),$(elt).readAttribute('value'));
				}else{
					addOption($('alldims'),$(elt).readAttribute('value'),$(elt).readAttribute('value'));
				}
			}else{
				selectedDimensions.remove($(elt).readAttribute('value'));
				removeOption($('alldims'),$(elt).readAttribute('value'));
				removeOption($('tagdims'),$(elt).readAttribute('value'));
				removeOption($('simdims'),$(elt).readAttribute('value'));
				if( $(elt).readAttribute('value') == $('dimtoslice').options[$('dimtoslice').selectedIndex].value){
					clearList($('dimtoslicevalues'));
				}
				if (isOptionExist($('currentdimtodice'),$(elt).readAttribute('value')) ==true){
					removeOption($('currentdimtodice'),$(elt).readAttribute('value'));
					clearList($('valuestodice'));
				}
					
				removeOption($('dimtoslice'),$(elt).readAttribute('value'));
				if ($('tagdims').length == 0 && $('alldims').length !=0) {
					addOption($('tagdims'),$('alldims').options[0].value,$('alldims').options[0].value);
					clearList($('dimtoslicevalues'));
					clearList($('valuestodice'));
					removeOption($('dimtoslice'),$('alldims').options[0].value);
					removeOption($('alldims'),$('alldims').options[0].value);
				}
			}
			if (selectedDimensions.keys().length == 0){
				$('selectedID').innerHTML=''; 
				//Element.hide("projecttip");
				Element.hide("tagCloud");
			}else{
				if ($('tagdims').length ==0){
					$('selectedID').innerHTML = 'You have selected '+ selectedDimensions.keys().length + ' dimension(s)';
					$('selectedID').innerHTML +='<br /> You have to select at list one tag-support dimension';
					Element.hide("tagCloud");
				}else{
					$('selectedID').innerHTML = 'You have selected '+ selectedDimensions.keys().length + ' dimension(s)';
					if ($('tagdims').length ==1) $('selectedID').innerHTML +='<br /> Default tag-support dimension is '+$('tagdims').options[0].value;
					Element.show("projecttip");
					Element.show("tagCloud");	
				}
			}
			uniqueCopyList($('tagdims'),$('dimtoslice'));
			enableDisableButton();
		});
	}
}

function getNextPage() {
	if((currentRecord - pagingSize) < 0) currentRecord += (currentRecord - pagingSize);
	else currentRecord += pagingSize;
	previousPage = currentPage;
	currentPage = nextPage;
	drawTable(currentPage, $('view'));
	drawTable(previousPage, $('previous'));
	getNextData();
	nbDim +=currentPage.length; 
	showNavigation();
}

function getPreviousPage() {
		currentRecord -= pagingSize;
		nextPage = currentPage;
		currentPage = previousPage;
		drawTable(currentPage, $('view'));
		drawTable(nextPage, $('next'));
		getPreviousData();
		nbDim -=nextPage.length; 
		showNavigation();
}

function showNavigation() {
	$('currentRec').innerHTML = " Dimensions " + (nbDim)  + " / " + recordCount; 
	(currentRecord == pagingSize || currentRecord == 0)
		?	Element.hide('previousLink')
		:	Element.show('previousLink');
	((currentRecord) >= recordCount)
		?	Element.hide('nextLink')
		:	Element.show('nextLink');
	Element.show('navigation');
	if ($('mp').alt == '+'){
		$('mp').setAttribute("src","figures/minus.png");
		$('mp').setAttribute("alt","-");
		$('plus').setAttribute("title","Hide details");
	}
}

function showOrHide(){
	if ($('mp').alt == '-'){
		Element.hide('navigation');
		$('mp').setAttribute("src","figures/plus.png");
		$('mp').setAttribute("alt","+");
		$('plus').setAttribute("title","Show details");
	}else{
		Element.show('navigation');
		$('mp').setAttribute("src","figures/minus.png");
		$('mp').setAttribute("alt","-");
		$('plus').setAttribute("title","Hide details");
	}
}

function showError(err){
	message="<p>There was an error on this page.</p>";
	//message+="Error description: " + err.description + "</p>";
	$('error').innerHTML=message;
}

/*---------------------------------------Operations' parameters selection-------------------------------*/
function addDim(){
	if($('alldims').selectedIndex <0) return false;
	multipleSwitch($('alldims'),$('tagdims'));
	uniqueCopyList($('tagdims'),$('dimtoslice'));
	enableDisableButton();
	if ($('tagdims').length>0){
			Element.show("tagCloud");
			Element.hide("fyourTagCloud");
	}
	if ($('tagdims').length>0){
		$('selectedID').innerHTML = 'You have selected '+ selectedDimensions.keys().length + ' dimension(s)';
	}
}

function addSim(){
	if($('alldims').selectedIndex <0) return false;
	multipleSwitch($('alldims'),$('simdims'));
	enableDisableButton();
	if ($('simdims').length>0){
		$('selectedID').innerHTML = 'You have selected '+ selectedDimensions.keys().length + ' dimension(s)';
	}
}

function remDim(){
	if($('tagdims').selectedIndex <0) return false;
	if($('tagdims').options[$('tagdims').selectedIndex].value == $('dimtoslice').options[$('dimtoslice').selectedIndex].value){
		clearList($('dimtoslicevalues'));
	}
	clearList($('valuestodice'));
	multipleRemoveFromList($('tagdims'),$('dimtorollup'));
	multipleRemoveFromList($('tagdims'),$('dimtoslice'));
	multipleRemoveFromList($('tagdims'),$('currentdimtodice'));
	multipleSwitch($('tagdims'),$('alldims'));
	enableDisableButton();
	if ($('tagdims').length==0){
	 	Element.hide("tagCloud");
		Element.hide("fyourTagCloud");
		$('selectedID').innerHTML = 'You have selected '+ selectedDimensions.keys().length + ' dimension(s)';
		$('selectedID').innerHTML +='<br /> You have to select at least one tag-support dimension';
	}else{
		$('selectedID').innerHTML = 'You have selected '+ selectedDimensions.keys().length + ' dimension(s)';
		//$('selectedID').innerHTML +='<br /> You have to select at least one tag-support dimension';
	}
}

function remSim(){
	if($('simdims').selectedIndex <0) return false;
	multipleSwitch($('simdims'),$('alldims'));
	enableDisableButton();
}

function enableDisableButton(){
	if($('alldims').length==0)  {
		$('addDim').disabled=true;
		$('addSim').disabled=true;
	} else{
		$('addDim').disabled=false;
		$('addSim').disabled=false;
	}
	if($('tagdims').length==0)  {
		$('remDim').disabled=true;
	}else{
		$('remDim').disabled=false;
	} 
	if($('simdims').length==0)  {
		$('remSim').disabled=true;
	} else{
		$('remSim').disabled=false;
	}	
}

function addDimToRollup(){
	multipleAdd($('tagdims'),$('dimtorollup'));
	if ($('dimtorollup').length==0) $('tagCloud').disabled=true;
			else $('tagCloud').disabled=false;
}

function removeDimFromRollup(){
	multipleRemove($('dimtorollup'));
	if ($('dimtorollup').length==0) $('tagCloud').disabled=true;
			else $('tagCloud').disabled=false;
}

function getValuesToDice(){
	if(getSelectedDim($('tagdims'))==null){
		//if ($('valuestodice').length==0) $('tagCloud').disabled=true;
		$('tagCloud').disabled=true;
		return;
	}
	clearList($('valuestodice'));
	clearList($('currentdimtodice'));
	addOption($('currentdimtodice'),'',"--- Selected dimensions ---");
	multipleAdd($('tagdims'),$('currentdimtodice'));
	var url='cs';
	var currentdim=arrayToQueryString(getSelectedDim($('tagdims')),'cdim');
	var currentcube=$('cubeID').options[$('cubeID').selectedIndex].value;
	var dim= arrayToQueryString(selectedDimensions.keys(),'dim'); 
	var pars= 'q=v&id='+currentcube+currentdim+dim;
	displayAttributeValues = function(response) {
		var results = eval('(' + response.responseText + ')')['cloud'];
		var tags = results['tag'];
		if(tags.length > 0) {
			for(var i = 0; i < tags.length; ++i) {
				var option = new Option(tags[i]['text']+' ('+tags[i]['trueweight']+')',tags[i]['text']);
	        	Try.these(
	            	function() {$('valuestodice').add(option, null);}, // FF
	            	function() {$('valuestodice').add(option, -1);}    // IE
	        	);
			}
		}else{
			var option = new Option(tags['text']+' ('+tags['trueweight']+')',tags['text']);
	        Try.these(
	            function() {$('valuestodice').add(option, null);}, // FF
	            function() {$('valuestodice').add(option, -1);}    // IE
	        );	
		}
		if ($('valuestodice').length==0) $('tagCloud').disabled=true;
				else $('tagCloud').disabled=false;
	}
	var myAjax = new Ajax.Request(
		url, 
		{method: 'get', parameters: pars, onComplete: displayAttributeValues, onFailure: reportError}
	);
}
