/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.sana.mediaviewer.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
//import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * This class configured as controller using annotation and mapped with the 
 * URL of 'module/sana/mediaviewer/mediaViewer.form'.
 */
@Controller
public class MediaViewerController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Success form view name */
	private final String SUCCESS_FORM_VIEW = "/module/sana/mediaviewer/mediaViewer";
	

	/**
	 * Returns any extra data in a key-->value pair kind of way
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest,
	 *      java.lang.Object, org.springframework.validation.Errors)
	 */
	//@Override
	protected Map<String, Object> referenceData(HttpServletRequest request, 
			Object obj, Errors err) throws Exception 
	{
		// this method doesn't return any extra data right now, just an empty map
		return new HashMap<String, Object>();
	}
	

	@RequestMapping(value = "/module/sana/mediaviewer/mediaViewer.form", method = RequestMethod.GET)
	public String handleRequest(@RequestParam(value="encounterId",required=false) String encounterID, 
			Model model) 
	{
        if(encounterID == null || "".equals(encounterID)) {
        	log.error("Invalid encounter ID, can't display media files");
            //return null;
        } else
        	model.addAttribute("enc",
        		Context.getEncounterService().getEncounter(new Integer(encounterID)));
        return SUCCESS_FORM_VIEW;
	}
}
