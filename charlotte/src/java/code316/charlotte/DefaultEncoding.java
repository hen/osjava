package code316.charlotte;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.nfunk.jep.JEP;


import code316.core.PropertiesUtil;


public class DefaultEncoding implements Encoding {
    private static final BigInteger NEGATIVE_ONE = new BigInteger("-1");
    private ArrayList fieldNames = new ArrayList();
    private int bitCount;
    private ArrayList fieldDefinitions = new ArrayList();
    private String name = "UNNAMED";
    
    public DefaultEncoding() {}
    
    private List getNames() {
        return (List) this.fieldNames.clone();        
    }
    
    public int getLength() {
        return this.bitCount;
    }
    

    public List getFieldDefinitions() {
        return (List) this.fieldDefinitions.clone();
    }
    
    private BigInteger extractFieldValue(String name, BigInteger bi) {
        int count = 0;
        Iterator tor = this.fieldNames.iterator();
        while (tor.hasNext()) {
            if (tor.next().equals(name)) {
                return extractFieldValue(count, bi);
            }
            count++;
        }
        
        throw new IllegalArgumentException("field named: '" + name + "' not found");
    }
    
    private BigInteger extractFieldValue(int i, BigInteger bi) {
        DefaultDefinition fd = (DefaultDefinition) this.fieldDefinitions.get(i);
        return bi.shiftRight(fd.getOffset()).and(getMaxValue(fd.getWidth()));
    }
    
    
    public BigInteger getMaxValue() {
        return DefaultEncoding.getMaxValue(getLength());            
    }
    
    public static BigInteger getMaxValue(int numBits) {
        return new BigInteger("1").shiftLeft(numBits).add(NEGATIVE_ONE);        
    }
    
    private String dumpValues(BigInteger bi) {
        int count = this.fieldDefinitions.size();
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            DefaultDefinition fd = (DefaultDefinition) this.fieldDefinitions.get(i);
            sb.append(fd.getName())
                .append("=")
                .append(extractFieldValue(i, bi))
                .append("\n");
        }
        
        return sb.toString();
    }
    
    private String dumpFields() {
        int count = this.fieldDefinitions.size();
    
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            DefaultDefinition fd = (DefaultDefinition) this.fieldDefinitions.get(i);
            sb.append(fd).append("\n");
        }
    
        return sb.toString();
    }
    
    private BigInteger encode(Map values) {        
        BigInteger encoded = new BigInteger("0");
        Iterator tor = this.fieldDefinitions.iterator();
        
        while (tor.hasNext()) {
            DefaultDefinition fd = (DefaultDefinition) tor.next();
            Object o = values.get(fd.getName());

            if ( o == null ) {
                continue;
            }

            BigInteger val = null;
            
            if ( o instanceof BigInteger ) {
                val = (BigInteger) o;                
            }
            else {
                val = new BigInteger(String.valueOf(o));                
            }

            if ( val.longValue() > getMaxValue(fd.getWidth()).longValue()) {
                throw new IllegalArgumentException("value to large for field width: " + val);            
            }
            
            encoded = encoded.or(val.shiftLeft(fd.getOffset()));
        }
        
        return encoded;
    }
    

    private FieldDefinition getField(String name) {
        int count = this.fieldDefinitions.size();
        for (int i = 0; i < count; i++) {
            DefaultDefinition fd = (DefaultDefinition) this.fieldDefinitions.get(i);
            if ( fd.getName().equals(name) ) {
                return fd;
            }            
        }

        return null;
    }
    
    private FieldDefinition getField(int index) {
        if ( index < 0 || index >= this.fieldDefinitions.size() ) {
            return null;
        }
        
        return (FieldDefinition) this.fieldDefinitions.get(index);
    }

    private double expandFieldValue(String name, BigInteger bits) {        
        FieldDefinition fd = getField(name);
        BigInteger raw = extractFieldValue(name, bits);
        String expression = fd.getOperands();
        JEP expressionParser = new JEP();
        
        
        if (  PropertiesUtil.isEmpty(expression) ) {
            return raw.doubleValue();
        }
       
        
        if (expression.indexOf('[') != -1 ) {
            // this is a look up expresion
            double []vals = parseLookupTable(expression);
            
            return vals[raw.intValue()];        
        }
        
        List ids = parseIdentifiers(expression);
        
        if ( ids.size() != 0 ) {
            expression = removeIdentifiers(expression);
            
            Iterator tor = ids.iterator();
            while (tor.hasNext()) {
                String idName = (String) tor.next();
                FieldDefinition fieldDefinition = (FieldDefinition) getField(idName);
                
                if ( fieldDefinition == null ) {
                    throw new IllegalArgumentException("unknown field: " + idName);
                }
                
                if ( fieldDefinition.getOffset() < fd.getOffset() ) {
                    throw new IllegalStateException("cannot access downstream field: " + idName);
                }
                
                expressionParser.addVariable(idName, expandFieldValue(idName, bits));                
            }
        }
        
        expressionParser.addVariable("x", raw.floatValue());
        expressionParser.addVariable("fieldWidth", fd.getWidth());
        expressionParser.addVariable("maxValue", DefaultEncoding.getMaxValue(fd.getWidth()).floatValue());
        expressionParser.parseExpression(expression);
        
         
        return expressionParser.getValue();        
    }
    

    private String removeIdentifiers(String expression) {
        StringBuffer sb = new StringBuffer(expression.length());
        char []buffer = expression.toCharArray();
        for (int i = 0; i < buffer.length; i++) {
            char ch = buffer[i];
            
            if ( ch != '$' && ch != '{' && ch != '}' ) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private List parseIdentifiers(String expression) {
        int begin = expression.indexOf("${");
        List ids = new ArrayList(1);
        
        while (begin != -1) {
            int end = expression.indexOf("}", begin + 2);
            String name = expression.substring(begin + 2, end); 
            ids.add(name);
            
            begin = expression.indexOf("${",  end);            
        }
        
        return ids;
    }

    /**
     * Parse a string into an array of doubles
     * e.g. string: [2.3, 1, 4.5]
     */
    private double[] parseLookupTable(String expression) {
        int start = expression.indexOf('[');
        int stop = expression.indexOf(']');
        
        if ( start == -1 || stop == -1 || start > stop ) {
            throw new IllegalArgumentException("invalid table definition: " + expression);
        }
                
        String def = expression.substring(start + 1, stop).trim();
        StringTokenizer st = new StringTokenizer(def, ",");
        double []vals = new double[st.countTokens()];        
        
        int i = 0;
        
        while (st.hasMoreTokens()) {
            vals[i] = Double.parseDouble(st.nextToken());
            i++;
        }
        
        return vals;
    }

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

   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addFieldDefinition(FieldDefinition def) {
        DefaultEncoding.this.fieldNames.add(def.getName());
        DefaultEncoding.this.fieldDefinitions.add(def);
        this.bitCount += def.getWidth();
    }

    public Value[] unpackFields(BigInteger bits) {
        int count = getFieldCount();
        Value []vals = new Value[count];
        
        for (int i = 0; i < vals.length; i++) {
            FieldDefinition fd = getField(i);
            vals[i] = new Value();
                      
            vals[i].setFieldDefinition(fd);
            vals[i].setRaw(extractFieldValue(i, bits).longValue());
            vals[i].setExpanded(expandFieldValue(fd.getName(), bits));            
        }
        
        return vals;
    }

    public FieldDefinition getFieldDefinition(int index) {
        return (FieldDefinition) this.fieldDefinitions.get(index);
    }

    public int getFieldCount() {
        return this.fieldDefinitions.size();
    }    
}
