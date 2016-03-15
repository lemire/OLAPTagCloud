package processdata;

import java.io.InputStream;
import olapdbtools.DbToolsFactory;

/**
 * This class is abstract. 
 * It should be extended to get an instance of a data processor.
 * @author kamel
 *
 */
public abstract class ProcessData {
	protected InputStream dataStream;
	protected String tableName;
	protected String description;
	protected int totalSize;
	protected String sqlString;
	protected String message;
	protected int availableData;
	protected static int NBLINESINBATCH=1000;
	
	protected ProcessData (){}
	
	public int getProcessedBytes() {return totalSize-availableData;}
	
	public int getTotalSize(){return totalSize;}
	
	/**
	 * Elements can be either nodes or lines.
	 * It depends on whether file type we are processing : XML or flat.
	 * @return Number of elements being processed
	 */
	public abstract int getNbOfElements();
	
	public abstract int uploadData(DbToolsFactory dbfact) throws Exception;
	
	public abstract void deleteCubeEntry(DbToolsFactory dbfact) throws Exception;
	
	/**
	 * Default values is equal to 1000 insert queries per batch
	 * @param value
	 */
	static void setNBofLinesToBatch(int value){
		NBLINESINBATCH=value;
	}
}
