package org.osjava.norbert;

public class AllowedRule extends AbstractRule {

    public AllowedRule(String path) {
        super(path);
    }

    public Boolean isAllowed(String query) {
        if("".equals(super.getPath())) {
            // What does the spec say here? 
            // I think it says to ignore any 
            // line without a match
            return null;
        }
        boolean test = query.startsWith( super.getPath() );
//        System.err.println("ALL: "+query+" vs "+super.getPath());
        if(!test) {
            return null;
        } else {
            return Boolean.TRUE;
        }
    }

}
