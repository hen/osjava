package org.osjava.jndi;

import java.util.Properties;
import org.osjava.jndi.util.HierarchicalMap;
import java.util.Hashtable;
import javax.sql.DataSource;

class TestUtils {

    static DataSource createFakeDS(String url, String driver, String user, String passwd, String name, String delimiter) {
        HierarchicalMap props = new HierarchicalMap("/");
        // even though delimiter is a /, we use . here for the 
        // test. This is because the dot needs to be in the file
        props.put(name+"/url", url);
        props.put(name+"/driver", driver);
        props.put(name+"/user", user);
        props.put(name+"/password", passwd);
        props.put(name+"/type", "javax.sql.DataSource");
        PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), delimiter);
        fake.setName(name);
        return fake;
    }

}
