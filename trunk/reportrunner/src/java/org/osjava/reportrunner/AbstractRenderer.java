package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public abstract class AbstractRenderer implements Renderer {

    private String name;
    private String label;
    private String mimeType;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void display(Result result, Report report, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out);
        display( result, report, writer );
        writer.flush();
    }

    public abstract void display(Result result, Report report, Writer out) throws IOException;

}
