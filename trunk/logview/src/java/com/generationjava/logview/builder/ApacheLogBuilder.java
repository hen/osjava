package com.generationjava.logview.builder;

import com.generationjava.logview.LogSource;

// handles conversion of apache log format to logview format
// specials: combined, common, referer, agent
// LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"" combined
// LogFormat "%h %l %u %t \"%r\" %>s %b" common
// LogFormat "%{Referer}i -> %U" referer
// LogFormat "%{User-agent}i" agent
public class ApacheLogBuilder extends FormattedLogBuilder {

    static public final String COMMON   = "${h} ${l} ${u} ${t} \"${r}\" ${s} ${b}";
    static public final String COMBINED = "${h} ${l} ${u} ${t} \"${r}\" ${s} ${b} \"${Referer}\" \"${User-Agent}\"";
    static public final String AGENT    = "${Referer} -> ${U}";
    static public final String REFERER  = "${User-agent}";

    public ApacheLogBuilder(String format, LogSource source) {
        super(format, source);
    }

    public void setFormat(String format) {
        if(format.charAt(0) == '$') {
            super.setFormat(format);
        } else {
            // parse as an apache and convert on fly to the above
        }
    }

}
