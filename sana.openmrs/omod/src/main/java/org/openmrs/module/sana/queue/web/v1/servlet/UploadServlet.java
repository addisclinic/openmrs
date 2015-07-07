package org.openmrs.module.sana.queue.web.v1.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.ModuleConstants;
import org.openmrs.module.sana.ModuleConstants.Property;
import org.openmrs.module.sana.api.MDSMessage;
import org.openmrs.module.sana.api.MDSQuestion;
import org.openmrs.module.sana.api.MDSResponse;
import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemService;
import org.openmrs.module.sana.queue.QueueItemStatus;
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
    
    public static class Params{
    	public static final String DESCRIPTION = "description"; 
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
        log.info("UploadServlet Got POST.");
        MDSMessage message = null;
        Patient patient = null;
        Date encounterDateTime = null;
		Encounter encounter = null;
        Map<String, Concept> idMap = null;
		Set<Obs> observations = null;
		QueueItem queueItem = null;
        MDSResponse mds = null;
        List<FileItem> files = Collections.emptyList();
        
        // Step through queue item creation, log and rethrow any APIExceptions
        // as ServletException for clean up at the end
        try{
        	// Check for any files
        	try {
        		files = getUploadedFiles(request);
        	} catch(APIException ex) {
        		log.error("Error getting Request files!");
        		mds = MDSResponse.fail("Error getting Request files");
				throw ex;
        	}


        	//TODO move this into a resource method so that we can handle the 
        	// xml changes
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
        			log.error("Invalid description");
        			mds = MDSResponse.fail("Invalid description for encounter");
    				throw new ServletException("Invalid description for encounter");
        			}
        	}

        	// Convert to MDSMessage
        	try {
        		Gson gson = new Gson();
        		message = gson.fromJson(jsonDescription, MDSMessage.class);
        	} catch (com.google.gson.JsonParseException ex) {
        		log.error("doPost(). Error parsing message");
        		mds = MDSResponse.fail("Error parsing MDSMessage: " + ex);
				throw ex;
        	}
        	if(log.isDebugEnabled())
        		log.debug(String.format("Received (%s) responses from %s",
        					message.questions.length, message.phoneId));
        	
        	// First check if it exists
        	/* 
        	// this does nothing for now
        	String uuid = request.getParameter("caseIdentifier");
        	if(uuid != null){
        		queueItem = Context.getService(QueueItemService.class)
        						.getQueueItemByUuid(uuid);
        		if(log.isDebugEnabled())
        			log.debug("updating queue item: " + uuid);
        	} else {
        		if(log.isDebugEnabled())
        			log.debug("");
        	}
        	*/
        	// get the patient
        	try{
        		patient = getPatient(message.patientId);
        	} catch(PatientIdentifierException ex){	
        		log.error("Error getting patient!" + message.patientId, ex);
        		mds = MDSResponse.fail(ex.getMessage());
				throw ex;
        	}

        	String mid = message.caseIdentifier;
        	// Only need this when debugging
        	if(log.isDebugEnabled()){
        		log.debug("Enounter POST, id: " + mid);
        		for(MDSQuestion q : message.questions)
        			log.debug("doPost: message("+mid+"): question:"+q);
        	}
        	
        	// Validate before we persist anything to the database when creating 
        	// the Encounter or Observations       
        	String pattern = Context.getAdministrationService()
        			.getGlobalProperty(Property.DATE_FORMAT);

    		// Check the date format
        	try {
        		if(log.isDebugEnabled())
        			log.debug("Encounter date POST: "+message.procedureDate);
        		DateFormat df = new SimpleDateFormat(pattern);
        		if(log.isDebugEnabled())
        			log.debug(Context.getDateTimeFormat().toPattern());
        		//TODO correct the following to use an actual date format
        		// i.e. df.parse(message.procedureDate);
        		encounterDateTime = new Date();
        	} catch (Exception ex) {
        		log.error("Date format error");
        		mds = MDSResponse.fail("date format error: " + message.procedureDate);
				throw ex;
        	}

    		// Validate the concepts
        	try{
        		idMap = makeIdToConceptMap(message);
        	} catch (Exception ex) {
        		log.error("Error validating concept: " + ex.getMessage());
        		mds = MDSResponse.fail("Concept error: " + ex.getMessage());
				throw ex;
        	}

        	try{
        		// translate the response into an encounter
        		encounter = makeEncounter(patient, encounterDateTime,
        				message.procedureTitle, Context.getAuthenticatedUser());
        		if(log.isDebugEnabled())
        			log.debug("Created encounter: "+encounter.getEncounterId());
        	} catch(Exception ex){
        		log.error("Error Creating Encounter!");
        		throw new ServletException(ex);
        	}
			try{
				// Create the observations
				observations = makeObsSet(encounter, patient, message, files,
						encounterDateTime, idMap);
				if(log.isDebugEnabled())
					log.debug("Created observations: "+ observations.size());
			} catch(Exception ex){
				log.error("Error generating item observation set!");
				throw new ServletException(ex);
			}

			// This constructs the queue item and saves it
			queueItem = makeQueueItem(encounter, patient, message);
			//queueItem.setCreator(Context.getUserContext().getAuthenticatedUser());
			if(log.isDebugEnabled())
				log.debug("Initialized queue item: "+ queueItem);
			Context.getService(QueueItemService.class).saveQueueItem(queueItem);
			mds = MDSResponse.succeed("Successfully uploaded " + "procedure " + message.caseIdentifier);
			
		// Exception Catch all - purges the encounter if it was created
        } catch (Exception ex){
        	if( encounter != null)
        		try{
        			Context.getEncounterService().purgeEncounter(encounter, true);
        		} catch (APIException e){
        			log.error("Failed purging encounter" + encounter.getUuid());
        		}
			log.error("Error inserting item into queue!",ex);
			// if the message was already set we skip this
			if (mds == null)
				mds = MDSResponse.fail(ex.getMessage());
        // write response and close
        } finally {
        	writeResponse(response, mds);
			// close the session
			Context.getUserContext().logout();
        }
	}
    
    
    /**
     * Returns zero or one patient from the OpenMRS Patient service looked up
     * by the patient identifier
     * 
     * @param patientIdentifier the id to look up
     * @return a patient with a matching identifier as produced by 
     * 			Patient.getIdentifier() or null
     */
    private Patient getPatient(String patientIdentifier) throws PatientIdentifierException{
        Patient patient = null;
    	// Safety check
        if(patientIdentifier == null || "".equals(patientIdentifier)) 
        	throw new PatientIdentifierException("Null patient id");
        
        PatientService patientService = Context.getPatientService();
        List<Patient> patients = patientService.getPatients(null, 
        		patientIdentifier, null, false);
        
        if(patients.size() == 1 ){
            for(Patient p : patients) {
            	if(p.getPatientIdentifier().getIdentifier().equals(patientIdentifier)){
            		patient = p;
            		break;
            	} 
            } 
        } else if (patients.size() > 1) {
            throw new PatientIdentifierException("Multiple patients found with id:  " 
        			+ patientIdentifier +". Audit patients for duplicate id's");
        }
        if (patients.size() == 0)
        	throw new PatientIdentifierException("No patients found with id: " 
        			+ patientIdentifier);
        return patient;
    }
    
    private Patient getPatientByUuid(String uuid) {
        Patient patient = null;
    	// Safety check
        if(uuid.isEmpty()) 
        	throw new APIException("Null patient id");
        
        return Context.getPatientService().getPatientByUuid(uuid);
    }
    
    private Encounter getEncounterByUuid(String uuid) {
    	// Safety check
        if(uuid.isEmpty()) 
        	throw new APIException("Null encounter id");
        return Context.getEncounterService().getEncounterByUuid(uuid);
    }
    
    @Override
    protected void doPut(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {

		
    	
    }
    
    /**
     * Gets any files uploaded with this request.
     * @param request the original request.
     * @return A list of FileItem objects.
     * @throws FileUploadException 
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    private List<FileItem> getUploadedFiles(HttpServletRequest request) throws 
    	APIException 
    {   
    	List<FileItem> files = Collections.emptyList();
    	try{
        	FileItemFactory factory = new DiskFileItemFactory();
        	ServletFileUpload upload = new ServletFileUpload(factory);
        	if(!request.getHeader("content-type").contains(
        		"application/x-www-form-urlencoded"))        
        		files = (List<FileItem>)upload.parseRequest(request);
    	} catch(FileUploadException ex) {
    		throw new APIException("File POST Error",ex);
    	}
    	return files;
    }
    
    private Map<String,List<FileItem>> parseFileObs(MDSMessage message, 
    		List<FileItem> files)
    {
    	Map<String,List<FileItem>> fileMap = new HashMap<String,List<FileItem>>();
    	log.debug("Sana.UploadServlet.parseFileObs(): file count: "
				+ ((files != null)? files.size(): "EMPTY"));
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
        return fileMap;
    }
   
    /**
     * Parses an MDSMessage, validates the Concepts and returns a Map of the
     * question ids to Concept objects
     * 
     * @param message The message to parse
     * @return a mapping of question id's to Concept objects
     */
    private Map<String, Concept> makeIdToConceptMap(MDSMessage message)
    {
    	Map<String,Concept> idMap = new HashMap<String,Concept>();
    	// validate valid Concepts and map to id
        for(MDSQuestion q : message.questions) {
            // Get Concept for Type/ID
            Concept c = getOrCreateConcept(q.type, q.concept, q.question);
            idMap.put(q.id, c);
        }
    	return idMap;
    }
    
    /**
     * Creates an observation set for an associated encounter from the data
     * attached to a POST call as a list of files and MDS message. The concepts
     * must be validated and passed as a map from the id to Concept.
     * 
     * @return
     * @throws IOException
     * @throws ParseException 
     */
    private Set<Obs> makeObsSet(Encounter encounter, Patient patient, MDSMessage message, 
    		List<FileItem> files, Date date, Map<String, Concept> idMap) 
    		throws IOException, ParseException
    {
    	log.debug("Entering");
    	Set<Obs> observations = new HashSet<Obs>();
    	Set<Obs> errors = new HashSet<Obs>();
        Map<String,List<FileItem>> fileMap = parseFileObs(message, files);
        
    	// Create the observations
        for(MDSQuestion q : message.questions) {
        	// Skip all null answers.
        	if((q.answer == null) || (q.answer.compareTo("") == 0))
        		continue;
        	Obs obs = null;
            Concept c = idMap.get(q.id);
            
            // 1.x versions allow multiple files per question so we need to
            // make one observation per file
            if(c.isComplex()) {
            	log.debug("Concept is complex checking for file.");
                // If no file was uploaded, then there is no obs to make.
                if(fileMap.containsKey(q.id)) {
                    // Make one obs per file
                    for(FileItem f : fileMap.get(q.id)) {
                    	obs = makeObs(encounter,patient,date,c, q, f);
                    }
                } else {
                	// No observation if no files
                	obs = null;
                }
            } else {
                // Not complex, make a regular obs 
            	obs = makeObs(encounter, patient,date,c, q, null);
            }
            if (obs != null){
            	log.debug("makeObsSet():"
        			+ "Eid: " + q.id + ", Concept: " + q.concept 
        			+ ", Obs created. Complex = " + obs.isComplex());
            	observations.add(obs);
            } else
        		log.warn("makeObsSet(): Null Obs for response: " + q.id);
        }
        return observations;
           
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
    private Encounter makeEncounter(Patient patient, Date procedureDate,
    		String formName, User user) 
    		throws IOException, PatientIdentifierException
    {
        // Now we assemble the encounter
    	log.debug("entering makeEncounter()");
        Encounter e = new Encounter();
        e.setPatient(patient);
        
        e.setEncounterDatetime(procedureDate);
        e.setDateCreated(procedureDate);
        e.setCreator(user);
        
        Location location = Context.getLocationService().getDefaultLocation();
        e.setLocation(location);
        
        // TODO Check that the intial provider has CHW role or add it
        //Provider provider = getProvider(user);
        //e.setProvider(provider);
        
        // TODO(XXX) Replace these, catch exceptions, etc.
        // Fetches the form from the procedureTitle field or creates a duplicate
        // of the Basic Form
        log.debug("Using form: " + formName);
        Form form = getForm(formName);
        e.setForm(form);
        
        // TODO Set a Global Property for Default
        EncounterType eType = null;
        eType = Context.getEncounterService().getEncounterType(formName);
        if(eType == null){
             // The description for the new EncounterType 
             // will need to be filled in manually
             String description = "TODO";
             eType =  new EncounterType(formName, description);
             Context.getEncounterService().saveEncounterType(eType);
        }
        e.setEncounterType(eType);
        //e.setEncounterType(Context.getEncounterService().getAllEncounterTypes()
        //		.get(0));
        
        Context.getEncounterService().saveEncounter(e);
        Integer encounterId = e.getId();
        Context.evictFromSession(e);
    	log.debug("exiting makeEncounter(): Created: " + e);
    	e = Context.getEncounterService().getEncounter(encounterId);
    	log.debug("Refreshed encounter: " + e);
    	return e;
    }
    
    private Provider getProvider(User user){
    	Provider provider = null;
    	
    	return provider;
    }
    
    /**
     * Gets the form by title or creates a duplicate of the Basic Form
     * @param name
     * @return
     */
    private Form getForm(String name){
    	Form form = null;
    	FormService fs = Context.getFormService();
    	form = fs.getForm(name);
    	if(form == null){
    	    log.warn("Form not found: " + name);
    	    log.warn("Duplicating Basic Form. Update with correct version");
    		Form basicForm = fs.getAllForms().get(0);
    		form = fs.duplicateForm(basicForm);
    		form.setName(name);
    		fs.saveForm(form);
    		log.debug("Created new form: " + form);
    	} else
        	log.debug("Found form: " + form.getName());
    	return form;
    }
    
    private QueueItem makeQueueItem(Encounter e, Patient p, MDSMessage message){
    	QueueItem q = new QueueItem();
    	q.setStatus(QueueItemStatus.NEW);
        q.setPhoneIdentifier(message.phoneId);
        q.setCaseIdentifier(message.caseIdentifier);
        q.setProcedureTitle(message.procedureTitle);
        q.setCreator(Context.getAuthenticatedUser());
        q.setDateUploaded(e.getEncounterDatetime());
        q.setChangedBy(Context.getAuthenticatedUser());
        q.setDateChanged(e.getEncounterDatetime());
        q.setDateCreated(e.getEncounterDatetime());
        q.setPatient(p);
        q.setEncounter(e);
        log.debug(q);
        return q;
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
     * @param name The 'concept' attribute of the Procedure xml element.
     * 		which should map to a 'Name' field for an existing OpenMRS
     * 		Concept.
     * @param question The 'question' attribute of the Procedure xml element
     * 		which should map to a 'description' field for an existing OpenMRS
     * 		Concept.
     * @return A valid OpenMRS Concept or null.
     */
    private Concept getOrCreateConcept(MDSMessage m, String type, 
    		String name, String question) 
    {    
        ConceptService cs = Context.getConceptService();
        Concept c = null;
		// Search for a concept name precisely 
        c = cs.getConcept(name);
        if (c != null){
        	log.debug("Found exact match!" + name);
        	return c;
        } else {
        	throw new NullPointerException("Concept not found:" + name);
        }
        
    }
    
    /** Convenience wrapper */
    private Concept getOrCreateConcept(String type,  String name, String desc){
    	return getOrCreateConcept(null,type,name,desc);
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
        else {
        	o.setValueText(q.type);
        	log.debug("Sana.UploadServlet.makeObs: file:" + f.getName());
            ComplexData cd = new ComplexData(f.getName(), f.getInputStream());
            o.setComplexData(cd);
        }
        return o;
    }
    
    /**
     * Constructs a new Observation. And saves it in the database. 
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
     * @throws ParseException 
     */
    private Obs makeObs(Encounter encounter, Patient p, Date date, Concept c, MDSQuestion q, 
    		FileItem f) throws IOException, ParseException 
    {
        log.debug("data=" + q.toString());
        log.debug("patient=" + p);
        log.debug("concept=" + c.getUuid() + "::" + c.getDisplayString());
        log.debug("date=" + date);
        
        Obs o = new Obs(p, c, date,
        			Context.getLocationService().getDefaultLocation());
        o.setCreator(Context.getAuthenticatedUser());
        o.setEncounter(encounter);
		log.debug("Setting Obs value text: " + q.answer);
		if (q.answer == null || q.answer.isEmpty()){
			log.error("Got an empty answer! ");
			throw new NullPointerException("Null answer: " + q.id);
		}
		
        if(!c.isComplex()){
    		o.setValueAsString(q.answer);
    		log.debug("Set Obs value text: " + o.getValueAsString(Context.getLocale()));
        
        } else {
        	if (f != null) {
        		log.debug("file:" + f.getName());
        		ComplexData cd = new ComplexData(f.getName(), f.getInputStream());
        		o.setComplexData(cd);
        	} else {
        		log.warn("Empty file! " + q.id);
    			throw new NullPointerException("Null file for answer! " + q.id);
        	}
        }
        log.debug("Preparing to saveObs()");
        Context.getObsService().saveObs(o, "");
        log.debug("Obs saved! " + o);
        //TODO We don't seem to need this block in the newer versions.
        //Integer id = o.getObsId();
        //Context.evictFromSession(o);
        //o = Context.getObsService().getObs(id);
        return o;
    }
    
    private Concept createAndGetConcept(String name, String type, String question)
    {
    	Concept c = null;
        // TODO make this constant / static somewhere
        if((name == null && type == null) || question == null){
        	String msg = String.format("Could not create: (%s, %s, %s)", 
					name, type, question);
        	log.error(msg);
			throw new NullPointerException(msg);
        }
        	
    	Map<String,String> typeMap = new HashMap<String,String>();
        typeMap.put("PLUGIN", "Complex");
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
        handlerMap.put("PLUGIN", "MediaFileHandler");

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
			ConceptName lc = new ConceptName(name, locale);
            lc.addTag(preferredTag);
            c.addName(lc);
			// question attribute from procedure xml
            ConceptDescription description = new ConceptDescription(
            		question, locale);
            description.setCreator(Context.getAuthenticatedUser());
            c.addDescription(description);
		}
        Context.getConceptService().saveConcept(c);
        return c;
    }
    
    // for testing 
    @Override
    protected void doGet(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
        MDSResponse mds = MDSResponse.succeed( "Hello World from UploadServlet");
        writeResponse(response, mds);
    }
    
    public void writeResponse(HttpServletResponse response, MDSResponse mds)
    	throws IOException
    {
        PrintWriter out = response.getWriter();
    	out.write(mds.toJSON());
    	out.flush();
    	out.close();
    }
    
}


