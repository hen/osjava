package code316.charlotte;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;

import junit.framework.TestCase;

public class EncodingTest extends TestCase {
    public static final int ENCODING_LENGTH = 32;
    public static final int VALUE_COUNT = 7;
    public static final String ENCODING = 
                                "# test encoding\n"
                                + "name: Test Encoding\t \n" 
                                + "angle=5 , (x / maxValue) * 2.8\n" 
                                + "fails=2, [700, 705, 775.562, 800.1]\n"
                                + "level=1 \n" 
                                + "\n"
                                + "# another comment \n"   
                                + "temp=13, (x / 8191) * 78\n"
                                + "door_open=1\n"
                                + "rms=4, (x * 15) + 3\n"
                                + "velocity=6, (${angle} / x * 4.312) + ${level}";

    
    private static Encoding getTestEncoding() throws Exception {
        Parser parser = new Parser();
        return parser.parse(new ByteArrayInputStream(ENCODING.getBytes()));        
    }
    
    public void testUnpackFields() throws Exception {
        Encoding e = getTestEncoding();
        BigInteger bits = new BigInteger("10101101011101111000000101101101", 2);
        
        Value []vals = e.unpackFields(bits);
        
        assertEquals(7, vals.length);
        
        // check values
        assertEquals(21, vals[0].getRaw());
        assertEquals(2, vals[1].getRaw());
        assertEquals(1, vals[2].getRaw());
        assertEquals(3824, vals[3].getRaw());
        assertEquals(0, vals[4].getRaw());
        assertEquals(5, vals[5].getRaw());
        assertEquals(45, vals[6].getRaw());
        
        assertEquals(1.896, vals[0].getExpanded(), .001);
        assertEquals(775.562, vals[1].getExpanded(), .001);
        assertEquals(1, vals[2].getExpanded(), 0);
        assertEquals(36.414, vals[3].getExpanded(), .001);
        assertEquals(0, vals[4].getExpanded(), 0);
        assertEquals(78, vals[5].getExpanded(), 0);
        assertEquals(1.1816, vals[6].getExpanded(), .001);
    }
    
    
    
    
    public void testParseEncoding() throws Exception {
        Encoding e = getTestEncoding();
        
        assertEquals("Test Encoding", e.getName());
        assertEquals(ENCODING_LENGTH, e.getLength());
        assertEquals(VALUE_COUNT, e.getFieldCount());
        
        
        System.out.println(DefaultEncoding.encodingToString(e));
    }    
    
    
    
    public void testMaxValue() throws Exception {
        Encoding e = getTestEncoding();

        assertEquals(new BigInteger("11111111111111111111111111111111", 2), e.getMaxValue());        
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

