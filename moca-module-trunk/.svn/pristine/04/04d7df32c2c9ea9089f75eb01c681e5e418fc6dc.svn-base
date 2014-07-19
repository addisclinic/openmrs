package org.moca.queue.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moca.queue.QueueItem;
import org.moca.queue.QueueItemService;
import org.moca.queue.db.QueueItemDAO;
import org.openmrs.api.APIException;
import org.moca.queue.DateItems;

/**
 * Implementation of QUeueItemService
 * 
 * @author Sana Development Team
 *
 */
public class QueueItemServiceImpl implements QueueItemService {

    private Log log = LogFactory.getLog(this.getClass());
    
    private QueueItemDAO dao;
    
    public QueueItemServiceImpl() { }
    
    private QueueItemDAO getQueueItemDAO() {
        return dao;
    }
    
    public void setQueueItemDAO(QueueItemDAO dao) {
        this.dao = dao;
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
}
