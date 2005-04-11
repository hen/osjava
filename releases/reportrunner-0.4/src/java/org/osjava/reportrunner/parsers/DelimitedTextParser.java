package org.osjava.reportrunner.parsers;

import org.osjava.reportrunner.*;

import java.text.*;

public class DelimitedTextParser extends AbstractParser {

    private String delimiter = ",";

    public void setDelimiter(String delimiter) { this.delimiter = delimiter; }
    public String getDelimiter() { return this.delimiter; }
    
    public Object parse(String input, Class type) {
        String[] inputs = input.split(this.delimiter);

        System.out.println("PARSING: "+input+" to "+inputs.length);
        
        // loop over each inputs and run a parser on each one?
        return inputs;
    }

}
