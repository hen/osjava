package org.osjava.reportrunner;

public class ReportGroup {

    private String name;
    private String label;
    private String datasource;
    private String filename;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }

    public String getDatasource() { return this.datasource; }
    public void setDatasource(String datasource) { this.datasource = datasource; }

    public String getFilename() { return this.filename; }
    public void setFilename(String filename) { this.filename = filename; }
}
