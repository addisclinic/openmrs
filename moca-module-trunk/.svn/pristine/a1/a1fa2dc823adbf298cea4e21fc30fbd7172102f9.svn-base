package org.moca.queue.web.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moca.queue.QueueItem;
import org.moca.queue.QueueItemService;
import org.moca.queue.QueueItemStatus;
import org.moca.queue.web.MDSMessage;
import org.moca.queue.web.MDSQuestion;
import org.moca.queue.web.MDSRequestResponse;
import org.moca.queue.web.MDSResponse;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;

import com.google.gson.Gson;

/**
 * Provides encounter upload services to the Sana Queue
 * 
 * @author Sana Development Team
 *
 */
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 4847187771370210197L;
    
    private Log log = LogFactory.getLog(this.getClass());
    
    /**
     * Returns zero or one patient from the OpenMRS Patient service looked up
     * by the patient identifier
     * 
     * @param patientIdentifier the id to look up
     * @return a patient with a matching identifier as produced by 
     * 			Patient.getIdentifier() or null
     */
    private Patient getPatient(String patientIdentifier) {
    	// Safety check
        if(patientIdentifier == null || "".equals(patientIdentifier)) 
            return null;
        
        PatientService patientService = Context.getPatientService();
        List<Patient> patients = patientService.getPatients(null, 
        		patientIdentifier, null, false);
        
        Patient patient = null;
        if(patients.size() > 0)
            for(Patient p : patients) {
        	if(p.getPatientIdentifier().getIdentifier().equals(patientIdentifier))
        		patient = p;
        		break;
            }
        return patient;
    }
    
    /**
     * Gets any files uploaded with this request.
     * @param request the original request.
     * @return A list of FileItem objects.
     * @throws FileUploadException 
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    private List<FileItem> getUploadedFiles(HttpServletRequest request) throws 
    	FileUploadException 
    {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        if(!request.getHeader("content-type").contains(
        		"application/x-www-form-urlencoded"))        
            return (List<FileItem>)upload.parseRequest(request);
        return new ArrayList<FileItem>();
    }

    /**
     * Called by the server to handle POST requests to this module.
     * 
     * POST requests must have the following fields:
     * <ul>
     * <li><b>description</b>JSON encoded encounter data</li> 
     * <li><b>[medImageFile-$element-$index.$ext,]</b>One or more file fields 
     * where the following come from the original Procedure xml
     *  	<ul>
     *  	<li>$element is the 'id' attribute value</li>
     *    	<li>$index is one of the csv's from the 'answer' attribute</li>
     *    	</ul>
     * </li>
     * </ul>
     * 
     * The 'description' field is parsed into an MDSMessage object consisting of
     * <ul>
     * <li><b>phoneId</b> the phone number of the client. Will be used for any 
     * 		response notifications</li>
     * <li><b>procedureDate</b> the date when the data was collected</li>
     * <li><b>procedureTitle</b> the title as visible in the Queue</li>
     * <li><b>patientId</b> a unique patient identifier</li>
     * <li><b>caseIdentifier</b> a client assigned UUID</li>
     * <li><b>responses</b> Encounter data which will be stored in OpenMRS as
     * 		Observations.</li>
     * </ul> 
     * 
     * <b>Note:<b> The Procedure xml format will of the 1.x branches will be 
     * changed in upcoming versions.
     */
    @Override
    protected void doPost(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
        log.info("MDSUploadServlet Got post");
        List<FileItem> files;
        try {
            files = getUploadedFiles(request);
        } catch(FileUploadException e) {
            MDSResponse.fail(request, response, 
            		"File parse error: " + e.getMessage(), log);
            return;
        }
        
        // Purely for debugging remove for release
        for(FileItem f : files) {
        	log.debug("UploadServlet.doPost: " +
        			"Got file fieldname:" + f.getFieldName() + 
        			" content type: " + f.getContentType() + 
        			" name:" + f.getName() + 
        			" size:" + f.getSize());
        }
        //TODO move this into a method so that we can handle the xml changes
        String jsonDescription = request.getParameter("description");
        if(jsonDescription == null) { 
        	// If we can't find it in the parameters, then we need to search 
        	// for it in the files list.
        	for (int i = 0; i < files.size(); ++i) {
        		FileItem f = files.get(i);
        		if (f == null)
        			continue;
        		if ("description".equals(f.getFieldName())) {
        			jsonDescription = f.getString();
        			files.remove(i);
        			break;
        		}
        	}
            // If still null, fail
        	if (jsonDescription == null) {
        		MDSResponse.fail(request,response, 
        				"Invalid description for encounter",log);
        		return;
        	}
        }
        // Convert to MDSMessage
        MDSMessage message;
        try {
        	Gson gson = new Gson();
        	message = gson.fromJson(jsonDescription, MDSMessage.class);
        } catch (com.google.gson.JsonParseException e) {
        	MDSResponse.fail(request, response, 
        			"Error parsing MDSMessage: " + e, log);
        	return;
        }
        String mid = message.caseIdentifier;
        // Only need this when debugging
        log.debug("Parsed message.id: " + mid);
        log.debug("message("+mid+"): patient: "+message.patientId);
        log.debug("message("+mid+"): phone: "+message.phoneId);
        log.debug("message("+mid+"): title: "+message.procedureTitle);
        log.debug("message("+mid+"): date: "+message.procedureDate);
        for(MDSQuestion q : message.questions)
        	System.out.println("UploadServlet.doPost: message("+mid+
        			"): question:"+q);
        
        // Check the date format
        String procedureDateString = message.procedureDate;        
        SimpleDateFormat sdf = Context.getDateFormat();
        Date procedureDate = new Date();
        try {
            procedureDate = sdf.parse(procedureDateString);
        } catch (Exception ex) { 
        	MDSResponse.fail(request, response, 
        			"date format: " + procedureDateString, log);
            return;
        }
        
        // Try to validate things inside the MDSMessage.
        Patient patient = getPatient(message.patientId);
        if(patient == null) {
        	MDSResponse.fail(request, response, 
        			"caseIdentifier:" + mid + ", Invalid patient id: " 
        			+ message.patientId,log);
            return;
        } else {
        	log.info("caseIdentifier:" + mid +", has patient " 
        			+ message.patientId);
        }
        // processUpload is called to translate the individual responses into 
        // OpenMRS Observations
        try{
        	processUpload(patient, message, procedureDate, files);
        }
        catch(Exception e){
        	StackTraceElement init = e.getStackTrace()[0];
        	log.error("Upload Error: " + e.toString()
        				+ ", source: " + init.toString()
                		+ ", method: " + init.getMethodName()  
                		+ " at line no. : " + init.getLineNumber());
        	MDSResponse.fail(request, response, e.toString(),log);
        	return;
        }
        MDSResponse.succeed(request, response, 
        		"successfully uploaded procedure",log);
    }

    /**
     * Takes an MDSmessage and creates Observations and Encounter in OpenMRS
     * 
     * @param patient The subject of the data collection
     * @param message The MDSMessage containing the encounter text
     * @param procedureDate The date of the encounter
     * @param files Files collected as part of the procedure
     * @throws IOException 
     */
    void processUpload(Patient patient, MDSMessage message, Date procedureDate, 
    		List<FileItem> files) throws IOException {

        // Parses the responses
        Map<String,List<FileItem>> fileMap = new HashMap<String,List<FileItem>>();
        for(FileItem f : files) {
            // Look for the form: medImageFile-id-number
            // TODO(XXX) we could run into trouble with this later if someone 
        	// puts a '-' into the EID
            String[] parts = f.getFieldName().split("-");
            assert(parts.length == 3);
            assert(parts[0].equals("medImageFile"));
            
            String eid = parts[1];
            if(fileMap.containsKey(eid)) {
                fileMap.get(eid).add(f);
                log.info("Upload case: " + message.caseIdentifier 
                		+ ", element: " +eid 
                		+", has file: "+f.getFieldName());
            } else {
                List<FileItem> fileList = new ArrayList<FileItem>();
                fileList.add(f);
                fileMap.put(eid, fileList);
            }
        }

        // Now we assemble the encounter
        Encounter e = new Encounter();
        e.setPatient(patient);
        e.setEncounterDatetime(procedureDate);
        e.setDateCreated(new Date());
        e.setCreator(Context.getAuthenticatedUser());
        e.setProvider(Context.getAuthenticatedUser().getPerson());
        Location location = Context.getLocationService().getDefaultLocation();
        e.setLocation(location);
        
        // TODO(XXX) Replace these, catch exceptions, etc.
        e.setForm(Context.getFormService().getAllForms().get(0));
        e.setEncounterType(Context.getEncounterService().getAllEncounterTypes()
        		.get(0));
        
        // Create the observations
        for(MDSQuestion q : message.questions) {
        	log.debug("Parsing question: "+q);
            // Get Concept for Type/ID
            Concept c = getOrCreateConcept(message, q.id, q.type, q.concept, 
            		q.question);
            boolean isComplex = c.isComplex();
            log.debug("case: " + message.caseIdentifier + " question: " + q.id 
            		+ "has Concept: " + c.getName() 
            		+" and is complex: " + isComplex);
            // 1.x versions allow multiple files per question so we need to
            // make one observation per file
            if(isComplex) {
                // If no file was uploaded, then there is no obs to make.
            	log.debug("Make one obs per file");
                if(fileMap.containsKey(q.id)) {
                    // Make one obs per file
                    for(FileItem f : fileMap.get(q.id)) {
                    	log.debug("case: " + message.caseIdentifier
                    			  + " question: " + q.id 
                    			  + " Creating new complex obs");
                    	e.addObs(makeObs(patient, procedureDate, c, q, f));
                    	Context.getEncounterService().saveEncounter(e);
                    	//observations.add(makeObs(patient, procedureDate, c, q, f));
                    }
                } else 
                	// No observation if no files
                	log.info("case: " + message.caseIdentifier
                			  + " question: " + q.id 
                			  + " No files for complex concept: " + c.getName());
                
            } else {
                // Not complex, make a regular obs 
            	log.debug("case: " + message.caseIdentifier
          			  + " question: " + q.id 
          			  + " Creating new text obs");
            	e.addObs(makeObs(patient, procedureDate, c, q, null));
            	Context.getEncounterService().saveEncounter(e);
            }
        }
        // This constructs the queue item and saves it
    	QueueItem q = new QueueItem();
        q.setStatus(QueueItemStatus.NEW);
        q.setPhoneIdentifier(message.phoneId);
        q.setCaseIdentifier(message.caseIdentifier);
        q.setProcedureTitle(message.procedureTitle);
        q.setCreator(Context.getAuthenticatedUser());
        q.setDateCreated(new Date());
        q.setChangedBy(Context.getAuthenticatedUser());
        q.setDateChanged(new Date());
        q.setDateUploaded(new Date());
        q.setPatient(patient);
        q.setEncounter(e);
        
        Context.getService(QueueItemService.class).createQueueItem(q);
        //TODO Remove this as we probably don't want to send an email for each
        //Send notification email of upload
        /*try{
        	String emailAddresses = Context.getAdministrationService()
        	.getGlobalProperty("moca.email_for_tracking_cases");
			if(emailAddresses != null && !emailAddresses.equals("")) {
				MDSNotificationReply.sendEmail(emailAddresses, 
					message.caseIdentifier, 
					patient.getPatientIdentifier().toString(), 
					e.getEncounterId().toString(), 
					"Successful upload of case for Patient ID#" 
					+ patient.getPatientIdentifier().toString() 
					+ " as OpenMRS Encounter " + e.getEncounterId());      
			}
        }catch(Exception ex){
			log.error("Couldn't send email notification upon upload of case to OpenMRS " + ex.toString());
		}*/
          
    }
    
    //TODO Do we want encounter uploads to trigger concept creation? This can 
    // cause duplicate and erroneous concepts loaded into the database.
    // Temporary hack until we get the Android XML format to have more flexibility.
    /**
     * Fetches or creates a Concept from the OpenMRS data store.
     *
     * @param m The mds message from an originating request.
     * @param eid The 'id' attribute of the Procedure xml element.
     * @param type The 'type' attribute of the Procedure xml element.
     * @param conceptName The 'concept' attribute of the Procedure xml element.
     * 		which should map to a 'Name' field for an existing OpenMRS
     * 		Concept.
     * @param question The 'question' attribute of the Procedure xml element
     * 		which should map to a 'description' field for an existing OpenMRS
     * 		Concept.
     * @return A valid OpenMRS Concept or null.
     */
    private Concept getOrCreateConcept(MDSMessage m, String eid, String type, 
    		String conceptName, String question) {

        if (conceptName == null || "".equals(conceptName)){
        	conceptName = m.caseIdentifier + ":" + eid;
        }
        log.debug("case: " + m.caseIdentifier + " element: " + eid  
        		+ " looking up concept, name: " + conceptName 
        		+ ", description: " + question);
        
        ConceptService cs = Context.getConceptService();
        Concept c = null;
		
		// Search for a concept named conceptName

		// Get concepts matching these words in this locale
        Locale defaultLocale = Context.getLocale();
		List<ConceptWord> conceptWords = new Vector<ConceptWord>();
		conceptWords.addAll(cs.getConceptWords(conceptName, defaultLocale));
		log.debug("Found Concept words with matching name:" 
					+ conceptWords.toString());
		// Use the description field to uniquely match 
		for(ConceptWord cw : conceptWords){
			try {
				log.debug("Testing: (" +conceptName + ", " + question + " ) " 
						+ " against ConceptWord:  (" + cw.getConceptName() + ", " 
						+ cw.getConcept().getDescription().getDescription() 
						+ " ) ");
				// stop checking if there is a match
				if(cw.getConcept().getDescription().getDescription()
						.equals(question))
				{
					log.debug("Concept Matched: (" + conceptName + ", " 
							+ question + " ) id: " + cw.getConcept().getId());
					break;
				}
			} catch(Exception err){
				log.error("Skipping concept " + conceptName + 
						" because of error: " + err.toString());
				//err.printStackTrace();
			}
		}
		
		//c = cs.getConceptByName(conceptName);
		if(c != null)
			log.info("concept found: " + c.getDisplayString());

		// Comment this out if you don't want the end users creating concepts
		// Create an appropriate concept for this question
        if(c == null) {
            // TODO make this constant / static somewhere
            Map<String,String> typeMap = new HashMap<String,String>();
            typeMap.put("PICTURE", "Complex");
            typeMap.put("SOUND", "Complex");
            typeMap.put("VIDEO", "Complex");
            typeMap.put("BINARYFILE", "Complex");
            typeMap.put("TEXT", "Text");
            typeMap.put("ENTRY", "Text");
            typeMap.put("SELECT", "Text");
            typeMap.put("MULTI_SELECT", "Text");
            typeMap.put("RADIO", "Text");
            typeMap.put("GPS", "Text");
            typeMap.put("INVALID", "Text");
            typeMap.put("PATIENT_ID", "Text");
            
            Map<String,String> handlerMap = new HashMap<String,String>();
            handlerMap.put("PICTURE", "ThumbnailingImageHandler");
            handlerMap.put("SOUND", "MediaFileHandler");
            handlerMap.put("VIDEO", "MediaFileHandler");
            handlerMap.put("BINARYFILE", "MediaFileHandler");

            String typeName = typeMap.get(type);
            
            if (typeName == null) {
            	typeName = "Text";
            }
            ConceptDatatype conceptType = Context.getConceptService()
            								.getConceptDatatypeByName(typeName);
            // Shouldn't happen
            if(conceptType == null)
                return null;
            ConceptClass conceptClass = Context.getConceptService()
            								.getConceptClassByName("Question");
            // Shouldn't happen
            if(conceptClass == null)
                return null;
            
            // Start building the Concept
            if(typeName.equals("Complex")) {
            	String handler = handlerMap.get(type);
            	if (handler == null) {
            		// Eep! Fall back on a basic file handler
            		handler = "FileHandler";
            	}
            	c = new ConceptComplex(null, handler); // I feel dirty.
            } else {
                c = new Concept();
            }
            // Concept fields
            c.setDatatype(conceptType);            
            c.setConceptClass(conceptClass);
            c.setCreator(Context.getAuthenticatedUser());
            c.setDateCreated(new Date());
            
            ConceptNameTag preferredTag = Context.getConceptService()
            			.getConceptNameTagByName(ConceptNameTag.PREFERRED);
            // Make sure it is available in all locales supported by the server
            List<Locale> locales = Context.getAdministrationService()
            			.getAllowedLocales();
			for (Locale locale : locales) {
				// concept attribute from procedure xml
				ConceptName name = new ConceptName(conceptName, locale);
	            name.addTag(preferredTag);
	            c.addName(name);
				// question attribute from procedure xml
	            ConceptDescription description = new ConceptDescription(
	            		question, locale);
	            description.setCreator(Context.getAuthenticatedUser());
	            c.addDescription(description);
			}
            Context.getConceptService().saveConcept(c);
        }
        return c;
    }
    
    /**
     * Constructs a new Observation
     * @param p The subject of the observation
     * @param c The Observation concept
     * @param q the question attribute from the procedure xml
     * @param f a file item associated with the observation
     * @return A new Obs 
     * @throws IOException
     * 
     * @deprecated
     */
    private Obs makeObs(Patient p, Concept c, MDSQuestion q, FileItem f) 
    	throws IOException 
    {
        Obs o = new Obs();
        
        o.setCreator(Context.getAuthenticatedUser());
        o.setDateCreated(new Date());
        o.setPerson(p);
        Location location = Context.getLocationService().getDefaultLocation();
        o.setLocation(location);
        o.setConcept(c);
        o.setValueText(q.answer);
        if(f == null)
        	o.setValueText(q.answer);
        else
        	o.setValueText(q.type);
        
        if(f != null) {
        	System.out.println("UploadServlet.makeObs: file:" + f.getName());
            ComplexData cd = new ComplexData(f.getName(), f.getInputStream());
            o.setComplexData(cd);
        }
        return o;
    }
    
    /**
     * Connstructs a new Observation. And saves it in the database. 
     * 
     * This version of makeObs is intended to be forward compatible, OpenMRS
     * ver. greater than 1.6, by handling modifications to the OpenMRS 
     * hibernation mechanism.
     * 
     * @param p The subject of the observation
     * @param date The date of the observation 
     * @param c The Observation concept
     * @param q the question attribute from the procedure xml
     * @param f a file item associated with the observation
     * @return A new Obs 
     * @throws IOException
     */
    private Obs makeObs(Patient p, Date date, Concept c, MDSQuestion q, 
    		FileItem f) throws IOException {
        Obs o = new Obs();
        
        o.setCreator(Context.getAuthenticatedUser());
        o.setPerson(p);
        o.setDateCreated(new Date());
        o.setObsDatetime(date);
        o.setConcept(c);
        Location location = Context.getLocationService().getDefaultLocation();
        o.setLocation(location);
        if(f == null)
        	o.setValueText(q.answer);
        else
        	o.setValueText(q.type);
        
        if(f != null) {
        	System.out.println("UploadServlet.makeObs: file:" + f.getName());
            ComplexData cd = new ComplexData(f.getName(), f.getInputStream());
            o.setComplexData(cd);
        }
        Context.getObsService().saveObs(o, "");
        Integer id = o.getObsId();
        Context.evictFromSession(o);
        return Context.getObsService().getObs(id);
    }
    
    //TODO Use this to look up encounters?
    // for testing 
    @Override
    protected void doGet(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
        MDSResponse.succeed(request, response, 
        		"Hello World from UploadServlet", log);
    }
}


