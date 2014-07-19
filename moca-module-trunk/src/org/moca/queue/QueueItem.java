package org.moca.queue;

import java.io.Serializable;
import java.util.Date;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.User;

/**
 * An item in the Sana queue
 * 
 * @author Sana Development Team
 *
 */
public class QueueItem implements Serializable {

    private static final long serialVersionUID = -3498039529037358695L;

    public Integer queueItemId;
    public QueueItemStatus status;
    public Date dateUploaded;
    public Patient patient;
    public Integer patientId;
    public Encounter encounter;
    public Integer encounterId;
    public String phoneIdentifier;
    public String procedureTitle;
    public String caseIdentifier;
    //Below three data-members used for archiving selected queueitem.
    public int archived;
    public Date archivedDate;
    public String archivedBy;
    
    public User creator;
	public Date dateCreated;
	public User changedBy;
	public Date dateChanged;
    
	/**
	 * A new empty queue item with status set to NEW
	 */
    public QueueItem() {
    	setStatus(QueueItemStatus.NEW);
    }
    
    /**
     * Instantiates a new queue item with a specified patient, encounter, and
     * date.
     * @param patient The item Patient
     * @param e The item Encounter 
     * @param d The item Date
     */
    public QueueItem(Patient patient, Encounter e, Date d) {
        setPatient(patient);
        setEncounter(e);
        setDateUploaded(d);
        setStatus(QueueItemStatus.NEW);
    }
    
    /**
     * Gets the item id
     * 
     * @return the item id
     */
    public Integer getQueueItemId() {
        return queueItemId;
    }
    
    /**
     * Sets the item id
     * 
     * @param queueItemId the new item id
     */
    public void setQueueItemId(Integer queueItemId) {
        this.queueItemId = queueItemId;
    }

    /**
     * Gets the visibility status of this item
     * @return true if the item status equals CLOSED
     */
    public Boolean getVisible() {
        return status.equals(QueueItemStatus.CLOSED);
    }
    
    /**
     * Sets the visibility of this item. If false, the status will be set to 
     * CLOSED
     * 
     * @param visible the new visibility
     */
    public void setVisible(Boolean visible) {
    	status = QueueItemStatus.CLOSED;
    }
    
    /**
     * Gets the status of this item
     * 
     * @return the item status
     */
    public QueueItemStatus getStatus() {
    	return status;
    }
    
    /**
     * Gets the status id value
     * @return
     */
    public Integer getStatusId() {
    	return status.ordinal();
    }
    
    /**
     * Sets the queue item status from an integer id value
     * @param id the ordinal value of the new status
     */
    public void setStatusId(Integer id) {
    	for (QueueItemStatus q : QueueItemStatus.values()) {
    		if(q.ordinal() == id) 
    			status = q;
    	}
    }
    
    /**
     * Sets the status of this ite,
     * @param status the new status
     */
    public void setStatus(QueueItemStatus status) {
        this.status = status;
    }
    
    /**
     * The Patient associated with this queue item 
     * @return
     */
    public Patient getPatient() {
        return patient;
    }
    
    /**
     * Sets the item Patient
     * @param patient the new Patient
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
        this.patientId = patient.getPatientId();
    }
    
    /**
     * Gets the item Patient id 
     * @return a Patient identifier
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * Sets the item Patient identifier
     * @param patientId the new patient id
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * Gets the date the item was uploaded
     * @return the uploaded date
     */
    public Date getDateUploaded() {
        return dateUploaded;
    }
    
    /**
     * Sets the uploaded date
     * @param dateUploaded the new uploaded date
     */
    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    /**
     * Gets the item Encounter
     * @return an Encounter
     */
    public Encounter getEncounter() {
        return encounter;
    }
    
    /**
     * Sets the item Encounter
     * @param encounter the new Encounter
     */
    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
        this.encounterId = encounter.getEncounterId();
    }
    
    /**
     * Gets the id of the item Encounter
     * @return the Encounter id
     */
    public Integer getEncounterId() {
        return encounterId;
    }

    /**
     * Sets the Encounter id
     * @param encounterId the new encounter id
     */
    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    /**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
    public Integer getId() {
    	return getQueueItemId();
    }

    /**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		setQueueItemId(id);
    }

	/**
	 * Gets the user who created this item 
	 * @return the item creator
	 */
    public User getCreator() {
    	return creator;
    }

	/**
	 * Sets the user who created this item
	 * @param creator the new User
	 */
    public void setCreator(User creator) {
    	this.creator = creator;
    }

	/**
	 * Gets the date this item was created
	 * @return the date created
	 */
    public Date getDateCreated() {
    	return dateCreated;
    }

	/** 
	 * Sets the date this item was created
	 * 
	 * @param dateCreated the new creation Date
	 */
    public void setDateCreated(Date dateCreated) {
    	this.dateCreated = dateCreated;
    }
    
    /**
     * Gets the client phone id
     * @return the item phone identifier
     */
    public String getPhoneIdentifier() {
    	return phoneIdentifier;
    }
    
    /**
     * Sets the client phone id
     * @param s the new phone identifier
     */
    public void setPhoneIdentifier(String s) {
    	this.phoneIdentifier = s;
    }
    
    /**
     * Sets the procedure title
     * @param s the new procedure title
     */
    public void setProcedureTitle(String s) {
    	this.procedureTitle = s;
    }
    
    /**
     * Gets the item procedure title
     * @return the item procedure title
     */
    public String getProcedureTitle() {
    	return procedureTitle;
    }
    
    /**
     * Sets the case identifier.
     * @param s th ecase identifier
     */
    public void setCaseIdentifier(String s) {
    	this.caseIdentifier = s;
    }
    
    /**
     * Gets the case identifier
     * @return the item case identifier
     */
    public String getCaseIdentifier() {
    	return caseIdentifier;
    }
	
    /**
     * Gets the USer who last changed this item
     * @return the last user to change this item
     */
    public User getChangedBy() {
    	return changedBy;
    }

    /**
     * Sets the last user to change this item
     * @param changedBy the new User who last changed this item
     */
    public void setChangedBy(User changedBy) {
    	this.changedBy = changedBy;
    }

    /**
     * 
     * @return
     */
    public Date getDateChanged() {
    	return dateChanged;
    }

    /**
     * Sets the last date this was changed
     * @param dateChanged the new change date
     */
    public void setDateChanged(Date dateChanged) {
    	this.dateChanged = dateChanged;
    }
    
    /**
     * Sets the archived status
     * @param archived the new archived status
     */
    public void setArchived(int archived)
    {
    	this.archived=archived;
    }
    
    /**
     * Gets the archived status
     * @return the items archived status
     */
    public int getArchived()
    {
    	return archived;
    }
    
    /**
     * Sets who archived this item
     * @param archivedBy an identifier for who archived this item
     */
    public void setArchivedBy(String archivedBy)
    {
    	this.archivedBy=archivedBy;
    }
    
    /**
     * Gets a string representing who archived this item
     * @return the responible archiver
     */
    public String getArchivedBy()
    {
    	return archivedBy;
    }
    
    /**
     * Sets the date this item was archived
     * @param archivedDate the new archival date
     */
    public void setArchivedDate(Date archivedDate)
    {
    	 this.archivedDate=archivedDate;
    }
    
    /**
     * Gets the archival date
     * @return the date this item was archived
     */
    public Date getArchivedDate()
    {
    	return archivedDate;
    }
}
