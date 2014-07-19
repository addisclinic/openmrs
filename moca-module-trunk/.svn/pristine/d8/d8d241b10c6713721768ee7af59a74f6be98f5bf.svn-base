package org.moca.queue.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moca.queue.QueueItem;
import org.moca.queue.QueueItemService;
import org.moca.queue.QueueItemStatus;
import org.openmrs.api.context.Context;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * This controller backs the /web/module/encounter.jsp page. This controller is 
 * tied to that jsp page in the /metadata/moduleApplicationContext.xml file
 */
public class EncounterFormController extends SimpleFormController {

	protected final Log log = LogFactory.getLog(getClass());
    
	/**
	 * Fetches the Encounter data which will be viewed and wraps it in a 
	 * EncounterFBO
	 */
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws 
    	Exception 
    {
    	String encounterId = request.getParameter("encounterId");
    	String queueItemId = request.getParameter("queueItemId");
    	QueueItemService queueService = Context.getService(
    			QueueItemService.class);
    	QueueItem q = null;
    	
    	if(queueItemId != null && !"".equals(queueItemId)) {
    		q = queueService.getQueueItem(Integer.parseInt(queueItemId)); 
    	} else {
    		Integer eid = Integer.parseInt(encounterId);
    		for(QueueItem qi : queueService.getQueueItems()) {
    			if(qi.getEncounterId().equals(eid)) {
    				q = qi;
    			}
    		}
    	}
    	if(q == null) {
    		throw new Exception();//TODO
    	}
		// Hint that NEW items should transition to IN_PROGRESS once edited.
		if (QueueItemStatus.NEW.equals(q.getStatus())) {
			q.setStatus(QueueItemStatus.IN_PROGRESS);
			queueService.updateQueueItem(q);
		}
    	EncounterFBO efbo = new EncounterFBO(q);
    	return efbo;
    }
    //TODO 
    /**
     * Creates a new map with a single entry having "queueItemStatusValues" as 
     * a key and a list of status of Queue items as the values
     */
    protected Map referenceData(HttpServletRequest request) {
    	Map<String,Object> data = new HashMap<String,Object>();
    	data.put("queueItemStatusValues",QueueItemStatus.values());
    	return data;
    }
}
