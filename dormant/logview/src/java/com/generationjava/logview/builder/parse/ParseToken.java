package com.generationjava.logview.builder.parse;

import com.generationjava.lang.Constant;

public interface ParseToken {

    // need a registry of Types. classregistry?
    // in LogTypes now. Depreccate this???????
    static public final Constant LITERAL = new Constant("Literal");
    static public final Constant IP = new Constant("IP");
    static public final Constant URL = new Constant("Url");
    static public final Constant EMAIL = new Constant("Email");
    static public final Constant DATE = new Constant("Date");
    static public final Constant STRING = new Constant("String");
    static public final Constant NUMBER = new Constant("Number");
    static public final Constant DECIMAL = new Constant("Decimal");
    static public final Constant FILENAME = new Constant("Filename"); // url?
    // ?? BROWSER? USERNAME? EVENT? CODE?

    public void setValue(String str);

    public String getValue();

    public void setType(String str);

    public String getType();

}
