/**
 * populate the "model" object with data from the images so that we can display a list of them
 * in our tab in the patient view
 */
package org.openmrs.module.sana.mediaviewer.web.controller;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.Extension.MEDIA_TYPE;
//import org.openmrs.module.web.extension.FormEntryHandler;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.controller.PortletController;
import org.springframework.web.servlet.ModelAndView;


/**
 * Controller for the patientEncounters portlet.
 * 
 * Provides a map telling which forms have their view and edit links overridden by form entry modules  
 */
public class PatientEncountersPortletController extends PortletController {

protected String viewName;
	
	/**
	 * @see org.openmrs.web.controller.PortletController
	 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ModelAndView mav = super.handleRequest(request, response);
		mav.setViewName(viewName);
		return mav;
	}

	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
        Patient patient = (Patient) model.get("patient");
        List<Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);
        model.put("allEncounters", encounters);
	}
	
	/**
	 * @param viewName the viewName to set
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	
	
}
