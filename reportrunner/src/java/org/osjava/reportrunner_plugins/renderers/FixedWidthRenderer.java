package org.osjava.reportrunner_plugins.renderers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Column;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;

public class FixedWidthRenderer extends AbstractRenderer {
    
    private String columnWidths = null;
    
    public void setColumnWidths(String columnWidths) { this.columnWidths = columnWidths; }
    public String getColumnWidths() { return this.columnWidths; }
    
    public void display(Result result, Report report, OutputStream out) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));       
        
        String[] splitWidths = this.columnWidths.split(",");
        int[] widths = new int[splitWidths.length];
        for (int i = 0; i < splitWidths.length; i++) {
            widths[i] = Integer.parseInt(splitWidths[i]);
        }
            
        Column[] columns = report.getColumns();
        for (int i = 0; i < columns.length; i++) {
            writer.write( this.padString(columns[i].getLabel(), widths[i]) );        
        }
        writer.newLine();
        
        while(result.hasNextRow()) {
            Object[] row = result.nextRow();
            for(int j = 0; j < row.length; j++) {
                writer.write( this.padString(row[j].toString(), widths[j]) );                
            }            
            writer.newLine();
        } 
        
        writer.newLine();        
        writer.flush();
    }
    
    public void display(Result result, Report report, Writer out) throws IOException {        
        throw new RuntimeException("This should not be used with a Writer. ");    
    }

    private String padString(String str, int len) {
        if (str == null) {
            str = "";
        }
        
        if (str.length() > len) {
            return str.substring(0, len);
        }
        
        StringBuffer sb = new StringBuffer(str);
        while (sb.length() < len) {
            sb.append(" ");
        }
        
        return sb.toString();
    }
}
