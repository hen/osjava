package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public interface Renderer {

    String getRendererName();
    void setRendererName(String name);
    String getRendererLabel();
    void setRendererLabel(String label);
    String getMimeType();
    void setMimeType(String mimeType);
    void display(Report report, OutputStream out) throws IOException;
    void display(Report report, Writer out) throws IOException;

}
