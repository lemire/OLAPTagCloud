package util;

public enum SimilarityMeasure {
	  COSINE   { public double similarity(double scalarproduct, double sumofsquares1, double sumofsquares2) {
			double denominator = Math.sqrt(sumofsquares1 * sumofsquares2);
			if(Math.abs(denominator) < 0.000001) return 0.0;
			return scalarproduct/ denominator; 
      }},
	  TANIMOTO { public double similarity(double scalarproduct, double sumofsquares1,double sumofsquares2) {
			double denominator = sumofsquares1 + sumofsquares2 - scalarproduct;
			if(Math.abs(denominator) < 0.000001) return 0.0;
			return scalarproduct/ denominator; 
      } };
	  // Do arithmetic op represented by this constant
	  public abstract double similarity(double scalarproduct, double sumofsquares1,double sumofsquares2);
}
