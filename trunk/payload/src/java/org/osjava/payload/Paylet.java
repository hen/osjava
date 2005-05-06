package org.osjava.payload;

import java.util.Properties;

public interface Paylet {

    /**
     * Execute the paylet with the provided arguments. 
     *
     * @param args Arguments to the paylet, never null.
     * @param payloadProperties The baked in payload.properties.
     * @param properties The user supplied deployment properties. 
     */
    void execute(String[] args, Properties payloadProperties, Properties properties);

}
