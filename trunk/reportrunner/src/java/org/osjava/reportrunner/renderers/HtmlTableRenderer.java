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
        out.write("<script src='renderers/htmltable/sorttable.js'></script>\n");
        out.write("<script src='renderers/htmltable/zebra.js'></script>\n");
        out.write("<script src='renderers/htmltable/invertBgColor.js'></script>\n");
        out.write("<script src='renderers/htmltable/colourPicker.js'></script>\n");
        out.write("<script>var picker = new ColorPicker();</script>\n");
        out.write("<span id='swatch'><a href='#' onClick=\"picker.select(document.getElementById('swatch'),'pick');return false;\" name='pick' id='pick'><img src='images/1x1.gif' height='10' width='10' style='border-color: #000000'></a></span>\n");
        out.write("<script>picker.writeDiv()</script>\n");

        out.write("<table border='1' class='sortable' id='report'>\n");
        Column[] columns = result.getHeader();
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
        out.write("</table>\n");
        out.write("<script>stripe('report', '#fff', '#edf3fe'); initInvertBgColor('report');</script>\n");
    }

}
