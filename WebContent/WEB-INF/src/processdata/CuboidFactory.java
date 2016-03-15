package processdata;

import util.*;
import olapdbtools.*;
import java.lang.ref.*;
import java.sql.SQLException;
import java.util.*;
import java.lang.reflect.*;
import olapdbtools.DbTools;
import olapdbtools.DbToolsException;
/**
 * This class is meant to store cuboids in RAM so that they can later be
 * retrieved if the garbage collector didn't clean them out already.
 *
 * The methods really should not be static, but for a first pass, it should do.
 * @author lemire
 *
 */
public class CuboidFactory {
  private  Hashtable<Triple, SoftReference<Cuboid>> mtable = 
	  new Hashtable<Triple, SoftReference<Cuboid>>();
  DbToolsFactory mydbfact;

  public CuboidFactory(DbToolsFactory dbfact) {
	  mydbfact = dbfact;
  }
  
 public String[] getAllDimensions(String id) throws ClassNotFoundException, SQLException, DbToolsException{
		DbTools myDB=mydbfact.newInstance();
		myDB.openConnection();
		String [] select ={"id","isxml"};
		String [] where ={"id"};
		String [] values ={StripString.toCleanUp(id.toLowerCase())};
		boolean isIDExist = myDB.hasResults("cubes",select,where,values);
		String [] answer;
		if (!isIDExist){
			throw new RuntimeException("can't find cube "+id);
		}else{
			String[] results = myDB.getResultsAsString();
			int isXML = Integer.parseInt(results[0].split(",")[1]);
			if (isXML ==0){//Flat file
				String sqlString="SHOW COLUMNS FROM "+results[0].split(",")[0];
				myDB.queryDataBase(sqlString);
				answer =  myDB.getResultsAsString();
			}else{//XML file
				String sqlString="SELECT DISTINCT label as Field FROM "+ results[0].split(",")[0]+"_edge WHERE flag='cda'";
				myDB.queryDataBase(sqlString);
				answer = myDB.getResultsAsString();
			}			
		}
		myDB.closeConnection();
		return answer;
  }

  public Cuboid getCuboid(String name, String[] dimensions) throws ClassNotFoundException, SQLException, DbToolsException {
	  Arrays.sort(dimensions);
	  if(name.equals("fake")) return new FakeCuboid();
	  Class cuboidtype;
	  DbTools myDB=mydbfact.newInstance();//new DbTools();
	  myDB.openConnection();
	  String [] select ={"id","isxml"};
	  String [] where ={"id"};
	  String [] values ={name.toLowerCase()};
	  boolean isIDExist = myDB.hasResults("cubes",select,where,values);
	  if (!isIDExist){
		  myDB.closeConnection();
		  throw new RuntimeException("requested data not foud");
	  }else{
		  String[] results = myDB.getResultsAsString();
		  myDB.closeConnection();
		  int isXML = Integer.parseInt(results[0].split(",")[1]);
		  if (isXML ==0){//Flat file
			  String groupby=dimensions[0];
			  for(int i=1; i< dimensions.length; i++){
				  groupby+=","+dimensions[i];
			  }
			  cuboidtype = Class.forName("processdata.FlatFileCuboid");
		  }else{
			  cuboidtype = Class.forName("processdata.XMLCuboid");
		  }
	  }
	  Triple mykey = new Triple(name,dimensions,cuboidtype);
	  if(mtable.containsKey(mykey)) {
    	  Cuboid cached = mtable.get(mykey).get();
    	  if(cached != null) return cached;
      }
      Constructor[] c =  cuboidtype.getConstructors();
      for(int k = 0; k < c.length; k++) {
    	  if(c[k].getParameterTypes().length != 3) continue;
    	  // I'm assuming that if there are three parameters, we have the right one.
    	  Constructor correctconstructor = c[k]; 
    	  try {
    	    Cuboid newcube = (Cuboid) correctconstructor.newInstance(name,dimensions,mydbfact);
    	    mtable.put(mykey, new SoftReference<Cuboid>(newcube));
    	    return newcube;
    	  } catch(IllegalAccessException iae) {
    		  iae.printStackTrace();
    	  } catch(InstantiationException ia) {
    		  ia.printStackTrace();
    	  } catch(InvocationTargetException ite) {
    		  ite.printStackTrace();
    	  }
      }
      throw new Error("Don't know how to instantiate a Cuboid from class "+cuboidtype.getName());
  }
}
