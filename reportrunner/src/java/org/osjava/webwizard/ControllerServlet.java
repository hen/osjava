package org.osjava.webwizard;

import org.osjava.reportrunner.*;
import org.osjava.reportrunner.servlets.ReportRunnerServlet;

import org.apache.velocity.Template;
import org.apache.velocity.servlet.VelocityServlet;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerServlet extends VelocityServlet {

    // expect to be a url of form:  http://foo.example.com/reports/controller/list_groups.vm
    public Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctxt) {
        // get path info such that the vm filename is known
        String file = request.getPathInfo();

        ctxt.put("RRR", new FieldMethodizer( new Constants() ) );

        ctxt.put("request", request);

        String groupName = request.getParameter(Constants.GROUP);
        if(groupName == null || "".equals(groupName)) {
            ReportGroup[] groups = ReportFactory.getReportGroups();
            ctxt.put("groups", groups);
        } else {
            ReportGroup group = ReportFactory.getReportGroup(groupName);
            ctxt.put("group", group);

            String reportName = request.getParameter(Constants.REPORT);
            if(reportName == null || "".equals(reportName)) {
                Report[] reports = ReportFactory.getReports(groupName);
                ctxt.put("reports", reports);
            } else {
                Report report = ReportFactory.getReport(groupName, reportName);
                ctxt.put("report", report);

                String resourceSet = request.getParameter("z");
                if(resourceSet != null) {
                    ReportRunnerServlet.applyResources(report, request);
                }
            }
        }

        // if list_groups 

        // if list_reports

        // if choose_report
        if("/choose_report".equals(file)) {
            file = handleChooseReport(request, ctxt);
        }

        // if enter_resource_params

        // if enter_params

        // if enter_variants

        // if list_renderers

        String template = "org/osjava/webwizard/templates"+file+".vm";
        try {
            return getTemplate(template);
        } catch( ResourceNotFoundException rnfe ) {
            rnfe.printStackTrace();
            // couldn't find the template
            throw new RuntimeException("Failed to find resource["+template+"]: " + rnfe);
        } catch( ParseErrorException pee ) {
            pee.printStackTrace();
            // syntax error : problem parsing the template
            throw new RuntimeException("Failed to parse resource["+template+"]: " + pee);
        } catch( Exception e ) {
            e.printStackTrace();
            // syntax error : general exception
            throw new RuntimeException("FIX THIS: " + e);
        }

    }

    private String handleChooseReport(HttpServletRequest request, Context ctxt) {

        Report report = (Report) ctxt.get("report");

        if(ReportRunnerServlet.hasResourceChoice(report, request) && (request.getParameter("z") == null || request.getParameter("z").equals("") ) ) {
            return "/enter_resource_params";
        } 

        if( (report.getVariants().length != 0) && (request.getParameter("y") == null || request.getParameter("y").equals("") ) ) {
            return "/enter_variants";
        } 

        ReportRunnerServlet.applyVariantParams(report, request);
        if(report.getParams().length != 0) {
            return "/enter_params";
        } else {
            return "/list_renderers";
        }
    }
}
