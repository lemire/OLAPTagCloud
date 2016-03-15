package util;

public class Triple {//<T,U,V>
    public String fst;
    public String[] snd;
    public Class thd;
    public Triple(String a, String[] b, Class c) { fst = a; snd = b; thd = c;}
    public String toString() { return "('" + fst + "','" + snd + "',"+thd+")";}
    //public T first() {return fst;}
    //public U second() {return snd;}
    //public V third() {return thd;}
    public int hashCode() {
    	//System.out.println("calling hashcode on "+this);
    	return fst.hashCode() ^ thd.hashCode();}
    public boolean equals (  Object oo) {
    	//System.out.println("calling equal on "+this);
        if (oo instanceof Triple) { //un-pretty
        	Triple o = (Triple) oo;
        	//System.out.println("comparing "+this+" with "+oo);
        	if(!o.fst.equals(this.fst)) {
        		//System.out.println("names are different");
        		return false;
        	}
        	if(!o.thd.equals(this.thd)) {
        		//System.out.println("classes are different");
        		return false;
        	}
        	if(o.snd.length != this.snd.length) {
        		//System.out.println("# dims are different");
        		return false;
        	}
        	for(int k = 0; k < this.snd.length; ++k)
        		if(!o.snd[k].equals(this.snd[k])) {
        			//System.out.println(o.snd[k]+" != "+snd[k]);
        			return false;
        		}
        	return true;
            //return fst.equals(o.fst) &&
            //    snd.equals(o.snd) && thd.equals(o.thd);
        } else return false;
    }
}
