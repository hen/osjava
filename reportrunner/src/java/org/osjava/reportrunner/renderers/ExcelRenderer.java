package org.osjava.reportrunner.renderers;

import java.io.*;

import org.osjava.reportrunner.util.ExcelWriter;
import org.osjava.reportrunner.*;

public class ExcelRenderer extends AbstractRenderer {

    public void display(Report report, Writer out) throws IOException {
        throw new RuntimeException("This should not be used with a Writer. ");
    }

    public void display(Report report, OutputStream out) throws IOException {
        Result data = report.execute();
        ExcelWriter xls = new ExcelWriter(out);

        while(data.hasNextRow()) {
            Object[] row = data.nextRow();
            for(int j=0; j<row.length; j++) {
                xls.writeField(""+row[j]);
            }
            xls.endBlock();
        }

        xls.flush();
    }

}
