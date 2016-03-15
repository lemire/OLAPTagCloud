package processdata;

import java.util.Hashtable;
import olapdbtools.DbTools;
import olapdbtools.DbToolsFactory;

/**
 * XMLCuboid is used when a cuboid is computed from an XML data set.
 * @author kamel
 *
 */

public class XMLCuboid extends Cuboid{
	public XMLCuboid(String data,String[] dim, DbToolsFactory dbfact) throws java.sql.SQLException, ClassNotFoundException, olapdbtools.DbToolsException{
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
			throw new Error("XML data not foud!");
		}else{
			String[] results = myDB.getResultsAsString();
			//This should not happen
			if (Integer.parseInt(results[0].split(",")[1]) ==0){
				myDB.closeConnection();
				throw new Error("Data set is not XML!");//the second field tell us where the data set is generated from XML data or not
			}
			mDescription=results[0].split(",")[2];
			String fromClause="from "+mDatasetID+"_edge E"+mDimensions[0]+", "+mDatasetID+"_data D"+mDimensions[0];
			String selectClause="select CONVERT(D"+mDimensions[0]+".value USING utf8) as "+ mDimensions[0];
			String groupbyClause="group by D"+mDimensions[0]+".value";;
			String whereClause="where E"+mDimensions[0]+".id=D"+mDimensions[0]+".id";
			whereClause+=" AND E"+mDimensions[0]+".label='"+mDimensions[0]+"'";
			for(int i=1; i< mDimensions.length; i++){
				fromClause+=", "+mDatasetID+"_edge E"+mDimensions[i]+", "+mDatasetID+"_data D"+mDimensions[i];
				selectClause+=", "+"CONVERT(D"+mDimensions[i]+".value USING utf8) as "+ mDimensions[i];
				groupbyClause+=", "+"D"+mDimensions[i]+".value";
				whereClause+=" AND E"+mDimensions[i]+".id=D"+mDimensions[i]+".id";
				whereClause+=" AND E"+mDimensions[i]+".label='"+mDimensions[i]+"'";
			}
			for(int i=0; i<=mDimensions.length-2; i++){
				whereClause+=" AND E"+mDimensions[i]+".idsource=E"+mDimensions[i+1]+".idsource";
			}
			selectClause+=", COUNT(*) as measure";
			String sqlString=selectClause +" "+fromClause + " "+ whereClause + " "+groupbyClause + " order by measure desc limit " + LIMIT;
			myDB.queryDataBase(sqlString);
			mCuboidValues = (Hashtable<String,String[]>) myDB.getResultsAsHashTable();
			myDB.closeConnection();
		}
	}
}
