package org.osjava.reportrunner.renderers;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

import org.osjava.reportrunner.*;

public class TextRenderer extends AbstractRenderer {

    public void display(Result result, Report report, Writer out) throws IOException {
        out.write("Report: "+report.getName()+"\n");
        while(result.hasNextRow()) {
            Object[] row = result.nextRow();
            for(int j=0; j<row.length; j++) {
                out.write(""+row[j]+"\t");
            }
            out.write("\n");
        }
    }

}
