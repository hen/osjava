package com.genscape.reportrunner.osjava;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.osjava.reportrunner.AbstractRenderer;
import org.osjava.reportrunner.Report;
import org.osjava.reportrunner.ReportException;
import org.osjava.reportrunner.Result;

public class VelocityRenderer extends AbstractRenderer {
	private String template;
	
	public void display(Result result, Report report, Writer out) throws IOException {
		throw new RuntimeException("This should not be used with a Writer. ");
	}

	public void display(Result result, Report report, OutputStream out)	throws IOException {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		
		try {
			//initialize velocity
			Velocity.init(props);
		} catch (Exception e) {
			throw new ReportException("Could not initialize Velocity", e);
		}	
			
		//get the template		
		Template template = null;
		try {			
			template = Velocity.getTemplate(this.template);
		} catch (Exception e) {
			throw new ReportException("Could not locate template: " + this.template + " in :" + this, e);
		}
		
		//create a context for velocity
		VelocityContext context = new VelocityContext();		
		
		//put result and report to the context
		context.put("result", result);
		context.put("report", report);
		
		//hand over passed-in variables to the context
		Map variables = this.getVariables();
		context.put("variables", variables);
		
		//convert results to arraylist to pass to velocity
		Collection results = new ArrayList();
		while(result.hasNextRow()) {						
			Object[] row = result.nextRow();
			Collection resultRow = Arrays.asList(row);						
			results.add(resultRow);		
		}			
		context.put("results", results);
		
		//create a Writer using the OutputStream			
		Writer writer = new BufferedWriter(new OutputStreamWriter(out));			
		
		try {
			template.merge(context, writer);
		} catch (Exception e) {
			throw new ReportException("Error merging Velocity template", e);
		}
		
		writer.close();
		
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
