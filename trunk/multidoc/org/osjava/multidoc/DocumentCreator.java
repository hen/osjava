package org.osjava.multidoc;

import java.io.IOException;

public interface DocumentCreator {

    Document create(String url) throws IOException;

}
