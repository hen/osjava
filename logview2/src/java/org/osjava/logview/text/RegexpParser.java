package org.osjava.logview.text;

import java.util.regex.*;

public class RegexpParser {

    private String re;

    public RegexpParser(String re) {
        this.re = re;
    }

    public String[] parse(String line) {
        Pattern pattern = Pattern.compile(this.re);
        Matcher matcher = pattern.matcher(line);
        matcher.matches();
        int sz = matcher.groupCount();
        String[] values = new String[sz];
        for(int i=1; i<=sz; i++) {
            values[i-1] = matcher.group(i);
        }
        return values;
    }

}
