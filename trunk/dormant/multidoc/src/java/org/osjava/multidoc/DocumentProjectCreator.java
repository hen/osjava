package org.osjava.multidoc;

import java.io.IOException;

public interface DocumentProjectCreator {

    DocumentProject create(String url) throws IOException;

}
