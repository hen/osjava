package org.osjava.multidoc;

import java.io.IOException;
import java.io.Writer;

public interface MultidocGenerator {

    void writeProjectFrame(Writer writer, DocumentSite site) throws IOException;
    void writePackagesFrame(Writer writer, DocumentSite site) throws IOException;
    void writeOverviewFrame(Writer writer, DocumentSite site) throws IOException;

}
