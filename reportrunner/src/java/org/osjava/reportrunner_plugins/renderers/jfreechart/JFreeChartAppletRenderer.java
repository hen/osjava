package org.osjava.reportrunner_plugins.renderers.jfreechart;

import java.io.*;
import java.util.*;

import com.generationjava.io.xml.*;

import org.osjava.reportrunner.*;

import org.osjava.reportrunner_plugins.*;

public class JFreeChartAppletRenderer extends AbstractRenderer {

    private String width;
    private String height;

    public void setWidth(String width) { this.width = width; }
    public void setHeight(String height) { this.height = height; }

    public void display(Result result, Report report, Writer out) throws IOException {
        out.write( new RRTool().generateAppletTag( getVariables(), this.width, this.height, null ) );
    }

}
