package org.osjava.multidoc;

import java.util.List;
import java.util.ArrayList;

public class Document {

    private String type;
    private String name;
    private List projects = new ArrayList();

    public Document(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public List getProjects() {
        return this.projects;
    }

    public void addProject(DocumentProject project) {
        this.projects.add(project);
    }

    public String toString() {
        return "["+type+" "+name+"];"+projects;
    }

}

