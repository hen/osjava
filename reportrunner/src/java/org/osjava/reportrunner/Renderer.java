package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public interface Renderer extends Nameable {

    // Name referers to the unique name of this renderer/report setup
    String getName();
    void setName(String name);

    String getLabel();
    void setLabel(String label);

    // Type refers to the type of renderer used, as set in the rendereres.xml
    String getType();
    void setType(String type);

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

    Renderer cloneRenderer();

    void display(Result result, Report report, OutputStream out) throws IOException;
    void display(Result result, Report report, Writer out) throws IOException;

}
