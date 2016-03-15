package olapdbtools;

import java.sql.*;
import java.util.Hashtable;
import java.util.Vector;
import org.json.*;

/**
 * This is just meant to contain useful
 * methods to connect to a given database 
 * 
 * @author kamel
 *
 */

public class DbTools implements DbInfo{
	protected DbTools(String username, String password, String hostname, int portnumber, String dbname, String driverclass){
		this.username=username;
		this.password=password;
		this.dbname=dbname;
		this.hostname=hostname;
		this.portnumber=portnumber;
		url = getUrl();
	}
	
	public String getDbName(){return dbname;}
	
	public void setDbName(String newdbname){
		dbname = newdbname;
		url = getUrl();
	}
	
	public String getUrl(){return "jdbc:mysql://"+getHostName()+":"+getPortNumber()+"/"+getDbName();}
	
	public String getHostName(){return hostname;}
	
	public void setHostName(String newhostname){
		hostname = newhostname;
		url = getUrl();
	}
	
	public int getPortNumber(){return portnumber;}
	
	public void setPortNumber(int port){
		portnumber = port;
		url = getUrl();
	}
	
	
	/**
	 * Establish a connection to the database at URL with user name and password
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void openConnection() throws  SQLException{
			con = ConnectionPool.getConnection(url, username, password);
			ConnectionPool.putItIn(url, con);
	}
	
	/*public Connection getConnection(){return con;}*/
	
	/**
	 * Execute update queries
	 * @param sqlString
	 * @return number of affected arrows, return -1 if the an exception happens
	 */
	public int updateDataBase(String sqlString) throws SQLException{
		stmt= con.createStatement();
		int ans = stmt.executeUpdate(sqlString);
		stmt.close();
		return ans;
	}
	
	/**
	 * Define an SQL query 
	 * @param sqlString
	 * @throws SQLException
	 */
	public void queryDataBase(String sqlString) throws SQLException{
		stmt=con.createStatement();
		results=stmt.executeQuery(sqlString);
	}
	
	/**
	 * getting SQL query results as a table of CSV strings
	 * @return results as an array of strings
	 * @throws SQLException
	 */
	public String[] getResultsAsString() throws SQLException{
		Vector<String> resultsAsString = new Vector<String>();
		ResultSetMetaData resultMeta = results.getMetaData();
		int nbColumn= resultMeta.getColumnCount();
		resetNbofRows();
		String line;
		results.beforeFirst();
		while(results.next()){
			line="";
			for (int i=0; i<nbColumn-1; i++){
				line +=results.getString(i+1)+",";
			}
			line +=results.getString(nbColumn);
			resultsAsString.addElement(line);
			++nbOfRows;
		}
		return ((String[]) resultsAsString.toArray(new String[resultsAsString.size()]));
	}
	
	/**
	 * getting results of table as a hash table
	 * @return Hashtable
	 * @throws SQLException
	 */
	public Hashtable<String,String[]> getResultsAsHashTable() throws SQLException{
		Hashtable<String,String[]> hashResults = new Hashtable<String,String[]> ();
		
		ResultSetMetaData resultMeta = results.getMetaData();
		int nbColumn= resultMeta.getColumnCount();
		resetNbofRows();
		Vector<Vector<String>> tempResults=(Vector<Vector<String>>) new Vector<Vector<String>>();
		for (int i=0; i< nbColumn; i++){
			tempResults.addElement(new Vector<String>());
		}
		tempResults.ensureCapacity(nbColumn);
		results.beforeFirst();
		while(results.next()){
			for (int i=0; i<nbColumn; i++){
				tempResults.elementAt(i).addElement(results.getString(i+1)); 
			}
			++nbOfRows;
		}
		for (int k=0; k< nbColumn;k++)
			hashResults.put(resultMeta.getColumnName(k+1), (String[]) tempResults.elementAt(k).toArray(new String[tempResults.elementAt(k).size()]));
		return (Hashtable<String,String[]>)hashResults;
	}
	
	/**
	 * returns SQL query results as a JSON object
	 * @return results as JSON
	 * @throws SQLException
	 */
	public String getResultsAsJSON() throws SQLException, JSONException{
		ResultSetMetaData resultMeta = results.getMetaData();
		int nbColumn= resultMeta.getColumnCount();
		resetNbofRows();
		JSONObject jsonRows=new JSONObject();
		results.beforeFirst();
		while(results.next()){
			JSONObject json=new JSONObject();
			for (int i=0; i<nbColumn; i++){
				json.put(resultMeta.getColumnName(i+1),results.getString(i+1));
			}
			jsonRows.accumulate("row", json);
			++nbOfRows;
		}
		JSONObject jsonResults=new JSONObject();
		jsonResults.put("results",jsonRows);
		return (jsonResults.toString());
	}
	
	public int getNbOfRows(){
		return nbOfRows;
	}
	
	public void resetNbofRows(){
		nbOfRows=0;
	}
	
	/**
	 * Check if a query has at least one result
	 * @param sqlString
	 * @return boolean (true: query has results, false; no results)
	 */
	public boolean hasResults(String sqlString) throws SQLException{
		boolean ans=false;
		resetNbofRows();
		stmt=con.createStatement();
		results=stmt.executeQuery(sqlString);
		if(results.first()) ans=true;	
		return ans;
	}
	
	public boolean hasResults(String table, String[] select, String[] where, String[] values) throws SQLException, DbToolsException{
		boolean ans=false;
		resetNbofRows();
		this.createSelectPreparedStatement(table,select,where);
		this.addBatchQuery(values);
		this.executePreparedStatementSelect();
		if(results.first()) ans=true;
		return ans;
	}
	
	/**
	 * To add a select prepared statement (prevent SQL injections)
	 * @param table
	 * @param select
	 * @param where
	 * @throws SQLException
	 * @throws DbToolsException 
	 */
	public void createSelectPreparedStatement(String table, String[] select, String[] where) throws SQLException, DbToolsException{
		if (select.length==0)  throw new DbToolsException("Select clause is empty");
		if (where.length==0)  throw new DbToolsException("Where clause is empty");
		String clauseWhere=where[0]+"=?";
		String clauseSelect=select[0];
		for (int i=1; i<where.length;i++){
			clauseWhere +=" AND "+where[i]+"=?";
		}
		for(int i=1;i< select.length;i++){
			clauseSelect+=","+select[i];
		}
		String sqlString="SELECT "+clauseSelect+" FROM "+table+ " WHERE " + clauseWhere +";";
		pstmt = con.prepareStatement(sqlString);
	}

	public void createSelectPreparedStatement(String table, String[] aggregate, String[] where, String [] groupby) throws SQLException, DbToolsException{
		if (groupby.length==0)  throw new DbToolsException("Select clause is empty");
		if (where.length==0)  throw new DbToolsException("Where clause is empty");
		if (aggregate.length==0)  throw new DbToolsException("No aggregation operations");
		String clauseWhere=where[0]+"=?";
		String clauseSelect=aggregate[0];
		String clauseGroupby=groupby[0];
		for (int i=1; i<where.length;i++){
			clauseWhere +=" AND "+where[i]+"=?";
		}
		for(int i=1;i< aggregate.length;i++){
			clauseSelect+=","+aggregate[i];
		}
		for(int i=1;i< groupby.length;i++){
			clauseGroupby+=","+groupby[i];
		}
		String sqlString="SELECT "+clauseSelect+","+clauseGroupby+" FROM "+table+ " WHERE " + clauseWhere +" GROUP BY "+clauseGroupby+";";
		pstmt = con.prepareStatement(sqlString);
	}
	
	public void executePreparedStatementSelect() throws SQLException{
		resetNbofRows();
		results = pstmt.executeQuery();
	}
	
	
	/**
	 * 
	 * @param values
	 * @throws SQLException
	 */
	public void addBatchQuery(String values[]) throws SQLException{
		for(int i = 0; i < values.length;i++){
			pstmt.setString(i+1, values[i]);
		}
	}

	
	/**
	 * create a prepared statement used to insert data
	 * @param table (table name)
	 * @param attribute names (array)
	 * @throws DbToolsException 
	 */
	public void createInsertPreparedStatement(String table, String attribute[]) throws SQLException, DbToolsException {
		if (attribute.length==0)  throw new DbToolsException("No attributes");
		String clauseValues="(?";
		String clauseAttributes="("+attribute[0];
		for (int i=1; i<attribute.length;i++){
			clauseValues +=",?";
			clauseAttributes +=","+attribute[i];
		}
		clauseValues +=")";
		clauseAttributes +=")";
		String sqlString="INSERT INTO "+table+ " " + clauseAttributes +" VALUES "+ clauseValues+";";
		pstmt = con.prepareStatement(sqlString);
	}
	
	public void closePreparedStatement() throws SQLException{
		pstmt.close();
	}
	
	private PreparedStatement getPreparedStatement(int p){
		return vpstmt.elementAt(p); 
	}
	
	/**
	 * Adding the values to insert
	 * @param values
	 * @param p
	 * @throws SQLException
	 * @throws DbToolsException 
	 */
	public void addBatchInsert(String values[],int p) throws SQLException, DbToolsException{
		if (values.length==0)  throw new DbToolsException("No values to insert");
		String line=null;
		for(int i = 0; i < values.length;i++){
			this.getPreparedStatement(p).setString(i+1, values[i]);
			line +=" "+ values[i];
		}
		this.getPreparedStatement(p).addBatch();
	}
	
	public void addBatchInsert(String values[]) throws SQLException{
		for(int i = 0; i < values.length;i++){
			pstmt.setString(i+1, values[i]);
		}
		pstmt.addBatch();
	}
	
	/**
	 * Execute prepared statement 
	 * Execute all batch queries
	 */
	public void executeBatchInsert() throws SQLException{
		resetNbofRows();
		pstmt.executeBatch();
		pstmt.clearBatch();
	}
	
	/**
	 * Execute the prepared statement indexed by p 
	 * @param p
	 * @throws SQLException
	 */
	public void executeBatchInsert(int p) throws SQLException{
		resetNbofRows();
		this.getPreparedStatement(p).executeBatch();
		this.getPreparedStatement(p).clearBatch();
	}
	
	/**
	 * To add an insert prepared statement
	 * @param table
	 * @param attribute
	 * @return number of affected arrows
	 * @throws SQLException
	 * @throws DbToolsException 
	 */
	public int addInsertPreparedStatement(String table, String[] attribute) throws SQLException, DbToolsException{
		if (attribute.length==0)  throw new DbToolsException("No attributes");
		String clauseValues="(?";
		String clauseAttributes="("+attribute[0];
		for (int i=1; i<attribute.length;i++){
			clauseValues +=",?";
			clauseAttributes +=","+attribute[i];
		}
		clauseValues +=")";
		clauseAttributes +=")";
		String sqlString="INSERT INTO "+table+ " " + clauseAttributes +" VALUES "+ clauseValues+";";
		
		vpstmt.addElement(con.prepareStatement(sqlString));
		return vpstmt.size()-1;
	}
		
	public void alterDataBase(String sqlString) throws SQLException{
		resetNbofRows();
		//System.out.println(sqlString);
		stmt=con.createStatement();
		stmt.execute(sqlString);
		stmt.close();
	}
	
	public void closeConnection() throws SQLException{
		ConnectionPool.putItBack(url, con);
	}
	
	private static Connection con; //a database connection
	private Statement stmt;//a statement
	private PreparedStatement pstmt;//Prepared statement
	private Vector<PreparedStatement> vpstmt=new Vector<PreparedStatement>();
	private ResultSet results;// Results of a given query
	private String username;
	private String password;
	private String hostname;
	private String dbname;
	private int portnumber;
	private String url;
	private int nbOfRows;
}
