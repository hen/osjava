package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public interface Renderer {

    String getName();
    void setName(String name);
    String getLabel();
    void setLabel(String label);
    String getDescription();
    void setDescription(String description);
    String getMimeType();
    void setMimeType(String mimeType);
    String getExtension();
    void setExtension(String extension);
    boolean isInline();
    void setInline(String yesno);
    void setVariable(String name, String value);
    Map getVariables();

    void display(Result result, Report report, OutputStream out) throws IOException;
    void display(Result result, Report report, Writer out) throws IOException;

}
