package org.osjava.reportrunner.renderers;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

import org.osjava.reportrunner.*;

public class HtmlTableRenderer extends AbstractRenderer {

    public void display(Report report, Writer out) throws IOException {
        Object[] data = report.execute();
        if(data == null) {
            out.write("There is no data. ");
            return;
        }
        out.write("<table>\n");
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
        for(int i=0; i<data.length; i++) {
            Object[] row = (Object[]) data[i];
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
