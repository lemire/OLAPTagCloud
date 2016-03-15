package processdata;

import java.util.Hashtable;
import olapdbtools.*;

/**
 * FlatFileCuboid is used when a cuboid is computed from a flat file.
 * 
 * (Daniel: I think I once refactored the code and it was reverted. 
 * A CSV file is not a flat file.)
 * 
 * 
 * 
 * @author kamel
 *
 */

public class FlatFileCuboid extends Cuboid {
	public FlatFileCuboid(String data,String[] dim, DbToolsFactory dbfact) throws java.sql.SQLException, ClassNotFoundException, olapdbtools.DbToolsException{
		mDimensions =dim;
		mDatasetID=data;
		String [] select ={"id","isxml","description"};
		String [] where ={"id"};
		String [] values ={mDatasetID};
		DbTools myDB=dbfact.newInstance();
		myDB.openConnection();
		boolean isIDExist = myDB.hasResults("cubes",select,where,values);
		if (!isIDExist){
			myDB.closeConnection();
			throw new Error("Data set not foud!");
		}else{
			String[] results = myDB.getResultsAsString();
			//This should not happen
			if (Integer.parseInt(results[0].split(",")[1]) !=0) {
				myDB.closeConnection();
				throw new Error("Data set is not a flat File!");//the second field tell us where the data set is genreated from XML data or not
			}
			mDescription=results[0].split(",")[2];
			String groupby=mDimensions[0];
			for(int i=1; i< mDimensions.length; i++){
				groupby+=","+mDimensions[i];
			}
			//Retrieving top-limit group bys
			String sqlString="Select "+ groupby +",count(*) as measure from "+ mDatasetID+"  group by "+ groupby + " order by measure desc Limit " + LIMIT;
			//System.out.println(sqlString);
			myDB.queryDataBase(sqlString);
			mCuboidValues = (Hashtable<String,String[]>) myDB.getResultsAsHashTable();
			myDB.closeConnection();
		}
	}
}
