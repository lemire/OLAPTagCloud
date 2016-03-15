package olapdbtools;
import java.io.*;

public class DbToolsFactory implements DbInfo{
	String username, password,hostname,dbname,driverclass;
	int portnumber;
	public DbToolsFactory(java.io.InputStream propertiesfilestream) throws ClassNotFoundException {
		java.util.Properties m = new java.util.Properties();
		try {
			//if(propertiesfilestream != null)
		  m.load(propertiesfilestream);
			//else System.out.println("a null stream");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		username=m.getProperty("USER",USER);
		password=m.getProperty("PASSWORD",PASSWORD);
		driverclass=m.getProperty("DRIVERCLASS",DRIVERCLASS);
		Class.forName(driverclass);//"org.gjt.mm.mysql.Driver");
		try {
		  portnumber=Integer.parseInt(m.getProperty("PORTNUMBER"),PORTNUMBER);
		} catch(NumberFormatException nfe) {portnumber=PORTNUMBER;}
		hostname=m.getProperty("HOST",HOST);
		dbname=TAGCLOUDDB;
	}
	
	public DbTools newInstance() {
		return new DbTools(username, password, hostname, portnumber, dbname,driverclass);
	}
}