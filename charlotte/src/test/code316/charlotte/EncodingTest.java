package code316.charlotte;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class EncodingTest extends TestCase {
    public static final String ENCODING = 
                                "# test encoding\n"
                                + "angle=5, (x / maxValue) * 2.8\n" 
                                + "fails=2, [700, 705, 775.562, 800.1]\n"
                                + "level=1\n" 
                                + "\n"
                                + "# another comment \n"   
                                + "temp=13, (x / 8191) * 78\n"
                                + "door_open=1\n"
                                + "rms=4, (x * 15) + 3\n";

    
    private static Encoding getTestEncoding() throws Exception {
        return new Encoding(new ByteArrayInputStream(ENCODING.getBytes()));        
    }
    
    public void testUnpackValues() throws Exception {
        Encoding e = getTestEncoding();
        BigInteger bits = new BigInteger("10101101011101111000000101", 2);
        
        Value []vals = e.unpackValues(bits);
        
        assertEquals(6, vals.length);
        
        // check values
        assertEquals(21, vals[0].getRaw());
        assertEquals(2, vals[1].getRaw());
        assertEquals(1, vals[2].getRaw());
        assertEquals(3824, vals[3].getRaw());
        assertEquals(0, vals[4].getRaw());
        assertEquals(5, vals[5].getRaw());
        
        assertEquals(1.896, vals[0].getExpanded(), .001);
        assertEquals(775.562, vals[1].getExpanded(), .001);
        assertEquals(1, vals[2].getExpanded(), .001);
        assertEquals(36.414, vals[3].getExpanded(), .001);
        assertEquals(0, vals[4].getExpanded(), .001);
        assertEquals(78, vals[5].getExpanded(), .001);
    }
    
    
    public void testExtractAllValues() throws Exception {
        Encoding e = getTestEncoding();
        
        BigInteger bits = new BigInteger("765781").shiftLeft(4);
        Map values = e.extractAllValues(bits);
        
        assertEquals(new BigInteger("5"), (BigInteger) values.get("angle"));
        assertEquals(new BigInteger("3"), (BigInteger) values.get("fails"));
        assertEquals(new BigInteger("0"), (BigInteger) values.get("level"));
        assertEquals(new BigInteger("6058"), (BigInteger) values.get("temp"));
        assertEquals(new BigInteger("1"), (BigInteger) values.get("door_open"));
        assertEquals(new BigInteger("0"), (BigInteger) values.get("rms"));
    }
    
    
    public void testParseEncodingVersion() throws Exception {
        Encoding e = new Encoding();
        
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));
        
        assertEquals(26, e.getLength());
        assertEquals(6, e.getFieldDefinitions().size());
        
        System.out.println(Encoding.encodingToString(e));
    }    
    
    
    public void testExpandFieldValue() throws Exception {
        Encoding e = getTestEncoding();
        
        double val = e.expandFieldValue("rms", new BigInteger("11"));
        assertEquals(168, val, 1.0);      
        
        val = e.expandFieldValue("angle", new BigInteger("10101", 2).shiftLeft(21));
        assertEquals(1.896, val, 0.001);
        
        val = e.expandFieldValue("fails", new BigInteger("10", 2).shiftLeft(19));
        assertEquals(775.562, val, 1.0);
        
        val = e.expandFieldValue("level", new BigInteger("1", 2).shiftLeft(18));
        assertEquals(1, val, 0.0);
        
        val = e.expandFieldValue("temp", new BigInteger("1010011100110", 2).shiftLeft(5));
        assertEquals(50.946, val, 0.001);

        val = e.expandFieldValue("door_open", new BigInteger("0", 2).shiftLeft(4));
        assertEquals(0, val, 0.0);
        
        val = e.expandFieldValue("rms", new BigInteger("1001", 2));
        assertEquals(138, val, 0.0);        
    }
    
    public void testParseNames() throws Exception {
        Encoding e = new Encoding();
        
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));
        
        List names = e.getNames();
        assertEquals(6, names.size());
        assertEquals("angle", names.get(0));
        assertEquals("fails", names.get(1));
        assertEquals("level", names.get(2));
        assertEquals("temp", names.get(3));
        assertEquals("door_open", names.get(4));
        assertEquals("rms", names.get(5));
    }

    
    
    public void testMaxValue() throws Exception {
        Encoding e = getTestEncoding();

        assertEquals(new BigInteger("11111111111111111111111111", 2), e.getMaxValue());        
    }
    /*    
    public void testExtractFieldsByIndex() throws IOException {
        Encoding e = new Encoding();
    
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));

        BigInteger bits = new BigInteger("10110100110100001001", 2);       
        
        assertEquals(new BigInteger("5"), e.extractFieldValue(0, bits));
        assertEquals(new BigInteger("2"), e.extractFieldValue(1, bits));
        assertEquals(new BigInteger("1"), e.extractFieldValue(2, bits));        
        assertEquals(new BigInteger("1668"), e.extractFieldValue(3, bits));        
        assertEquals(new BigInteger("1"), e.extractFieldValue(4, bits));
    }
    
    public void testExtractFieldsByName() throws IOException {
        Encoding e = new Encoding();
    
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));

        BigInteger bits = new BigInteger("10110100110100001001", 2);       

        assertEquals(new BigInteger("5"), e.extractFieldValue("angle", bits));
        assertEquals(new BigInteger("2"), e.extractFieldValue("fails", bits));
        assertEquals(new BigInteger("1"), e.extractFieldValue("level", bits));        
        assertEquals(new BigInteger("1668"), e.extractFieldValue("temp", bits));        
        assertEquals(new BigInteger("1"), e.extractFieldValue("door_open", bits));
    }
    
    public void testUnpackFieldsByName() throws IOException {
        Encoding e = new Encoding();
    
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));

        BigInteger bits = new BigInteger("10110100110100001001", 2);       

        float result = e.unpackFieldValue(3, bits).floatValue();

        assertEquals(new BigDecimal("16").floatValue(), result, 1);        
    }
    
    
    public void testDumpValues() throws Exception {
        Encoding e = new Encoding();
    
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));
        
        BigInteger bi = new BigInteger("10110100110100001001", 2);
        System.out.println("dump: " + e.dumpValues(bi));
    }
    
    
    
    public void testEncode() throws Exception {
        Encoding e = new Encoding();
    
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));
        
        HashMap map = new HashMap();
        
        map.put("angle", new BigInteger("3"));
        map.put("fails", new BigInteger("2"));
        map.put("level", new BigInteger("1"));
        map.put("temp", new BigInteger("3336"));
        map.put("door_open", new BigInteger("1"));
        
        
        BigInteger encoded = e.encode(map);
        
        assertEquals(new BigInteger("481809"), encoded);        
    }


    public void testGetByName() throws Exception {
        Encoding e = new Encoding();
        Encoding e2 = new Encoding();
    
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));
        e2.parse(new ByteArrayInputStream(ENCODING.getBytes()));
        
        assertEquals(e.getField("temp").getName(), "temp");
        assertEquals(null, e.getField("fairy"));

        assertEquals(e2.getField("temp").getName(), "temp");
        assertEquals(null, e2.getField("fairy"));
    }
    
    public void testGetByIndex() throws Exception {
        Encoding e = new Encoding();
        Encoding e2 = new Encoding();
    
        e.parse(new ByteArrayInputStream(ENCODING.getBytes()));
        e2.parse(new ByteArrayInputStream(ENCODING.getBytes()));
        
        assertEquals(e.getField(3).getName(), "temp");
        assertEquals(null, e.getField(10));
        assertEquals(null, e.getField(-1));
        
        assertEquals(e2.getField(3).getName(), "temp");
        assertEquals(null, e2.getField(10));
        assertEquals(null, e2.getField(-1));
    }
    

*/
}

