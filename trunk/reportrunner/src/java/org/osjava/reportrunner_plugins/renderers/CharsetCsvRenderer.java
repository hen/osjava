package org.osjava.reportrunner_plugins.renderers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Result;

import com.generationjava.io.CsvWriter;

public class CharsetCsvRenderer extends AbstractRenderer {
    
    private String fieldDelimiter;
    private String encoding;
    
    public void setFieldDelimiter(String fieldDelimiter) { this.fieldDelimiter = fieldDelimiter; }
    public String getFieldDelimiter() { return this.fieldDelimiter; }
    
    public void setEncoding(String encoding) { this.encoding = encoding; }
    public String getEncoding() { return this.encoding; }
    
    public void display(Result result, Report report, OutputStream out) throws IOException {
        Writer writer = null;
        
        if (this.encoding != null && this.encoding.length() > 0) {        
            writer = new OutputStreamWriter(out, this.encoding);
        } else {
            writer = new OutputStreamWriter(out);
        }
        
        CsvWriter csv = new CsvWriter(writer);
        if(this.fieldDelimiter != null) {
            csv.setFieldDelimiter(this.fieldDelimiter.charAt(0));
        }
        
        while(result.hasNextRow()) {
            Object[] row = result.nextRow();
            for(int j=0; j<row.length; j++) {
                csv.writeField(""+row[j]);
            }
            csv.endBlock();
        }      
        
        writer.flush();
    }
    
    public void display(Result result, Report report, Writer out) throws IOException {        
        throw new RuntimeException("This should not be used with a Writer. ");    
    }
        
}
