package org.osjava.reportrunner.renderers;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

import org.osjava.reportrunner.*;

public class HtmlTableRenderer extends AbstractRenderer {

    public void display(Result result, Report report, Writer out) throws IOException {
        if(result == null) {
            out.write("There is no result. ");
            return;
        }
        out.write("<table border='1'>\n");
        Column[] columns = report.getColumns();
        if(columns != null) {
            out.write("<tr>\n");
            for(int i=0; i<columns.length; i++) {
                out.write("<th>");
                out.write(columns[i].getLabel());
                out.write("</th>\n");
            }
            out.write("</tr>\n");
        }
        while(result.hasNextRow()) {
            Object[] row = result.nextRow();
            out.write("<tr>\n");
            for(int j=0; j<row.length; j++) {
                out.write("<td>");
                out.write(""+row[j]);
                out.write("</td>\n");
            }
            out.write("</tr>\n");
        }
        out.write("<table>\n");
    }

}
