/*-----------------Some useful functions----------------------------------------*/
function multipleSwitch(inlist,outlist) {
   var inlistLength = inlist.length;
   var arrSelected = new Array();
   var count = 0;
   for (var i = 0; i < inlistLength; i++) {
     if (inlist.options[i].selected) {
       arrSelected[count] = inlist.options[i].value;
       addOption(outlist,inlist.options[i].value,inlist.options[i].value);
     }
     count++;
   }
   for (var i = 0; i < inlistLength; i++) {
     for (var k = 0; k < arrSelected.length; k++) {
       if (inlist.options[i].value == arrSelected[k]) {
         	Element.remove(inlist.options[i]);
       }
     }
     inlistLength = inlist.length;
   }
   //enableDisableButton();
}

function multipleAdd(inlist,outlist) {
   var inlistLength = inlist.length;
   for (var i = 0; i < inlistLength; i++) {
     if (inlist.options[i].selected) {
     	if (isOptionExist(outlist,inlist.options[i].value)==false)
       		addOption(outlist,inlist.options[i].value,inlist.options[i].value);
     }
   }
}

function getSelectedDim(inlist) {
   var inlistLength = inlist.length;
   var selectedDim = new Array();
   var count=0;
   for (var i = 0; i < inlistLength; i++) {
     if (inlist.options[i].selected) {
     	selectedDim[count] = inlist.options[i].value;
     	count++;
     }
   }
   if (count == 0) return null;
   return selectedDim;
}

/**
*Check if at least one option is selected
**/
function isOptionSelected(inlist) {
   var inlistLength = inlist.length;
   for (var i = 0; i < inlistLength; i++) {
     if (inlist.options[i].selected) {
     	return true;
     }
   }
   return false;
}


function copyList(inlist,outlist) {
   var inlistLength = inlist.length;
   for (var i = 0; i < inlistLength; i++) {
       	addOption(outlist,inlist.options[i].value,inlist.options[i].value);
   }
}

function uniqueCopyList(inlist,outlist) {
   var inlistLength = inlist.length;
   for (var i = 0; i < inlistLength; i++) {
   		if (isOptionExist(outlist,inlist.options[i].value) ==false)
       		addOption(outlist,inlist.options[i].value,inlist.options[i].value);
   }
}

function multipleRemoveFromList(inlist,outlist) {
   var inlistLength = inlist.length;
   for (var i = 0; i < inlistLength; i++) {
     if (inlist.options[i].selected) {
     	if (isOptionExist(outlist,inlist.options[i].value)==true)
       		removeOption(outlist,inlist.options[i].value);
     }
   }
}

function multipleRemove(inlist) {
   var inlistLength = inlist.length;
   var arrSelected = new Array();
   var count = 0;
   for (var i = 0; i < inlistLength; i++) {
     if (inlist.options[i].selected) {
       		arrSelected[count] = inlist.options[i].value;
       		count++;
     }
   }
   for (var i = 0; i < inlistLength; i++) {
     for (var k = 0; k < arrSelected.length; k++) {
       if (inlist.options[i].value == arrSelected[k]) {
         	Element.remove(inlist.options[i]);
       }
     }
     inlistLength = inlist.length;
   }
}


function isOptionExist(list,value){
	for(var k=0; k< list.length; k++)
		if(list.options[k].value ==value)
			return true;
	return false; 
}

function toswitch(inlist,outlist){
	if (inlist.selectedIndex >=0){
		addOption(outlist,inlist.options[inlist.selectedIndex].value,inlist.options[inlist.selectedIndex].value);
	    Element.remove(inlist.options[inlist.selectedIndex]);
	 }
}

function addOption(list,value,text){
	try{
		var option = new Option(text,value);
		Try.these(
			function() {list.add(option, null);}, // FF
			function() {list.add(option, -1);}    // IE
		);
	} catch(err){
		showError(err);
	}
}

function removeOption(list,value){
	try{
		var listLength=list.length;
		if(listLength==0) return false;
		for (var i = 0; i < listLength; i++) {
	       if (list.options[i].value == value) {
	         	Element.remove(list.options[i]);
	       }
	       listLength=list.length;
	    }
	}  catch(err){
		showError(err);
	}
}

function clearList(list){
	try {
		var listLength=list.length;
		if (listLength==0) return;
		while (listLength >0){
			Element.remove(list.options[listLength-1]);
			listLength=list.length;
		}
	} catch(err){
		showError(err);
	}
}

/**
*Generating a queery sting from list elements
**/
function listToQueryString(list,parameter){
	if (list){
		if(list.length){
			var queryString='';
			for (var i = 0; i < list.length; i++) {
				if (list.options[i].value !='')
					queryString +='&'+parameter+'='+list.options[i].value;
			}
			return queryString;
		}
	}
	return null;
}

/**
*Generating a query string from array elements
**/
function arrayToQueryString(array,parameter){
	if (array){
		if(array.length){
			var queryString='';
			for (var i = 0; i < array.length; i++) {
				if (array[i] !='')
					queryString +='&'+parameter+'='+array[i];
			}
			return queryString;
		}
	}
	return null;
}

function arrayToString(array){
	if (array){
		if(array.length){
			var myString=array[0];
			for (var i = 1; i < array.length; i++) {
				myString +=","+array[i];
			}
			return myString;
		}
	}
	return null;
}

function listToArray(list){
	if (list){
		if (list.length){
			var out= new Array();
			for (var i = 0; i < list.length; i++) {
				out[i]=list.options[i].value;
			}
			return out;
		}
	}
	return null;
}

function listToArrayWhenNotNull(list){
	if (list){
		if (list.length){
			var out= new Array();
			for (var i = 0; i < list.length; i++) {
				if(list.options[i].value !='')
					out[i]=list.options[i].value;
			}
			return out;
		}
	}
	return null;
}

function selectedOptionsToArray(list){
	if (list){
		if (list.length){
			var out= new Array();
			var count=0;
			for (var i = 0; i < list.length; i++) {
				if (list.options[i].selected){
					out[count]=list.options[i].value;
					++count;
				}
			}
			return out;
		}
	}
	return null;
}

function arrayToList(array,list){
	if (array){
		if (array.length){
			var list;
			for (var i = 0; i < array.length; i++) {
				addOption(list,array[i],array[i]);
			}
		}
	}
}

function getRandomID(){
	var now = new Date();
	return Math.random(now.getTime());
}


/**
*
*  UTF-8 data encode / decode
*  http://www.webtoolkit.info/
*
**/

var Utf8 = {
	// public method for url encoding
	encode : function (string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";

		for (var n = 0; n < string.length; n++) {

			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	},

	// public method for url decoding
	decode : function (utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while ( i < utftext.length ) {

			c = utftext.charCodeAt(i);

			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}

		}

		return string;
	}
}