package org.moca.queue.web;

/**
 * A message formatted for sending information about an Encounter
 * 
 * @author Sana Development Team
 *
 */
public class MDSMessage {
	/** The phone number of the device on which data was collected */
    public String phoneId;
    /** The date of the encounter */
    public String procedureDate;
    /** The title of the procedure that was used to collect encounter data */
    public String procedureTitle;
    /** The encounter subject identifier */
    public String patientId;
    /** A client assigned unique identifier */
    public String caseIdentifier;
    /** The observation data */
    public MDSQuestion[] questions;
    
    /** A new MDSMessage with no data */
    MDSMessage() {}
    
    @Override
    public String toString(){
    	return String.format("client: \"%s\", date: \"%s\", id: \"%s\", ", 
    			phoneId, procedureDate, caseIdentifier);
    }
}
