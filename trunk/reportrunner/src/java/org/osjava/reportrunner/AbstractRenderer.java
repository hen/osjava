package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

import org.apache.commons.lang.BooleanUtils;

public abstract class AbstractRenderer implements Renderer {

    private String name;
    private String label;
    private String description;
    private String mimeType;
    private String extension;
    private boolean inline;
    private Properties variables = new Properties();;

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

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isInline() {
        return this.inline;
    }

    public void setInline(String yesno) {
        this.inline = BooleanUtils.toBoolean(yesno);
    }

    public void setVariable(String name, String value) {
        this.variables.setProperty(name, value);
    }

    public Map getVariables() {
        return this.variables;
    }

    public void display(Result result, Report report, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out);
        display( result, report, writer );
        writer.flush();
    }

    public abstract void display(Result result, Report report, Writer out) throws IOException;

}
