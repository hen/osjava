package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public abstract class AbstractRenderer implements Renderer {

    private String name;
    private String label;
    private String mimeType;

    public String getRendererName() {
        return this.name;
    }

    public void setRendererName(String name) {
        this.name = name;
    }

    public String getRendererLabel() {
        return this.label;
    }

    public void setRendererLabel(String label) {
        this.label = label;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void display(Report report, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out);
        display( report, writer );
        writer.flush();
    }

    public abstract void display(Report report, Writer out) throws IOException;

}
