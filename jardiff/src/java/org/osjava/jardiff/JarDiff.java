/*
 * org.osjava.jardiff.JarDiff
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
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.objectweb.asm.ClassReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;
/**
 * A class to perform a diff between two jar files.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class JarDiff
{
    /**
     * A map containing information about classes which are dependencies.
     * Keys are internal class names.
     * Values are instances of ClassInfo.
     */
    protected Map depClassInfo = new HashMap();

    /**
     * A map containing information about classes in the old jar file.
     * Keys are internal class names.
     * Values are instances of ClassInfo.
     */
    protected Map oldClassInfo = new HashMap();

    /**
     * A map containing information about classes in the new jar file.
     * Keys are internal class names.
     * Values are instances of ClassInfo.
     */
    protected Map newClassInfo = new HashMap();

    /**
     * An array of dependencies which are jar files, or urls.
     */
    private URL[] deps;

    /**
     * A class loader used for loading dependency classes.
     */
    private URLClassLoader depLoader;

    /**
     * The name of the old version.
     */
    private String oldVersion;

    /**
     * The name of the new version.
     */
    private String newVersion;

    /**
     * Class info visitor, used to load information about classes.
     */
    private ClassInfoVisitor infoVisitor = new ClassInfoVisitor();

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
        hf.printHelp("JarDiff -f <from jar> -t <to jar> [-F <from name>] [-T <to name>] [-d <dependency path>] [-o <xml|html|xhtml>] [-O <file>]", options);
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
            tmp = new Option("d","dep",true,"dependency path");
            options.addOption(tmp);
            tmp = new Option("o","output-format",true,"output format, xml or html");
            options.addOption(tmp);
            tmp = new Option("O","out",true,"output file");
            options.addOption(tmp);
            tmp = new Option("h","help",false,"print help on command line arguments");
            options.addOption(tmp);
            tmp = new Option("x","xsl",true,"custom xsl sheet to format output with");
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

            /* This is left commented out, moved back to using DOM for now.
            SAXTransformerFactory stf =
                (SAXTransformerFactory) TransformerFactory.newInstance();
            stf.setErrorListener(
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

            TransformerHandler oth;
            if(cli.hasOption('o')) {
                String val = cli.getOptionValue('o');
                if("xml".equals(val)) {
                    oth = stf.newTransformerHandler();
                } else if(FORMATS.contains(val)) {
                    URL url = JarDiff.class.getClassLoader()
                        .getResource("style/jardiff-"+val+".xsl");

                    oth = stf.newTransformerHandler(
                            stf.newTemplates( new StreamSource( url.toString() ) )
                            );
                } else {
                    showHelp(options, "Invalid output format: "+val);
                    return;
                }
            } else {
                oth = stf.newTransformerHandler();
            }
            OutputStream out;
            if(cli.hasOption('O')) {
                out = new FileOutputStream(cli.getOptionValue('O'));
            } else {
                out = System.out;
            }
            oth.setResult(new StreamResult(out));
            */
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

    /**
     * Create a new JarDiff object.
     */
    public JarDiff() {
    }

    /**
     * Set the name of the old version.
     *
     * @param oldVersion the name
     */
    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    /**
     * Get the name of the old version.
     *
     * @return the name
     */
    public String getOldVersion() {
        return oldVersion;
    }

    /**
     * Set the name of the new version.
     *
     * @param newVersion
     */
    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    /**
     * Get the name of the new version.
     *
     * @return the name
     */
    public String getNewVersion() {
        return newVersion;
    }

    /**
     * Set the dependencies.
     *
     * @param deps an array of urls pointing to jar files or directories
     *             containing classes which are required dependencies.
     */
    public void setDependencies(URL[] deps) {
        this.deps = deps;
    }

    /**
     * Get the dependencies.
     *
     * @return the dependencies as an array of URLs
     */
    public URL[] getDependencies() {
        return deps;
    }

    /**
     * Load classinfo given a ClassReader.
     *
     * @param reader the ClassReader
     * @return the ClassInfo
     */
    private synchronized ClassInfo loadClassInfo(ClassReader reader) 
        throws IOException 
    {
        infoVisitor.reset();
        reader.accept(infoVisitor, false);
        return infoVisitor.getClassInfo();
    }

    /**
     * Load all the classes from the specified URL and store information
     * about them in the specified map.
     * This currently only works for jar files, <b>not</b> directories
     * which contain classes in subdirectories or in the current directory.
     *
     * @param infoMap the map to store the ClassInfo in.
     * @throws DiffException if there is an exception reading info about a 
     *                       class.
     */
    private void loadClasses(Map infoMap, URL path) throws DiffException {
        try {
            File jarFile = null;
            if(!"file".equals(path.getProtocol()) || path.getHost() != null) {
                // If it's not a local file, store it as a temporary jar file.
                // java.util.jar.JarFile requires a local file handle.
                jarFile = File.createTempFile("jardiff","jar");
                // Mark it to be deleted on exit.
                jarFile.deleteOnExit();
                InputStream in = path.openStream();
                OutputStream out = new FileOutputStream(jarFile);
                byte[] buffer = new byte[4096];
                int i;
                while( (i = in.read(buffer,0,buffer.length)) != -1) {
                    out.write(buffer, 0, i);
                }
                in.close();
                out.close();
            } else {
                // Else it's a local file, nothing special to do.
                jarFile = new File(path.getPath());
            }
            loadClasses(infoMap, jarFile);
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * Load all the classes from the specified URL and store information
     * about them in the specified map.
     * This currently only works for jar files, <b>not</b> directories
     * which contain classes in subdirectories or in the current directory.
     *
     * @param infoMap the map to store the ClassInfo in.
     * @param file the jarfile to load classes from.
     * @throws IOException if there is an IOException reading info about a 
     *                     class.
     */
    private void loadClasses(Map infoMap, File file) throws DiffException {
        try {
            JarFile jar = new JarFile(file);
            Enumeration e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = (JarEntry) e.nextElement();
                String name = entry.getName();
                if (!entry.isDirectory() && name.endsWith(".class")) {
                    ClassReader reader
                        = new ClassReader(jar.getInputStream(entry));
                    ClassInfo ci = loadClassInfo(reader);
                    infoMap.put(ci.getName(), ci);
                }
            }
        } catch (IOException ioe) {
            throw new DiffException(ioe);
        }
    }

    /**
     * Load old classes from the specified URL.
     *
     * @param loc The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException.
     */
    public void loadOldClasses(URL loc) throws DiffException {
        loadClasses(oldClassInfo, loc);
    }

    /**
     * Load new classes from the specified URL.
     *
     * @param loc The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException.
     */
    public void loadNewClasses(URL loc) throws DiffException {
        loadClasses(newClassInfo, loc);
    }

    /**
     * Load old classes from the specified File.
     *
     * @param file The location of a jar file to load classes from.
     * @throws DiffException if there is an IOException
     */
    public void loadOldClasses(File file) throws DiffException {
        loadClasses(oldClassInfo, file);
    }

    /**
     * Load new classes from the specified File.
     *
     * @param file The location of a jar file to load classes from.
     * @throws DiffExeption if there is an IOException
     */
    public void loadNewClasses(File file) throws DiffException {
        loadClasses(newClassInfo, file);
    }

    /**
     * Perform a diff sending the output to the specified handler, using
     * the specified criteria to select diffs.
     *
     * @param handler The handler to receive and handle differences.
     * @param criteria The criteria we use to select differences.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void diff(DiffHandler handler, DiffCriteria criteria)
        throws DiffException 
    {
        java.util.Set onlyOld = new TreeSet(oldClassInfo.keySet());
        java.util.Set onlyNew = new TreeSet(newClassInfo.keySet());
        java.util.Set both = new TreeSet(oldClassInfo.keySet());
        onlyOld.removeAll(newClassInfo.keySet());
        onlyNew.removeAll(oldClassInfo.keySet());
        both.retainAll(newClassInfo.keySet());
        // TODO: Build the name from the MANIFEST rather than the filename
        handler.startDiff(oldVersion, newVersion);
        handler.startRemoved();
        Iterator i = onlyOld.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) oldClassInfo.get(s);
            if (criteria.validClass(ci))
                handler.classRemoved(ci);
        }
        handler.endRemoved();
        handler.startAdded();
        i = onlyNew.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) newClassInfo.get(s);
            if (criteria.validClass(ci))
                handler.classAdded(ci);
        }
        handler.endAdded();
        java.util.Set removedMethods = new TreeSet();
        java.util.Set removedFields = new TreeSet();
        java.util.Set addedMethods = new TreeSet();
        java.util.Set addedFields = new TreeSet();
        java.util.Set changedMethods = new TreeSet();
        java.util.Set changedFields = new TreeSet();
        handler.startChanged();
        i = both.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo oci = (ClassInfo) oldClassInfo.get(s);
            ClassInfo nci = (ClassInfo) newClassInfo.get(s);
            if (criteria.validClass(oci) || criteria.validClass(nci)) {
                Map oldMethods = oci.getMethodMap();
                Map oldFields = oci.getFieldMap();
                Map newMethods = nci.getMethodMap();
                Map newFields = nci.getFieldMap();
                Iterator j = oldMethods.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validMethod((MethodInfo) entry.getValue()))
                        removedMethods.add(entry.getKey());
                }
                j = oldFields.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validField((FieldInfo) entry.getValue()))
                        removedFields.add(entry.getKey());
                }
                j = newMethods.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validMethod((MethodInfo) entry.getValue()))
                        addedMethods.add(entry.getKey());
                }
                j = newFields.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validField((FieldInfo) entry.getValue()))
                        addedFields.add(entry.getKey());
                }
                changedMethods.addAll(removedMethods);
                changedMethods.retainAll(addedMethods);
                removedMethods.removeAll(changedMethods);
                addedMethods.removeAll(changedMethods);
                changedFields.addAll(removedFields);
                changedFields.retainAll(addedFields);
                removedFields.removeAll(changedFields);
                addedFields.removeAll(changedFields);
                j = changedMethods.iterator();
                while (j.hasNext()) {
                    String desc = (String) j.next();
                    MethodInfo oldInfo = (MethodInfo) oldMethods.get(desc);
                    MethodInfo newInfo = (MethodInfo) newMethods.get(desc);
                    if (!criteria.differs(oldInfo, newInfo))
                        j.remove();
                }
                j = changedFields.iterator();
                while (j.hasNext()) {
                    String desc = (String) j.next();
                    FieldInfo oldInfo = (FieldInfo) oldFields.get(desc);
                    FieldInfo newInfo = (FieldInfo) newFields.get(desc);
                    if (!criteria.differs(oldInfo, newInfo))
                        j.remove();
                }
                boolean classchanged = criteria.differs(oci, nci);
                if (classchanged || !removedMethods.isEmpty()
                        || !removedFields.isEmpty() || !addedMethods.isEmpty()
                        || !addedFields.isEmpty() || !changedMethods.isEmpty()
                        || !changedFields.isEmpty()) {
                    handler.startClassChanged(s);
                    handler.startRemoved();
                    j = removedFields.iterator();
                    while (j.hasNext())
                        handler
                            .fieldRemoved((FieldInfo) oldFields.get(j.next()));
                    j = removedMethods.iterator();
                    while (j.hasNext())
                        handler.methodRemoved((MethodInfo)
                                oldMethods.get(j.next()));
                    handler.endRemoved();
                    handler.startAdded();
                    j = addedFields.iterator();
                    while (j.hasNext())
                        handler
                            .fieldAdded((FieldInfo) newFields.get(j.next()));
                    j = addedMethods.iterator();
                    while (j.hasNext())
                        handler.methodAdded((MethodInfo)
                                newMethods.get(j.next()));
                    handler.endAdded();
                    handler.startChanged();
                    if (classchanged)
                        handler.classChanged(oci, nci);
                    j = changedFields.iterator();
                    while (j.hasNext()) {
                        Object tmp = j.next();
                        handler.fieldChanged((FieldInfo) oldFields.get(tmp),
                                (FieldInfo) newFields.get(tmp));
                    }
                    j = changedMethods.iterator();
                    while (j.hasNext()) {
                        Object tmp = j.next();
                        handler.methodChanged((MethodInfo) oldMethods.get(tmp),
                                ((MethodInfo)
                                 newMethods.get(tmp)));
                    }
                    handler.endChanged();
                    handler.endClassChanged();
                    removedMethods.clear();
                    removedFields.clear();
                    addedMethods.clear();
                    addedFields.clear();
                    changedMethods.clear();
                    changedFields.clear();
                }
            }
        }
        handler.endChanged();
        handler.endDiff();
    }
}
