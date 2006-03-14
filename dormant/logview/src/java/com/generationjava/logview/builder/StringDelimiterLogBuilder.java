package com.generationjava.logview.builder;

import org.apache.commons.lang.StringUtils;

import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogSource;

import com.generationjava.logview.log.SimpleLogEvent;
import com.generationjava.logview.log.SimpleLogField;

// very simple log builder that assumes all String and 
// a simple delimiter. Improve to handle a ".." idea in which 
// delimiter characters are not seen. Use CSVReader code.
public class StringDelimiterLogBuilder extends AbstractLogBuilder {

    private String[] headers;
    private String delimiter;

    public StringDelimiterLogBuilder(String[] hdrs, String delimiter, LogSource src) {
        this.headers = hdrs;
        this.delimiter = delimiter;
        setSource(src);
    }

    public LogEvent parseLogEvent() {
        SimpleLogEvent event = new SimpleLogEvent();
        String entry = getSource().nextEntry();
        String[] values = StringUtils.split(entry, this.delimiter);
        int sz = values.length;
        for(int i=0; i<sz; i++) {
            SimpleLogField field = new SimpleLogField(this.headers[i], values[i]);
            event.set(this.headers[i], field);
        }
        return event;
    }

    public Object cloneObject() throws CloneNotSupportedException {
        StringDelimiterLogBuilder builder = new StringDelimiterLogBuilder(headers, delimiter, getSource());
        return builder;
    }

}
