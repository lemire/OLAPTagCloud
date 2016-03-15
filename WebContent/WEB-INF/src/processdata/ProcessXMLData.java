package processdata;

import javax.xml.stream.*;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

import olapdbtools.DbTools;
import olapdbtools.DbToolsException;
import olapdbtools.DbToolsFactory;

/**
 * This class parses an XML file and store its mapping in a relational database.
 * For each XML file three tables are created: edge, attribute and data.
 * The mapping method is described in the paper.
 * @author kamel
 *
 */
public class ProcessXMLData extends ProcessData{
	
	protected int nbOfNodes;
	protected int totalSize;
	protected static int NBVARCHAR=8196;
	protected final static String CDATA ="cda";
	protected final static String REF ="ref";
	
	public ProcessXMLData(InputStream dataStream, String tableName, String description) throws IOException{
		this.dataStream = dataStream;
		this.tableName=tableName;
		this.description = description;
		totalSize = dataStream.available();
		availableData = totalSize;
		nbOfNodes=0;
	}
	
	
	/** 
	* Uploading XML data
	*
	* @return the number of nodes read
	* @throws DbToolsException 
	* @throws ClassNotFoundException 
	* @throws XMLStreamException 
	* @throws SQLException 
	* @throws DbToolsException
    */
	public int uploadData(DbToolsFactory dbfact) throws ClassNotFoundException, XMLStreamException, SQLException, DbToolsException{
		System.out.println("ProcessXMLData... uploadData");    
		DbTools mydb = dbfact.newInstance();//a database connection
		System.out.println("Opening database connection...");
		mydb.openConnection();
		sqlString="Insert into cubes values('"+tableName+"','"+description+"',1)";
		System.out.println("Opening database connection...ok");
		//try{
			mydb.alterDataBase(sqlString);
			//System.out.println("sqlString: "+sqlString);
			sqlString="Create table "+tableName+"_attribute (id int, name VARCHAR(20), value VARCHAR("+NBVARCHAR+")) DEFAULT CHARSET=utf8";
			//System.out.println("sqlString: "+sqlString);
			mydb.alterDataBase(sqlString);
			sqlString="Create table "+tableName+"_edge (idsource int, ordinal int, label VARCHAR(20), flag char(3), id int) DEFAULT CHARSET=utf8";
			System.out.println("sqlString: "+sqlString);
			mydb.alterDataBase(sqlString);
			sqlString="Create table "+tableName+"_data (id int, value VARCHAR("+NBVARCHAR+")) DEFAULT CHARSET=utf8";
			System.out.println("sqlString: "+sqlString);
			mydb.alterDataBase(sqlString);

			XMLInputFactory factory = (XMLInputFactory) XMLInputFactory.newInstance();
			/**
			 * Daniel thinks it is better to leave the entities untouched and not
			 * to try to load the DTDs at all. 
			 */

			factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
			factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
			factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			XMLStreamReader parser = (XMLStreamReader)factory.createXMLStreamReader(dataStream);
			int event=parser.next();
			try {
				availableData = dataStream.available();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			int i=0;
			String cdata=null;
			String flag=REF;
			Stack<Integer> id = new Stack<Integer>();
			id.push(i);
			Map<Integer,Integer> tagPos = new Hashtable<Integer,Integer>();

			String [] attNameEdge={"idSource","ordinal","label","flag","id"};
			int idEdge=mydb.addInsertPreparedStatement(tableName+"_edge", attNameEdge);

			String [] attNameAttribute={"id","name","value"};
			int idAttribute=mydb.addInsertPreparedStatement(tableName+"_attribute", attNameAttribute);

			String [] attNameData={"id","value"};
			int idData=mydb.addInsertPreparedStatement(tableName+"_data", attNameData);

			while (event != XMLStreamConstants.END_DOCUMENT){
				switch (event) {
				case XMLStreamConstants.CHARACTERS:
					cdata = parser.getText().trim();
					if (cdata.length() !=0){
						flag=CDATA;
					}
					break;
				case XMLStreamConstants.START_ELEMENT:
					++nbOfNodes;
					i++;
					id.push(i);
					for(int j=0; j< parser.getAttributeCount(); j++){
						String value = parser.getAttributeValue(j).replaceAll("'","\\\\'");
						String [] attributeValues={(new Integer(i)).toString(), parser.getAttributeLocalName(j),value};
						mydb.addBatchInsert(attributeValues,idAttribute);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					String label = parser.getLocalName();
					int idTarget= id.pop();
					int idSource= id.peek();
					if (tagPos.containsKey(id.peek())){
						Integer si= tagPos.get(id.peek());
						tagPos.put(id.peek(), new Integer(si+1));
					} else
						tagPos.put(id.peek(), new Integer(1));
					String [] edgeValues={(new Integer(idSource)).toString(), (new Integer(tagPos.get(id.peek()))).toString(),label,flag,(new Integer(idTarget)).toString()};
					mydb.addBatchInsert(edgeValues,idEdge);
					if (CDATA.equals(flag)){
						String [] dataValues={(new Integer(idTarget)).toString(), cdata};
						mydb.addBatchInsert(dataValues,idData);
						cdata=null;
					}
					flag=REF;
					break;
				}
				event=parser.next();
				try {
					availableData = dataStream.available();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
				if (nbOfNodes % NBLINESINBATCH==0) 
					for (int k=0; k<3; k++)
						mydb.executeBatchInsert(k);
			}

			for (int k=0; k<3; k++)
				mydb.executeBatchInsert(k);
			parser.close();
			mydb.closeConnection();
			return nbOfNodes;
	}
	
	public  void deleteCubeEntry(DbToolsFactory dbfact) throws SQLException{
		DbTools mydb = dbfact.newInstance();
		mydb.openConnection();
		sqlString="delete from cubes where id='"+tableName+"'";
		mydb.alterDataBase(sqlString);
		sqlString="drop table IF EXISTS "+tableName+"_attribute";
		mydb.alterDataBase(sqlString);
		sqlString="drop table IF EXISTS "+tableName+"_data";
		mydb.alterDataBase(sqlString);
		sqlString="drop table IF EXISTS "+tableName+"_edge";
		mydb.alterDataBase(sqlString);
		mydb.closeConnection();
	}
	
	/**
	 * Number of processed nodes
	 * @return integer
	 */
	public int getNbOfElements() {
		return nbOfNodes;
	}
}
