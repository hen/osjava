package org.osjava.reportrunner;

import java.util.HashMap;

public class ReportGroup {

    private String name;
    private String label;
    private String description;
    private HashMap resources = new HashMap();;
    private String filename;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public Resource getResource(String name) { return (Resource) this.resources.get(name); }
    public void putResource(String name, Resource resource) { this.resources.put(name, resource); }

    public String getFilename() { return this.filename; }
    public void setFilename(String filename) { this.filename = filename; }
}
