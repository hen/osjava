package org.osjava.scraping;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class MemoryPage extends AbstractPage {

    private String page;

    public MemoryPage(String page) {
        this.page = page;
    }

    public Reader read() throws IOException {
        return new StringReader(this.page);
    }

}
