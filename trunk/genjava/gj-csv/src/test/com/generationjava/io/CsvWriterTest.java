package com.generationjava.io;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

public class CsvWriterTest extends TestCase {

    public CsvWriterTest(String name) {
        super(name);
    }

    public void testWriteFields() {
        try {
            StringWriter sw = new StringWriter();
            CsvWriter csv = new CsvWriter(sw);
            csv.writeField("1");
            csv.writeField("2");
            csv.writeField("3");
            csv.writeField("4");
            csv.writeField("5");
            csv.endBlock();
            csv.writeField("6");
            csv.writeField("7");
            csv.writeField("8");
            csv.writeField("9");
            csv.writeField("0");
            csv.endBlock();
            csv.close();
            assertEquals("Does not write simple csv file out correctly. ", 
                         "1,2,3,4,5\n6,7,8,9,0\n",
                         sw.toString() );
        } catch(IOException ioe) {
            fail("IOException should not have been thrown. ");
        }
    }

}
