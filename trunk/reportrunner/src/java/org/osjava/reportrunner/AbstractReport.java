package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public abstract class AbstractReport implements Report {

    private String name;
    private String label;
    private String renderers;
    private List params = new ArrayList();

    public String getReportName() {
        return this.name;
    }

    public void setReportName(String name) {
        this.name = name;
    }

    public String getReportLabel() {
        return this.label;
    }

    public void setReportLabel(String label) {
        this.label = label;
    }

    public String getRenderers() {
        return this.renderers;
    }

    public void setRenderers(String renderers) {
        this.renderers = renderers;
    }

    public Param[] getParams() {
        return (Param[]) this.params.toArray(new Param[0]);
    }

    public void addParam(Param param) {
        this.params.add(param);
    }

    public abstract Object[] execute();
    public abstract Choice[] getParamChoices(Param param);

}
