/*
 *   Copyright 2003-2004 Lance Lavandowska
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.osjava.atom4j.digester;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.digester.xmlrules.DigesterRulesSource;
import org.osjava.atom4j.pojo.Content;

/**
 * This class can be used to generate a RuleSet either programatically
 * or through the FromXmlRuleSet parser.
 * Created on Apr 8, 2004
 * @author lance.lavandowska
 */
public class ContentRuleSet implements RuleSet, DigesterRulesSource
{
    protected static String[] overriddenContentAttributeNames = {"type", "xml:lang"};
    protected static String[] overriddenContentPropertyNames = {"mimeType", "language"};
    
    protected String path = "";
    protected String namespaceURI;
    protected String setMethod;
    
    public ContentRuleSet(String path, String setMethod) 
    {
        super();
        this.path = path;
        this.setMethod = setMethod;
    }
    
    public ContentRuleSet(String path, String setMethod, String namespace) 
    {
        super();
        this.path = path;
        this.setMethod = setMethod;
        this.namespaceURI = namespace;
    }
    
    /* 
     * @see org.apache.commons.digester.RuleSet#addRuleInstances(org.apache.commons.digester.Digester)
     */
    public void addRuleInstances(Digester digester)
    {
        digester.addObjectCreate( path, Content.class.getName());
        digester.addSetProperties(path, 
                                  overriddenContentAttributeNames, 
                                  overriddenContentPropertyNames);
        digester.addRule(path, new ContentRule());  
        digester.addSetNext(path, setMethod, Content.class.getName());  
    }

    /* 
     * @see org.apache.commons.digester.xmlrules.DigesterRulesSource#getRules(org.apache.commons.digester.Digester)
     */    
    public void getRules(Digester digester)
    {
        addRuleInstances(digester);
    }

    /* 
     * @see org.apache.commons.digester.RuleSet#getNamespaceURI()
     */
    public String getNamespaceURI()
    {
        return namespaceURI;
    }
}
