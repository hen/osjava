package org.osjava.norbert;

public abstract class AbstractRule implements Rule {

    private String path;

    public AbstractRule(String path) {
        this.path = path.trim();
    }

    public String getPath() {
        return this.path;
    }

    public abstract Boolean isAllowed(String query);

    public String toString() {
        return getClass().getName() + " on " + this.path;
    }

}
