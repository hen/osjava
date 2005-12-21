package org.osjava.reportrunner;

import java.util.*;

public class VariantOption implements Nameable {

    private String name;
    private String label;
    private String value;
    private List params = new ArrayList();

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

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Param[] getParams() {
        return (Param[]) this.params.toArray(new Param[0]);
    }

    public void addParam(Param param) {
        this.params.add(param);
    }

}
