package org.osjava.reportrunner;

import javax.sql.DataSource;
import java.net.URL;
import java.io.File;
import java.net.MalformedURLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Resource {

    private String name;
    private String label;
    private String uri;
    private String type;

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }

    public String getUri() { return this.uri; }
    public void setUri(String uri) { this.uri = uri; }

    public String getType() { return this.type; }
    public void setType(String type) { this.type = type; }

    public Object accessResource() {
        if("javax.sql.DataSource".equals(this.type)) {
            DataSource ds = null;
            try {
                Context ctxt = new InitialContext();
                Context envCtxt = (Context) ctxt.lookup("java:comp/env");
                ds = (DataSource) envCtxt.lookup(this.uri);
            } catch(NamingException ne) {
                ne.printStackTrace();
            }
            return ds;
        } else 
        if("java.io.File".equals(this.type)) {
            return new File(this.uri);
        } else 
        if("java.net.URL".equals(this.type)) {
            URL url = null;
            try {
                url = new URL(this.uri);
            } catch(MalformedURLException murle) {
                murle.printStackTrace();
            }
            return url;
        } else {
            return this.uri;
        }
    }
}
