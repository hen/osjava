package org.osjava.reportrunner_plugins.renderers.timechart;

import java.io.*;
import java.util.*;

import com.generationjava.io.xml.*;

import org.osjava.reportrunner.*;

public class TimeChartRenderer extends AbstractRenderer {

    public void display(Result result, Report report, Writer out) throws IOException {
        PrettyPrinterXmlWriter writer = new PrettyPrinterXmlWriter(new SimpleXmlWriter(out));
        writer.writeEntity("html")
               .writeEntity("applet")
                .writeAttribute("archive", "timechart/timechart.jar,timechart/jfreechart-0.9.20.jar,timechart/jcommon-0.9.5.jar,timechart/gj-xml-1.1.jar")
                .writeAttribute("code", "org.osjava.reportrunner_plugins.renderers.timechart.TimeChartApplet.class")
                .writeAttribute("width", "400")
                .writeAttribute("height", "400")
                .writeEntity("param")
                 .writeAttribute("name", "timestamp-field")
                 .writeAttribute("value", "timestamp")
                .endEntity()
               .endEntity()
              .endEntity();
        writer.close();
    }

}
