package org.osjava.scraping;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class NullPage extends AbstractPage {

    public NullPage() {
    }

    public Reader read() throws IOException {
        return new StringReader("");
    }

}
