package com.generationjava.io;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.IOException;
import java.io.StringReader;

public class FixedWidthReaderTest extends TestCase {

    public FixedWidthReaderTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //   SimpleAlias(Object) -> getAlias()

    public void testRead() {
        try {
            String test = "hello*one*ant*8*beer***amonggrass**";
            StringReader rdr = new StringReader(test);
            FixedWidthReader fwr = new FixedWidthReader(rdr);
    //        fwr.setWidths( new int[] {5,5,4,2,0,5,7} );
            String widths = "5,5,4,2,*,5,7";
            fwr.setWidths( widths );
            fwr.setTrim( "*" );
            Object[] objs = null;
            while( (objs = fwr.readLine()) != null ) {
                assertEquals("hello", objs[0]);
                assertEquals("one", objs[1]);
                assertEquals("ant", objs[2]);
                assertEquals("8", objs[3]);
                assertEquals("beer", objs[4]);
                assertEquals("among", objs[5]);
                assertEquals("grass", objs[6]);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
