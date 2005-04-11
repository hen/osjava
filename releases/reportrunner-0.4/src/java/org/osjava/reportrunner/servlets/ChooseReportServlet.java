package org.osjava.reportrunner.servlets;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.*;
import org.osjava.reportrunner.*;

public class ChooseReportServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String groupName = request.getParameter(ReportRunnerServlet.GROUP);
        String reportName = request.getParameter(ReportRunnerServlet.REPORT);
        Report report = ReportFactory.getReport(groupName, reportName);

        if(hasResourceChoice(report, request) && (request.getParameter("z") == null || request.getParameter("z").equals("") ) ) {
            // redirect to choose resources page
            response.sendRedirect("enter_resource_params.jsp?"+request.getQueryString());
            return;
        } 

        if( (report.getVariants().length != 0) && (request.getParameter("y") == null || request.getParameter("y").equals("") ) ) {
            // redirect to choose variants page
            response.sendRedirect("enter_variants.jsp?"+request.getQueryString());
            return;
        } 

        applyVariantParams(report, request);
        if(report.getParams().length != 0) {
            // redirect to choose params page
            response.sendRedirect("enter_params.jsp?"+request.getQueryString());
        } else {
            // redirect to choose renderer page
            response.sendRedirect("list_renderers.jsp?"+request.getQueryString());
        }
    }

    public static void applyVariantParams(Report report, HttpServletRequest request) {
        Variant[] variants = report.getVariants();
        for(int i=0; i<variants.length; i++) {
            String key = request.getParameter(variants[i].getName());
            VariantOption[] options = variants[i].getOptions();
            for(int j=0; j<options.length; j++) {
                if(key.equals(options[j].getName())) {
                    VariantOption option = options[j];
                    Param[] params = option.getParams();
                    for(int k=0; k<params.length; k++) {
                        report.addParam(params[k]);
                    }
                    variants[i].setSelected(option);
                    break;
                }
            }
        }
    }

    public static boolean hasResourceChoice(Report report, HttpServletRequest request) {
        List list = Arrays.asList(report.getResourceNames());
        Param[] resourceParams = report.getReportGroup().getResourceParams();
        for(int i=0; i<resourceParams.length; i++) {
            if(list.contains(resourceParams[i].getName())) {
                return true;
            }
        }
        return false;
    }
}
