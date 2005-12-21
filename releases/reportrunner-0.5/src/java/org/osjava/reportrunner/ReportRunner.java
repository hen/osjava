
package org.osjava.reportrunner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osjava.reportrunner.servlets.ReportRunnerServlet;

public class ReportRunner {
    
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: ReportRunner group=groupname report=reportname renderer=renderername <parameters>");
            System.out.println("If any required information is missing, ReportRunner will prompt for this information.");
        }
                       
        try {
            ReportRunner rr = new ReportRunner();
            rr.runReport(args);            
        } catch (Exception e) {  
            e.printStackTrace();
        } 
    }
    
    public void runReport(String[] args) {
        
        String groupName = null;
        String reportName = null;
        String rendererName = null;
                
        ParameterMap parameterMap = new ParameterMap();
        
        for (int i = 0; i < args.length; i++) {
            String[] nameValuePair = args[i].split("=");
            
            if ("group".equalsIgnoreCase(nameValuePair[0])) {
            	groupName = nameValuePair[1];
            } else if ("report".equalsIgnoreCase(nameValuePair[0])) {
            	reportName = nameValuePair[1];
            } else if ("renderer".equalsIgnoreCase(nameValuePair[0])) {
            	rendererName = nameValuePair[1];            	
            } else {
            	parameterMap.set(nameValuePair[0], nameValuePair[1]);
            }
        }
        
        // Collect any missing information which is required
        if (groupName == null) {
        	groupName = this.selectReportGroup();
        }
        
        if (reportName == null) {
        	reportName = this.selectReport(groupName);
        }
                                               
        // which report they want
        Report report = ReportFactory.getReport(groupName, reportName);
        
        // Choose resources if applicable
        if(hasResourceChoice(report)) {
        	System.out.println("Select Resource(s) to run against");
        	System.out.println("---------------------------------");
        	
        	Param[] params = report.getReportGroup().getResourceParams();
        	List list = Arrays.asList(report.getResourceNames());            
        	for(int i=0; i<params.length; i++) {
                if(!list.contains(params[i].getName())) {
                    continue;
                }
                List choiceList = new ArrayList();
                Choice[] choices = report.getReportGroup().getResourceParamChoices(params[i]);
                for(int j=0; j<choices.length; j++) {
                	choiceList.add(choices[i]);
                	System.out.print((j + 1) + ") ");
                	System.out.println(choices[j].getLabel());                	
                }
                String resourceChoice = ((Choice) choiceList.get(this.getChoice(choiceList.size()) - 1)).getValue();
                parameterMap.set(params[i].getName(), resourceChoice);
            }                                    
        }
        
        // Choose variants if applicable
        Variant[] variants = report.getVariants();
        if (variants.length != 0) {
        	boolean headerPrinted = false;
        	        
        	for(int i=0; i<variants.length; i++) {
        		// If user supplied this via command line, skip this variant
        		if (parameterMap.getValue(variants[i].getName()) != null) {
        			continue;
        		}
        		
        		if (!headerPrinted) {
        			System.out.println("Select Variants");
                	System.out.println("---------------");
                	headerPrinted = true;
        		}
        		
        		List choiceList = new ArrayList();
        		VariantOption[] options = variants[i].getOptions();
        		for(int j=0; j<options.length; j++) {        		
        			choiceList.add(options[j].getName());
        			System.out.print((j + 1) + ") ");
        			System.out.println(options[j].getLabel());
        		}         	
        		String variantChoice = (String) choiceList.get(this.getChoice(choiceList.size()) - 1);
        		parameterMap.set(variants[i].getName(), variantChoice);
        	}
        }
      
        applyVariantParams(report, parameterMap);
        
        // Parameters
        Param[] params = report.getParams();
        if (params != null && params.length != 0) {
        	for (int i = 0; i < params.length; i++) {
        		boolean headerPrinted = false;
        		
        		// If user supplied this via command line, skip this parameter
        		if (parameterMap.getValue(params[i].getName()) != null) {
        			continue;
        		}
        		
        		if (!headerPrinted) {
        			System.out.println("Enter Parameters");
                	System.out.println("----------------");
                	headerPrinted = true;
        		}
        		
        		System.out.print(params[i].getLabel() + ": ");
        		String value = this.getString();
        		parameterMap.set(params[i].getName(), value);
        	}
        }
        
        // Renderers
        if (rendererName == null) {
        	rendererName = this.selectRenderer(report);                		
        }
        
        //System.out.println(new java.util.Date()+" Applying resources");
        applyResources(report, parameterMap);      
        //System.out.println(new java.util.Date()+" Applying params");
        
        // does report require parameters?        
        if(params != null) {
            for(int i=0; i<params.length; i++) {
                Parser parser = params[i].getParser();
                Object value = null;
                if( Object[].class.isAssignableFrom( params[i].getType() ) ) {                    
                    String[] parameters = parameterMap.getValues(params[i].getName());
                    
                    params[i].setOriginalValue(parameters);
                    if(parser != null) {
                        if(parameters.length == 1) {
                            value = parser.parse(parameters[0], params[i].getType());
                        } else {
                            Object[] array = new Object[parameters.length];
                            for(int j=0; j<array.length; j++) {
                                array[j] = parser.parse(parameters[j], params[i].getType());
                            }
                            value = array;
                        }
                    } else {
                        value = parameters;
                    } 
                } else {
                    String parameter = parameterMap.getValue(params[i].getName());
                    params[i].setOriginalValue(parameter);
                    
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
                
                System.out.println("Set " + params[i].getName() + " to value " + value);
            }
        }
        
        //System.out.println(new java.util.Date()+" Obtaining renderers");
        Renderer[] renderers = report.getRenderers();
        Renderer renderer = null;
        // TODO: Move into Report.getRenderer(String)
        for(int i=0; i<renderers.length; i++) {
            if(rendererName.equals(renderers[i].getName())) {
                renderer = renderers[i];
                break;
            }
        }
        
        //System.out.println(new java.util.Date()+" Preparing response");
        // prepare response                   
        /*
        response.setContentType( renderer.getMimeType() );
        if(!renderer.isInline()) {
            response.setHeader("Content-Disposition", "attachment; filename="+report.getName()+"."+renderer.getExtension());
        }
        */
        
        
        DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String filename = report.getName() + df.format(new Date()) + "." + renderer.getExtension();
        
        OutputStream os = null;
        try {                       
            os = new BufferedOutputStream(new FileOutputStream(filename));
            
            // render results
            if(renderer != null && report != null) {
                
                long rep_start = System.currentTimeMillis();
                //System.out.println(new java.util.Date()+" Executing report");
                Result result = report.execute();
                long rep_time = System.currentTimeMillis() - rep_start;
                //System.out.println(new java.util.Date()+" Took "+rep_time);
                
                if(result == null) {
                    throw new RuntimeException("Result is null. ");
                }
                if(result.hasNextRow() == false) {
                    throw new EmptyReportException();
                }
                //System.out.println(new java.util.Date()+" Formatting result");
                result = new FormattingResult(result, report);
                
                long rend_start = System.currentTimeMillis();
                //System.out.println(new java.util.Date()+" Rendering result");
                //renderer.display( result, report, response.getOutputStream() );
                renderer.display(result, report, os);
                long rend_time = System.currentTimeMillis() - rend_start;
                //System.out.println(new java.util.Date()+" Took "+rend_time);
                
                logReport(report, rep_time, rend_time);
            } else {
                throw new RuntimeException("Renderer or Report is null. ");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            // response.getOutputStream().flush();
            try {
                os.close();
            } catch (IOException ioe2) {                
            }
        }        
    }
    
    private String selectReportGroup() {
    	System.out.println("Select Report Group");
    	System.out.println("-------------------");
    	
    	ReportGroup[] groups = ReportFactory.getReportGroups();
        for(int i=0; i<groups.length; i++) {
        	System.out.print((i + 1) + ") ");
        	System.out.println(groups[i].getLabel() + " - " + groups[i].getDescription());          
        }
        
        return groups[this.getChoice(groups.length) - 1].getName();    
    }
    
    private String selectReport(String groupName) {
    	System.out.println("Select Report");
    	System.out.println("-------------");
    	
    	Report[] reports = ReportFactory.getReports(groupName);
    	for(int i=0; i<reports.length; i++) {
    		System.out.print((i + 1) + ") ");
        	System.out.println(reports[i].getLabel() + " - " + reports[i].getDescription());          
        }
        
        return reports[this.getChoice(reports.length) - 1].getName();      
    }
    
    private String selectRenderer(Report report) {
    	System.out.println("Select Renderer");
    	Renderer[] renderers = report.getRenderers();
    	for(int i=0; i<renderers.length; i++) {
    		System.out.print((i + 1) + ") ");
    		System.out.println(renderers[i].getLabel());          
    	}
    	
    	return renderers[this.getChoice(renderers.length) - 1].getName();
    }
    
    public static void applyResources(Report report, ParameterMap parameterMap) {
        String[] required = report.getResourceNames();
        for(int i=0; i<required.length; i++) {
            String value = parameterMap.getValue(required[i]);
            if(value != null && !value.equals("")) {                
                report.setResource(required[i], parameterMap.getValue(required[i]));
            } 
        }
    }
    
    public static void applyVariantParams(Report report, ParameterMap parameterMap) {
        Variant[] variants = report.getVariants();
        for(int i=0; i<variants.length; i++) {
            String key = parameterMap.getValue(variants[i].getName());
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
    
    public static boolean hasResourceChoice(Report report) {
        List list = Arrays.asList(report.getResourceNames());
        Param[] resourceParams = report.getReportGroup().getResourceParams();
        for(int i=0; i<resourceParams.length; i++) {
            if(list.contains(resourceParams[i].getName())) {
                return true;
            }
        }
        return false;
    }
    
    private static synchronized void logReport(Report report, long report_time, long render_time) {
        FileWriter writer = null;
        try {
            writer = new FileWriter( new File("rrr.log"), true );
            // add more to this
            StringBuffer buffer = new StringBuffer();
            buffer.append(new Date());
            buffer.append(",");
            buffer.append(report_time);
            buffer.append(",");
            buffer.append(render_time);
            buffer.append(",");
            buffer.append(report.getName());
            buffer.append("\n");
            writer.write(buffer.toString());
            writer.flush();
            writer.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if(writer != null) { try { writer.close(); } catch(IOException ioe) { } }
        }
    }
    
    private int getChoice(int numSelections) {
    	int choice = 0;
    	while (choice < 1) {
    		System.out.print("> ");
    		
    		byte[] b = new byte[50];
    		
    		try {
    			System.in.read(b);
    		} catch (IOException ioe) {
    			ioe.printStackTrace(System.err);
    		}
    		
    		String s = new String(b);
    		if (s != null && s.length() > 0) {
    			try {
    				int c = Integer.parseInt(s);
    				if (c > 0 && c <= numSelections) {
    					choice = c;
    				}
    			} catch (NumberFormatException nfe) {    				
    			}
    		}
    	}
    	
    	return choice;
    }
    
    private String getString() {
    	byte[] b = new byte[255];
    	
    	try {
    		System.in.read(b);    		
    	} catch (IOException ioe) {
    		ioe.printStackTrace(System.err);
    	}
    	
    	return new String(b);
    }
    
    private class ParameterMap {        
        private Map map;
        
        public ParameterMap() {
            map = new HashMap();
        }
        
        public void set(String key, String value) {
            ArrayList list = (ArrayList) map.get(key);
            if (list == null) {
                list = new ArrayList();
            }
            list.add(value);
            
            map.put(key, list);           
        }
        
        public String getValue(Object key) {
            ArrayList list = (ArrayList) map.get(key);
            if (list == null) {
                return null;
            } else {
                return (String) list.get(0);
            }          
        }
        
        public String[] getValues(Object key) {
            ArrayList list = (ArrayList) map.get(key);
            if (list == null) {
                return null;
            } else {
                return (String[]) list.toArray();
            }   
        }
        
    }
}
