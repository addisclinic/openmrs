package org.moca.queue.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moca.queue.QueueItem;
import org.moca.queue.QueueItemService;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

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
            Object o = Context.getService(QueueItemService.class);
            QueueItemService service = (QueueItemService)o;
            for (QueueItem i : service.getVisibleQueueItems()) {
                writer.print("Queue Item:" + i.getPatientId() + " " 
                		+ (i.getVisible() ? "vis" :"invis") + " " 
                		+ i.getEncounter().getObs().size() + " obs");
            }
        } catch(APIException api) {
            api.printStackTrace(writer);
        }
    
    }
    
}
