package tagcloud;


public interface TagSimilarity {
   
   /**
    * @param t
    * @param s
    * @return double
    */
   public double similarity(Tag t, Tag s);
}
