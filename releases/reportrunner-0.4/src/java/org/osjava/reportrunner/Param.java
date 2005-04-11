package org.osjava.reportrunner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class Param implements Nameable {

    private String name;
    private String label;
    private String widget;
    private String description;
    private String defaultValue;
    private Class type = String.class;
    private Object value;
    private Object originalValue;
    private String binding;
    private Parser parser;
    private String[] options;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDefault() {
        return this.defaultValue;
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Class getType() {
        return this.type;
    }

    public void setType(String typeName) {
        if(typeName == null) {
            return;
        }
        if(typeName.endsWith("[]")) {
            typeName = "[L" + typeName.substring(0,typeName.length() - "[]".length()) + ";";
        }
        try {
            this.type = Thread.currentThread().getContextClassLoader().loadClass(typeName);
        } catch(ClassNotFoundException cnfe) {
        }
    }

    public Parser getParser() {
        return this.parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getOriginalValue() {
        return this.originalValue;
    }

    public void setOriginalValue(Object originalValue) {
        this.originalValue = originalValue;
    }

    public String getBinding() {
        return this.binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getWidget() {
        return this.widget;
    }

    public void setWidget(String widget) {
        this.widget = widget;
    }

    // debug
    public String toString() { 
        return ReflectionToStringBuilder.toString( this );
    }

    public void setOptions(String options) {
        this.options = StringUtils.split(options, ",");
    }

    public String[] getOptions() {
        return this.options;
    }

}
