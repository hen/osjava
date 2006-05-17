package org.osjava.reportrunner_plugins;

import java.util.*;

import java.net.URLEncoder;

import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.Variant;
import org.osjava.reportrunner.Param;

// partly for use by Velocity
public class RRTool {

    public RRTool() {
    }

    public String test(Map m, String p) { return "FOO: "+p+" "+m.size(); }

    public String generateAppletTag(Map vars, String width, String height, String serUrl) {
        return generateAppletTag(vars, width, height, serUrl, null, null);
    }

    public String generateAppletTag(Map vars, String width, String height, String serUrl, String appletClass, String appletClasspath) {

        if(appletClass == null) {
            appletClass =  "org.osjava.reportrunner_plugins.renderers.jfreechart.JFreeChartApplet.class";
        }
        if(appletClasspath == null) {
            appletClasspath = "jfreechart/commons-codec-1.3.jar,jfreechart/commons-io-1.0.jar,jfreechart/commons-lang-2.0.jar,jfreechart/reportrunner-SNAPSHOT.jar,jfreechart/jfreechart-0.9.21.jar,jfreechart/jcommon-0.9.6.jar";
        }

        try {
        String paramIE = "";
        String paramNN = "";

        if(vars != null) {
            Iterator keys = vars.keySet().iterator();
            while(keys.hasNext()) {
                Object key = keys.next();
                paramIE = paramIE + "    <PARAM NAME=\""+key+"\" VALUE=\""+vars.get(key)+"\">";
                paramNN = paramNN + "    "+key+" = "+vars.get(key)+" \\";
            }
        }

        if(serUrl != null) {
            paramIE = paramIE + "    <PARAM NAME=\"serUrl\" VALUE=\""+serUrl+"\">";
            paramNN = paramNN + "    serUrl = "+serUrl+" \\";
        }

        String html =
        "<!--\"CONVERTED_APPLET\"-->" +
        "<!-- HTML CONVERTER -->" +
        "<script language=\"JavaScript\" type=\"text/javascript\"><!--" +
        "    var _info = navigator.userAgent;" +
        "    var _ns = false;" +
        "    var _ns6 = false;" +
        "    var _ie = (_info.indexOf(\"MSIE\") > 0 && _info.indexOf(\"Win\") > 0 && _info.indexOf(\"Windows 3.1\") < 0);" +
        "//--></script>" +
        "    <comment>" +
        "        <script language=\"JavaScript\" type=\"text/javascript\"><!--" +
        "        var _ns = (navigator.appName.indexOf(\"Netscape\") >= 0 && ((_info.indexOf(\"Win\") > 0 && _info.indexOf(\"Win16\") < 0 && java.lang.System.getProperty(\"os.version\").indexOf(\"3.5\") < 0) || (_info.indexOf(\"Sun\") > 0) || (_info.indexOf(\"Linux\") > 0) || (_info.indexOf(\"AIX\") > 0) || (_info.indexOf(\"OS/2\") > 0) || (_info.indexOf(\"IRIX\") > 0)));" +
        "        var _ns6 = ((_ns == true) && (_info.indexOf(\"Mozilla/5\") >= 0));" +
        "//--></script>" +
        "    </comment>" +
        "" +
        "<script language=\"JavaScript\" type=\"text/javascript\"><!--" +
        "    if (_ie == true) document.writeln('<object classid=\"clsid:8AD9C840-044E-11D1-B3E9-00805F499D93\" WIDTH = \"100\" HEIGHT = \"100\"  codebase=\"http://java.sun.com/update/1.4.2/jinstall-1_4-windows-i586.cab#Version=1,4,0,0\"><noembed><xmp>');" +
        "    else if (_ns == true && _ns6 == false) document.writeln('<embed ' +" +
        "	    'type=\"application/x-java-applet;version=1.4\" \\" +
        "            CODE = \""+appletClass+"\" \\" +
        "            ARCHIVE = \""+appletClasspath+"\" \\" +
        "            WIDTH = \""+width+"\" \\" +
        "            HEIGHT = \""+height+"\" \\" +
        paramNN +
        "	    'scriptable=false ' +" +
        "	    'pluginspage=\"http://java.sun.com/products/plugin/index.html#download\"><noembed><xmp>');" +
        "//--></script>" +
        "<applet  CODE = \""+appletClass+"\" ARCHIVE = \""+appletClasspath+"\" WIDTH = \""+width+"\" HEIGHT = \""+height+"\"></xmp>" +
        "    <PARAM NAME = CODE VALUE = \""+appletClass+"\" >" +
        "    <PARAM NAME = ARCHIVE VALUE = \""+appletClasspath+"\" >" +
        "    <param name=\"type\" value=\"application/x-java-applet;version=1.4\">" +
        "    <param name=\"scriptable\" value=\"false\">" +
        "" + paramIE +
        "" +
        "" +
        "</applet>" +
        "</noembed>" +
        "</embed>" +
        "</object>" +
        "" +
        "<!--\"END_CONVERTED_APPLET\"-->";

        return html;
        } catch(Exception e) {
            e.printStackTrace();
            return "EXCEPTION";
        }
    }

    /**
     * Does not include the _report, _group or _renderer parts of a 
     * Report's query string.
     */
    public static String generateQueryString(Report report, Collection ignore) {
        StringBuffer url = new StringBuffer();

        // Variants
        Variant[] variant = report.getVariants();
        for (int i=0;i<variant.length;i++) {
            String name = variant[i].getName();
            if(ignore.contains(name)) {
                continue;
            }
            String selectedName = variant[i].getSelected().getName();
            url.append("&" + URLEncoder.encode(name) + "=" + URLEncoder.encode(selectedName));
        }

        // Params
        Param[] param = report.getParams();
        for (int i=0;i<param.length;i++) {
            String name = param[i].getName();
            if(ignore.contains(name)) {
                continue;
            }
            Object value = param[i].getOriginalValue();
            url.append("&" + URLEncoder.encode(name) + "=");
            if (value instanceof Object[]) {
                Object[] values = (Object[]) value;
                for (int j=0;j<values.length;j++) {
                    String valueString = URLEncoder.encode(values[j].toString());
                    url.append("&" + URLEncoder.encode(name) + "=" + valueString);
                }
            } else {
                String valueString = URLEncoder.encode(value.toString());
                url.append(valueString);
            }
        }

        return url.toString();
    }

}
