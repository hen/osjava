package org.osjava.reportrunner.renderers;

import java.io.*;

import org.osjava.reportrunner.util.ExcelWriter;
import org.osjava.reportrunner.*;

public class ExcelRenderer extends AbstractRenderer {

    public void display(Result result, Report report, Writer out) throws IOException {
        throw new RuntimeException("This should not be used with a Writer. ");
    }

    public void display(Result result, Report report, OutputStream out) throws IOException {
        ExcelWriter xls = new ExcelWriter(out);

        Column[] columns = result.getHeader();
        if(columns != null) {
            for(int i=0; i<columns.length; i++) {
                xls.writeField(columns[i].getLabel());
            }
            xls.endBlock();
        }

        while(result.hasNextRow()) {
            Object[] row = result.nextRow();
            for(int j=0; j<row.length; j++) {
                xls.writeField(row[j]);
            }
            xls.endBlock();
        }

        xls.flush();
    }

}
