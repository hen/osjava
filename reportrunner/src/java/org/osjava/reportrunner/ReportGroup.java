package org.osjava.reportrunner;

public class ReportGroup {

    private String name;
    private String label;
    private String resources;
    private String filename;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }

    public String getResources() { return this.resources; }
    public void setResources(String resources) { this.resources = resources; }

    public String getFilename() { return this.filename; }
    public void setFilename(String filename) { this.filename = filename; }
}
