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
            csv.setBlockDelimiter('\n');
            csv.setFieldDelimiter(',');
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
            assertEquals("Does not return the right Writer", sw, csv.getWriter() );
        } catch(IOException ioe) {
            fail("IOException should not have been thrown. ");
        }
    }

    public void testWriteObjectFields() {
        try {
            StringWriter sw = new StringWriter();
            CsvWriter csv = new CsvWriter(sw);
            csv.setBlockDelimiter('\n');
            csv.setFieldDelimiter(',');
            csv.writeField(new Integer(1));
            csv.writeField(new Integer(2));
            csv.writeField(new Long(3));
            csv.writeField(new Long(4));
            csv.writeField(new Short((short)5));
            csv.endBlock();
            csv.writeField(new Short((short)6));
            csv.writeField(new Byte((byte)7));
            csv.writeField(new Byte((byte)8));
            csv.writeField(new Integer(9));
            csv.writeField(new Integer(0));
            csv.endBlock();
            csv.close();
            assertEquals("Does not write simple csv file from objects out correctly. ", 
                         "1,2,3,4,5\n6,7,8,9,0\n",
                         sw.toString() );
            assertEquals("Does not return the right Writer", sw, csv.getWriter() );
        } catch(IOException ioe) {
            fail("IOException should not have been thrown. ");
        }
    }

    public void testWriteLines() {
        try {
            StringWriter sw = new StringWriter();
            CsvWriter csv = new CsvWriter(sw);
            String[] strs = new String[] { "1", "2", "3", "4", "5" };
            csv.writeLine(strs);
            strs = new String[] { "6", "7", "8", "9", "0" };
            csv.writeLine(strs);
            csv.close();
            assertEquals("Does not write simple csv file out correctly. ", 
                         "1,2,3,4,5\n6,7,8,9,0\n",
                         sw.toString() );
        } catch(IOException ioe) {
            fail("IOException should not have been thrown. ");
        }
    }

    public void testWriteObjectLines() {
        try {
            StringWriter sw = new StringWriter();
            CsvWriter csv = new CsvWriter(sw);
            Object[] objs = new Object[] { new Long(1), new Integer(2), new Byte((byte)3), "4", new Short((short)5) };
            csv.writeLine(objs);
            objs = new String[] { "6", "7", "8", "9", "0" };
            objs = new Object[] { new Long(6), new Integer(7), new Byte((byte)8), "9", new Short((short)0) };
            csv.writeLine(objs);
            csv.close();
            assertEquals("Does not write lines of objects to csv file correctly. ", 
                         "1,2,3,4,5\n6,7,8,9,0\n",
                         sw.toString() );
        } catch(IOException ioe) {
            fail("IOException should not have been thrown. ");
        }
    }

    public void testEscapeQuotes() {
        try {
            StringWriter sw = new StringWriter();
            CsvWriter csv = new CsvWriter(sw);
            String[] strs = new String[] { "That's \"Mr. Monkey\", to you", "Fool" };
            csv.writeLine(strs);
            strs = new String[] { "Not \"Monkey\"", "Fool" };
            csv.writeLine(strs);
            csv.close();
            assertEquals("Does not write simple csv file out correctly. ", 
                         "\"That's \"\"Mr. Monkey\"\", to you\",Fool\n" +
                         "Not \"Monkey\",Fool\n",
                         sw.toString() );
        } catch(IOException ioe) {
            fail("IOException should not have been thrown. ");
        }
    }

}
