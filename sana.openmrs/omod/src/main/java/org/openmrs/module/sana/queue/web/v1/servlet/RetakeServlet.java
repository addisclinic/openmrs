package org.openmrs.module.sana.queue.web.v1.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.ModuleConstants.Property;
import org.openmrs.module.sana.api.MDSNotificationReply;
import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemService;
import org.openmrs.module.sana.queue.QueueItemStatus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * A servlet for handling requests which require a retake notfication to be sent
 * to the CHW
 * 
 * @author Sana Development Team
 *
 */
public class RetakeServlet extends HttpServlet {

	public static final class SMS{
	    public static final String DATE_FORMAT = "EEE MMM dd yyyy HH:mm zzz";
		public static final String DEFAULT_MSG = "Diagnosis could not be made. More info needed: ";
	}
	
    private static final long serialVersionUID = 22771370210197L;
    private Log log = LogFactory.getLog(this.getClass());
	/**
	 * POST requests sent here will result in an email and sms message 
	 * requesting additional information for diagnosis being sent back to the
	 * CHW
	 */
    @Override
    protected void doPost(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
    	String msg = request.getParameter("msg");
        String queueItemId = request.getParameter("queueItemId");
        PrintWriter output = response.getWriter();
        log.info("Request more info with message " + msg 
        		+ " for queue item " + queueItemId);
        
        if(queueItemId == null || queueItemId.equals("")){
        	log.error("Error, Retake Button: could not retrieve queue item ID");
        	output.print("FAIL");
        	return;
        }
        
        QueueItemService queueService = Context.getService(
        		QueueItemService.class);
        LocationService lservice = (LocationService)Context.getService(
        		LocationService.class);
        ConceptService cservice = Context.getConceptService();
        
    	QueueItem q = queueService.getQueueItem(Integer.parseInt(queueItemId)); 
    	
    	if(q == null)
    	{
    		log.error("QueueItem found is null");
    		output.print("FAIL");
    		return;
    	}
    	
    	Encounter e = q.getEncounter();
    	if(msg != null && !msg.equals("")){
        	//Create a new obs with the doctor's request for more information
            Obs o = new Obs();
            o.setPerson(q.getPatient());
            o.setLocation(lservice.getDefaultLocation());
            
            //Get DOCTOR INFO REQUEST concept
            Concept infoConcept = cservice.getConceptByName(
            		"DOCTOR INFO REQUEST");
            if(infoConcept != null)
            	o.setConcept(infoConcept);
            else{
            	log.error("ERROR: Couldn't find concept DOCTOR INFO REQUEST. "
            			+ "Go to Concept Dictionary and add this concept.");
            	output.print("FAIL");
            	return;
            }
            
            o.setDateCreated(new Date());
            o.setObsDatetime(new Date());
            o.setCreator(Context.getAuthenticatedUser());
            o.setValueText(msg);
            //Update and save the encounter and queue item
            e.addObs(o);       
            Context.getObsService().saveObs(o, "");
        }
        else{
        	msg = "";
        }

        log.debug("msg " + msg);
        
        ////////////////////////////////////////
        //DAS Add Below 072314
        ////////////////////////////////////////
        //Set plan
        String doctor_request = "";
        if(msg.equals(""))
        	doctor_request = "";
        else
        	doctor_request = msg;
    	
    	//Change case status to deferred
    	q.setStatus(QueueItemStatus.DEFERRED);
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
			boolean sms = sendSMS(q, doctor_request, patientId);
		} catch (ConnectException err) {
    		log.error("Couldn't connect to notification server " 
    			+ Context.getAdministrationService().getGlobalProperty(Property.MDS_URI));
    		//fail(output, "Failed to send SMS message." + err.getMessage());
    	} catch (Exception err) {
    		log.error("Unable to send notification", err);
    		//fail(output, "Failed to send SMS message." + err.getMessage());
    	}
		
    	try{
			//If person who uploaded the case has an email, then email the 
    		// specialist response to them if they indicated so in their user
    		// profile
			if(notificationPref.equalsIgnoreCase("email")){
				boolean email = sendMail(q, msg, patientId);	
			}
		} catch (ConnectException err) {
			log.error("Couldn't connect to email notification server " 
					+ Context.getAdministrationService().getGlobalProperty(
							Property.EMAIL_URL));
		} catch (Exception err) {
			log.error("Email failed: " + err.getMessage());
			//fail(output,"Failed to send email message " + err.getMessage());
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
    private boolean sendSMS(QueueItem q, String doctor_request,
    		String patientId) throws Exception
    {
		//TODO move this to a method
    	//Send SMS
		User user = q.getEncounter().getCreator();
		String smsNumber = user.getUserProperty("Contact Phone");
		String SMSmessage = "Request: " + doctor_request;
		log.debug("Sending sms -> " + smsNumber);
		boolean sms = MDSNotificationReply.sendSMS(smsNumber, 
				q.getCaseIdentifier(), patientId, SMSmessage);
		log.debug("sms sent -> " + smsNumber + ", success: " + sms);
		return sms;
    }
    
    /**
     * Formats and sends and email message
     */
    private boolean sendMail(QueueItem q, String doctor_request,
    		String patientId) throws Exception
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
	    		+"\nRequest: " + doctor_request;
	    	log.debug(": email -> " +
	    			"patient: " + patientId + " msg: " + emailMessage);
			String subject = "Information Request for Patient " + patientId;
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