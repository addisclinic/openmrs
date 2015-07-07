package org.openmrs.module.sana.queue.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.sana.ModuleConstants;
import org.openmrs.module.sana.ModuleConstants.Privilege;
import org.openmrs.module.sana.queue.DateItems;
import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemService;
import org.openmrs.module.sana.queue.db.QueueItemDAO;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;

/**
 * Implementation of QUeueItemService
 * 
 * @author Sana Development Team
 *
 */
public class QueueItemServiceImpl extends BaseOpenmrsService implements 
	QueueItemService 
{

    private Log log = LogFactory.getLog(this.getClass());
    
    private QueueItemDAO dao;
    
    public QueueItemServiceImpl() { }
    
    private QueueItemDAO getQueueItemDAO() {
        return dao;
    }
    
    public void setQueueItemDAO(QueueItemDAO dao) {
        this.dao = dao;
    }
    
    public QueueItem saveQueueItem(QueueItem queueItem){
    	log.debug("saveQueueItem(). Entering");
    	boolean isNew = false;
		Date newDate = queueItem.getEncounter().getEncounterDatetime();
    	log.debug("Encounter date: " + newDate);
		Date originalDate = null;
    	
    	if (queueItem.getQueueItemId() == null) {
			isNew = true;
	    	log.debug("Got a  new queue item");
		}
    	
    	// Not new we update some of the Encounter info
    	if (!isNew) {
    		log.info("Updating previously queued encounter!");
    		Encounter encounter = queueItem.getEncounter();
    		Patient p = encounter.getPatient();
    		originalDate = encounter.getEncounterDatetime();
    		if (OpenmrsUtil.compare(originalDate, newDate) != 0) {
				
				// if the obs datetime is the same as the 
				// original encounter datetime, fix it
				if (OpenmrsUtil.compare(queueItem.getDateCreated(), 
						originalDate) == 0) {
					encounter.setEncounterDatetime(newDate);
				}
			}
    		
    		// if the Person in the encounter doesn't match the Patient in the , 
    		// fix it
			if (!encounter.getPatient().getPersonId().equals(p.getPatientId())){
				encounter.setPatient(p);
			}
			
			for (Obs obs : encounter.getAllObs(true)) {
				// if the date was changed
				if (OpenmrsUtil.compare(originalDate, newDate) != 0) {
					
					// if the obs datetime is the same as the 
					// original encounter datetime, fix it
					if (OpenmrsUtil.compare(obs.getObsDatetime(), originalDate) == 0) 
					{
						obs.setObsDatetime(newDate);
					}
				}
				
				// if the Person in the obs doesn't match the Patient in the 
				// encounter, fix it
				if (!obs.getPerson().getPersonId().equals(p.getPatientId())) {
					obs.setPerson(p);
				}
				
			}
    	}
    	log.debug("Saving queu item.");
    	dao.saveQueueItem(queueItem);
    	return queueItem;
    }
    
    public void createQueueItem(QueueItem queueItem) throws APIException {
        getQueueItemDAO().createQueueItem(queueItem);
    }

    public QueueItem getQueueItem(Integer queueItemId) throws APIException {
        return getQueueItemDAO().getQueueItem(queueItemId);
    }

    public List<QueueItem> getQueueItems() throws APIException {
        return getQueueItemDAO().getQueueItems();
    }

    public List<QueueItem> getVisibleQueueItems() throws APIException {
        return getQueueItemDAO().getVisibleQueueItemsInOrder();
    }
    
    public List<QueueItem> getClosedQueueItems() throws APIException {
        return getQueueItemDAO().getClosedQueueItemsInOrder();
    }
    
    public List<QueueItem> getDeferredQueueItems() throws APIException {
        return getQueueItemDAO().getDeferredQueueItemsInOrder();
    }

    public void updateQueueItem(QueueItem queueItem) throws APIException {
        getQueueItemDAO().updateQueueItem(queueItem);
    }
    
    public List<QueueItem> getProDateRows(String strpro , int days , 
    		String checkpro, String checkdate,int iArchieveState,
    		int startvalue,int endvalue,int sortvalue) throws APIException
    {
    	 return getQueueItemDAO().getProDateRows(strpro, days , checkpro , 
    			 checkdate, iArchieveState, startvalue, endvalue,sortvalue);
    }
    
    public int getProDateRowsCount(String strpro , int days , String checkpro, 
    		String checkdate,int iArchieveState,int startvalue,int endvalue,
    		int sortvalue) throws APIException
    {
    	 return getQueueItemDAO().getProDateRowsCount(strpro, days , checkpro , 
    			 checkdate, iArchieveState, startvalue, endvalue,sortvalue);
    }
    
    public int getProDateRowsClosedCount(String strpro , int days , 
    		String checkpro, String checkdate,int iArchieveState,
    		int startvalue,int endvalue,int sortvalue) throws APIException
    {
    	 return getQueueItemDAO().getProDateRowsClosedCount(strpro, days , 
    			 checkpro , checkdate, iArchieveState, startvalue, endvalue,
    			 sortvalue);
    }
    
    public int getProDateRowsDeferredCount(String strpro , int days , 
    		String checkpro, String checkdate,int iArchieveState,
    		int startvalue,int endvalue,int sortvalue) throws APIException
    {
    	 return getQueueItemDAO().getProDateRowsDeferredCount(strpro, days , 
    			 checkpro , checkdate, iArchieveState, startvalue, endvalue,
    			 sortvalue);
    }
    
    public List<QueueItem> getProDateRowsClosed(String strpro , int days , 
    		String checkpro, String checkdate, int iArchieveState,
    		int startvalue,int endvalue,int sortvalue) throws APIException
    {
    	 return getQueueItemDAO().getProDateRowsClosed(strpro, days , checkpro,
    			 checkdate,iArchieveState,startvalue, endvalue,sortvalue);
    }
    
    public List<QueueItem> getProDateRowsDeferred(String strpro , int days , 
    		String checkpro, String checkdate, int iArchieveState,
    		int startvalue,int endvalue,int sortvalue) throws APIException
    {
    	 return getQueueItemDAO().getProDateRowsDeferred(strpro, days, checkpro,
    			 checkdate,iArchieveState,startvalue, endvalue,sortvalue);
    }
    
    public List<QueueItem> getProcedureAllRows()
    {
    	return getQueueItemDAO().getProcedureAllRows();
    }
    
    public List<DateItems> getDateMonths()
    {
    	return getQueueItemDAO().getDateMonths();
    }
    
    public List<QueueItem> getArchivedRows()
    {
    	return getQueueItemDAO().getArchivedRows();
    }
    
    public List<QueueItem> getClosedArchivedRows()
    {
    	return getQueueItemDAO().getClosedArchivedRows();
    }
    
    public List<QueueItem> getDeferredArchivedRows()
    {
    	return getQueueItemDAO().getDeferredArchivedRows();
    }
    
    public void getUnArchivedRows(int arr[])
    {
    	 getQueueItemDAO().getUnArchivedRows(arr);
    }

	@Authorized(Privilege.MANAGE_QUEUE)
	public QueueItem getQueueItemByUuid(String uuid) {
		return getQueueItemDAO().getQueueItemByUuid(uuid);
	}

	@Authorized(Privilege.MANAGE_QUEUE)
	public void purgeQueueItem(QueueItem queueItem) {
		getQueueItemDAO().purgeQueueItem(queueItem);
		
	}

	@Authorized(Privilege.MANAGE_QUEUE)
	public void voidQueueItem(QueueItem queueItem) {
		getQueueItemDAO().voidQueueItem(queueItem);
	}
}
