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
    private List columns = new ArrayList();

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

    public void setRenderers(String renderers) {
        this.renderers = renderers;
    }

    public Renderer[] getRenderers() {
        String[] array = org.apache.commons.lang.StringUtils.split(this.renderers, ",");
        Renderer[] array2 = ReportFactory.getRenderers();
        List list = new ArrayList();
        for(int i=0; i<array.length; i++) {
            for(int j=0; j<array2.length; j++) {
                if(array[i].equals(array2[j].getName())) {
                    list.add(array2[j]);
                    break;
                }
            }
        }
        return (Renderer[]) list.toArray( new Renderer[0] );
    }

    public Param[] getParams() {
        return (Param[]) this.params.toArray(new Param[0]);
    }

    public void addParam(Param param) {
        this.params.add(param);
    }

    public Column[] getColumns() {
        return (Column[]) this.columns.toArray(new Column[0]);
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public abstract Object[] execute();
    public abstract Choice[] getParamChoices(Param param);

}
