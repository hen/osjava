package org.osjava.multidoc;

import java.io.IOException;
import java.io.Writer;
import java.io.File;

public interface MultidocGenerator {

    void generate(File targetDirectory, DocumentSite site, Document document) throws IOException;
    // remove these write ones?
    void writeProjectFrame(Writer writer, DocumentSite site, Document document) throws IOException;
    void writePackagesFrame(Writer writer, DocumentSite site, Document document) throws IOException;
    void writeOverviewFrame(Writer writer, DocumentSite site, Document document) throws IOException;

}
