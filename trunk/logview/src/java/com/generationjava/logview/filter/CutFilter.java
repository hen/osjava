package com.generationjava.logview.filter;

import com.generationjava.logview.LogField;
import com.generationjava.logview.LogFilter;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.log.SimpleLogField;
import com.generationjava.logview.log.OverlayLogEvent;

public class CutFilter implements LogFilter {

    private String field;
    private int cut;

    public CutFilter(String field, int cut) {
        this.field = field;
        this.cut = cut;
    }

    public LogEvent filter(LogEvent event) {
        LogField logField = event.get(this.field);
        String value = logField.getValue().toString();
        value = value.substring(0,this.cut);

        OverlayLogEvent ole = new OverlayLogEvent(event);
        ole.overlay( logField.getName(), new SimpleLogField(logField.getName(),
                                               logField.getType(), value )
                   );

        event = ole;

        return event;
    }

}
