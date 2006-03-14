package org.osjava.multidoc;

public class DocumentPackage {

    private String name;
    private String url;
    private String description;

    public DocumentPackage(String name, String url, String description) {
        this.name = name;
        this.url = url;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.name + " - '" + this.description + "'";
    }

    public boolean equals(Object obj) {
        if(obj instanceof DocumentPackage) {
            DocumentPackage dp = (DocumentPackage)obj;
            return (this.name == dp.name);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }

}
