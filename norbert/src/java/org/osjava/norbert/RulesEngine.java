package org.osjava.norbert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains a series of Rules. It then runs a path against these 
 * to decide if it is allowed or not. 
 */
public class RulesEngine {

    private List rules;

    public RulesEngine() {
        this.rules = new ArrayList();
    }

    public void allowPath(String path) {
        add( new AllowedRule(path) );
    }

    public void disallowPath(String path) {
        add( new DisallowedRule(path) );
    }

    public void add(Rule rule) {
        this.rules.add(rule);
    }

    public boolean isAllowed(String path) {

        Iterator iterator = this.rules.iterator();
        while(iterator.hasNext()) {
            Rule rule = (Rule)iterator.next();
            Boolean test = rule.isAllowed(path);
            if(test != null) {
                return test.booleanValue();
            }
        }

        return true;
    }

    public boolean isEmpty() {
        return this.rules.isEmpty();
    }

    public String toString() {
        return "RulesEngine: " + this.rules;
    }

}
