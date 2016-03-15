/**
* This JavaScript file is the companion to 
* upload.js
*/

Event.observe(window, 'load', init, false);

function init(){
     //Observe events (submit) on formUpload
     Event.observe('formUpload', 'submit', startStatusCheck, false);
     //Observe events (keyup) on sourceID and attDelimiter
     Event.observe('sourceID', 'keyup', checkID, false);
     Event.observe('attDelimiter', 'keyup', checkDelimiter, false);
     //Obseve events : click on radio buttons
     Event.observe('fileFormatXML', 'click', checkFormat, false);
     Event.observe('fileFormatFlat', 'click', checkFormat, false);
     //Observe events : change on dataFile and dtdFile
     Event.observe('dataFile', 'change', checkDataFile, false);
     //Submit button is disabled
      $('submitButton').disabled = true;
}

var updater = null;
var isSourceIDOk=false;
var isDataFileOk=false;
var isDelimiterOk=true;

function startStatusCheck(){
    $('submitButton').disabled = true;
    updater = new Ajax.PeriodicalUpdater(
                                'status',
                                'up',
                                {asynchronous:true, frequency:2, method: 'get', parameters: 'c=status', onFailure: reportError});
    return true;
}

function reportError(request){
    $('submitButton').disabled = false;
    $('status').innerHTML = '<div class="error">Error communicating with server. Please try again.</div>';
}

function killUpdate(errmessage,stats){
    $('submitButton').disabled = true;
    updater.stop();
    if(errmessage != ''){
      $('status').innerHTML = "<div class=\"error\"><p>Error processing results: </p>" + errmessage + "</div>";
    } else {
      $('status').innerHTML = "<p>Loading completed: </p>" + stats + "</div>";
    }
    $('sourceID').value="";
    $('checkID').innerHTML = "";
}

function checkID(){
	if ($F('sourceID').match(/^\w+$/g)) {
		var url='up';
		var pars= 'f=status'+'&id='+ $F('sourceID');
		var myAjax = new Ajax.Request(
					url, 
					{method: 'get', parameters: pars, onComplete: processCheckID,onFailure: reportErrorCheck}
					);
	}else{
		$('checkID').innerHTML = "<span class=\"error\">Your ID is null or non-alphanumeric. Please insert a not null alphanumeric ID.</span>";
		isSourceIDOk=false;
		checkForm();
	}
}

function processCheckID(response){
	if (response.responseText==1){
		$('checkID').innerHTML = "<span class=\"ok\">ID "+$F('sourceID')+" is available.</span>";
		isSourceIDOk=true;
	}else 
		if (response.responseText==0){
			$('checkID').innerHTML = "<span class=\"error\">ID "+ $F('sourceID') +" already exists. Try another ID.</span>";
			isSourceIDOk=false;
		}else{
			$('checkID').innerHTML = "<span class=\"error\">"+response.responseText+"<span>";
			isSourceIDOk=false;
		}
	checkForm();
}

function checkDataFile(){
	if  ($F('dataFile') !=''){
		isDataFileOk=true;
	}else{
		isDataFileOk=false;
	}
	checkForm();
}

function checkDelimiter(){
	if  ($F('attDelimiter') !=''){
		isDelimiterOk=true;
	}else{
		isDelimiterOk=false;
	}
	checkForm();
}

function checkFormat(){
	if ($F('fileFormatFlat')=='flat'){
		$('attDelimiter').disabled=false;
		Element.show("attDelimiter");
      	Element.show("labelatt");
	}
	$('submitButton').disabled=true;
	if ($F('fileFormatXML')=='xml'){
		$('attDelimiter').disabled=true;
		Field.clear('attDelimiter');
		Element.hide("attDelimiter");
      	Element.hide("labelatt");
		checkForm();
	}
}

function reportErrorCheck(request){
    $('checkID').innerHTML = '<span class="error">Error communicating with server. Please try again.</span>';
}

function checkForm(){
	$('submitButton').disabled=true;//must do this for Safari browser
	if ($F('fileFormatXML')=='xml'){
		$('submitButton').disabled = ! (isSourceIDOk && isDataFileOk);
	}
	if ($F('fileFormatFlat')=='flat'){
		$('submitButton').disabled = ! (isSourceIDOk && isDelimiterOk && isDataFileOk);
	}
}
