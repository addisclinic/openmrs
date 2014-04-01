package org.openmrs.module.sana.queue.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@Controller
//@RequestMapping(value = "module/sana/mds/logs")
public class MDSLogController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Success form view name */
	private final String SUCCESS_FORM_VIEW = "/module/sana/mds/logs";
	
	/**
	 * Initially called after the formBackingObject method to get the landing form name  
	 * @return String form view name
	 */
	//@RequestMapping(method = RequestMethod.GET, value="/logs")
	public String showForm(ModelMap map){
		return SUCCESS_FORM_VIEW;
	}
}
