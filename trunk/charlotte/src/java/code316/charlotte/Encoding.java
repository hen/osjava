package code316.charlotte;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.nfunk.jep.JEP;

public class Encoding implements EncodingDefinition {
    private static final BigInteger NEGATIVE_ONE = new BigInteger("-1");
    private int lineNumber;
    private ArrayList fieldNames = new ArrayList();
    private int bitCount;
    private ArrayList fieldDefinitions = new ArrayList();
    private char WHITE_SPACE[] = {' ', '\t'};   
    private Endidness endidness = Endidness.BIG; 
    private JEP expressionParser;
    private String name = "";
    
    public Encoding() {
        this.expressionParser = new JEP();
        this.expressionParser.addStandardConstants();
        this.expressionParser.addStandardFunctions();
    }
    
    public Encoding(InputStream in) throws IOException {
        this();
        parse(in);
    }
    
   
    private class Parser {
        public void parse(InputStream _in) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(_in));
            String line = null;
            int lineNumber = 0;
            int bitPos = 0;
            
            while ((line = in.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if ( line.startsWith("#") || line.length() == 0 ) {
                    // skip comments
                    continue;                    
                }
                
                FieldDefinition def = parseDefinition(line);
                
                if ( def == null ) {
                    throw new IllegalStateException("invalid value definition on line: " + lineNumber);
                }
                
                Encoding.this.bitCount += def.getLength();

                bitPos += def.getLength();
                
                
                addFieldDefinition(def);
            }
            
            int count = Encoding.this.fieldDefinitions.size();
            int pos = 0;
            for (int i = count - 1; i >= 0; i--) {
                FieldDefinition fd = (FieldDefinition) fieldDefinitions.get(i);
                fd.setOffset(pos);
                pos += fd.getLength();                
            }
        }
    

        private FieldDefinition parseDefinition(String line) {
            int equalPos = line.indexOf('=');
            int commaPos = line.indexOf(',');
            
            if ( equalPos == -1 ) {
                return null;
            }
            
            FieldDefinition def = null;
            
            String typeAndName = line.substring(0, equalPos).trim();
            
            FieldType fieldType = parseType(typeAndName);
            String fieldName = parseName(typeAndName);
            
            if ( fieldType == null ) {
                fieldType = FieldType.INTEGER.copy();
            }

            // create depending on parsed type
            if ( fieldType.equals(FieldType.FLOAT) ) {
                def = new FloatingPointDefinition();
            }
            else {
                def = new IntegerDefinition();
            }

            def.setName(fieldName);            
            
            String bitDef = null;

            if ( commaPos != -1 ) {
                // definition is from equal to comma
                if ( commaPos <= (equalPos + 1)) {
                    return null;
                }
                else {
                    bitDef = line.substring(equalPos + 1, commaPos).trim();
                    def.setOperands(line.substring(commaPos + 1).trim());
                }
            }
            else {
                // rest of line is definition
                bitDef = line.substring(equalPos + 1).trim();
            }
            
            if ( bitDef == null ) {
                return null;
            }
            
            parseIntegerBitDefinition((IntegerDefinition) def, bitDef);
                        
            return def;
        }
        
        

        private void parseIntegerBitDefinition(IntegerDefinition def, String bitDefinition) {
            def.setLength(Integer.parseInt(bitDefinition));
        }
                

        /**
         * parse a type and name from a line that looks like this:
         * [type] <name>
         * if more than one word is found, then the first word
         * is a type name and the second word is a name.
         * 
         * if only one word is found then that
         * word is a name and the name can not be a reserved word.
         * 
         * there can be no leading or trailing whitespace
         * 
         * @param typeAndName
         * @return a field type or null if no type is found
         */
        private String parseName(String typeAndName) {
            int pos = indexOf(WHITE_SPACE, typeAndName);
            
            if ( pos == -1) {
                return typeAndName;
            }
            
            String name = typeAndName.substring(pos + 1).trim();
            
            if ( indexOf(WHITE_SPACE, name) != -1 ) {
                throw new IllegalStateException("white space found where none was expected");
            }
            
            if ( FieldType.getTypeByName(name) != null ) {
                throw new IllegalStateException("name is a reserved word: '" + name + "'"); 
            }
            
            return name;
        }


        /**
         * parse a type and name from a line that looks like this:
         * [type] <name>
         * if more than one word is found, then the first word
         * is a type name.
         * 
         * if only one word is found then that
         * word is a name and the name can not be a reserved word.
         * 
         * there can be no leading or trailing whitespace
         * 
         * @param typeAndName
         * @return a field type or null if no type is found
         */
        private FieldType parseType(String typeAndName) {
            int pos = indexOf(WHITE_SPACE, typeAndName);
            
            if ( pos == -1) {
                return null;
            }
            
            String name = typeAndName.substring(0, pos).trim();
            FieldType ft = FieldType.getTypeByName(name);
            
            if ( ft == null ) {
                throw new IllegalStateException("unknown type: '" + name + "'");
            }
            
            return ft;
        }

        private int indexOf(char[] findAny, String typeAndName) {
            for (int i = 0; i < findAny.length; i++) {
                int index;
                if ( (index = typeAndName.indexOf(findAny[i])) != -1) {
                    return index;
                }
            }
            return -1;
        }

        private int parseInteger(String bitDef) {
            int pos = bitDef.indexOf('.');
            
            return (pos == -1) 
                    ? Integer.parseInt(bitDef)
                    : Integer.parseInt(bitDef.substring(0, pos));
        }

        private int parseDecimal(String bitDef) {
            int pos = bitDef.indexOf('.');
            
            return (pos == -1) 
                    ? 0
                    : Integer.parseInt(bitDef.substring(pos + 1));
        }

//        private float computeDecimal(float f) {
//            if ( f == 0 ) {
//                return 0;
//            }
//            
//            return (float) (f * Math.pow(10, Float.toString(f).length()));
//        }
    } // end class definition for parse



    public void parse(InputStream in) throws IOException {
        Parser p = new Parser();
        p.parse(in);
    }
    
    public List getNames() {
        return (List) this.fieldNames.clone();        
    }
    
    public int getValueCount() {
        return this.fieldNames.size();
    }
    
    /**
     * 
     * @deprecated Use getLength
     * 
     */
    public int getBitCount() {
        return getLength();
    }
    
    public int getLength() {
        return this.bitCount;
    }
    

    public List getFieldDefinitions() {
        return (List) this.fieldDefinitions.clone();
    }
    
    public BigInteger extractFieldValue(String name, BigInteger bi) {
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
    
    public BigInteger extractFieldValue(int i, BigInteger bi) {
        SimpleDefinition fd = (SimpleDefinition) this.fieldDefinitions.get(i);
        return bi.shiftRight(fd.getOffset()).and(getMaxValue(fd.getLength()));
    }
    
    
    public BigDecimal unpackFieldValue(int i, BigInteger bi) {
        SimpleDefinition fd = (SimpleDefinition) this.fieldDefinitions.get(i);

        BigInteger val = bi.shiftRight(fd.getOffset()).and(getMaxValue(fd.getLength()));

        return Operator.resolve(val, fd.getOperands());
    }


    public Map extractAllValues(BigInteger bits) {
        int count = this.fieldDefinitions.size();
        Map values = new HashMap();
        
        for (int i = 0; i < count; i++) {
            SimpleDefinition fd = (SimpleDefinition) this.fieldDefinitions.get(i);
            values.put(fd.getName(), extractFieldValue(i, bits));
        }
        
        return values;
    }
    
    public BigInteger getMaxValue() {
        return Encoding.getMaxValue(getBitCount());            
    }
    
    public static BigInteger getMaxValue(int numBits) {
        return new BigInteger("1").shiftLeft(numBits).add(NEGATIVE_ONE);        
    }
    
    public String dumpValues(BigInteger bi) {
        int count = this.fieldDefinitions.size();
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            SimpleDefinition fd = (SimpleDefinition) this.fieldDefinitions.get(i);
            sb.append(fd.getName())
                .append("=")
                .append(extractFieldValue(i, bi))
                .append("\n");
        }
        
        return sb.toString();
    }
    
    public String dumpFields() {
        int count = this.fieldDefinitions.size();
    
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            SimpleDefinition fd = (SimpleDefinition) this.fieldDefinitions.get(i);
            sb.append(fd).append("\n");
        }
    
        return sb.toString();
    }
    
    public BigInteger encode(Map values) {        
        BigInteger encoded = new BigInteger("0");
        Iterator tor = this.fieldDefinitions.iterator();
        
        while (tor.hasNext()) {
            SimpleDefinition fd = (SimpleDefinition) tor.next();
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

            if ( val.longValue() > getMaxValue(fd.getLength()).longValue()) {
                throw new IllegalArgumentException("value to large for field width: " + val);            
            }
            
            encoded = encoded.or(val.shiftLeft(fd.getOffset()));
        }
        
        return encoded;
    }
    
    public static BigDecimal bitsToBigDecimal(BigInteger bits, int mantissa, int exponent) {
        BigInteger num = bits.shiftRight(exponent).and(getMaxValue(mantissa));
        int power = getMaxValue(exponent).and(bits).intValue();
 
        return new BigDecimal(num).setScale(exponent).movePointRight(power);
    }
    
    public static BigDecimal bitsToBigDecimal(int bits, int mantissa, int exponent) {
        return bitsToBigDecimal(new BigInteger(Integer.toString(bits)), mantissa, exponent);
    }

    public FieldDefinition getField(String name) {
        int count = this.fieldDefinitions.size();
        for (int i = 0; i < count; i++) {
            SimpleDefinition fd = (SimpleDefinition) this.fieldDefinitions.get(i);
            if ( fd.getName().equals(name) ) {
                return fd;
            }            
        }

        return null;
    }
    
    public FieldDefinition getField(int index) {
        if ( index < 0 || index >= this.fieldDefinitions.size() ) {
            return null;
        }
        
        return (SimpleDefinition) this.fieldDefinitions.get(index);
    }

    public double expandFieldValue(String name, BigInteger integer) {        
        FieldDefinition fd = getField(name);
        BigInteger raw = extractFieldValue(name, integer);
        String expression = fd.getOperands();
        
        
        if ( expression == null ) {
            return raw.doubleValue();
        }
       
        
        if (expression.indexOf('[') != -1 ) {
            // this is a look up expresion
            double []vals = parseLookupTable(expression);
            
            return vals[raw.intValue()];        
        }
        
        this.expressionParser.addVariable("x", raw.floatValue());
        this.expressionParser.addVariable("fieldWidth", fd.getLength());
        this.expressionParser.addVariable("maxValue", Encoding.getMaxValue(fd.getLength()).floatValue());
        this.expressionParser.parseExpression(expression);
        
        return expressionParser.getValue();        
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
        
        me.append("encoding name: ")
        .append(ed.getName())
        .append("\n")
        .append("length: ")
        .append(ed.getLength())
        .append(" bits\n");
        
        
        me.append("\nmap: \n    ");
        char marker = 'a';
        
        Iterator tor = ed.getFieldDefinitions().iterator();
        while (tor.hasNext()) {
            FieldDefinition td = (FieldDefinition) tor.next();
            
            int length = td.getLength();
            for (int i = 0; i < length; i++) {
                me.append(marker);                
            }
            
            legend.append(marker)
            .append(" - ")
            .append(td.getName())
            .append(", ")
            .append(length)
            .append(" bits\n");
            
            marker++;
        }

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
        Encoding.this.fieldNames.add(def.getName());
        Encoding.this.fieldDefinitions.add(def);
    }
    
}
