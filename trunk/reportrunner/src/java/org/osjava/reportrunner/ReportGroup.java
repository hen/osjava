package org.osjava.reportrunner;

import java.util.*;

public class ReportGroup {

    private String name;
    private String label;
    private String description;
    private HashMap resources = new HashMap();;
    private List resource_params = new ArrayList();
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

    public Param[] getResourceParams() {
        return (Param[]) this.resource_params.toArray(new Param[0]);
    }

    public void addResourceParam(Param param) {
        this.resource_params.add(param);
    }

    // returning null if no choice to be made for this param
    public Choice[] getResourceParamChoices(Param param) {
        String[] options = param.getOptions();
        if(options == null || options.length < 2) {
            return null;
        }
        Choice[] choices = new Choice[options.length];
        for(int i=0; i<options.length; i++) {
            choices[i] = new Choice(options[i], this.getResource(options[i]).getLabel());
        }
        return choices;
    }

}
