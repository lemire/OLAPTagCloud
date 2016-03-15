/************************
*************************
* Kamel made this file obselete. 
* The new code is in datacloud.js
*************************
*************************/










/***************
* WARNING: MAKE SURE YOU EDIT THIS FILE IN UTF-8 MODE.
* IF YOU ARE NOT SURE, ASK!
* (right click on the file, go to properties)
*****************/
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


/*
function wasAltPressed(evt) {
 if(event.altKey)
  return event.altKey
 return (evt.modifiers & Event.ALT_MASK) ? true : false;
}*/

/**
* what happens if the user clicks on a tag
*/
function clickedTag(event) {
    e = getEvent(event);
    var src =  e.srcElement || e.target;
    var action="s1"; /* default left click */
    /* both IE and Netscape agree that button code 2 is a right click
       although they disagree on the codes for left and centre */
    /* if (e.button == 2) action="s2"; my browser grabs this first!*/
    if (e.ctrlKey) action="s2";  // ctrl left click
    var divcloudelement = document.createElement("div");
    divcloudelement.className = "tagcloud";

    var clickedText = "Error--clicked element does not have tag";
    if (src.nodeName=="span" || src.nodeName=="SPAN") 
       clickedText = src.firstChild.data; /* ummm...don't put any right parentheses in tags...*/
    else if (src.nodeName=="IMG" || src.nodeName =="img") 
       clickedText = src.getAttribute("alt");
    else alert("you clicked on " + src.nodeName);
    clickedText = myencode(clickedText);
    //alert(clickedText);
    /* javascript has regex support, so we could escape parenthesis...*/

    // we recover the parent node and add us as a next sibling
    // to the clicked upon tag cloud
    if(! src.parentNode) alert("no this.parentNode?");
    var currentcloudele = src.parentNode;
    while((currentcloudele.nodeName != "div") && (currentcloudele.nodeName != "DIV"))  {
      currentcloudele = currentcloudele.parentNode;
    }
    currentcloudele.parentNode.insertBefore(divcloudelement,currentcloudele.nextSibling);
    /**
    * we kill all following tag clouds
    */
    el = divcloudelement.nextSibling;
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
    parr.appendChild(document.createTextNode("↓"));
    currentcloudele.parentNode.insertBefore(parr,divcloudelement);
    // we temporarily enter some text in case the network is slow
    divcloudelement.appendChild(document.createTextNode("loading tag cloud... please wait."));
    // we go load the stuff
    loadCloud(currentcloudele.getAttribute("cloud"),
    currentcloudele.getAttribute("operation")+action+"("+clickedText+")",
    divcloudelement);
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
  if(! container.getElementsByTagName)
    container = document.getElementById(tagcloudid);
  if( ! container)  alert("container is null!");
  if( ! container.getElementsByTagName)  alert("could not retrieve element!");
  return container;
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


function tagHTML(e) {
 var spanele = document.createElement("span");
 spanele.appendChild(document.createTextNode(e.getAttribute("text")));
 spanele.className = "tag"+e.getAttribute("weight");
 spanele.setAttribute("title",e.getAttribute("trueweight"));
 return spanele;
}

/* returns the sizes of rollup boxes, annotating the doc tree */
function computeRollupBoxes(e) {
  if (e.nodeName == "tag") {
     var x = tagHTML(e);
     var workarea = document.getElementById("testingarea");
     //if (workarea.firstChild != null) // try for just 1 garbage tag; nothing works to kill junk
     //   workarea.replaceChild(x, workarea.firstChild);
     // else
       workarea.appendChild(x);
     //remove it someday...
     e.setAttribute("width",x.offsetWidth);
     e.setAttribute("height",x.offsetHeight);
     //alert("leaf element is width " + x.offsetWidth+ " height "+x.offsetHeight);
     // workarea.removeChild(x);  // does not work....why???
     // while (workarea.firstChild) workarea.removeChild(workarea.firstChild); // copy DL
     // I get wonky results if I try this cleanup??
     return;
   } else if (e.nodeName == "bundle") {
      var x = tagHTML(e.firstChild);  // must accommodate the rollup tag
      document.getElementById("testingarea").appendChild(x);
      var wid = x.offsetWidth;
      var hei = x.offsetHeight;
      //alert("rollup tag has width "+wid+ " and height " +hei);
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
            // alert("rowWidth now "+ rowWidth);

            if (childHeight > rowHeight) rowHeight = childHeight;
         }
         rowWidth -= 2;  // see above, compensate for n items having n-1 gaps
         if (rowWidth > expandedWid) expandedWid = rowWidth;
         expandedHgt += rowHeight; //? assume no vertical separation?
         // alert("done row, width = "+rowWidth);
      }
      // alert("finished rows");
      if (expandedHgt > hei) hei = expandedHgt;
      if (expandedWid > wid) wid = expandedWid;
      e.setAttribute("width",wid);
      e.setAttribute("height",hei);
      var wpad = expandedWid - x.offsetWidth;
      var hpad = expandedHgt - x.offsetHeight;
      e.setAttribute("wpad", wpad); // -ve if tag bigger than its kids
      e.setAttribute("hpad", hpad); // ditto
      // alert("wpad for hierarchical guy is "+ wpad + " and hpad is " + hpad);
   }
   else {
     alert("unknown nodename " + e.nodeName + " in computeRollupBoxes");
     return;
   }
   return;
}


 /**
 * this function is the part that converts the
 * the XML (from our servlets) to HTML
 */
 function convertToHTML(thistag) {
       if(thistag.nodeName == "tag") {
         var spanele = tagHTML(thistag);
         addEventsToElement(spanele);
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
             // tabele.setAttribute("border","1"); // temp temp
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
             
             /*
             // I need an image that contains the tag.  Unfortuntely, the height and
             // width are not used for displaying  alt.  And we don't control font with alt  
             var r = Math.random();
             if(r>0.66)
               imgele.setAttribute("src","http://sjwebserver.unbsj.ca/~owen/images/kaserhead.jpg");
             else if(r > 0.33)
               imgele.setAttribute("src","http://eric.univ-lyon2.fr/~kaouiche/images/photo1.jpg");
             else
               imgele.setAttribute("src","http://www.daniel-lemire.com/fr/images/JPG/profile2004.jpg");
             imgele.setAttribute("alt",thistag.firstChild.getAttribute("text"));
             imgele.setAttribute("height","30");  // to be dynamic; match omitted tbl's hgt
             imgele.setAttribute("width", "100");
             addEventsToElement(imgele);
             */
             
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
         // tableele.setAttribute("rules","all"); // temp temp
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
       } 
       
       /* else if (thistag.nodeName == "row") {
         var trele = document.createElement("tr");
         var children = thistag.childNodes;
         for(var i = 0; i < children.length; ++i) {
           if((children[i].nodeName != "tag") && (children[i].nodeName != "bundle"))
             alert("warning: row contains element "+children[i].nodeName);
           var tdele = document.createElement("td");
           tdele.appendChild(convertToHTML(children[i]));
           trele.appendChild(tdele);
         }
         return trele;
       }*/
        else {
         alert("don't know how to handle element: "+thistag.nodeName);
       }
 }
 
 function myencode(str) {
 return encodeURIComponent(str); //encodeURIComponent
 }


/**
* load the cloud "cloud" with operations "operation" applied to it,
* and load it to the element (or element id) tagcloudele
* This loads it from the server and actually converts the XML
* response to HTML
*/
function loadCloud(cloud,operation,tagcloudele) {
 tagcloudele = convertToElementWhenNeeded(tagcloudele);
 tagcloudele.setAttribute("cloud",cloud);
 tagcloudele.setAttribute("operation",operation);
 tagcloudele.className = "tagcloud";
 //alert(operation+ " "+myencode(operation));
 var url  = "tc?c="+cloud+"&o="+operation+"&as=xml";
 //alert(url);
 var beforedate = new Date();
 /**
 * this is an embedded function
 * might be nice to take it out of the loadCloud function
 * It is long and messy because we do lots of small
 * things.
 * TODO
 */
 succesfunc = function(transport) {
    if(! transport.responseXML) alert("no xml response!");
    var d = transport.responseXML;
    // oddly enough, Konqueror does not return cloud as the first child!
    for(var i = 0; i < d.childNodes.length; ++i) 
      if(d.childNodes[i].tagName == "cloud") {
        var ts = d.childNodes[i].childNodes;
        break;
      }
    cleanElement(tagcloudele);
    var counter = 0;
    if(ts.length > 0) {
      computeRollupBoxes(ts[0]);  // for non-hierarchical, will toss some attributes onto the first tag
      tagcloudele.appendChild(convertToHTML(ts[0]));
      }
    else alert("empty cloud!");
    for(counter = 1; counter < ts.length; ++counter) {
       tagcloudele.appendChild(document.createTextNode(" "));
       tagcloudele.appendChild(convertToHTML(ts[counter]));
    }
    var menubarelement = document.createElement("p");
    menubarelement.className = "menubar";
    menubarelement.appendChild(document.createTextNode("put a menu here: close this tag, do this, do that "));
    axml = document.createElement("a");
    axml.setAttribute("href",url);
    axml.appendChild (document.createTextNode("Download as XML"));
    menubarelement.appendChild(document.createTextNode(" "));
    menubarelement.appendChild(axml);
    tagcloudele.parentNode.insertBefore(menubarelement,tagcloudele);
    menubarelement.appendChild(document.createTextNode(" "));
    auri = document.createElement("a");
    auri.setAttribute("href","?page=clouddemo&c="+cloud+"&o="+operation);
    auri.appendChild (document.createTextNode("Permanent link to this cloud"));
    menubarelement.appendChild(auri);
    var sb = $("statusbar");
    cleanElement(sb);
    var afterdate = new Date();
    //var msg = "number of tags loaded: "+counter+ "\n";
    var timeelapsed = (afterdate.getTime()-beforedate.getTime());
    var msg = "time elapsed: "+timeelapsed+" ms"
    sb.appendChild(document.createTextNode(msg));    
    var cmd = "optimizeCloud('"+ tagcloudele.getAttribute("cloud") + "','"+tagcloudele.getAttribute("operation") +"')"
    //alert(cmd);
    setTimeout(cmd,2);
  }
  /**
  * end of big messy function
  */
  /**
  * next we call prototype's function.
  */ 
  new Ajax.Request(url,{
    method: 'get', 
    onFailure: function(transport) {
      alert("We are in trouble (failure to load XML -- response from server was "+ transport.status+")");
      },
    onComplete: function(transport, object) {
       if((transport.status >= "200") && (transport.status < "300")) 
       succesfunc(transport);
     }
  });
  /**
  * that's it, go home
  */
}

/**
* this is used for span elements containing our tags
*/
function addEventsToElement(ele) {
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
}
/*
function addEvents(tagcloudid) {
  // next line disables context menu
  //document.oncontextmenu = bogus;
  var container = convertToElementWhenNeeded(tagcloudid);
  var childNodes =container.getElementsByTagName('span');
  var sb = $("statusbar");
  if( ! sb ) {
    alert("sb is null!");
  }
  cleanElement(sb);
  sb.appendChild(document.createTextNode("number of tags: "+childNodes.length));
  for (var i = 0; i < childNodes.length; i++) {
    addEventsToElement(childNodes[i]);  
  }
}*/
