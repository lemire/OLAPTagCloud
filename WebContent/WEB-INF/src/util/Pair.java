package util;

public class Pair<T,U> {
    public T fst;
    public U snd;
    public Pair(T a, U b) { fst = a; snd = b;}
    public String toString() { return "('" + fst + "','" + snd + "')";}
    public T first() {return fst;}
    public U second() {return snd;}
    public int hashCode() {
        return fst.hashCode() ^ snd.hashCode();}
    public boolean equals (  Object oo) {
        if (oo instanceof Pair) { //un-pretty
            Pair<T,U> o = (Pair<T,U>) oo;
            return fst.equals(o.fst) &&
                snd.equals(o.snd);
        } else return false;
    }
}
