package org.osjava.reportrunner.renderers;

import java.io.*;
import java.util.*;
import com.generationjava.io.*;
import com.generationjava.lang.*;

import org.osjava.reportrunner.*;

public class CsvRenderer extends AbstractRenderer {

    private String fieldDelimiter;

    public void setFieldDelimiter(String fieldDelimiter) { this.fieldDelimiter = fieldDelimiter; }
    public String getFieldDelimiter() { return this.fieldDelimiter; }

    public void display(Report report, Writer out) throws IOException {
        Object[] data = report.execute();
        CsvWriter csv = new CsvWriter( out );
        if(this.fieldDelimiter != null) {
            csv.setFieldDelimiter(this.fieldDelimiter.charAt(0));
        }

        for(int i=0; i<data.length; i++) {
            Object[] row = (Object[]) data[i];
            for(int j=0; j<row.length; j++) {
                csv.writeField(""+row[j]);
            }
            csv.endBlock();
        }
    }

}
