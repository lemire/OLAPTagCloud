package tagcloud.optimization.benchmark;

import java.util.*;
import config.ConfigFileLocations;
import processdata.*;
import tagcloud.*;
import tagcloud.optimization.*;
import olapdbtools.*;
import tagcloudfunction.*;
import util.*;
import java.io.*;

/**
 * The goal of this class is to
 * see how fast are our tag cloud optimization
 * techniques.
 * 
 * @author lemire
 *
 */
public class BenchmarkSpeed implements ConfigFileLocations {
	public static final String defaultID = "swivel1";
	public static int TIMES = 100;
	
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
		
		String outfilename="test_results/speedtest_"+id+"_"+new Date().getTime()+".txt";
		FileOutputStream fos = new FileOutputStream(outfilename,true);
		System.setOut(new PrintStream(fos,true));
		String[] mcs = {"PWMC10","PWMC100","PWMC1000"};///{"EC1"};//,"MC10","MC20","MC30","MC40","MC50","MC60","MC70","MC80","MC90","MC100"};
		String columns = "# cuboidsize tagcloudsize computesimilaritymatrix timeNN  ";
		for(int k = 0; k < mcs.length;++k)
			columns+=  " time"+mcs[k]+ "  ";
		System.out.println("# "+columns);
		System.out.println("# we test only COSINE ");
		System.out.println("# repetition of the run is set at "+TIMES);
		System.out.println("# processing 'data cube' "+id);
		String[] setofallfields = cf.getAllDimensions(id);
		String[] setofalldimensions = new String[setofallfields.length];
		String[] setoffirstdimension = new String[1];
		for (int k=0; k< setofallfields.length; ++k)//setofalldimensions.length
			setofalldimensions[k]=setofallfields[k].split(",")[0];
		setoffirstdimension[0] = setofalldimensions[0];
		util.SimilarityMeasure sm = util.SimilarityMeasure.COSINE;//, util.SimilarityMeasure.TANIMOTO};
		Cuboid c = cf.getCuboid(id, setofalldimensions);
		for(String tagclouddim :  setoffirstdimension) {
			System.out.println("# displaying tags on dimension "+ tagclouddim);
			for(String simdim : setofalldimensions) {
				if(!simdim.equals(tagclouddim)) {
					System.out.println("# Computing similarity on dimension "+ simdim);
					ProjectOnDimensions p =  new ProjectOnDimensions(StringArrays.toArray(tagclouddim),StringArrays.toArray(simdim) );
					TagCloudModel tcm = c.getTagCloudModel(p);
					Vector<Double> data = new Vector<Double>();
					data.add(new Double(c.getSize()));
					TagCloudView tcv = tcm.getView();
					tcv.normalizeWeights();
					Vector<Tag>l = (Vector<Tag>) tcv.getTags();
					data.add(new Double(l.size()));
					long before, after;
					before = System.currentTimeMillis();
					for(int k = 0; k < TIMES; ++k) {
						tcm.getSimilarity(sm);
					}
					after = System.currentTimeMillis();
					data.add(new Double((after-before)/1000.0));
					TagSimilarity ts = tcm.getSimilarity(sm);
					NearestNeighbour NN = new NearestNeighbour(ts);
					before = System.currentTimeMillis();
					for(int k = 0; k < TIMES; ++k) {
						NN.optimize((Vector<Tag>)l.clone());
					}
					after = System.currentTimeMillis();
					data.add(new Double((after-before)/1000.0));
					PointWiseMonteCarlo PWMC10 = new PointWiseMonteCarlo(ts,10);
					PointWiseMonteCarlo PWMC100 = new PointWiseMonteCarlo(ts,100);
					PointWiseMonteCarlo PWMC1000 = new PointWiseMonteCarlo(ts,1000);
					before = System.currentTimeMillis();
					for(int k = 0; k < TIMES; ++k) {
						PWMC10.optimize((Vector<Tag>)l.clone());
					}
					after = System.currentTimeMillis();
					data.add(new Double((after-before)/1000.0));
					before = System.currentTimeMillis();
					for(int k = 0; k < TIMES; ++k) {
						PWMC100.optimize((Vector<Tag>)l.clone());
					}
					after = System.currentTimeMillis();
					data.add(new Double((after-before)/1000.0));
					before = System.currentTimeMillis();
					for(int k = 0; k < TIMES; ++k) {
						PWMC1000.optimize((Vector<Tag>)l.clone());
					}
					after = System.currentTimeMillis();
					data.add(new Double((after-before)/1000.0));
					//
					for (Double d : data)
						System.out.print(d+" ");
					System.out.println();
				}
			}
		}
		fos.close();
		System.err.println("test results are saved in: "+new File(outfilename).getAbsolutePath().toString());
	}
}