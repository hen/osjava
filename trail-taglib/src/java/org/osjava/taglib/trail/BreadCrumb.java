package org.osjava.taglib.trail;

public class BreadCrumb {
    private String url;
    private String label;

    public BreadCrumb(String url, String label) {
        this.url = url;
        this.label = label;
    }

    public String getUrl() { return this.url; }
    public String getLabel() { return this.label; }

    public int hashCode() { return this.url.hashCode() & this.label.hashCode(); }

    public boolean equals(Object obj) {
        if( !(obj instanceof BreadCrumb) ) {
            return false;
        }

        BreadCrumb b = (BreadCrumb) obj;

        return this.url.equals(b.url) && this.label.equals(b.label);
    }

    public String toString() {
        return "[" + this.label + "|" + this.url + "]";
    }

}
