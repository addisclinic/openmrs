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
package org.openmrs.module.sana.queue.web.v1.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.io.PrintWriter;
import java.lang.Integer;
import java.net.ConnectException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.ModuleConstants.Property;
import org.openmrs.module.sana.api.MDSNotificationReply;
import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemService;
import org.openmrs.module.sana.queue.QueueItemStatus;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.User;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.Gson;

/**
 * Handles requests for diagnostic information to be sent to CHW after an 
 * Encounter has been viewed by a Specialist
 *
 * @author Sana Development Team
 *
 */
public class SaveResponseServlet extends HttpServlet {
    private static final long serialVersionUID = -569574L;
    private Log log = LogFactory.getLog(this.getClass());

    /** Prints a FAIL message */
    private void fail(PrintWriter output,String message) {
    	output.println("\nERROR: " + message + "\n");
    	log.error(message);
    }

	/**
	 * POST requests sent here will result in an email and sms message 
	 * with follow up information for sent back to the CHW
	 */
    @Override
    protected void doPost(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
        
        String encounterId = request.getParameter("encounterId");
        String queueItemId = request.getParameter("queueItemId");
        String doctorDiagnosis = request.getParameter("HiddenDiagnoses");
        String doctorUrgency = request.getParameter("Urgency");
        String doctorTreatment = request.getParameter("Treatment");
        String doctorAssessment = request.getParameter("Assessment");
        String doctorRecommendations = request.getParameter("Recommendations");
        PrintWriter output = response.getWriter();
        
        QueueItemService queueService = Context.getService(
        		QueueItemService.class);
        LocationService lservice = (LocationService)Context.getService(
        		LocationService.class);
        ConceptService cservice = Context.getConceptService();
        
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
    	Encounter e = q.getEncounter();
    	
    	//Extract the doctor's response from the HTML form
    	//Parse the diagnosis list
    	String diagnosisList = "";
    	if(doctorDiagnosis != null && !doctorDiagnosis.equals("")){
        	StringTokenizer stokenizer = new StringTokenizer(
        			doctorDiagnosis,",");
        	String diagnosis = "";
        	String name = "";
        	Concept diagnosisConcept = cservice.getConcept("DOCTOR DIAGNOSIS");
        	
    		while(stokenizer.hasMoreTokens())
        	{
    			diagnosis = stokenizer.nextToken();
    			//Create a new obs with this diagnosis
                Obs o = new Obs();
                o.setPerson(q.getPatient());
                o.setLocation(lservice.getDefaultLocation());
                
                //Get DIAGNOSIS concept
                if(diagnosisConcept != null)
                	o.setConcept(diagnosisConcept);
                else{
                	fail(output,"Couldn't find concept DOCTOR DIAGNOSIS. Go to"
                			+" Concept Dictionary and add this concept.");
                	return;
                }
                
                o.setDateCreated(new Date());
                o.setObsDatetime(new Date());
                o.setCreator(Context.getAuthenticatedUser());
                o.setValueText(diagnosis);
                
                //Extract diagnosis name (without SNOMED ID)
                if(diagnosis.contains("|"))
                	name = diagnosis.substring(0,diagnosis.indexOf('|')-1)
                			.toLowerCase();
                else
                	name = diagnosis.toLowerCase();
                
                //Add diagnosis to list
                if(diagnosisList.equals(""))
                	diagnosisList += name;
                else
                	diagnosisList +=  ", " + name;
               
                //Update and save the encounter and queue item
                e.addObs(o);
                Context.getObsService().saveObs(o, "");
        	}
    	}
    	//If null, set to empty string when sending back to phone
    	else{
    		doctorDiagnosis = "";
    	}
    	
    	log.error("Diagnosis " + doctorDiagnosis);
    	
    	if(doctorUrgency != null && !doctorUrgency.equals("")){
        	//Create a new obs with the doctor's response
            Obs o = new Obs();
            o.setPerson(q.getPatient());
            o.setLocation(lservice.getDefaultLocation());
            
            //Get URGENCY LEVEL concept
            Concept urgencyConcept = cservice.getConceptByName(
            		"DOCTOR URGENCY LEVEL");
            
            if(urgencyConcept != null)
            	o.setConcept(urgencyConcept);
            else{
            	fail(output,"Couldn't find concept DOCTOR URGENCY LEVEL. Go to"
            			+" Concept Dictionary and add this concept.");
            	return;
            }
            
            o.setDateCreated(new Date());
            o.setObsDatetime(new Date());
            o.setCreator(Context.getAuthenticatedUser());
            o.setValueText(doctorUrgency);
            
            //Update and save the encounter and queue item
            e.addObs(o);
            Context.getObsService().saveObs(o, "");
        }
    	//If null, set to empty string when sending back to phone
    	else{
    		doctorUrgency = "";
    	}
    	
    	log.debug("doctorUrgency " + doctorUrgency);
    	
        if(doctorTreatment != null && !doctorTreatment.equals("")){
        	//Create a new obs with the doctor's response
            Obs o = new Obs();
            o.setDateCreated(new Date());
            o.setPerson(q.getPatient());
            o.setLocation(lservice.getDefaultLocation());
            
            //Get TREATMENT RECOMMENDATION concept
            Concept treatmentConcept = cservice.getConceptByName(
            		"DOCTOR TREATMENT RECOMMENDATION");
            
            if(treatmentConcept != null)
            	o.setConcept(treatmentConcept);
            else{
            	fail(output,"Couldn't find concept DOCTOR TREATMENT " +
            			"RECOMMENDATION. Go to Concept Dictionary and add this "
            			+"concept.");
            	return;
            }
            
            o.setObsDatetime(new Date());
            o.setCreator(Context.getAuthenticatedUser());
            o.setValueText(doctorTreatment);
            
            //Update and save the encounter and queue item
            e.addObs(o);
            Context.getObsService().saveObs(o, "");
        }
        //If null, set to empty string when sending back to phone
        else{
        	doctorTreatment = "";
        }
        
        log.debug("doctorTreatment " + doctorTreatment);
        
        if(doctorAssessment != null && !doctorAssessment.equals("")){
        	//Create a new obs with the doctor's response
            Obs o = new Obs();
            o.setPerson(q.getPatient());
            o.setLocation(lservice.getDefaultLocation());
            
            //Get DOCTOR ASSESSMENT concept
            Concept assessmentConcept = cservice.getConceptByName(
            		"DOCTOR ASSESSMENT");
            
            if(assessmentConcept != null)
            	o.setConcept(assessmentConcept);
            else{
            	fail(output,"Couldn't find concept DOCTOR ASSESSMENT. Go to "
            			+"Concept Dictionary and add this concept.");
            	return;
            }
            
            o.setDateCreated(new Date());
            o.setObsDatetime(new Date());
            o.setCreator(Context.getAuthenticatedUser());
            o.setValueText(doctorAssessment);
            o.setEncounter(e);
            //Update and save the encounter and obs
            e.addObs(o);       
            Context.getObsService().saveObs(o, "");
        }
        else{
        	doctorAssessment = "";
        }
        
        log.debug("doctorAssessment " + doctorAssessment);
        
        if(doctorRecommendations != null && !doctorRecommendations.equals("")){
        	//Create a new obs with the doctor's response
            Obs o = new Obs();
            o.setPerson(q.getPatient());
            o.setLocation(lservice.getDefaultLocation());
            
            //Get DOCTOR RECOMMENDATIONS concept
            Concept recommendationsConcept = cservice.getConceptByName(
            		"DOCTOR RECOMMENDATIONS");
            
            if(recommendationsConcept != null)
            	o.setConcept(recommendationsConcept);
            else{
            	fail(output,"Couldn't find concept DOCTOR RECOMMENDATIONS. Go to "
            			+"Concept Dictionary and add this concept.");
            	return;
            }
            
            o.setDateCreated(new Date());
            o.setObsDatetime(new Date());
            o.setCreator(Context.getAuthenticatedUser());
            o.setValueText(doctorRecommendations);
            o.setEncounter(e);
            //Update and save the encounter and obs
            e.addObs(o);       
            Context.getObsService().saveObs(o, "");
        }
        else{
        	doctorRecommendations = "";
        }
        
        log.debug("doctorRecommendations " + doctorRecommendations);
        
        //Set plan
        String plan = "";
        if(doctorTreatment.equals("") && doctorRecommendations.equals(""))
        	plan = "";
        else if(doctorTreatment.equals(""))
            plan = doctorRecommendations;
        else if(doctorRecommendations.equals(""))
        	plan = doctorTreatment;
        else
        	plan = doctorTreatment + ", " + doctorRecommendations;
        
    	Context.getEncounterService().saveEncounter(e);
        q.setEncounter(e);
    	q.setStatus(QueueItemStatus.CLOSED);
    	q.setDateChanged(new Date());
        queueService.updateQueueItem(q);   
        
        //Get phone number and construct SMS message and email message
        String patientId = e.getPatient().getPatientIdentifier().toString();
        
   	 	
        // Get the user's preferred notification
		String notificationPref = e.getCreator().getUserProperty(
				"notification");
    	 
    	// TODO Come up with a better way to trigger notification type
		//Send sms
		
		try{
			boolean sms = sendSMS(q, diagnosisList, plan, patientId);
		} catch (ConnectException err) {
    		log.error("Couldn't connect to notification server " 
    			+ Context.getAdministrationService().getGlobalProperty(Property.MDS_URI));
    		fail(output, "Failed to send SMS message." + err.getMessage());
    	} catch (Exception err) {
    		log.error("Unable to send notification", err);
    		fail(output, "Failed to send SMS message." + err.getMessage());
    	}
		
    	try{
			//If person who uploaded the case has an email, then email the 
    		// specialist response to them if they indicated so in their user
    		// profile
			if(notificationPref.equalsIgnoreCase("email")){
				boolean email = sendMail(q, diagnosisList, plan, patientId, doctorUrgency);	
			}
		} catch (ConnectException err) {
			log.error("Couldn't connect to email notification server " 
					+ Context.getAdministrationService().getGlobalProperty(
							Property.EMAIL_URL));
		} catch (Exception err) {
			log.error("Email failed: " + err.getMessage());
			fail(output,"Failed to send email message " + err.getMessage());
		}
    	response.sendRedirect(request.getContextPath() 
				+  "/module/sana/queue/v1/queue.form");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
    	response.sendRedirect(request.getContextPath() 
				+  "/module/sana/queue/v1/queue.htm");
    	
    }
    
    /**
     * Formats the SMS message
     */
    private boolean sendSMS(QueueItem q, String diagnosisList, String plan,
    		String patientId) throws Exception
    {
		//TODO move this to a method
    	//Send SMS
		User user = q.getEncounter().getCreator();
		String smsNumber = user.getUserProperty("Contact Phone");
		String SMSmessage = "DX: " + diagnosisList + "; Plan: " + plan;
		log.debug("Sending sms -> " + smsNumber);
		boolean sms = MDSNotificationReply.sendSMS(smsNumber, 
				q.getCaseIdentifier(), patientId, SMSmessage);
		log.debug("sms sent -> " + smsNumber + ", success: " + sms);
		return sms;
    }
    
    /**
     * Formats and sends and email message
     */
    private boolean sendMail(QueueItem q, String diagnosisList, String plan,
    		String patientId, String doctorUrgency) throws Exception
    {
		//If person who uploaded the case has an email, then email the 
		// specialist response to them
		//String uploaderEmailAddress = e.getCreator().getPerson()
		// 		.getAttribute("Contact Email").getValue();
    	boolean email = false;
		String uploaderEmailAddress = null;
		Encounter e = q.getEncounter();
		uploaderEmailAddress = e.getCreator().getUserProperty(
					"notificationAddress", "noaddress@noserver.com");
		log.debug(": email -> " +
	    			"uploaderEmailAddress: " +  uploaderEmailAddress);
		if(uploaderEmailAddress != null && !uploaderEmailAddress.equals("")){
			// TODO We shouldn't be hard coding this
			SimpleDateFormat dateFormat = new SimpleDateFormat(
	        		"EEE MMM dd yyyy HH:mm zzz");
			
			//Create a list of email addresses you want the official 
			// specialist response to be sent to
	    	String emailMessage = "Date of Encounter: " 
	    		+ dateFormat.format(q.getDateCreated()) 
	    		+"\nReferring Clinician: " + q.getCreator().getGivenName() 
	    			+" " + q.getCreator().getFamilyName() 
	    		+"\nPatient ID: " + patientId  
	    		//+"\nName: " + q.getEncounter().getPatient().getGivenName() + " " 
	    		//+q.getEncounter().getPatient().getFamilyName() 
	    		+"\nAge: " + q.getEncounter().getPatient().getAge().toString() 
	    		+"\nSite: " + e.getLocation().getDisplayString() 
	    		+"\n\nDate of Specialist Consult: " 
	    			+dateFormat.format(q.getDateChanged()) 
	    		+"\nSpecialist: " 
	    			+Context.getAuthenticatedUser().getGivenName() + " "  
	    			+Context.getAuthenticatedUser().getFamilyName() 
	    		+"\nUrgency Level: " + doctorUrgency 
	    		+"\nDiagnosis: " + diagnosisList 
	    		+"\nPlan: " + plan;
	    	log.debug(": email -> " +
	    			"patient: " + patientId + " msg: " + emailMessage);
			String subject = "Referral Response for Patient " + patientId;
    		List<String> emailAddr = new ArrayList<String>();
    		emailAddr.add(uploaderEmailAddress);
    		
    		Gson gson = new Gson();
    		Type listType = new TypeToken<ArrayList<String>>() {}.getType();
    		String emailAddresses = gson.toJson(emailAddr, listType);
    		
    		email = MDSNotificationReply.sendEmail(emailAddresses, 
    				q.getCaseIdentifier(), patientId, subject,emailMessage);

		} else {
			log.warn("Null email address");
		}
    	log.debug(": email -> " + "success: " + email);
		return email;
    }
}
