function optimizeCloud(cloudName, operationName) {
 var cloudcontainer = getCloudElement(cloudName, operationName) ;
 //alert("normally, I would now optimize "+cloudcontainer);
 if((cloudcontainer.firstChild.nodeName=="TABLE") || (cloudcontainer.firstChild.nodeName=="table"))
   hierarchicalOptimizer(cloudcontainer);
 else 
   classicalOptimizer(cloudcontainer);
}

function hierarchicalOptimizer(cloudcontainer) {
  //alert("owen does his crazy stuff");
}


function classicalOptimizer(cloudcontainer) {
  //alert("classical optimization");
}