package com.generationjava.logview.filter;

import com.generationjava.logview.LogField;
import com.generationjava.logview.LogFilter;
import com.generationjava.logview.LogEvent;

public class ContainsFilter implements LogFilter {

    private String field;
    private String substring;
    private boolean positive;

    public ContainsFilter(String field, String substring) {
        this(field, substring, true);
    }

    // positive match = true/false
    public ContainsFilter(String field, String substring, boolean positive) {
        this.field = field;
        this.substring = substring;
        this.positive = positive;
    }

    public LogEvent filter(LogEvent event) {
        LogField logField = event.get(this.field);
        String value = logField.getValue().toString();
        int index = value.indexOf(substring);

        boolean ret = true;
        if(index < 0) {
            ret = false;
        }

        if(this.positive == false) {
            ret = !ret;
        }
        
        if(ret == true) {
            return event;
        } else {
            return null;
        }
    }

}
