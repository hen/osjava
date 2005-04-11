package org.osjava.reportrunner.renderers;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

import org.osjava.reportrunner.*;

/**
 * Like a HtmlTableRenderer, but the data goes in columns not rows.
 * This works well for high column, low row reports. 
 */
public class InvertedHtmlTableRenderer extends AbstractRenderer {

    public void display(Result result, Report report, Writer out) throws IOException {
        if(result == null) {
            out.write("There is no result. ");
            return;
        }
        /* Rewrite these to handle the inverted concept 
        out.write("<script src='renderers/htmltable/sorttable.js'></script>\n");
        out.write("<script src='renderers/htmltable/zebra.js'></script>\n");
        out.write("<script src='renderers/htmltable/invertBgColor.js'></script>\n");
        out.write("<script src='renderers/htmltable/colourPicker.js'></script>\n");
        out.write("<script>var picker = new ColorPicker();</script>\n");
        out.write("<span id='swatch'><a href='#' onClick=\"picker.select(document.getElementById('swatch'),'pick');return false;\" name='pick' id='pick'><img src='images/1x1.gif' height='10' width='10' style='border-color: #000000'></a></span>\n");
        out.write("<script>picker.writeDiv()</script>\n");
        */

        List rows = new ArrayList();
        out.write("<table border='1' class='sortable' id='report'>\n");
        Column[] columns = result.getHeader();
        if(columns != null) {
            for(int i=0; i<columns.length; i++) {
                List tmp = new ArrayList();
                tmp.add( "<th>" + columns[i].getLabel() + "</th>\n" );
                rows.add(tmp);
            }
        }
        while(result.hasNextRow()) {
            Object[] row = result.nextRow();
            for(int i=0; i<row.length; i++) {
                List tmp = null;
                if(rows.size() <= i) {
                    tmp = new ArrayList();
                    rows.add(tmp);
                }
                tmp = (List) rows.get(i);
                if(tmp == null) {
                    tmp = new ArrayList();
                    rows.add(tmp);
                }
                tmp.add( "<td>" + row[i] + "</td>\n" );
            }
        }
        Iterator rowsIterator = rows.iterator();
        while(rowsIterator.hasNext()) {
            List cells = (List) rowsIterator.next();
            Iterator cellsIterator = cells.iterator();
            out.write("<tr>");
            while(cellsIterator.hasNext()) {
                out.write( (String) cellsIterator.next() );
            }
            out.write("</tr>\n");
        }
        out.write("</table>\n");
        out.write("<script>stripe('report', '#fff', '#edf3fe'); initInvertBgColor('report');</script>\n");
    }

}
