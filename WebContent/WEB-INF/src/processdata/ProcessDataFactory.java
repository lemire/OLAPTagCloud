package processdata;

import java.io.InputStream;
import util.StripString;

/**
 * This class is meant to build data processors (XML or text file processor)
 * @author kamel
 *
 */
public class ProcessDataFactory {
	public ProcessData getProcessData(InputStream dataStream, String sourceID, String description, String delimiter, Boolean isXML) throws Exception{
		if( isXML == true){
			return new ProcessXMLData(dataStream,StripString.toCleanUp(sourceID),StripString.toCleanUp(description));
		}else{
			return new ProcessTextFile(dataStream,StripString.toCleanUp(sourceID),StripString.toCleanUp(delimiter),StripString.toCleanUp(description));
		}
	}
}
