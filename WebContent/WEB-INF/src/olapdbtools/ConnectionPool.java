package olapdbtools;

import java.util.*;
import java.sql.*;

public class ConnectionPool {
	static int UNUSEDLIMIT = 10;
	static java.util.Hashtable<String, Vector<Connection>> mBigPool = new java.util.Hashtable<String, Vector<Connection>>();
	
	public static Connection getConnection(String url,String username, String password) throws SQLException {
		Vector<Connection> available = mBigPool.get(url);
		// warning, not synchronized?
		if (available == null ){ 
			return DriverManager.getConnection(url,username,password);
		}
		if(available.size()>0) {
			return available.remove(available.size()-1);
		}
		return DriverManager.getConnection(url,username,password);
	}
	
	public static void putItBack(String url, Connection c)  throws SQLException {
		// warning... synchronization?
		if(mBigPool.size() > UNUSEDLIMIT) {
			c.close();
		} else {
			mBigPool.get(url).add(c);
		}
	}
	
	public static void putItIn(String url, Connection con){
		Vector<Connection> tmp =new Vector<Connection>();
		tmp.add(con);
		mBigPool.put(url,tmp);
	}
	
	public static void closeAll() {
		for(String key :  mBigPool.keySet()) {
			Vector<Connection> v = mBigPool.remove(key);
			for(Connection c : v) 
				try {
				  c.close();
				} catch(SQLException e) {e.printStackTrace();}
		}
	}
}