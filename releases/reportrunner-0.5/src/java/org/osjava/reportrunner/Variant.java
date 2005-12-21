package org.osjava.reportrunner;

import java.util.*;

public class Variant implements Nameable {

    private String name;
    private String label;
    private List options = new ArrayList();
    private VariantOption selected;

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

    public VariantOption[] getOptions() {
        return (VariantOption[]) this.options.toArray(new VariantOption[0]);
    }

    public void addOption(VariantOption option) {
        this.options.add(option);
    }

    public VariantOption getSelected() {
        return this.selected;
    }

    public void setSelected(VariantOption option) {
        this.selected = option;
    }

}
