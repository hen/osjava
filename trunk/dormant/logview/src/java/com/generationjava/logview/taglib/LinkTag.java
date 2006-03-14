package com.generationjava.logview.taglib;

import com.generationjava.taglib.AbstractBodyTag;

/**
 * <logreport:param name="order" value="reverse"/>
 *
 * @author bayard@generationjava.com
 * @version 0.1, 20011030
 */
public class LinkTag extends AbstractBodyTag {

    private String field;
    private String link;

    /**
     * Empty constructor. 
     */
    public LinkTag() {
        super();
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Initialise any properties to default values.
     */
    public void initAttributes() {
    }

}
