package org.openmrs.module.sana.queue.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.api.context.Context;


public class FormServlet extends HttpServlet {
    private static final long serialVersionUID = 6220645083994509298L;
	private Log log = LogFactory.getLog(this.getClass());
	
	@Override 
    protected void doGet(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
        log.debug("doGet");
        
        String formIdStr = request.getParameter("formId");

        if(formIdStr == null || "".equals(formIdStr)) {
        	throw new ServletException("No formId provided.");
        }
        
        Integer formId = Integer.parseInt(formIdStr);
        
        Form form = Context.getFormService().getForm(formId);
        
        if(form == null) {
        	throw new ServletException("No form exists with id=" + formId);
        }
        

    }

}
