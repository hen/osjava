package org.osjava.reportrunner.renderers;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

import org.osjava.reportrunner.*;

public class TextRenderer extends AbstractRenderer {

    public void display(Report report, Writer out) throws IOException {
        Result data = report.execute();
        out.write("Report: "+report.getName()+"\n");
        while(data.hasNextRow()) {
            Object[] row = data.nextRow();
            for(int j=0; j<row.length; j++) {
                out.write(""+row[j]+"\t");
            }
            out.write("\n");
        }
    }

}
