package code316.charlotte;
import junit.framework.TestCase;

public class BitSpinnerTest extends TestCase {

    public BitSpinnerTest(String arg0) {
        super(arg0);
    }
    
    public void testConstruction() throws Exception {
    	EncodingDefinition ed = new DefaultEncodingDefinition();
    	
    	ed.add(new IntegerTypeDefinition("version", 4));	
    	ed.add(new IntegerTypeDefinition("raining", 1));
    	ed.add(new FloatTypeDefinition("temperature", "8.2"));
    	
    	System.out.println(ed);
    }
    
    public void testParser() throws Exception {
    	String file = "encoding pathMonitorData7 {\n"
        				+ "   encodingId int(4);\n"
        				+ "   temperature float(8.2);\n"
        				+ "   pathClear flag;\n"
        				+ "   animalsPassedLocation int(8);\n"
        				+ "}\n";

    }
}
