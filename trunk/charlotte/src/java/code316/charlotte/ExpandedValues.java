package code316.charlotte;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nfunk.jep.JEP;


public class ExpandedValues {
    private BigInteger bits;
    private Encoding encoding;
    private JEP expressionParser = new JEP();
    private List names = this.encoding.getNames();
    
    public ExpandedValues(Encoding encoding, BigInteger bits) {
        this.bits = bits;
        this.encoding = encoding;
        expand();
    }

    private void expand() {
        Map values = this.encoding.extractAllValues(this.bits);
        this.names = this.encoding.getNames();
        
        // set up variables to be used in parsing expressions
        
        for (Iterator iter = this.names.iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            
            double rawVal = this.encoding.expandFieldValue(name, bits);            
            this.expressionParser.addVariable(name, rawVal);
        }
    }
}
