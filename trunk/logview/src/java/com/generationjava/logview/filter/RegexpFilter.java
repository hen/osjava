package com.generationjava.logview.filter;

import com.generationjava.logview.LogField;
import com.generationjava.logview.LogFilter;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogViewRuntimeException;

import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.MalformedPatternException;

public class RegexpFilter implements LogFilter {

    private String field;
    private Pattern pattern;
    private PatternMatcher matcher = new Perl5Matcher();

    public RegexpFilter(String field, String regexp) {
        this.field = field;
        Perl5Compiler p5c = new Perl5Compiler();
        try {
            this.pattern = p5c.compile(regexp);
        } catch(MalformedPatternException mpe) {
            throw new LogViewRuntimeException("Bad regexp: "+regexp, mpe);
        }
    }

    public LogEvent filter(LogEvent event) {
        LogField logField = event.get(this.field);
        String value = logField.getValue().toString();
        
        boolean match = this.matcher.contains(value, this.pattern);
        
        if(match) {
            return event;
        } else {
            return null;
        }
    }

}
