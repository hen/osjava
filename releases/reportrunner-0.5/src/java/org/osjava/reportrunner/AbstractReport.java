package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public abstract class AbstractReport implements Report {

    private String name;
    private String author;
    private String label;
    private String description;
    private List params = new ArrayList();
    private List columns = new ArrayList();
    private List renderers = new ArrayList();
    private List variants = new ArrayList();
    private ReportGroup reportGroup;

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

    public void addRenderer(Renderer renderer) {
        this.renderers.add(renderer);
    }

    public Renderer[] getRenderers() {
        return (Renderer[]) this.renderers.toArray(new Renderer[0]);
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

    public Variant[] getVariants() {
        return (Variant[]) this.variants.toArray(new Variant[0]);
    }

    public void addVariant(Variant variant) {
        this.variants.add(variant);
    }

    public ReportGroup getReportGroup() {
        return this.reportGroup;
    }

    public void setReportGroup(ReportGroup reportGroup) {
        this.reportGroup = reportGroup;
    }

    public String[] getResourceNames() {
        return new String[0];
    }

    public void setResource(String name, String resourceName) {
        ; // do nothing
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public abstract Result execute() throws ReportException;
    public abstract Choice[] getParamChoices(Param param);

}
