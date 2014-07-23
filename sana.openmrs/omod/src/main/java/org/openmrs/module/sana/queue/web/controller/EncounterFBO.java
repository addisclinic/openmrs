package org.openmrs.module.sana.queue.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptSource;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemStatus;

/**
 * An OpenMRS Encounter as viewed through the Sana Queue
 * @author Sana Development Team
 *
 */
public class EncounterFBO {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Constructs a new view of an encounter mapped to the Queue
	 * @param q the item in the queue
	 */
	public EncounterFBO(QueueItem q) {
		this.queueItem = q;
		this.status = q.getStatus().toString();
		this.patientResponses = "";
		this.existingDiagnoses = "";
		this.diagnoses = new ArrayList<Concept>();
		this.doctorName = Context.getAuthenticatedUser().getGivenName() + " " +
						  Context.getAuthenticatedUser().getFamilyName();
		
		//Initialize concept sources
		this.conceptSources = new ArrayList<String>();
		
        //Add default concept source
        conceptSources.add("Default");
        
		List<ConceptSource> sources = Context.getConceptService()
		.getAllConceptSources();
        Iterator<ConceptSource> iteratorSources = sources.iterator();
		
        while(iteratorSources.hasNext()){
        	conceptSources.add(iteratorSources.next().getName());
        }
        
        
		Map<Concept, List<Obs>> om = new HashMap<Concept, List<Obs>>();
		for(Obs o : q.getEncounter().getObs()) {
			List<Obs> ol = null;
			if(om.containsKey(o.getConcept())) {
				ol = om.get(o.getConcept());
			} else {
				ol = new ArrayList<Obs>();
				om.put(o.getConcept(), ol);
			}
			ol.add(o);
		}

        //Initialize patient response with date of initial encounter
        patientResponses = "<b>Date of Encounter:</b><br>" 
        		+ q.getDateCreated().toString().substring(0,10) + "<br><br>";        
		
		String diagnoses = "";
		String urgency = "";
		String assessment = "";
		String recommendations = "";
		String treatment = "";
		String infoRequest = "";
		String conceptName = "";
		String doctorName = "";
		Locale l = Context.getLocale();
		
		for(Obs o : q.getEncounter().getObs()) {
			//append to summary of all patient responses
			if(!o.isComplex())
			{		
				conceptName = o.getConcept().getDisplayString();
				log.debug(conceptName);
				
				//Case is Deferred
				if(q.getStatus().equals(QueueItemStatus.DEFERRED))
				{
					if(conceptName.equals("DOCTOR INFO REQUEST")){
						infoRequest +=  "<br>" + o.getValueAsString(l);
						doctorName = o.getCreator().getGivenName() + " " 
						+ o.getCreator().getFamilyName();	
					}
				}
				//Already has diagnosis
				if(q.getStatus().equals(QueueItemStatus.CLOSED))
				{
					if(conceptName.equals("DOCTOR DIAGNOSIS")){
						String value = o.getValueAsString(l);
						if(value.contains("|"))
							diagnoses += "<br>" +value.substring(0,
									value.indexOf('|')-1).toLowerCase();
						else
							diagnoses += "<br>" + o.getValueAsString(l);
						doctorName = o.getCreator().getGivenName() + " " 
									+ o.getCreator().getFamilyName();
					}
					else if(conceptName.equals("DOCTOR URGENCY LEVEL")){
						urgency +=  "<br>" + o.getValueAsString(l);
						doctorName = o.getCreator().getGivenName() + " " 
									+ o.getCreator().getFamilyName();
					}
					else if(conceptName.equals("DOCTOR TREATMENT RECOMMENDATION")){
						treatment +=  "<br>" + o.getValueAsString(l);
						doctorName = o.getCreator().getGivenName() + " " 
									+ o.getCreator().getFamilyName();	
					}
					else if(conceptName.equals("DOCTOR ASSESSMENT")){
						assessment +=  "<br>" + o.getValueAsString(l);
						doctorName = o.getCreator().getGivenName() + " " 
									+ o.getCreator().getFamilyName();
					}
					else if(conceptName.equals("DOCTOR RECOMMENDATIONS")){
						recommendations +=  "<br>" + o.getValueAsString(l);
						doctorName = o.getCreator().getGivenName() + " " 
									+ o.getCreator().getFamilyName();
					}
					else if(conceptName.equals("DOCTOR INFO REQUEST")){
						infoRequest +=  "<br>" + o.getValueAsString(l);
						doctorName = o.getCreator().getGivenName() + " " 
									+ o.getCreator().getFamilyName();	
					}
				}
				
				//Set patient responses
				//Don't list personal info obs (i.e. birthdate)
				if(!conceptName.equals("PATIENT ID") &&
					!conceptName.equals("FIRST NAME") &&
					!conceptName.equals("LAST NAME") &&
					!conceptName.equals("BIRTHDATE MONTH") &&
					!conceptName.equals("BIRTHDATE DAY") &&
					!conceptName.equals("BIRTHDATE YEAR") &&
					!conceptName.equals("GENDER") &&
					!conceptName.equals("PATIENT ALREADY ENROLLED") &&
					!conceptName.equals("DOCTOR DIAGNOSIS") &&
					!conceptName.equals("DOCTOR TREATMENT RECOMMENDATION") &&
					!conceptName.equals("DOCTOR URGENCY LEVEL") &&
					!conceptName.equals("DOCTOR ASSESSMENT") &&
					!conceptName.equals("DOCTOR RECOMMENDATIONS") &&
					!conceptName.equals("DOCTOR INFO REQUEST")){
					if (o.getValueAsString(l).equals("")){
						patientResponses += "<b>" + o.getConcept()
							.getDescription().getDescription() 
							+ "</b><br>No response given<br><br>";
					}
					else{
						patientResponses += "<b>" + o.getConcept()
							.getDescription().getDescription() 
							+ "</b><br>" + o.getValueAsString(l) + "<br><br>";
					}
				}
			}
		}
		
				
		//Set existing diagnoses
		if(!diagnoses.equals("") || !treatment.equals("") 
				|| !assessment.equals("") || !recommendations.equals("") 
				|| !urgency.equals("") || !infoRequest.equals(""))
		{
			existingDiagnoses = "<b>Date of Specialist Consult: </b><br>" 
				+ q.getDateChanged().toString().substring(0,10) 
				+ "<br><br><b>Specialist:</b><br>" + doctorName;
			
			if(!infoRequest.equals(""))
				existingDiagnoses += "<br><br><b>Request for More Info: </b>" 
					+ infoRequest;
			
			if(!urgency.equals(""))
				existingDiagnoses += "<br><br><b>Urgency Level: </b>" 
					+ urgency;
			
			if(!diagnoses.equals("") || !treatment.equals("") 
					|| !assessment.equals("") || !recommendations.equals(""))
				existingDiagnoses += "<br><br><b>Diagnosis: </b>" + diagnoses + 
								 	 "<br><br><b>Plan: </b>" + treatment + assessment + recommendations;

			log.debug("Existing Diagnoses: " + existingDiagnoses);
		}

		//Set obs map
		this.obsMap = om;
		
	}
	
	public String patientResponses;
	public String status;
	public String doctorName;
	public String existingDiagnoses;
	public List<Concept> diagnoses;
	public QueueItem queueItem;
	public Map<Concept, List<Obs>> obsMap;
	public List<String> conceptSources;
	
	/**
	 * The wrapped QueueItem
	 * @return
	 */
    public QueueItem getQueueItem() {
    	return queueItem;
    }
	
    /**
     * Sets the QueueItem
     * @param queueItem
     */
    public void setQueueItem(QueueItem queueItem) {
    	this.queueItem = queueItem;
    }
    
    /**
     * Sets a list of all concept sources from Observations associated with the 
     * underlying Encounter
     * @param sources
     */
    public void setConceptSources(List<String> sources){
    	conceptSources = sources;
    }
    
    /**
     * Set the reviewing Specialist 
     * @param doctorName
     */
    public void setDoctorName(String doctorName){
    	this.doctorName = doctorName;
    }
    
    /**
     * Sets the status
     * @param status
     */
    public void setStatus(String status){
    	this.status = status;
    }
	
    /**
     * A map of Concept to Observations in the underlying encounter
     * @return
     */
    public Map<Concept, List<Obs>> getObsMap() {
    	return obsMap;
    }

    /**
     * Sets the map of Concept to Observations in the underlying encounter
     * @return
     */
    public void setObsMap(Map<Concept, List<Obs>> obsMap) {
    	this.obsMap = obsMap;
    }
    
    /**
     * The list of all Concepts from Observations in the underlying encounter
     * @return
     */
    public List<String> getConceptSources(){
    	return conceptSources;
    }

    /**
     * The list of all patient responses from Observations in the underlying 
     * encounter
     * @return
     */
    public String getPatientResponses(){
    	return patientResponses;
    }
    
    /**
     * Gets the reviewing Specialist
     * @return
     */
    public String getDoctorName(){
    	return doctorName;
    }
    
    /**
     * A list of all Diagnoses assigned to this encounter
     * @return
     */
    public List<Concept> getDiagnoses(){
    	return diagnoses;
    }
    
    /**
     * Gets the status
     * @return
     */
    public String getStatus(){
    	return status;
    }
    
    /**
     * Gets previously assigned diagnoses
     * @return
     */
    public String getExistingDiagnoses(){
    	return existingDiagnoses;
    }
}
