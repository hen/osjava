package org.osjava.jndi;

import java.util.Properties;
import java.util.Hashtable;
import javax.sql.DataSource;

class TestUtils {

    static DataSource createFakeDS(String url, String driver, String user, String passwd, String name, String delimiter) {
        Properties props = new Properties();
        // even though delimiter is a /, we use . here for the 
        // test. This is because the dot needs to be in the file
        props.setProperty(name+"/url", url);
        props.setProperty(name+"/driver", driver);
        props.setProperty(name+"/user", user);
        props.setProperty(name+"/password", passwd);
        props.setProperty(name+"/type", "javax.sql.DataSource");
        PropertiesDataSource fake = new PropertiesDataSource(props, new Hashtable(), delimiter);
        fake.setName(name);
        return fake;
    }

}
