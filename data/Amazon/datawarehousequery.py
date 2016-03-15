#!/usr/bin/env python
# file: datawarehousequery.py
#
#
# This little program will search for all matching CDs for a list of artists
# (one per line) in the file artsts_big.txt. It will query AWS 4.0 (Amazon
# Web Service 4.0) using REST and the data is recovered using simple XPath
# expressions.
#
# Please replace the ID by your own Amazon ID.
#
# if libxml2 is reported missing, just make sure libxml2 is installed
# (under gentoo, do emerge libxml2)
try: 
  import libxml2
except :
  print "make sure libxml2 is installed. Do emerge libxml2 under gentoo"
  print "under macos, install fink and do 'fink install libxml2-py25'"
import urllib2, urllib, sys,re,traceback
ID="0JP6QSZBVY57XE67R782"# please enter your own ID here
uri="http://webservices.amazon.com/AWSECommerceService/2005-10-05"
url="http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&SubscriptionId=%s&Operation=ItemSearch&SearchIndex=Music&Artist=%s&ItemPage=%i&ResponseGroup=Request,ItemIds,SalesRank,ItemAttributes,Reviews"
outputcontent = ["ASIN","Title","Artist","TotalReviews","ReleaseDate","Amount", "SalesRank","AverageRating"]#,
strdescription = ""
for i in range(len(outputcontent)-1):
  strdescription+=outputcontent[i]+","
strdescription+=outputcontent[len(outputcontent)-1]
#
input = open("artists_big.txt")
output = open("amazonresults.csv", "w")
output.write(strdescription+"\n")
log = open("amazonlog.txt", "w")
def getNodeContentByName(node, name):
        for i in node: 
                if (i.name==name): return i.content
        return None
for artist in input:#go through all artists
        print "Recovering albums for artist : ", artist
        page = 1
        while(True):# recover all pages
                resturl = url %(ID,urllib.quote(artist),page)
                log.write("Issuing REST request: "+resturl+"\n")
                try :
                        data = urllib2.urlopen(resturl).read()
                        print "saving result of query to answer.xml"
                        fout = open("answer.xml", 'w')
                        fout.write(data)
                        fout.close()
                except urllib2.HTTPError,e:
                        log.write("\n")
                        log.write(str(traceback.format_exception(*sys.exc_info())))
                        log.write("\n")
                        log.write("could not retrieve :\n"+resturl+"\n")
                        continue
                try :
                        doc = libxml2.parseDoc(data)
                except libxml2.parserError,e:
                        log.write("\n")
                        log.write(str(traceback.format_exception(*sys.exc_info())))
                        log.write("\n")
                        log.write("could not parse (is valid XML?):\n"+data+"\n")
                        continue
                ctxt=doc.xpathNewContext()
                ctxt.xpathRegisterNs("aws",uri)
                isvalid = (ctxt.xpathEval("//aws:Items/aws:Request/aws:IsValid")[0].content == "True")
                if not isvalid : 
                        log.write("The query %s failed " % (resturl))
                        errors = ctxt.xpathEval("//aws:Error/aws:Message")
                        for message in errors: log.write(message.content+"\n")
                        continue
                for itemnode in ctxt.xpathEval("//aws:Items/aws:Item"):
                        attr = {}
                        for nodename in outputcontent:
                                content = getNodeContentByName(itemnode,nodename)
                                if(content <> None): 
                                        content = re.sub("'","\\'",content)
                                        if(nodename == "SalesRank"):
                                                content = re.sub(",","",content)
                                        attr[nodename] = content
                        #columns = "("
                        #keys = attr.keys()
                        #for i in range(len(keys)-1):
                        #        columns += keys[i]+","
                        #columns+=keys[len(keys)-1]+")"
                        #row = "("
                        row =""
                        values = attr.values()
                        for i in range(len(values)-1):
                                row+="'"+str(values[i])+"',"
                        row+="'"+str(values[len(values)-1])+"'"
                        #command = "INSERT INTO music "+columns+" VALUES "+row+";\n"
                        #print row #output.write(command)
                        output.write(row+"\n")
                NumberOfPages = int(ctxt.xpathEval("//aws:Items/aws:TotalPages")[0].content)
                if(page >= NumberOfPages): break
                page += 1
input.close()
output.close()
log.close()

