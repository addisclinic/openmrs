package org.openmrs.module.sana.queue;

import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Person;

public class EncounterQueueItem extends BaseOpenmrsData implements java.io.Serializable{
	
	
	private Integer queueItemId;
    private QueueItemStatus status;
    private Encounter encounter;
    private int archived;
    private Person archivedBy;
    
	
	public Integer getId() {
		return queueItemId;
	}
	public void setId(Integer arg0) {
		queueItemId = arg0;
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
    public void setArchivedBy(Person archivedBy)
    {
    	this.archivedBy=archivedBy;
    }
    
    /**
     * Gets a string representing who archived this item
     * @return the responible archiver
     */
    public Person getArchivedBy()
    {
    	return archivedBy;
    }
    
    
}
