package org.osjava.norbert;

public interface Rule {

    /**
     * Boolean.TRUE means it is allowed. 
     * Boolean.FALSE means it is not allowed.
     * null means that this rule is not applicable.
     */
    Boolean isAllowed(String path);

}
