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
package org.moca.queue.web.servlet;

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
import org.moca.queue.QueueItem;
import org.moca.queue.QueueItemService;
import org.moca.queue.QueueItemStatus;
import org.moca.queue.web.MDSNotificationReply;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.Concept;
import org.openmrs.Encounter;

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
        String doctorComments = request.getParameter("Comments");
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
    	
    	log.error("doctorUrgency " + doctorUrgency);
    	
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
        
        log.error("doctorTreatment " + doctorTreatment);
        
        if(doctorComments != null && !doctorComments.equals("")){
        	//Create a new obs with the doctor's response
            Obs o = new Obs();
            o.setPerson(q.getPatient());
            o.setLocation(lservice.getDefaultLocation());
            
            //Get DOCTOR COMMENTS concept
            Concept commentsConcept = cservice.getConceptByName(
            		"DOCTOR COMMENTS");
            
            if(commentsConcept != null)
            	o.setConcept(commentsConcept);
            else{
            	fail(output,"Couldn't find concept DOCTOR COMMENTS. Go to "
            			+"Concept Dictionary and add this concept.");
            	return;
            }
            
            o.setDateCreated(new Date());
            o.setObsDatetime(new Date());
            o.setCreator(Context.getAuthenticatedUser());
            o.setValueText(doctorComments);
            
            //Update and save the encounter and obs
            e.addObs(o);       
            Context.getObsService().saveObs(o, "");
        }
        else{
        	doctorComments = "";
        }
        
        log.error("doctorComments " + doctorComments);
        
        //Set plan
        String plan = "";
        if(doctorTreatment.equals("") && doctorComments.equals(""))
        	plan = "";
        else if(doctorTreatment.equals(""))
            plan = doctorComments;
        else if(doctorComments.equals(""))
        	plan = doctorTreatment;
        else
        	plan = doctorTreatment + ", " + doctorComments;
        
    	Context.getEncounterService().saveEncounter(e);
        q.setEncounter(e);
    	q.setStatus(QueueItemStatus.CLOSED);
    	q.setDateChanged(new Date());
        queueService.updateQueueItem(q);   
        
        //Get phone number and construct SMS message and email message
        String patientId = e.getPatient().getPatientIdentifier().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"EEE MMM dd yyyy HH:mm zzz");
   	 	
        // Get the user's preferred notification
		String notificationPref = e.getCreator().getUserProperty(
				"notification");
    	//Send SMS
    	try {
        	String SMSmessage = "DX: " + diagnosisList + "; Plan: " + plan;
	    	System.out.println("SaveResponseServlet.doPost(): sms -> " +
	    			"patient: " + patientId + " msg: " + SMSmessage);
    		boolean sms = MDSNotificationReply.sendSMS(q.getPhoneIdentifier(), 
    				q.getCaseIdentifier(), patientId, SMSmessage);

	    	System.out.println("SaveResponseServlet.doPost(): sms -> " +
	    			"success: " + sms);
    	}
    	catch (ConnectException err) {
			log.error("Couldn't connect to notification server " 
					+ Context.getAdministrationService().getGlobalProperty(
							"moca.notification_server_url"));
		} catch (Exception err) {
			fail(output, "Failed to send SMS message." + err.getMessage());
		}
    	 
		//Send Email
    	try{
			//If person who uploaded the case has an email, then email the 
    		// specialist response to them
    		//String uploaderEmailAddress = e.getCreator().getPerson()
    		// .getAttribute("Contact Email").getValue();
    		String uploaderEmailAddress = null;
    		if(notificationPref.equalsIgnoreCase("email")){
    			uploaderEmailAddress = e.getCreator().getUserProperty(
    					"notificationAddress", "noaddress@noserver.com");

    	    	System.out.println("SaveResponseServlet.doPost(): email -> " +
    	    			"uploaderEmailAddress: " +  uploaderEmailAddress);
    		}
    		if(uploaderEmailAddress != null && !uploaderEmailAddress.equals("")){
				//Create a list of email addresses you want the official 
    			// specialist response to be sent to
    	    	String emailMessage = "Date of Encounter: " 
    	    		+ dateFormat.format(q.getDateUploaded()) 
    	    		+"\nReferring Clinician: " + q.getCreator().getGivenName() 
    	    			+" " + q.getCreator().getFamilyName() 
    	    		+"\nPatient ID: " + patientId  
    	    		+"\nName: " + q.getPatient().getGivenName() + " "  
    	    			+q.getPatient().getFamilyName() 
    	    		+"\nAge: " + q.getPatient().getAge().toString() 
    	    		+"\nSite: " + e.getLocation().getDisplayString() 
    	    		+"\n\nDate of Specialist Consult: " 
    	    			+dateFormat.format(q.getDateChanged()) 
    	    		+"\nSpecialist: " 
    	    			+Context.getAuthenticatedUser().getGivenName() + " "  
    	    			+Context.getAuthenticatedUser().getFamilyName() 
    	    		+"\nUrgency Level: " + doctorUrgency 
    	    		+"\nDiagnosis: " + diagnosisList 
    	    		+"\nPlan: " + plan;
    	    	System.out.println("SaveResponseServlet.doPost(): email -> " +
    	    			"patient: " + patientId + " msg: " + emailMessage);
    			String subject = "Referral Response for Patient " + patientId;
	    		List<String> emailAddr = new ArrayList<String>();
	    		emailAddr.add(uploaderEmailAddress);
	    		
	    		Gson gson = new Gson();
	    		Type listType = new TypeToken<ArrayList<String>>() {}.getType();
	    		String emailAddresses = gson.toJson(emailAddr, listType);
	    		
	    		boolean email = MDSNotificationReply.sendEmail(emailAddresses, 
	    				q.getCaseIdentifier(), patientId, subject,emailMessage);

    	    	System.out.println("SaveResponseServlet.doPost(): email -> " +
    	    			"success: " + email);
    		}
		}
    	catch (ConnectException err) {
			log.error("Couldn't connect to email notification server " 
					+ Context.getAdministrationService().getGlobalProperty(
							"moca.email_notification_server_url"));
		} catch (Exception err) {
			err.printStackTrace();
			fail(output,"Failed to send email message " + err.getMessage());
		}

		response.sendRedirect(request.getContextPath() 
				+  "/admin/encounters/encounter.form?encounterId="+encounterId);
    }
    
    
}
