package com.generationjava.logview;

// holds the output of a Reportlet (?)
// how to chain reportlets???

// ability to set a Properties of presentation options
// ability to set a Link. add new Link("some url") against column "ip"
public interface Report {

    public void setLink(String field, String link);

}
