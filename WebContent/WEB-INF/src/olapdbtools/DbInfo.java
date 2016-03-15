package olapdbtools;


/**
 * Informations (by default) needed to connect to the database server
 * @author kamel
 */
public interface DbInfo {
	public String USER = "tagcloud";
   public String PASSWORD = "olap";
   public String TAGCLOUDDB = "tagcloud";
   public int PORTNUMBER = 3306;
   public String HOST = "localhost";
   public String DRIVERCLASS = "org.gjt.mm.mysql.Driver";
}
