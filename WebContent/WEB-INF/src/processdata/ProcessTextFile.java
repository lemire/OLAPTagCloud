package processdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.InputStreamReader;
import olapdbtools.*;

/**
 * ProcessFlatFile parses a text file and store it in a relational database
 * 
 * Note: a flat file is something different, akin to what MySQL and other
 * relational databases use to store their table. See for example:
 * http://en.wikipedia.org/wiki/Flat_file
 * 
 * @author kamel
 *
 */
public class ProcessTextFile extends ProcessData{
	
	protected int nbOfLines;
	protected String delimiter;
	protected static int NBVARCHAR=255;
	
	public ProcessTextFile(InputStream dataStream, String tableName, String delimiter, String description) throws IOException{
		this.dataStream = dataStream;
		this.tableName=tableName;
		this.delimiter=delimiter;
		this.description = description;
		totalSize=dataStream.available();
		availableData = totalSize;
		nbOfLines=0;
	}
	
	public String getCharDelimiter(){return delimiter;}
	
	/**
	 * Reading a line of data from the buffer
	 * @param buffer
	 * @return line of data
	 */
	public String getLine(BufferedReader buffer) throws IOException{
		String line = buffer.readLine();
		++nbOfLines;
		return line;
	}

	
	/**
	 *This method stores data from InputStream in a relationnel table 
	 *
	 * @return the number of lines read
	 * @throws DbToolsException 
	 * @throws IOException 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public int uploadData(DbToolsFactory dbfact) throws ClassNotFoundException, SQLException, DbToolsException, IOException{
		System.out.println("ProcessFlatFile... upload Data...");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader (dataStream));
		System.out.println("buffered reader opened... reading a line... ");
		String line = getLine(bufferRead);
		if(line == null) throw new DbToolsException("No data?");
		System.out.println("One line read: "+line);
		// Processing the first line
		String[] attributeName = line.split("\\"+delimiter); //Getting the name of attributes
		for(int k = 0; k < attributeName.length; ++k) {
			attributeName[k] = attributeName[k].trim();
			attributeName[k] = attributeName[k].replaceAll("\"", "");
			attributeName[k] = attributeName[k].replaceAll("\\W", "_");
		}
		if (attributeName.length==0)  throw new DbToolsException("Unable to get the name of dimensions. Check the delimiter!");		
		DbTools mydb = dbfact.newInstance();
		//try{
			System.out.println("opening database connection... ");
			mydb.openConnection();
			System.out.println("Database connection opened... ");
			String fielddesc = "";
			for(int k = 0; k < attributeName.length; ++k) {
				fielddesc += attributeName[k]+ " VARCHAR("+NBVARCHAR+") ";
				if(k < attributeName.length - 1) fielddesc +=",";
			}
			fielddesc = "("+fielddesc+")";
			//Creating an ID entry
			//cubes is a table where will be stored all uploaded cubes desc: cubes (id varchar(20), description tinytext) 
			sqlString="Insert into cubes values('"+tableName+"','"+description+"',0)";
			mydb.alterDataBase(sqlString);
			//System.out.println("Create a cube entry "+sqlString);
			//Creating the table where data will be stored
			sqlString="Create table "+tableName+" "+fielddesc+" DEFAULT CHARSET=utf8";//Create a table
			//System.out.println("Create table " + sqlString);
			mydb.alterDataBase(sqlString);
			mydb.createInsertPreparedStatement(tableName, attributeName);
			String[] attributeValue;
			while ((line = getLine(bufferRead))!=null){
				//System.out.println("Line " + line);
				attributeValue=line.split("\\"+delimiter);
				for(int k = 0; k < attributeValue.length; ++k) {
					attributeValue[k] = attributeValue[k].trim();
					// we do not allow double quotes anywhere
					attributeValue[k] = attributeValue[k].replaceAll("\"", "");
					// we do not allow leading single quote
					attributeValue[k] = attributeValue[k].replaceAll("^'", "");
					// we do not allow ending single quote
					attributeValue[k] = attributeValue[k].replaceAll("'$", "");
				}		
				mydb.addBatchInsert(attributeValue);
				if (nbOfLines % NBLINESINBATCH==0) {
					mydb.executeBatchInsert();
				}
				//after reading NBLINEBATCH lines all the batch queries are executed
			}
			if (nbOfLines >0){
				mydb.executeBatchInsert();
			}else{//nbOfLines==0 no data 
				throw new DbToolsException("No data?");
			}
			mydb.closePreparedStatement();
			mydb.closeConnection();
			
			return nbOfLines;
	}
	
	public  void deleteCubeEntry(DbToolsFactory dbfact) throws SQLException{
		DbTools mydb = dbfact.newInstance();
		mydb.openConnection();
		sqlString="delete from cubes where id='"+tableName+"'";
		mydb.alterDataBase(sqlString);
		sqlString="drop table IF EXISTS "+tableName;
		mydb.alterDataBase(sqlString);
		mydb.closeConnection();
	}
	
	/**
	 * Number of processd lines
	 * @return integer
	 */
	public int getNbOfElements() {
		return nbOfLines;
	}
}
