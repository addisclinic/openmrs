package org.openmrs.module.sana.queue.db;

import java.util.List;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.sana.queue.DateItems;
import org.openmrs.module.sana.queue.QueueItem;

/** 
 * Methods which are implemented to hibernate Queue Items
 *  
 * @author Sana Development Team
 */
public interface QueueItemDAO {

    public void saveQueueItem(QueueItem queueItem) throws DAOException;
	
    public void createQueueItem(QueueItem queueItem) throws DAOException;
    
    public QueueItem getQueueItem(Integer queueItemId) throws DAOException;
    
    public void updateQueueItem(QueueItem queueItem) throws DAOException;
    
    public List<QueueItem> getQueueItems() throws DAOException;
    
    public List<QueueItem> getVisibleQueueItemsInOrder() throws DAOException;
    
    public List<QueueItem> getClosedQueueItemsInOrder() throws DAOException;
    
    public List<QueueItem> getDeferredQueueItemsInOrder() throws DAOException;
    
    public List<QueueItem> getProDateRows(String strpro, int days, 
    		String checkpro , String checkdate, int iArchieveState,
    		int startvalue,int endvalue,int sortvalue);
    
    public int getProDateRowsCount(String strpro, int days, String checkpro , 
    		String checkdate, int iArchieveState, int startvalue, int endvalue,
    		int sortvalue);
    
    public int getProDateRowsClosedCount(String strpro, int days, 
    		String checkpro , String checkdate, int iArchieveState,
    		int startvalue, int endvalue, int sortvalue);
    
    public int getProDateRowsDeferredCount(String strpro, int days, 
    		String checkpro , String checkdate, int iArchieveState,
    		int startvalue,int endvalue,int sortvalue);
    
    public List<QueueItem> getProDateRowsClosed(String strpro, int days, 
    		String checkpro , String checkdate, int iArchieveState,
    		int startvalue,int endvalue,int sortvalue);
    
    public List<QueueItem> getProDateRowsDeferred(String strpro, int days, 
    		String checkpro , String checkdate, int iArchieveState,
    		int startvalue,int endvalue,int sortvalue);
    
    public List<QueueItem> getProcedureAllRows(); 
    
    public List<DateItems> getDateMonths();
    
    public List<QueueItem> getArchivedRows();
    
    public List<QueueItem> getClosedArchivedRows();
    
    public List<QueueItem> getDeferredArchivedRows();
    
    public void getUnArchivedRows(int arr[]);

	public QueueItem getQueueItemByUuid(String uuid);

	public void purgeQueueItem(QueueItem queueItem);
	
	public void voidQueueItem(QueueItem queueItem);
}
