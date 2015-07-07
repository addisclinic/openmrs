package org.openmrs.module.sana.queue.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "module/sana/mds/")
public class MDSAdminController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Success form view name */
	private final String SUCCESS_FORM_VIEW = "/module/sana/mds/admin";
	
	/**
	 * Initially called after the formBackingObject method to get the landing form name  
	 * @return String form view name
	 */
	@RequestMapping(method = RequestMethod.GET, value="admin")
	public String showAdminForm(ModelMap map){
		return SUCCESS_FORM_VIEW;
	}
	
	/** Success form view name */
	private final String LOG_FORM_VIEW = "/module/sana/mds/logs";
	
	/**
	 * Initially called after the formBackingObject method to get the landing form name  
	 * @return String form view name
	 */
	@RequestMapping(method = RequestMethod.GET, value="logs")
	public String showLogForm(ModelMap map){
		return LOG_FORM_VIEW;
	}
	
	/** Success form view name */
	private final String SXML_FORM_VIEW = "/module/sana/mds/sxml";
	
	/**
	 * Initially called after the formBackingObject method to get the landing form name  
	 * @return String form view name
	 */
	@RequestMapping(method = RequestMethod.GET, value="sxml")
	public String showSXMLForm(ModelMap map){
		return SXML_FORM_VIEW;
	}
	
}
