package org.osjava.reportrunner.servlets;

import java.io.*;
import java.util.Date;
import javax.servlet.http.*;
import org.osjava.reportrunner.*;

public class ReportRunnerServlet extends HttpServlet {

    public static final String GROUP = "_group";
    public static final String REPORT = "_report";
    public static final String RENDERER = "_renderer";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String groupName = request.getParameter(GROUP);
        String reportName = request.getParameter(REPORT);
        String rendererName = request.getParameter(RENDERER);

        // which report they want
        Report report = ReportFactory.getReport(groupName, reportName);
        applyResources(report, request);

        // does report require parameters?
        Param[] params = report.getParams();
        if(params != null) {
            for(int i=0; i<params.length; i++) {
                Parser parser = params[i].getParser();
                Object value = null;
                if( Object[].class.isAssignableFrom( params[i].getType() ) ) {
                    String[] parameters = request.getParameterValues(params[i].getName());
                    if(parser != null) {
                        Object[] array = new Object[parameters.length];
                        for(int j=0; j<array.length; j++) {
                            array[j] = parser.parse(parameters[j], params[i].getType());
                        }
                        value = array;
                    } else {
                        value = parameters;
                    } 
                } else {
                    String parameter = request.getParameter(params[i].getName());
                    if(parser != null) {
                        value = parser.parse(parameter, params[i].getType());
                    } else {
                        value = parameter;
                    }
                }

                // else use the params type to call a stock parser; numbers, booleans etc.
                // the stock parsers need to be configurable; so parsers.xml will exist
                // for the moment, hard code

                params[i].setValue(value);
            }
        }

        Renderer[] renderers = report.getRenderers();
        Renderer renderer = null;
        // TODO: Move into Report.getRenderer(String)
        for(int i=0; i<renderers.length; i++) {
            if(rendererName.equals(renderers[i].getName())) {
                renderer = renderers[i];
                break;
            }
        }

        // prepare response
        response.setContentType( renderer.getMimeType() );
        if(!renderer.isInline()) {
            response.setHeader("Content-Disposition", "attachment; filename="+report.getName()+"."+renderer.getExtension());
        }

        // render results
        if(renderer != null && report != null) {

            long rep_start = System.currentTimeMillis();
            Result result = report.execute();
            long rep_time = System.currentTimeMillis() - rep_start;
            
            if(result == null) {
                throw new RuntimeException("Result is null. ");
            }
            if(result.hasNextRow() == false) {
                throw new EmptyReportException();
            }
            result = new FormattingResult(result, report);

            long rend_start = System.currentTimeMillis();
            renderer.display( result, report, response.getOutputStream() );
            long rend_time = System.currentTimeMillis() - rend_start;

            logReport(report, request, rep_time, rend_time);
        } else {
            throw new RuntimeException("Renderer or Report is null. ");
        }

        response.getOutputStream().flush();

    }

    private static synchronized void logReport(Report report, HttpServletRequest request, long report_time, long render_time) {
        FileWriter writer = null;
        try {
            writer = new FileWriter( new File("rrr.log") );
            // add more to this
            StringBuffer buffer = new StringBuffer();
            buffer.append(new Date());
            buffer.append(",");
            buffer.append(report_time);
            buffer.append(",");
            buffer.append(render_time);
            buffer.append(",");
            buffer.append(report.getName());
            writer.write(buffer.toString());
            writer.flush();
            writer.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if(writer != null) { try { writer.close(); } catch(IOException ioe) { } }
        }
    }

    public static void applyResources(Report report, HttpServletRequest request) {
        String[] required = report.getResourceNames();
        for(int i=0; i<required.length; i++) {
            String value = request.getParameter(required[i]);
            if(value != null && !value.equals("")) {
                report.setResource(required[i], request.getParameter(required[i]));
            }
        }
    }
}
