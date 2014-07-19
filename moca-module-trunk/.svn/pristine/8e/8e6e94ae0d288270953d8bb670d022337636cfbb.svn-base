package org.moca.queue;

import java.util.List;

import org.moca.queue.db.QueueItemDAO;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.springframework.transaction.annotation.Transactional;
import org.moca.queue.DateItems;

@Transactional
public interface QueueItemService {
    
    public void setQueueItemDAO(QueueItemDAO dao);
    
    @Authorized({"Manage Moca Queue"})
    public void createQueueItem(QueueItem queueItem) throws APIException;
    
    @Authorized({"View Moca Queue"})
    @Transactional(readOnly=true)
    public QueueItem getQueueItem(Integer queueItemId) throws APIException;
    
    @Authorized({"Manage Moca Queue"})
    public void updateQueueItem(QueueItem queueItem) throws APIException;
    
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getQueueItems() throws APIException;
    
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getVisibleQueueItems() throws APIException;

    @Authorized({"View Moca Queue"})
    public List<QueueItem> getClosedQueueItems() throws APIException;
    
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getDeferredQueueItems() throws APIException;
     
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getProDateRows(String strpro, int days , String checkpro, String checkdate,int iArchieveState,int startvalue,int endvalue,int sortvalue) throws APIException;
    
    @Authorized({"View Moca Queue"})
    public int getProDateRowsCount(String strpro, int days , String checkpro, String checkdate,int iArchieveState,int startvalue,int endvalue,int sortvalue) throws APIException;
    
    @Authorized({"View Moca Queue"})
    public int getProDateRowsClosedCount(String strpro, int days , String checkpro, String checkdate,int iArchieveState,int startvalue,int endvalue,int sortvalue) throws APIException;
    @Authorized({"View Moca Queue"})
    public int getProDateRowsDeferredCount(String strpro, int days , String checkpro, String checkdate,int iArchieveState,int startvalue,int endvalue,int sortvalue) throws APIException;
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getProDateRowsClosed(String strpro, int days , String checkpro, String checkdate ,int iArchieveState,int startvalue,int endvalue,int sortvalue) throws APIException;
    
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getProDateRowsDeferred(String strpro, int days , String checkpro, String checkdate,int iArchieveState,int startvalue,int endvalue,int sortvalue) throws APIException;

    @Authorized({"View Moca Queue"})
    public List getProcedureAllRows(); 
    
    @Authorized({"View Moca Queue"})
    public List<DateItems> getDateMonths();
    
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getArchivedRows();
    
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getClosedArchivedRows();
    
    @Authorized({"View Moca Queue"})
    public List<QueueItem> getDeferredArchivedRows();
    
    
    @Authorized({"View Moca Queue"})
    public void getUnArchivedRows(int arr[]);
    

}
