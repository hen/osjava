package com.generationjava.servlet;

import java.io.IOException;
import java.io.File;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.generationjava.io.find.FileFinder;
import com.generationjava.io.find.Finder;

public class FileSystemServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response) 
        throws ServletException, IOException
    {
        ServletConfig config = getServletConfig();

        // the root of the file-system
        String root = config.getInitParameter("root");
        
        // pattern of files to consider
        String filePattern = config.getInitParameter("file-pattern");
        // pattern of dirs to consider
        String dirsPattern = config.getInitParameter("dirs-pattern");
        // also ones to exclude??

        // a properties file mapping a mime type to a java class
        // to display it. Must extend display(file).
        String mimeUrl = config.getInitParameter("mime");
        
        // use getResource to load mime mapping. FileW.

        Writer out = response.getWriter();
        out.write("root="+root);
        out.write("<br>");
        out.write("file-pattern="+filePattern);
        out.write("<br>");
        out.write("dirs-pattern="+dirsPattern);
        out.write("<br>");
        out.write("mime="+mimeUrl);
        out.write("<br>");

        // if no request passed in, then run on root.
        String target = request.getParameter("target");
        /*
        if(target == null) {
            target = root;
        } else {
            // SECURITY. ISH.
            target = root + target;
        }
        */
        target = root;

        // open request-target as a directory. 
        Finder finder = new FileFinder();
        File file = new File(target);

        String[] found = finder.find(file);
        for(int i=0; i<found.length; i++) {
            out.write("+"+found[i]);
            out.write("<br>");
        }
        
        // if request-target == directory
            // get all files in the directory that match pattern?
            // Ask each displayer to do an iconDisplay for each one.
            // show on a html page as clickable icons.
        // if request-target = a file.
            // ask its displayer to show it in long form.
        
        // handle the concept of a file-writing.
        // maybe make an applet front end rather than browser someday.
        // offer multiple views etc.
        
    }

}
