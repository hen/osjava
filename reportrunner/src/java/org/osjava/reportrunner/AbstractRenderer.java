package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public abstract class AbstractRenderer implements Renderer {

    private String name;
    private String mimeType;

    public String getRendererName() {
        return this.name;
    }

    public void setRendererName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void display(Report report, OutputStream out) throws IOException {
        display( report, new OutputStreamWriter(out) );
    }

    public abstract void display(Report report, Writer out) throws IOException;

}
