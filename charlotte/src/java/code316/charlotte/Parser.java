package code316.charlotte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


class Parser {
    private int lineNumber = 0;
    public Encoding parse(InputStream _in) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(_in));
        String line = null;
        int bitPos = 0;
        Encoding encoding = new DefaultEncoding();
        String encodingName = null;
        
        while ((line = in.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            
            if ( line.startsWith("#") || line.length() == 0 ) {
                // skip comments and blank lines
                continue;                    
            }
            else if ( line.startsWith("name:") ) {
                if ( encodingName != null) {
                    throw new IllegalArgumentException("error on line: " + lineNumber + " (encoding name already defined)");
                }
                
                encodingName = parseEncodingName(line);
                encoding.setName(encodingName);
                continue;
            }
            
            FieldDefinition def = parseDefinition(line);
            
            if ( def == null ) {
                throw new IllegalStateException("invalid value definition on line: " + lineNumber);
            }
            
            bitPos += def.getWidth();
            
            
            encoding.addFieldDefinition(def);
        }
        
        int count = encoding.getFieldCount();
        int pos = 0;
        for (int i = count - 1; i >= 0; i--) {
            FieldDefinition fd = (FieldDefinition) encoding.getFieldDefinition(i);
            fd.setOffset(pos);
            pos += fd.getWidth();                
        }
        
        return encoding;
    }


    private FieldDefinition parseDefinition(String line) {
        int equalPos = line.indexOf('=');
        int commaPos = line.indexOf(',');
        
        if ( equalPos == -1 ) {
            return null;
        }
        
        FieldDefinition def = new DefaultDefinition();
        
        String typeAndName = line.substring(0, equalPos).trim();
        
        String fieldName = parseName(typeAndName);
        

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
        
        def.setWidth(Integer.parseInt(bitDef));
                    
        return def;
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
        int pos = indexOfAny(Encoding.WHITE_SPACE, typeAndName);
        
        if ( pos == -1) {
            return typeAndName;
        }
        
        String name = typeAndName.substring(pos + 1).trim();
        
        if ( indexOfAny(Encoding.WHITE_SPACE, name) != -1 ) {
            throw new IllegalStateException("white space found where none was expected.  line: " 
                        + this.lineNumber);
        }
        
       
        return name;
    }

    
    /**
     * Encoding name line looks like this:
     * name: My Favorite Encoding 
     * @param line
     * @return
     */
    private String parseEncodingName(String line) {
        int pos = line.indexOf(':');
        
        if ( pos == -1) {
            return null;
        }
        
        return line.substring(pos + 1).trim();
    }
    
    
    private int indexOfAny(char[] findAny, String typeAndName) {
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
}