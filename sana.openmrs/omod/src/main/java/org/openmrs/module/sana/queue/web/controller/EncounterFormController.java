package org.openmrs.module.sana.queue.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemService;
import org.openmrs.module.sana.queue.QueueItemStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * This controller backs the /web/module/encounter.jsp page and is tied to that 
 * jsp page by the RequestMapping annotation.
 *
 * @author Sana Development Team
 *
 */
@Controller
@SessionAttributes("encounter")
public class EncounterFormController {

	protected final Log log = LogFactory.getLog(getClass());

	/** Success form view name */
	private final String SUCCESS_VIEW = "/module/sana/queue/v1/encounterViewer";

	@RequestMapping(value = "/module/sana/queue/v1/encounterViewer.form", method = RequestMethod.GET)
    protected String setupForm(ModelMap map, @RequestParam(value="encounterId", required=false)
    	String encounterId, @RequestParam(value="queueItemId", required=false)
    		String queueItemId) throws Exception
    		{
    		
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
    	map.put("encounter", efbo);
    	return SUCCESS_VIEW;
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
