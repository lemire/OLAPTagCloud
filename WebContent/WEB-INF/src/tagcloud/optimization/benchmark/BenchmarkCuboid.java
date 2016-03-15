package tagcloud.optimization.benchmark;

import java.util.Date;
import java.util.Vector;
import config.ConfigFileLocations;
import processdata.*;
import tagcloud.*;
import tagcloud.optimization.NearestNeighbour;
import olapdbtools.*;
import tagcloudfunction.*;
import util.*;
import java.io.*;

/**
 * The goal of this class is to pick so data already in the 
 * database and to benchmark tag cloud optimization over it.
 * 
 * Ideas on what to benchmark: 
 * 1) Je retiens ta trouvaille http://www.swivel.com/data_sets/show/1002247
 * On peut faire plein de choses dont l'aggr√©gation sur les noms, en utilisant les villes pour
 *  la similarit√©. J'ai r√©cup√©r√© une copie du fichier:
 *  http://databasedata.googlepages.com/1002247.csv
 *  
 *  2) J'ai r√©cup√©r√© des donn√©es chez Amazon en utilisant leur service web. 
 *  J'ai une liste d'artistes et chaque artiste a plusieurs albums. On peut donc, 
 *  par exemple, faire une projection sur l'artiste et calculer la similarit√© sur le "average rating".
 *  Le fichier est ici:
 *  http://databasedata.googlepages.com/amazonresults.zip
 * 
 * @author lemire
 *
 */
public class BenchmarkCuboid implements ConfigFileLocations {
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
		
		String outfilename="test_results/test_"+id+"_"+new Date().getTime()+".txt";
		FileOutputStream fos = new FileOutputStream(outfilename,true);
		System.setOut(new PrintStream(fos,true));
		String[] mcs = {"PWMC10","PWMC100","PWMC1000"};///{"EC1"};//,"MC10","MC20","MC30","MC40","MC50","MC60","MC70","MC80","MC90","MC100"};
		String columns = "# simmatrixdensity badness time badnessNN timeNN  ";
		for(int k = 0; k < mcs.length;++k)
			columns+= "badness"+mcs[k]+ " time"+mcs[k]+ "  ";
		System.out.println("# "+columns);
		System.out.println("# we test both COSINE and TANIMOTO similarity measures");
		System.out.println("# processing 'data cube' "+id);
		String[] setofallfields = cf.getAllDimensions(id);
		String[] setofalldimensions = new String[setofallfields.length];
		
		
		for (int k=0; k< setofallfields.length; ++k)//setofalldimensions.length
			setofalldimensions[k]=setofallfields[k].split(",")[0];

		util.SimilarityMeasure[] sms = {util.SimilarityMeasure.COSINE, util.SimilarityMeasure.TANIMOTO};
		Cuboid c = cf.getCuboid(id, setofalldimensions);
		for(String tagclouddim :  setofalldimensions) {
			System.out.println("# displaying tags on dimension "+ tagclouddim);
			for(String simdim : setofalldimensions) {
				if(!simdim.equals(tagclouddim)) {
					// now the juice!
					System.out.println("# Computing similarity on dimension "+ simdim);
					//Rollup r =  new Rollup(StringArrays.toArray(tagclouddim),StringArrays.toArray(simdim) );
					ProjectOnDimensions p =  new ProjectOnDimensions(StringArrays.toArray(tagclouddim),StringArrays.toArray(simdim) );
					TagCloudModel tcm = c.getTagCloudModel(p);
					//
					Vector<Double> data = new Vector<Double>();
					//
					for(util.SimilarityMeasure sm : sms) {
						{
							TagCloudView tcv = tcm.getView();
							NearestNeighbour nn = 	new NearestNeighbour(tcv.getTagCloudModel().getSimilarity(sm));
							data.add(new Double(nn.similarityMatrixDensity(tcv.getTags())));
						}
						{
							TagCloudView tcv = tcm.getView();
							tcv.normalizeWeights();
							data.add(new Double(tcv.clusteringBadness(sm)));
							data.add(new Double(0.0));
							tcv = null;
						}
						//
						{
							TagCloudView tcv2 = tcm.getView();
							tcv2.normalizeWeights();
							long before = System.currentTimeMillis();
							tcv2.optimize("NN",sm);
							long after = System.currentTimeMillis();
							data.add(new Double(tcv2.clusteringBadness(sm)));
							data.add(new Double((after-before)/1000.0));
							tcv2 = null;
						}
						for(int k = 0; k < mcs.length;++k)
						{
							//System.out.println(mcs[k]);
							TagCloudView tcv3 = tcm.getView();
							tcv3.normalizeWeights();
							long before = System.currentTimeMillis();
							tcv3.optimize(mcs[k],sm);
							long after = System.currentTimeMillis();
							data.add(new Double(tcv3.clusteringBadness(sm)));
							data.add(new Double((after-before)/1000.0));
							tcv3 = null;
						}
					}
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