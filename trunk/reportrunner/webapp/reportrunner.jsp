<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.*" %>
ReportRunner:
<%
String reportName = request.getParameter("_report");
String rendererName = request.getParameter("_renderer");

        // ask user which report they want
        Report report = ReportFactory.getReport(reportName);

        if(report instanceof org.osjava.reportrunner.reports.SqlReport) {
            ((org.osjava.reportrunner.reports.SqlReport)report).setDataSource("jdbc/rollerdb");
        }

        // does report require parameters?
        Param[] params = report.getParams();
        if(params != null) {
            for(int i=0; i<params.length; i++) {
                Object value = Convert.convert(request.getParameter(params[i].getName()), params[i].getType() );
                params[i].setValue(value);
            }
            out.write("Data: "+Arrays.asList(params));
        }

        // what renderers does the report work with?

        // which renderer does user want?
        Renderer renderer = ReportFactory.getRenderer(rendererName);

        out.write("["+renderer.getMimeType()+"]");

        // bang
        if(renderer != null && report != null) {
            renderer.display( report, out );
        }
%>
