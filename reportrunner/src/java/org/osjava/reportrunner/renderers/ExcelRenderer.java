package org.osjava.reportrunner.renderers;

import java.io.*;

import org.osjava.reportrunner.util.ExcelWriter;
import org.osjava.reportrunner.*;

public class ExcelRenderer extends AbstractRenderer {

    public void display(Report report, Writer out) throws IOException {
        throw new RuntimeException("This should not be used with a Writer. ");
    }

    public void display(Report report, OutputStream out) throws IOException {
        Object[] data = report.execute();
        ExcelWriter xls = new ExcelWriter(out);

        for(int i=0; i<data.length; i++) {
            Object[] row = (Object[]) data[i];
            for(int j=0; j<row.length; j++) {
                xls.writeField(""+row[j]);
            }
            xls.endBlock();
        }

        xls.flush();
    }

}
