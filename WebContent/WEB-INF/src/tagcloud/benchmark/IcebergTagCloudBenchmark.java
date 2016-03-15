package tagcloud.benchmark;

import tagcloud.*;
import olapdbtools.*;
import util.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.Hashtable;


/**
 * The goal of this class is to verify experimentally that an
 * iceberg is enough to generate tag clouds.
 * 
 * The experiments are dependent on the parameters LIMIT and TAGCLOUDSIZE.
 * 
 * @author lemire
 *
 */
public class IcebergTagCloudBenchmark {
	public static final int LIMIT = 150;// size of iceberg
	//public static final int[] LIMITs = {150,600,1200,4800,19600};// size of iceberg
	public static final int TAGCLOUDSIZE = 50;// how many tags to display?
	//public static final int[] TAGCLOUDSIZEs = {50,100,150,200};// how many tags to display?
	public static final String defaultID = "swivel";
	public static final String[] defaultDims = {"State","MiddleInitial","Surname","City"};
	//public static final int[] LIMITs = {200,300,400,600,800,1200,4800,10000,15000,19200};// size of iceberg
	public static final int[] LIMITs = {20,40,60,80,120,480,1000,1500,1920};// size of iceberg
	public static final int[] TAGCLOUDSIZEs = {50,100,150,200};// how many tags to display?
	/*public static final String defaultID = "swivel";
	public static final String[] defaultDims = {"State","Surname","City"};*/
	
	/*public static final String defaultID = "usincome";
	public static final String[] defaultDims = {"PARENT","AAGE","CAPLOSS","HHDFMX"};*/
	public static final boolean experiments=true;
	
	public static void main(String[] args) throws SQLException, IOException,ClassNotFoundException {
		if(!unittesting()) return;
		File f = new File("WebContent/WEB-INF/conf/tagcloud.properties");
		if (!experiments) System.out.println("# reading config file: "+ f.getAbsolutePath());
		DbToolsFactory dbfact = new DbToolsFactory(new FileInputStream(f));
		String databaseID;
		String[] dims;
		if(args.length < 1) {
			System.out.println("# No database ID as a parameter");
			System.out.println("# Defaulting to "+defaultID);
			databaseID = defaultID;
			//return;
		}	else {
			databaseID = args[0];
		}
		if(args.length - 1 < 1) {
			System.out.println("# No dimensions specified");
			System.out.println("# Switching to default");
			dims =defaultDims;
			//return;
		}	else {
			if(args.length - 1 == 1)
				System.out.println("# You specified only one dimension! Are you sure?");
			dims = new String[args.length - 1];
			for(int k = 1; k < args.length; ++k) dims[k]= args[k+1];
		}
		//for(int limit:LIMITs)
			//for(int tagcloudsize : TAGCLOUDSIZEs)
				//benchmark(databaseID, dims, dbfact.newInstance(), limit,tagcloudsize);
		//benchmark(databaseID, dims, dbfact.newInstance(), LIMIT,TAGCLOUDSIZE);
		benchmark(databaseID, dims, dbfact.newInstance(),LIMITs,TAGCLOUDSIZEs);
		ConnectionPool.closeAll();
		if (experiments) System.out.println("# End");
	}
	
	public static void benchmark(String databaseID, String[] dims, DbTools db, int limit, int tagcloudsize) throws SQLException {
		// first, I am going to create the iceberg in a temporary table
		if (!experiments) System.out.println("# Start benchmarking");
		if (!experiments) System.out.println("# Computing cardinalities");
		int[] cardinality= new int[dims.length];
		db.openConnection();
		int i=0;
		for (String att : dims){
			db.queryDataBase("select count(Distinct "+att+") from "+databaseID);
			cardinality[i++] =(new Integer(db.getResultsAsString()[0]));
			System.out.println("#CD"+ i +"="+cardinality[i-1]);
		}
		db.alterDataBase("drop table IF EXISTS iceberg"); // in case it is around
		db.alterDataBase("create temporary table iceberg as select count(*) as measure,"+StringArrays.toString(dims)+" from "+databaseID+" group by "+StringArrays.toString(dims)+" order by measure desc limit "+limit);
		System.out.println();
		if (experiments) System.out.println("# limit tagcloudsize nbdims nbtagdims cardinality exac(ms) iceberg(ms) entropy(exact) falseNegative falsePositive");
		for (int projection = 0; projection <dims.length; ++projection) {
		  String tagdim = dims[projection];
		  if (!experiments) System.out.println("# projecting on "+ tagdim);
		  long before1 = System.currentTimeMillis();
		  if(!db.hasResults("select count(*) as measure,"+tagdim+" from "+databaseID+" group by "+tagdim+ " order by measure desc limit "+tagcloudsize))
			  System.err.println("# Empty projection? Bug!?!");
		  Vector<Tag> exact = convertToTags(db.getResultsAsHashTable(),tagdim);
		  long after1 = System.currentTimeMillis();
		  long before2 = System.currentTimeMillis();
		  if(!db.hasResults("select sum(measure) as measure,"+tagdim+" from iceberg group by "+tagdim+ " order by measure desc limit "+tagcloudsize))
			  System.err.println("# Empty projection? Bug!?!");
		  Vector<Tag> icebergapprox = convertToTags(db.getResultsAsHashTable(),tagdim);
		  long after2 = System.currentTimeMillis();
		  if (!experiments) System.out.println("# exact processing took "+(after1-before1)/1000.0+"s, iceberg processing took "+(after2-before2)/1000.0+"s");
		  if (!experiments) System.out.println("# False negative index (1 is bad, 0 is good) : "+falseNegativeIndex(exact,icebergapprox));
		  if (!experiments) System.out.println("# False positive index (1 is bad, 0 is good) : "+falsePositiveIndex(exact,icebergapprox));

		  if (!experiments) System.out.println("# Tag cloud entropy (large entropy => hard to approximate) : "+entropy(exact));
		  if (experiments) System.out.println(limit +" "+tagcloudsize +" "+ defaultDims.length+" "+ dims.length+" "+cardinality[projection]+" " +(after1-before1)/1000.0+" "+ (after2-before2)/1000.0+ " "+entropy(exact)+" "+falseNegativeIndex(exact,icebergapprox)+" "+falsePositiveIndex(exact,icebergapprox));
		}
		if (!experiments) System.out.println("# Conjecture: often, when the entropy is high, we do a good job at approximating the tag cloud.");
	}
	
	public static void benchmark(String databaseID, String[] dims, DbTools db, int[] limits, int[] tagcloudsizes) throws SQLException {
		// first, I am going to create the iceberg in a temporary table
		if (!experiments) System.out.println("# Start benchmarking");
		if (!experiments) System.out.println("# Computing cardinalities");
		int[] cardinality= new int[dims.length];
		db.openConnection();
		int i=0;
		for (String att : dims){
			db.queryDataBase("select count(Distinct "+att+") from "+databaseID);
			cardinality[i++] =(new Integer(db.getResultsAsString()[0]));
			System.out.println("#CD"+ i +"="+cardinality[i-1]);
		}
		
		for (int projection = 0; projection <dims.length; ++projection) {
			String tagdim = dims[projection];
			System.out.println();
			if (experiments) System.out.println("# projecting on "+ tagdim);
			if (experiments) System.out.println("# limit tagcloudsize nbdims nbtagdims cardinality exac(ms) iceberg(ms) entropy(exact) falseNegative falsePositive");
			for(int limit : limits){
		  		db.alterDataBase("drop table IF EXISTS iceberg"); // in case it is around
				db.alterDataBase("create temporary table iceberg as select count(*) as measure,"+StringArrays.toString(dims)+" from "+databaseID+" group by "+StringArrays.toString(dims)+" order by measure desc limit "+limit);
				for(int tagcloudsize : tagcloudsizes){
					long before1 = System.currentTimeMillis();
					if(!db.hasResults("select count(*) as measure,"+tagdim+" from "+databaseID+" group by "+tagdim+ " order by measure desc limit "+tagcloudsize))
						System.err.println("# Empty projection? Bug!?!");
					Vector<Tag> exact = convertToTags(db.getResultsAsHashTable(),tagdim);
					long after1 = System.currentTimeMillis();
					long before2 = System.currentTimeMillis();
					if(!db.hasResults("select sum(measure) as measure,"+tagdim+" from iceberg group by "+tagdim+ " order by measure desc limit "+tagcloudsize))
						System.err.println("# Empty projection? Bug!?!");
					Vector<Tag> icebergapprox = convertToTags(db.getResultsAsHashTable(),tagdim);
					long after2 = System.currentTimeMillis();
					if (!experiments) System.out.println("# exact processing took "+(after1-before1)/1000.0+"s, iceberg processing took "+(after2-before2)/1000.0+"s");
					if (!experiments) System.out.println("# False negative index (1 is bad, 0 is good) : "+falseNegativeIndex(exact,icebergapprox));
					if (!experiments) System.out.println("# False positive index (1 is bad, 0 is good) : "+falsePositiveIndex(exact,icebergapprox));
					if (!experiments) System.out.println("# Tag cloud entropy (large entropy => hard to approximate) : "+entropy(exact));
					if (experiments) System.out.println(limit +" "+tagcloudsize +" "+ defaultDims.length+" "+dims.length+" "+cardinality[projection]+" " +(after1-before1)/1000.0+" "+ (after2-before2)/1000.0+ " "+entropy(exact)+" "+falseNegativeIndex(exact,icebergapprox)+" "+falsePositiveIndex(exact,icebergapprox));
				}
		  	}
		}
		if (!experiments) System.out.println("# Conjecture: often, when the entropy is high, we do a good job at approximating the tag cloud.");
	}

	
	public static Vector<Tag> convertToTags(Hashtable<String,String[]> results, String dim) {
		Vector<Tag> x = new Vector<Tag>();
		String[] m = results.get("measure");
		String[] t = results.get(dim);
		for(int k = 0; k < m.length;++k)
			x.add(new Tag(t[k],Integer.parseInt(m[k])));
		return x;
	}


	public static boolean isTagIncluded(Tag tt, Vector<Tag> x) {
		for(Tag t: x)
			if(t.getText().equals(tt.getText())) return true;
		return false;
	}
	
	/** we seek the largest tag in approx which does not appear in exact
	* and we divide it by the largest tag in approx
	* the value will range between 0 and 1 assuming positive weights
	* 1 means that things are very bad, 0 means that it is pretty good
	*/
	public static double falsePositiveIndex(Vector<Tag> exact, Vector<Tag> approx) {
		double maxapprox = 0.0, maxapproxnotinexact = 0.0;
		for(Tag t : approx) {
			if(t.getWeight() < 0.0)
				System.err.println("Error, negative weight!");
			if(t.getWeight() > maxapprox) maxapprox = t.getWeight();
			if(t.getWeight() > maxapproxnotinexact) {
				if(!isTagIncluded(t,exact))
					maxapproxnotinexact = t.getWeight();
			}
		}
		if(maxapprox <= 0.001) return 0.0;
		return maxapproxnotinexact/maxapprox;
	}
	
	/** we seek the largest tag in exact which does not appear in approx
	* and we divide it by the largest tag in exact
	* the value will range between 0 and 1  assuming positive weights
	* 1 means that things are very bad, 0 means that it is pretty good
	*/
	public static double falseNegativeIndex(Vector<Tag> exact, Vector<Tag> approx) {
		return falsePositiveIndex(approx, exact);
	}
	
	public static boolean unittesting() {
		Vector<Tag> set1 = new Vector<Tag>();
		Vector<Tag> set2 = new Vector<Tag>();
		Vector<Tag> set3 = new Vector<Tag>();
		set1.add(new Tag("a",45));
		set1.add(new Tag("b",15));
		set1.add(new Tag("c",40));
		set1.add(new Tag("d",44));
		set2.add(new Tag("a",45));
		set2.add(new Tag("bb",440));
		set2.add(new Tag("c",40));
		set2.add(new Tag("d",44));
		set3.add(new Tag("b",12));
		set3.add(new Tag("c",1));
		set3.add(new Tag("d",13));
		boolean ok = true;
		if(falseNegativeIndex(set1,set3)!= 1.0) {
			System.err.println("# set3 should have a terrible false negative, got "+falseNegativeIndex(set1,set3));
			ok = false;
		}
		if(falsePositiveIndex(set1,set2)!= 1.0) {
			System.err.println("# set2 should have a terrible false positive, got "+falsePositiveIndex(set1,set3));
			ok = false;
		}
		return ok;

	}
	public static double largest(Vector<Tag> x) {
		double maxv = Double.MIN_VALUE;
		for(Tag t : x)
			if(t.getWeight() > maxv) maxv = t.getWeight();
		return maxv;
	}

	public static double smallest(Vector<Tag> x) {
		double minv = Double.MAX_VALUE;
		for(Tag t : x)
			if(t.getWeight() < minv) minv = t.getWeight();
		return minv;
	}
	

	public static double sum(Vector<Tag> x) {
		double s = 0.0;
		for(Tag t : x) {
			if(t.getWeight() < 0) System.err.println("# Negative weights?");
			s+= t.getWeight();
		}
		return s;
	}

	public static double entropy(Vector<Tag> x) {
		double s = sum(x);
		double entropy = 0.0;
		for(Tag t : x) {
			if(t.getWeight() < 0) System.err.println("# Negative weights?");
			entropy += - t.getWeight() / s * Math.log(t.getWeight() / s)/Math.log(2);
		}
		return entropy;
	}
}
