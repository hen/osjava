//package com.generationjava.scaffolds;

import com.generationjava.compare.*;
import com.generationjava.lang.*;
import com.generationjava.logview.builder.*;
import com.generationjava.logview.filter.*;
import com.generationjava.logview.log.*;
import com.generationjava.logview.loglet.*;
import com.generationjava.logview.renderer.*;
import com.generationjava.logview.report.*;
import com.generationjava.logview.reportlet.*;
import com.generationjava.logview.source.*;
import com.generationjava.logview.*;

public class LogViewRunner {

     String filename;

     static public void main(String[] strs) throws Exception {
         LogViewRunner lvr = new LogViewRunner();
         lvr.filename = strs[0];
         lvr.test();
     }

     public void test() throws CascadedException {
         test6();
     }    

     // handles a very simple csv log. Uses memory and 
     // prints it out, then the unique file names, then 
     // counts all lines, counts fields.
     public void test1() throws CascadedException {
         try {
             FileLogSource src = new FileLogSource(this.filename);
             String[] hdrs = new String[] {"file", "name", "time"};
             MemoryLog initialLog = new MemoryLog("test", 
                                            hdrs);
             StringDelimiterLogBuilder builder = new StringDelimiterLogBuilder(hdrs, ",", src);
             builder.fillLog(initialLog);
             Renderer renderer = new HtmlRenderer();
             Reportlet reportlet = new TableReportlet();

             Loglet loglet = new NullLoglet();
             Log log = loglet.parse(initialLog);
             Report report = reportlet.report(log);
             renderer.render(report);

             loglet = new UniqueLoglet("file");
             log = loglet.parse(initialLog);
             report = reportlet.report(log);
             renderer.render(report);

             loglet = new CountLoglet();
             log = loglet.parse(initialLog);
             report = reportlet.report(log);
             renderer.render(report);

             loglet = new CountFieldLoglet("fileCount", "file");
             log = loglet.parse(initialLog);
             report = reportlet.report(log);
             renderer.render(report);
         } catch(LogViewException lve) {
             throw new CascadedException("Problem analysing log: ", lve);
         }
     }

     // Apache CLF format again. Just prints it out, 
     // using a MemoryLog.
     public void test2() throws CascadedException {
         try {
             FileLogSource src = new FileLogSource(this.filename);
             FormattedLogBuilder builder = new FormattedLogBuilder("${ip:IP} ${username:STRING} ${email:EMAIL} [${date:DATE}] \"${cmd:STRING} ${filename:FILE} ${protocol:STRING}\" ${code:INTEGER} ${size:INTEGER}", src);
             MemoryLog initialLog = new MemoryLog("clf-test", 
                                            builder.getHeaders());
             builder.fillLog(initialLog);
             Renderer renderer = new AsciiRenderer();
             Reportlet reportlet = new TableReportlet();

             Loglet loglet = new NullLoglet();
             Log log = loglet.parse(initialLog);
             Report report = reportlet.report(log);
             renderer.render(report);
         } catch(LogViewException lve) {
             throw new CascadedException("Problem analysing log: ", lve);
         }
     }

     // this test takes an apache CLF file and outputs a 
     // list of the Unique ip addresses. The Null loglet 
     // will list out the CLF file in ascii-table form, 
     // this is commented out. You can also do a CountField 
     // loglet, also commented out.
     // This uses a StreamLog, ie) can handle large files.
     public void test3() throws CascadedException {
         try {
             FileLogSource src = new FileLogSource(this.filename);
             FormattedLogBuilder builder = new FormattedLogBuilder("${ip:IP} ${username:STRING} ${email:EMAIL} [${date:DATE}] \"${cmd:STRING} ${filename:FILE} ${protocol:STRING}\" ${code:INTEGER} ${size:INTEGER}", src);
             Log initialLog = new StreamLog("streaming-clf-test", 
                                            builder.getHeaders(),
                                            builder);
             Renderer renderer = new AsciiRenderer();
             Reportlet reportlet = new ChartReportlet("ip", "count");
             Report report = null;


             Loglet srcLoglet = new SourceLoglet(initialLog);
             // chaining concept
             Loglet loglet = new NullLoglet();
             loglet.setLoglet(srcLoglet);
             Log log = loglet.parse();
             // chaining happens....
//             Report report = reportlet.report(log);
//             renderer.render(report);

             loglet = new CountFieldLoglet("ip");
//             loglet = new UniqueLoglet("ip");
             loglet.setLoglet(srcLoglet);
             log = loglet.parse();
             Loglet sortloglet = new SortLoglet("count", 5, new ComparableComparator());
             log = sortloglet.parse(log);
             report = reportlet.report(log);
             report.setLink("ip", "some-url");
             renderer.render(report);

         } catch(LogViewException lve) {
             throw new CascadedException("Problem analysing log: ", lve);
         }
     }

     public void test4() throws CascadedException {
         try {
             /* XML Example:
             <log:build name="ip-log">
               <log:loglet type="visit" field="ip">
                 <log:property name="by" value="date"/>
               </log:loglet>
               <log:loglet type="countfield" field="ip"/>
               <log:loglet type="sort" field="count">
                 <log:property name="count" value="30"/>
               </log:loglet>
               <log:loglet type="dns"/>
             </log:build>
             <logview:report type="chart" xlabel="ip" ylabel="count" on="ip-log" name="ip-chart"/>
             <logview:render in="ascii" on="ip-chart">
             */
             FileLogSource src = new FileLogSource(this.filename);
             FormattedLogBuilder builder = new FormattedLogBuilder("${ip:IP} ${username:STRING} ${email:EMAIL} [${date:DATE}] \"${cmd:STRING} ${filename:FILE} ${protocol:STRING}\" ${code:INTEGER} ${size:INTEGER}", src);
             Log initialLog = new StreamLog("streaming-clf-test", 
                                            builder.getHeaders(),
                                            builder);
//             Renderer renderer = new HtmlRenderer();
             Renderer renderer = new AsciiRenderer();
             Reportlet reportlet = new ChartReportlet("ip", "count");
             Report report = null;


             Loglet srcLoglet = new SourceLoglet(initialLog);
//             Loglet filter = new FilterLoglet("Find an IP", new ContainsFilter("ip", "195.13.79"));
//             filter.setLoglet(srcLoglet);
             Loglet visit = new VisitLoglet("ip","date");
//             visit.setLoglet(filter);
             visit.setLoglet(srcLoglet);
             Loglet count = new CountFieldLoglet("ip");
             count.setLoglet(visit);
//             Loglet dns = new DnsLoglet("ip");
//             dns.setLoglet(count);
//             Log log = dns.parse();
             Log log = count.parse();
             Loglet sortloglet = new SortLoglet("count", 30, new ComparableComparator());
//             Loglet dns = new DnsLoglet("ip");
//             dns.setLoglet(sortloglet);
//             log = dns.parse(log);
             log = sortloglet.parse(log);
             report = reportlet.report(log);
             renderer.render(report);

         } catch(LogViewException lve) {
             throw new CascadedException("Problem analysing log: ", lve);
         }
     }

     // handles a p6spy spy.log. Uses memory and 
     // counts the number of java fields
     public void test5() throws CascadedException {
         try {
             FileLogSource src = new FileLogSource(this.filename);
             String[] hdrs = new String[] {"date", "time", "unknown", "type", "java", "sql"};
             MemoryLog initialLog = new MemoryLog("test5 - p6spy", 
                                            hdrs);
             StringDelimiterLogBuilder builder = new StringDelimiterLogBuilder(hdrs, "|", src);
             builder.fillLog(initialLog);

             Renderer renderer = new AsciiRenderer();
             Reportlet reportlet = new ChartReportlet("java", "count");
             Report report = null;

             Loglet srcLoglet = new SourceLoglet(initialLog);
             Loglet loglet = new CountFieldLoglet("java");
             loglet.setLoglet(srcLoglet);
             Log log = loglet.parse(initialLog);
             report = reportlet.report(log);
             renderer.render(report);
         } catch(LogViewException lve) {
             throw new CascadedException("Problem analysing log: ", lve);
         }
     }

     // as above, but an average of time for the java field
     public void test6() throws CascadedException {
         try {
             FileLogSource src = new FileLogSource(this.filename);
             String[] hdrs = new String[] {"date", "time", "unknown", "type", "java", "sql"};
             MemoryLog initialLog = new MemoryLog("test5 - p6spy", 
                                            hdrs);
             StringDelimiterLogBuilder builder = new StringDelimiterLogBuilder(hdrs, "|", src);
             builder.fillLog(initialLog);

             Renderer renderer = new AsciiRenderer();
             Reportlet reportlet = new ChartReportlet("java", "average");
             Report report = null;

             Loglet srcLoglet = new SourceLoglet(initialLog);
             Loglet loglet = new AverageFieldLoglet("java", "time");
             loglet.setLoglet(srcLoglet);
             Log log = loglet.parse(initialLog);
             report = reportlet.report(log);
             renderer.render(report);
         } catch(LogViewException lve) {
             throw new CascadedException("Problem analysing log: ", lve);
         }
     }
}
