package tagcloud.optimization.benchmark;

import java.util.Date;
import config.ConfigFileLocations;
import processdata.*;
import tagcloud.*;
import tagcloud.optimization.NearestNeighbour;
import olapdbtools.*;
import tagcloudfunction.*;
import util.*;
import java.io.*;

/**
 * 
 * 
 * @author kamel
 *
 */
public class SimDisplay implements ConfigFileLocations {
	public static final String defaultID = "swivel1";
	
	public static void main(String[] args) throws Exception {
		File f = new File("WebContent/WEB-INF/conf/tagcloud.properties");
		
		System.out.println("reading config file: "+ f.getAbsolutePath());
		DbToolsFactory dbfact = new DbToolsFactory(new FileInputStream(f));
		CuboidFactory cf = new CuboidFactory(dbfact);
		String id;
		if(args.length < 1) {
			System.out.println("Default to database ID "+defaultID);
			id = defaultID;
		} else 
			id = args[0];
		System.out.println("Start benchmarking...");
		
		String outfilename="test_results/sim_matrix_"+id+"_"+new Date().getTime()+".txt";
		FileOutputStream fos = new FileOutputStream(outfilename,true);
		System.setOut(new PrintStream(fos,true));
		System.out.println("# processing 'data cube' "+id);
		String[] setofallfields = cf.getAllDimensions(id);
		String[] setofalldimensions = new String[setofallfields.length];
		
		
		for (int k=0; k< setofallfields.length; ++k)
			setofalldimensions[k]=setofallfields[k].split(",")[0];
		
		Cuboid c = cf.getCuboid(id, setofalldimensions);
		for(String tagclouddim :  setofalldimensions) {
			System.out.println("# displaying tags on dimension "+ tagclouddim);
			for(String simdim : setofalldimensions) {
				if(!simdim.equals(tagclouddim)) {
					System.out.println("# Computing similarity on dimension "+ simdim);
					ProjectOnDimensions p =  new ProjectOnDimensions(StringArrays.toArray(tagclouddim),StringArrays.toArray(simdim));
					TagCloudModel tcm = c.getTagCloudModel(p);
					TagCloudView tcv = tcm.getView();
					NearestNeighbour nn = new NearestNeighbour(tcv.getTagCloudModel().getSimilarity(util.SimilarityMeasure.COSINE));
					nn.printSimilarityMatrix(tcv.getTags());
				}
			}
		}
		fos.close();
		System.err.println("test results are saved in: "+new File(outfilename).getAbsolutePath().toString());
	}
}