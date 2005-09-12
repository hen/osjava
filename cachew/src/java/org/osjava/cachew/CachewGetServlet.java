/*
 * Created on Jun 10, 2005
 */
package org.osjava.cachew;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hen
 */
public class CachewGetServlet extends HttpServlet {

    static CachewManager cManager = new CachewManager();
		
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		
		// getPath. Ask CachewManager for CachewItem matching that.
		CachewItem cItem = cManager.getItem(path);
		
		// if not found, ask CachewManager to create new CachewItem.
		if(cItem == null) {
			cItem = cManager.createItem(path);
		}

		// write CachewItem to stream
		response.setContentType(cItem.getContentType());
		OutputStream out = response.getOutputStream();
		out.write(cItem.getBytes());
		out.flush();
	}
}
