package org.osjava.norbert;

public class DisallowedRule extends AbstractRule {

    public DisallowedRule(String path) {
        super(path);
    }

    public Boolean isAllowed(String query) {
        if("".equals(super.getPath())) {
            return null;
        }
        boolean test = query.startsWith( super.getPath() );
//        System.err.println("DIS: "+query+" vs "+super.getPath());
        if(!test) {
            return null;
        } else {
            return Boolean.FALSE;
        }
    }


}
