package org.osjava.taglib.trail;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class OriginalRequestFilter implements Filter {

    private FilterConfig config = null;

    public void init(FilterConfig config) {
        this.config = config;
    }

    public void destroy() {
        config = null; 
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hsr = (HttpServletRequest) request;

        if( hsr.getAttribute("javax.servlet.forward.request_uri") != null ) {
             hsr.setAttribute( "javax.servlet.forward.request_uri", hsr.getRequestURI() );
        }
        if( hsr.getAttribute("javax.servlet.forward.context_path") != null ) {
             hsr.setAttribute( "javax.servlet.forward.context_path", hsr.getContextPath() );
        }
        if( hsr.getAttribute("javax.servlet.forward.servlet_path") != null ) {
             hsr.setAttribute( "javax.servlet.forward.servlet_path", hsr.getServletPath() );
        }
        if( hsr.getAttribute("javax.servlet.forward.path_info") != null ) {
             hsr.setAttribute( "javax.servlet.forward.path_info", hsr.getPathInfo() );
        }
        if( hsr.getAttribute("javax.servlet.forward.query_string") != null ) {
             hsr.setAttribute( "javax.servlet.forward.query_string", hsr.getQueryString() );
        }

        // continue
        chain.doFilter(request, response);
    }

}
