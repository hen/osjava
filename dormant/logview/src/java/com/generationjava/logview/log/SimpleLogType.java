package com.generationjava.logview.log;

import com.generationjava.logview.LogType;
import com.generationjava.lang.Constant;

public class SimpleLogType extends Constant implements LogType {

    public SimpleLogType(String name) {
        super(name);
    }

    public String getName() {
        return (String)getValue();
    }

}
