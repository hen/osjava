package org.osjava.logview.text;

import org.apache.commons.lang.StringUtils;

public class DelimitedLineParser {

    private String delimiter;

    public DelimitedLineParser(String delimiter) {
        this.delimiter = delimiter;
    }

    public String[] parse(String line) {
        String[] values = StringUtils.split(line, this.delimiter);
        return values;
    }

}
