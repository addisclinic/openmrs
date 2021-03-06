package org.openmrs.module.sana.queue.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemService;

//@Deprecated
public class QueueServlet extends HttpServlet {
    private static final long serialVersionUID = -5697343624828598856L;
    private Log log = LogFactory.getLog(this.getClass());
    
    /**
     * 
     */
    @Override 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("doGet");
        PrintWriter writer = response.getWriter();
        writer.print("Hello Worldz (from Servlet doGet)<br/>");
        
        try {
        	
            QueueItemService service = (QueueItemService)Context.getService(QueueItemService.class);;
            for (QueueItem i : service.getVisibleQueueItems()) {
                writer.print("Queue Item:" + i.getPatientId() + " " 
                		+ (i.getVisible() ? "vis" :"invis") + " " 
                		+ i.getEncounter().getObs().size() + " obs");
            }
            
        } catch(APIException api) {
            api.printStackTrace(writer);
        } finally {
        	writer.close();
        }
    
    }
    
    @Override 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter writer = response.getWriter();
    	try{
    		writer.print("Hello Worldz (from Servlet doPost)<br/>");
    	} finally {
    		writer.close();
    	}
    }
    
    @Override
    public String getServletInfo() {
        return QueueServlet.class.getName();
    }
}
