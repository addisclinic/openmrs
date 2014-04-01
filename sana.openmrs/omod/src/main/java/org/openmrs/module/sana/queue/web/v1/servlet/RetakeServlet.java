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
    	
    	//Change case status to deferred
    	q.setStatus(QueueItemStatus.DEFERRED);
    	q.setDateChanged(new Date());
        queueService.updateQueueItem(q);   
    	
    	String patientId = q.getPatient().getPatientIdentifier().toString();
    	SimpleDateFormat dateFormat = new SimpleDateFormat(SMS.DATE_FORMAT);
        
    	String smsMessage = SMS.DEFAULT_MSG + msg;
    	String emailMessage = "More info needed (before diagnosis can be made): " 
    			+ msg
    		+ "\n\nDate of Encounter: " + dateFormat.format(q.getDateCreated()) 
    		+ "\nReferring Clinician: " + q.getCreator().getGivenName() 
    			+ " " + q.getCreator().getFamilyName()
    		+ "\nPatient ID: " + patientId  
			+ "\nName: " + q.getPatient().getGivenName() + " " 
				+ e.getPatient().getFamilyName() 
			+ "\nAge: " + q.getPatient().getAge().toString() 
			+ "\nSite: " + e.getLocation().getDisplayString() 
			+"\n\nDate of Specialist Consult: " 
				+ dateFormat.format(q.getDateChanged()) 
			+ "\nSpecialist: " + Context.getAuthenticatedUser().getGivenName() 
				+ " " + Context.getAuthenticatedUser().getFamilyName();
								
    	String subject = "Requesting More Information for Patient " + patientId;
        User user = q.getEncounter().getCreator();
    	//Send SMS
    	try {
    		MDSNotificationReply.sendSMS(user.getUserProperty("Contact Phone"), 
    				q.getCaseIdentifier(), patientId, smsMessage);
        	output.print("OK");
    	}
    	catch (ConnectException err) {
			log.error("Couldn't connect to notification server " 
					+ Context.getAdministrationService().getGlobalProperty(
							Property.MDS_URI));
		} catch (Exception err) {
			log.error(err.getClass() + " " + err.getMessage(), err);
			response.setContentType("text; charset=UTF-8");
			output.print("FAIL");
		}
    	// TODO SMS or email
		//Send Email
    	/*
		try{
			//If person who uploaded the case has an email, then email the 
			// specialist response to them
    		//String uploaderEmailAddress = e.getCreator().getPerson()
			// .getAttribute("Contact Email").getValue();
    		String uploaderEmailAddress = e.getCreator().getUserProperty(
    				"notificationAddress","noaddress@noserver.com");
			
    		if(uploaderEmailAddress != null && !uploaderEmailAddress.equals(""))
    		{
				//Create a list of email addresses you want the official 
    			// specialist response to be sent to
	    		List<String> emailAddr = new ArrayList<String>();
	    		emailAddr.add(uploaderEmailAddress);
	    		
	    		Gson gson = new Gson();
	    		Type listType = new TypeToken<ArrayList<String>>() {}.getType();
	    		String emailAddresses = gson.toJson(emailAddr, listType);
	    		
	    		MDSNotificationReply.sendEmail(emailAddresses, 
	    				q.getCaseIdentifier(), patientId, subject, 
	    				emailMessage);	
    			output.print("OK");
    		}
		}
    	catch (ConnectException err) {
			log.error("Couldn't connect to email notification server " 
					+ Context.getAdministrationService().getGlobalProperty(
							Property.EMAIL_URL));
		} catch (Exception err) {
			log.error(err.getClass() + " " + err.getMessage(), err);
			response.setContentType("text; charset=UTF-8");
			output.print("FAIL");
		}
		*/
    }
}


