package org.osjava.reportrunner;

public class Choice {

    private String value;
    private String label;

    public Choice(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() { return this.value; }
    public void setValue(String value) { this.value = value; }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }
}
