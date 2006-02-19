/*
 * org.osjava.jardiff.Main
 *
 * $Id: IOThread.java 1952 2005-08-28 18:03:41Z cybertiger $
 * $URL: https://svn.osjava.org/svn/osjava/trunk/osjava-nio/src/java/org/osjava/nio/IOThread.java $
 * $Rev: 1952 $
 * $Date: 2005-08-28 18:03:41 +0000 (Sun, 28 Aug 2005) $
 * $Author: cybertiger $
 *
 * Copyright (c) 2005, Antony Riley
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name JarDiff nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.jardiff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;

/**
 * A static entry point for use from the command line.
 */
public class Main {

    /**
     * Private constructor to prevent this class being instantiated.
     */
    private Main() {
    }

    /**
     * Define set of valid formats.
     * This is here to make maintenance more easy.
     */
    private final static Set FORMATS = new HashSet();

    /**
     * Can you javadoc static code, I think not, but lets try anyway.
     */
    static {
        FORMATS.add("html");
        FORMATS.add("xhtml");
        FORMATS.add("text");
    }

    /**
     * Utility function for showing help using commons-cli.
     * @param options Command line options.
     * @param msg Optional message to show.
     */
    private static void showHelp(Options options, String msg) {
        if(msg != null) {
            System.out.println(msg);
        }
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("JarDiff -f <from jar> -t <to jar> [-F <from name>] [-T <to name>] [[-o <xml|html|xhtml|text>]|[-x <xsl file>]] [-O <file>] [-s <href>] [-fa <href>] [-ta <href>]", options);
    }

    /**
     * Main method to allow this to be run from the command line.
     * This needs work, currently only takes two arguments, which are
     * the old jar file and the new jar file.
     *
     * @param args A string array of length two containing the filenames of
     *             two jar files, the first of which being the older of the
     *             two.
     * @throws Exception when there is an underlying exception, e.g.
     *                   writing to a file caused an IOException
     */
    public static void main(String[] args) throws Exception {
        try {
            Options options = new Options();
            Option tmp;
            tmp = new Option("f","from",true,"from jar file");
            options.addOption(tmp);
            tmp = new Option("t","to",true,"to jar file");
            options.addOption(tmp);
            tmp = new Option("F","from-name",true,"from name");
            options.addOption(tmp);
            tmp = new Option("T","to-name",true,"to name");
            options.addOption(tmp);
            //tmp = new Option("d","dep",true,"dependency path");
            //options.addOption(tmp);
            tmp = new Option("o","output-format",true,"output format, xml or html");
            options.addOption(tmp);
            tmp = new Option("O","out",true,"output file");
            options.addOption(tmp);
            tmp = new Option("h","help",false,"print help on command line arguments");
            options.addOption(tmp);
            tmp = new Option("x","xsl",true,"custom xsl sheet to format output with");
            options.addOption(tmp);
            tmp = new Option("s","stylesheet",true,"stylesheet to link to when generating html");
            options.addOption(tmp);
            tmp = new Option("fa","from-api",true,"relative location of from api");
            options.addOption(tmp);
            tmp = new Option("ta","to-api",true,"relative location of to api");
            options.addOption(tmp);
            Parser parser = new GnuParser();
            CommandLine cli = null;
            try {
                cli = parser.parse(options, args);
            } catch (ParseException pe) {
                showHelp(options, pe.getMessage());
                return;
            }
            args = cli.getArgs();
            if(cli.hasOption('h'))
            {
                showHelp(options, null);
                return;
            }
            if(args.length > 0) {
                showHelp(options, "Additional arguments specified");
                return;
            }
            if(!cli.hasOption('f')) {
                showHelp(options, "Missing required argument: -f");
                return;
            }
            if(!cli.hasOption('t')) {
                showHelp(options, "Missing required argument: -t");
                return;
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setErrorListener(
                    new ErrorListener() {
                        public void warning(TransformerException te) {
                            System.err.println("xslt warning: "+te.getMessageAndLocation());
                        }
                        public void error(TransformerException te) {
                            System.err.println("xslt error: "+te.getMessageAndLocation());
                        }
                        public void fatalError(TransformerException te) {
                            System.err.println("xslt fatal error: "+te.getMessageAndLocation());
                        }
                    });
            Transformer ot;
            if(cli.hasOption('o')) {
                if(cli.hasOption('x')) {
                    showHelp(options, "Cannot use both -x and -o");
                    return;
                }
                String val = cli.getOptionValue('o');
                if("xml".equals(val)) {
                    ot = tf.newTransformer();
                } else if(FORMATS.contains(val)) {
                    URL url = JarDiff.class.getClassLoader()
                        .getResource("style/jardiff-"+val+".xsl");
                    ot = tf.newTransformer(
                            new StreamSource( url.toString() )
                            );
                } else {
                    showHelp(options, "Invalid output format: "+val);
                    return;
                }
            } else if(cli.hasOption('x')) {
                File xsl = new File(cli.getOptionValue('x'));
                ot = tf.newTransformer(new StreamSource(xsl));
            } else {
                ot = tf.newTransformer();
            }
            if(cli.hasOption("s")) {
                ot.setParameter("stylesheet", cli.getOptionValue("s"));
            }
            if(cli.hasOption("fa")) {
                ot.setParameter("from-api", cli.getOptionValue("fa"));
            }
            if(cli.hasOption("ta")) {
                ot.setParameter("to-api", cli.getOptionValue("ta"));
            }
            OutputStream out;
            if(cli.hasOption('O')) {
                out = new FileOutputStream(cli.getOptionValue('O'));
            } else {
                out = System.out;
            }
            JarDiff jd = new JarDiff();
            File oldFile = new File(cli.getOptionValue('f'));
            File newFile = new File(cli.getOptionValue('t'));
            if(cli.hasOption('F')) {
                jd.setOldVersion(cli.getOptionValue('F'));
            } else {
                jd.setOldVersion(oldFile.getName());
            }
            if(cli.hasOption('T')) {
                jd.setNewVersion(cli.getOptionValue('T'));
            } else {
                jd.setNewVersion(newFile.getName());
            }
            jd.loadOldClasses(oldFile);
            jd.loadNewClasses(newFile);
            jd.diff(
                    new DOMDiffHandler(ot, new StreamResult(out)), 
                    new SimpleDiffCriteria()
                    );
            out.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
