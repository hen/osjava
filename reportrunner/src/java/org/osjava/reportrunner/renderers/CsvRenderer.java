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

    public void display(Result result, Report report, Writer out) throws IOException {
        CsvWriter csv = new CsvWriter( out );
        if(this.fieldDelimiter != null) {
            csv.setFieldDelimiter(this.fieldDelimiter.charAt(0));
        }

        Column[] columns = result.getHeader();
        if(columns != null) {
            for(int i=0; i<columns.length; i++) {
                csv.writeField(columns[i].getLabel());
            }
            csv.endBlock();
        }

        while(result.hasNextRow()) {
            Object[] row = result.nextRow();
            for(int j=0; j<row.length; j++) {
                csv.writeField(""+row[j]);
            }
            csv.endBlock();
        }
    }

}
