/*
 * Copyright (c) 2004, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of OSJava nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
