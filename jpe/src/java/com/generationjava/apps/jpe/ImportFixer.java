package com.generationjava.apps.jpe;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;

public class ImportFixer {

        private Vector unfound = new Vector();
        private Vector fixed = new Vector();
        private Vector imports = new Vector();
        private Vector names = new Vector();
        private String pckge = null;
        private int importLine = 0;
        
        private static final String[] importsLit2 = { "","java.io","java.util","java.net","java.sql","java.awt","java.awt.event" };
        private static final Vector imports2 = toVector(importsLit2);

        private static final String wordRegexp = "[a-zA-Z][a-zA-Z0-9_]*";
        private static final String classRegexp = "("+wordRegexp+")";
        private static final String maybeSpaceRegexp = "\\s*";
        private static final String leastOneSpaceRegexp = "\\s"+maybeSpaceRegexp;

        private static RE castRe;
        private static RE declRe;
            private static RE assiRe;
            private static RE instRe;
    private static RE newRe;
        private static RE classRe;
        private static RE stringLitRe;
        private static RE cCommentRe;
        private static RE cPlusCommentRe;
        private static RE cPlusStartCommentRe;
        private static RE cPlusEndCommentRe;
        
    public ImportFixer() {
                try {
                            castRe = new RE("[^a-zA-Z0-9_]\\("+classRegexp+"\\)");
                        declRe = new RE(classRegexp+leastOneSpaceRegexp+wordRegexp+maybeSpaceRegexp+";");
                            assiRe = new RE(classRegexp+leastOneSpaceRegexp+wordRegexp+maybeSpaceRegexp+"=");
                            instRe = new RE("instanceof"+leastOneSpaceRegexp+classRegexp);
                          newRe = new RE("new"+leastOneSpaceRegexp+classRegexp);
                        classRe = new RE("class"+leastOneSpaceRegexp+classRegexp);
                        stringLitRe = new RE("\"[^\"]*\"");
                        cPlusCommentRe = new RE("/\\*[^/*]*\\*/");
                        cPlusStartCommentRe = new RE("/\\*[^/*]*");
                        cPlusEndCommentRe = new RE("[^/*]*\\*/");
                        cCommentRe = new RE("//.*$");
                } catch(REException ree) {
                        System.err.println("bad ree: "+ree.getMessage());
                }                
    }

        public void parse(Reader rdr) {
                // parse the source for all its information
                parseSource(rdr);
                
                // then go over the class names and match any possible to the imports
                Enumeration enum = names.elements();
OUT:        while(enum.hasMoreElements()) {
                        String name = (String)enum.nextElement();
                        Enumeration enum2 = imports.elements();
                        while(enum2.hasMoreElements()) {
                                String imp = (String)enum2.nextElement();
                                if(classMatch(name,imp,false)) {
                                        continue OUT;
                                }
                        }
                        unfound.addElement(name);
                }

                Vector names2 = unfound;
                unfound = new Vector();
                // then look in the wildcard entrys and fix them to not be wildcards
                enum = names2.elements();
OUT:        while(enum.hasMoreElements()) {
                        String name = (String)enum.nextElement();
                        Enumeration enum2 = imports.elements();
                        while(enum2.hasMoreElements()) {
                                String imp = (String)enum2.nextElement();
                                if(classMatch(name,imp,true)) {
                                        continue OUT;
                                }
                        }
                        unfound.addElement(name);
                }

                names2 = unfound;
                unfound = new Vector();

                // then go hunting for any left.
                enum = names2.elements();
OUT:        while(enum.hasMoreElements()) {
                        String name = (String)enum.nextElement();
                        Enumeration enum2 = imports2.elements();
                        while(enum2.hasMoreElements()) {
                                String imp = (String)enum2.nextElement();
                                if(classMatch(name,imp+".*",true)) {
                                        String str = name;
                                        if(!"".equals(imp)) {
                                                str = imp+"."+name;
                                        }
                                        fixed.addElement(str);
                                        continue OUT;
                                }
                        }
                        unfound.addElement(name);
                }
        }

        public void parseSource(Reader reader) {
                imports.addElement("java.lang.*");
                try {
                        BufferedReader bir = new BufferedReader(reader);
                        String line;
                        REMatch match;
                        boolean inComment = false;
                        Vector localClassNames = new Vector();
                        int count = 0;
                        
                        while( (line = bir.readLine()) != null) {
                                count++;
                                if(line.startsWith("package ")) {
                                        pckge = line.substring(8,line.length()-1);
                                        imports.addElement(pckge+".*");
                                }
                                if(line.startsWith("import ")) {
                                        imports.addElement(line.substring(7,line.length()-1));
                                        this.importLine = count;
                                }
                                if(line.indexOf("class ") != -1) {  // improve
                                                match = classRe.getMatch(line);
                                                if(match != null) {
                                                        int st = match.getSubStartIndex(1);
                                                        int end = match.getSubEndIndex(1);
                                                        if(st != -1 && end != -1) {
                                                            localClassNames.addElement(line.substring(st,end));
                                                    }
                                        }
                                        break;
                                }
                        }

                        while( (line = bir.readLine()) != null) {
                          // if in comment, see if time to come out of it
                                if(inComment) {
                                        if(line.indexOf("*/") != -1) {
                                                inComment = false;
                                                line = cPlusEndCommentRe.substitute(line,"");                                                
                                        } else {
                                                continue;
                                        }
                                }
                          // remove any literal Strings????
                            // s/"[^"]*"//g
                            line = stringLitRe.substitute(line,"");
                          // remove any commented out bits
                                // s/\/\*[^\/\*]\*\///
                            line = cPlusCommentRe.substitute(line,"");
                          // also handle being in a comment section
                                // then handle multi-line         \* ... *\. turn on a 'incomment' flag.
                                if(line.indexOf("/*") != -1) {
                                        inComment = true;
                                        line = cPlusStartCommentRe.substitute(line,"");
                                }
                          // chunk off any //'s
                                // s/\/\/.*$//
                            line = cCommentRe.substitute(line,"");
// NOT POSSIBLE TO DO CASTING PROPERLY. CAN MAKE MISTAKES.
                          // look for casting:   (Word) - primitives
                            // /\(([a-zA-Z0-9_]*)\)      can pick up variables here :(
                                match = castRe.getMatch(line);
                                if(match != null) {
                                                int st = match.getSubStartIndex(1);
                                                int end = match.getSubEndIndex(1);
                                                if(st != -1 && end != -1) {
                                                        if(!names.contains(line.substring(st,end))) {
                                                            names.addElement(line.substring(st,end));
                                                    }
                                            }
                                }
                          // look for declarations:   Word word;
                            // /([a-zA-Z0-9_]*)\s*[a-zA-Z0-9_]*;/
                                match = declRe.getMatch(line);
                                if(match != null) {
                                                int st = match.getSubStartIndex(1);
                                                int end = match.getSubEndIndex(1);
                                                if(st != -1 && end != -1) {
                                                        if(!names.contains(line.substring(st,end))) {
                                                            names.addElement(line.substring(st,end));
                                                    }
                                            }
                                }
                          // look for declaration/assigns: Word word=..;
                            // /([a-zA-Z0-9_]*)\s*[a-zA-Z0-9_]*\s*=/
                                match = assiRe.getMatch(line);
                                if(match != null) {
                                                int st = match.getSubStartIndex(1);
                                                int end = match.getSubEndIndex(1);
                                                if(st != -1 && end != -1) {
                                                        if(!names.contains(line.substring(st,end))) {
                                                            names.addElement(line.substring(st,end));
                                                    }
                                            }
                                }
                          // look for instanceof: instanceof Word
                              // /instanceof\s*[a-zA-Z0-9_]*/
                                match = instRe.getMatch(line);
                                if(match != null) {
                                                int st = match.getSubStartIndex(1);
                                                int end = match.getSubEndIndex(1);
                                                if(st != -1 && end != -1) {
                                                        if(!names.contains(line.substring(st,end))) {
                                                            names.addElement(line.substring(st,end));
                                                    }
                                            }
                                }
                          // look for arguments: (Argument argument, Arg arg..)
                            // /\(([a-zA-Z0-9_]*)\s*[a-zA-Z0-9_]*,... handle list.
                          // look for throws Exception
                              // /throws\s*([a-zA-Z0-9_]*),... handle list.
                          // look for new Word(
                                // /new\s*([a-zA-Z0-9_]*)
                                match = newRe.getMatch(line);
                                if(match != null) {
                                                int st = match.getSubStartIndex(1);
                                                int end = match.getSubEndIndex(1);
                                                if(st != -1 && end != -1) {
                                                        if(!names.contains(line.substring(st,end))) {
                                                            names.addElement(line.substring(st,end));
                                                    }
                                            }
                                }
                          // look for any class declarations, these must be removed
                            // /class\s*([a-zA-Z0-9_]*)
                                match = classRe.getMatch(line);
                                if(match != null) {
                                                int st = match.getSubStartIndex(1);
                                                int end = match.getSubEndIndex(1);
                                                if(st != -1 && end != -1) {
                                                    localClassNames.addElement(line.substring(st,end));
                                            }
                                }
                        }
                        bir.close();
                        // remove class declarations from the list of classes
                        Enumeration enum = localClassNames.elements();
                        while(enum.hasMoreElements()) {
                                names.removeElement(enum.nextElement());
                        }
                } catch(IOException ioe) {
                }
        }

        public String fixImport(String text) {
                Reader rdr = new StringReader(text);
                parse(rdr);
                Vector vec = new Vector();
                Enumeration enum = imports.elements();
                while(enum.hasMoreElements()) {
                        vec.addElement(enum.nextElement());
                }
                enum = fixed.elements();
                while(enum.hasMoreElements()) {
                        vec.addElement(enum.nextElement());
                }
                vec.removeElement("java.lang.*");
                vec.removeElement(pckge+".*");
                Vector sortedImports = packageSort(vec);
                
                try {
                        BufferedReader bir = new BufferedReader(new StringReader(text));
                        String line;
                        int count = 0;

                        while( (line = bir.readLine()) != null) {
                                if(count == importLine) {
                                        break;
                                }
                                count++;
                        }
                        StringWriter writer = new StringWriter();
                        if(pckge != null) {
                                writer.write("package ");
                                writer.write(pckge);
                                writer.write(";\n\n");
                        }
                        enum = sortedImports.elements();
                        while(enum.hasMoreElements()) {
                                String str = (String)enum.nextElement();
                                writer.write("import ");
                                writer.write(str);
                                writer.write(";\n");
                                System.err.println("import "+str+";");
                        }
                        writer.write("\n");
                        while( (line = bir.readLine()) != null) {
                                writer.write(line);
                                writer.write("\n");
                        }
                        return writer.toString();
                } catch(IOException ioe) {
                        ioe.printStackTrace();  // it's a StringReader!
                }
                return null;
        }

        public static String[] toStringArray(Vector vec) {
                String[] ret = new String[vec.size()];
                for(int i=0;i<vec.size();i++) {
                        ret[i] = (String)vec.elementAt(i);
                }
                return ret;
        }

        public static Vector toVector(String[] strs) {
                Vector ret = new Vector();
                for(int i=0;i<strs.length;i++) {
                        ret.addElement(strs[i]);
                }
                return ret;
        }

        public static boolean classMatch(String clss, String imp, boolean allowWilds) {
                int idx = imp.lastIndexOf('.');
                if(idx != -1) {
                        if(clss.equals(imp.substring(idx+1))) {
                                return true;
                        } else {
                                if(allowWilds) {
                                        if(imp.endsWith("*")) {
                                                String clss2 = imp.substring(0,idx)+"."+clss;
                                                if(clss2.startsWith(".")) {
                                                        clss2 = clss2.substring(1);
                                                }
                                                try {
                                                        Class.forName(clss2);
                                                        return true;
                                                } catch(ClassNotFoundException cnfe) {
                                                        return false;
                                                }
                                        } else {
                                                return false;
                                        }
                                } else {
                                        return false;
                                }
                        }
                } else {
                        return clss.equals(imp);
                }
        }
        
        public Vector packageSort(Vector vec) {
                return vec;
        }
}
