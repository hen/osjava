package code316.charlotte;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class EncodingUtil {
    private static final BigInteger NEGATIVE_ONE = new BigInteger("-1");

    public static String encodingToString(Encoding ed) {
        StringBuffer me = new StringBuffer();
        StringBuffer legend = new StringBuffer();
        StringBuffer map = new StringBuffer();
        
        me.append("encoding name: ")
        .append(ed.getName())
        .append("\n")
        .append("length: ")
        .append(ed.getLength())
        .append(" bits\n");
        
        
        me.append("\nmap: \n");
        char marker = 'A';
        
        Iterator tor = ed.getFieldDefinitions().iterator();
        while (tor.hasNext()) {
            FieldDefinition td = (FieldDefinition) tor.next();
            
            int length = td.getWidth();
            for (int i = 0; i < length; i++) {
                map.append(marker);                
            }
            
            legend.append(marker)
            .append(" - ")
            .append(td.getName())
            .append(": ")
            .append(length)
            .append(" bits,")
            .append(" mutator expression: ")
            .append(td.getOperands())
            .append("\n");
            
            marker++;
            map.append("|");
        }
        
        int count = map.length();
        StringBuffer temp = new StringBuffer((count * 3) + 2);
        temp.append(" ");
        for (int i = 0; i < (count - 1); i++) {
            temp.append("-");
        }
        
        temp.append("\n|")
            .append(map)
            .append("\n");
        
        temp.append(" ");
        for (int i = 0; i < (count - 1); i++) {
            temp.append("-");
        }
        
        me.append(temp);
    
        me.append("\n\nlegend: \n").append(legend);
        
        return me.toString();
    }

    public static BigInteger getMaxValue(int numBits) {
        return new BigInteger("1").shiftLeft(numBits).add(NEGATIVE_ONE);        
    }
    

    public static Value[] expandFieldsToArray(Encoding encoding, BigInteger bits) {
        int count = encoding.getFieldCount();
        Value []vals = new Value[count];
    
        for (int i = 0; i < vals.length; i++) {
            FieldDefinition fd = encoding.getFieldDefinition(i);
            vals[i] = new Value();
                  
            vals[i].setFieldDefinition(fd);
            vals[i].setRaw(encoding.extractFieldValue(i, bits).longValue());
            vals[i].setExpanded(encoding.expandFieldValue(fd.getIndex(), bits));            
        }
    
        return vals;
    }
    
    /**
     * returns a Map of Doubles representing a set of expanded values
     * @param encoding
     * @param bits
     * @return
     */
    public static Map expandFields(Encoding encoding, BigInteger bits) {
        Value []vals = expandFieldsToArray(encoding, bits);        
        Map expanded = new HashMap(vals.length);
        
        for (int i = 0; i < vals.length; i++) {            
            expanded.put(vals[i].getFieldDefinition().getName(), new Double(vals[i].getExpandedAsDouble()));
        }
    
        return expanded;
    }
    
    /**
     * returns a Map of Doubles representing a set of extracted values
     * @param encoding
     * @param bits
     * @return
     */
    public static Map extractFields(Encoding encoding, BigInteger bits) {
        Value []vals = expandFieldsToArray(encoding, bits);        
        Map expanded = new HashMap(vals.length);
        
        for (int i = 0; i < vals.length; i++) {            
            expanded.put(vals[i].getFieldDefinition().getName(), new Double(vals[i].getRaw()));
        }
    
        return expanded;
    }        
    
}
