package org.osjava.reportrunner.renderers;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;

import org.osjava.reportrunner.*;

public class XmlRenderer extends AbstractRenderer {

    public void display(Result result, Report report, Writer out) throws IOException {
        PrettyPrinterXmlWriter writer = new PrettyPrinterXmlWriter(new SimpleXmlWriter(out));
        writer.setIndent("  ");
        writer.setNewline("\n");

        boolean numbers = false;

        writer.writeEntity("report");
        writer.writeAttribute("name", report.getName());

        Column[] columns = report.getColumns();
        if(columns.length == 0) {
            numbers = true;
        }

        while(result.hasNextRow()) {
            writer.writeEntity("data");
            Object[] row = result.nextRow();
            for(int j=0; j<row.length; j++) {
                writer.writeAttribute( numbers?"field"+j:columns[j].getName(), row[j] );
            }
            writer.endEntity();
        }

        writer.endEntity();
    }

}
