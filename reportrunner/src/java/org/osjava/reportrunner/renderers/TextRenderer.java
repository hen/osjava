package org.osjava.reportrunner.renderers;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

import org.osjava.reportrunner.*;

public class TextRenderer extends AbstractRenderer {

    public void display(Report report, Writer out) throws IOException {
        Object[] data = report.execute();
        out.write("Report: "+report.getReportName()+"\n");
        for(int i=0; i<data.length; i++) {
            Object[] row = (Object[]) data[i];
            for(int j=0; j<row.length; j++) {
                out.write(""+row[j]+"\t");
            }
            out.write("\n");
        }
    }

}
