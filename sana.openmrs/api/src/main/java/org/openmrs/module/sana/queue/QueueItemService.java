package org.openmrs.module.sana.queue;

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.sana.ModuleConstants.Privilege;
import org.openmrs.module.sana.queue.DateItems;
import org.openmrs.module.sana.queue.db.QueueItemDAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface QueueItemService extends OpenmrsService{
    
    public void setQueueItemDAO(QueueItemDAO dao);
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public QueueItem saveQueueItem(QueueItem queueItem) throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public void createQueueItem(QueueItem queueItem) throws APIException;
    
    @Authorized({Privilege.VIEW_QUEUE})
    @Transactional(readOnly=true)
    public QueueItem getQueueItem(Integer queueItemId) throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public void updateQueueItem(QueueItem queueItem) throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getQueueItems() throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getVisibleQueueItems() throws APIException;

    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getClosedQueueItems() throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getDeferredQueueItems() throws APIException;
     
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getProDateRows(String strpro, int days , 
    		String checkpro, String checkdate, int iArchieveState,
    		int startvalue,int endvalue,int sortvalue) throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public int getProDateRowsCount(String strpro, int days , String checkpro, 
    		String checkdate, int iArchieveState, int startvalue, int endvalue,
    		int sortvalue) throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public int getProDateRowsClosedCount(String strpro, int days , 
    		String checkpro, String checkdate,int iArchieveState,
    		int startvalue,int endvalue,int sortvalue) throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public int getProDateRowsDeferredCount(String strpro, int days , 
    		String checkpro, String checkdate, int iArchieveState, 
    		int startvalue,int endvalue,int sortvalue) throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getProDateRowsClosed(String strpro, int days , 
    		String checkpro, String checkdate , int iArchieveState,
    		int startvalue, int endvalue, int sortvalue) throws APIException;
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getProDateRowsDeferred(String strpro, int days , 
    		String checkpro, String checkdate,int iArchieveState,
    		int startvalue, int endvalue,int sortvalue) throws APIException;

    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getProcedureAllRows(); 
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<DateItems> getDateMonths();
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getArchivedRows();
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getClosedArchivedRows();
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public List<QueueItem> getDeferredArchivedRows();
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public void getUnArchivedRows(int arr[]);

    @Authorized({Privilege.MANAGE_QUEUE})
    public QueueItem getQueueItemByUuid(String uuid);
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public void purgeQueueItem(QueueItem queueItem);
    
    @Authorized({Privilege.MANAGE_QUEUE})
    public void voidQueueItem(QueueItem queueItem);

}
