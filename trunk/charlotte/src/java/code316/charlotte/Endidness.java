package code316.charlotte;


public interface Endidness {
    public static final class Big implements Endidness{}   
    public static final class Little implements Endidness{}
    
    public static final Endidness BIG = new Big();
    public static final Endidness LITTLE = new Little();
}
