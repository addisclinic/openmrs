package org.moca.queue.db.hibernate;

import java.util.List;
import org.openmrs.api.context.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.moca.queue.QueueItem;
import org.moca.queue.QueueItemStatus;
import org.moca.queue.db.QueueItemDAO;
import org.moca.queue.DateItems;
import org.openmrs.api.db.DAOException;
import org.openmrs.User;
import org.hibernate.Session;
import org.hibernate.Query;
import java.util.Calendar;
import org.hibernate.criterion.*;

/**
 * Hibernates Queue items
 * 
 * @author Sana Development Team
 *
 */
public class HibernateQueueItemDAO implements QueueItemDAO {
    
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Hibernate session factory
     */
    private SessionFactory sessionFactory;
    int i=0,j=0;
    String actstatus;
    public HibernateQueueItemDAO() { }
    
    /**
     * Set session factory
     * 
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) { 
        this.sessionFactory = sessionFactory;
    }   
    
    public void createQueueItem(QueueItem queueItem) throws DAOException {
        sessionFactory.getCurrentSession().saveOrUpdate(queueItem);
    }

    public QueueItem getQueueItem(Integer queueItemId) throws DAOException {
        return (QueueItem) sessionFactory.getCurrentSession().get(
        		QueueItem.class, queueItemId);
    }

    public void updateQueueItem(QueueItem queueItem) throws DAOException {
        if(queueItem.getQueueItemId() == null) {
            createQueueItem(queueItem);
        } else {
            queueItem = (QueueItem)sessionFactory.getCurrentSession()
            					.merge(queueItem);
            sessionFactory.getCurrentSession().saveOrUpdate(queueItem);
        }
    }
    
    private Criteria allQueueItems() {
    	//System.out.println("on allQueueItems");
        return sessionFactory.getCurrentSession().createCriteria(
        		QueueItem.class);
    }
    
    private Criteria defaultSort(Criteria criteria) {
    	return criteria.addOrder(Order.desc("dateCreated"));
    }
    
    private Criteria dateLastChangedSort(Criteria criteria) {
    	return criteria.addOrder(Order.desc("dateChanged"));
    }
    
    @SuppressWarnings("unchecked")
    public List<QueueItem> getQueueItems() throws DAOException {
        return allQueueItems().list();
    }

    @SuppressWarnings("unchecked")
    public List<QueueItem> getVisibleQueueItemsInOrder() throws DAOException {
    	//System.out.println("on getVisibleQueueItemsinOrder");
        Criteria queueItems = allQueueItems().add(Expression.or(
        		Expression.eq("statusId", QueueItemStatus.NEW.ordinal()), 
        		Expression.eq("statusId", QueueItemStatus.IN_PROGRESS.ordinal())
        		));
        queueItems = defaultSort(queueItems);
        return queueItems.list();
    }

    @SuppressWarnings("unchecked")
    public List<QueueItem> getClosedQueueItemsInOrder() throws DAOException {
        Criteria queueItems = allQueueItems().add(
        		Expression.eq("statusId", QueueItemStatus.CLOSED.ordinal()));
        queueItems = dateLastChangedSort(queueItems);
        return queueItems.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<QueueItem> getDeferredQueueItemsInOrder() throws DAOException {
        Criteria queueItems = allQueueItems().add(
        		Expression.eq("statusId", QueueItemStatus.DEFERRED.ordinal()));
        queueItems = dateLastChangedSort(queueItems);
        return queueItems.list();
     }
    
    @SuppressWarnings("unchecked")
    public List<QueueItem> getProDateRows(String strpro , int days , 
    		String  checkpro, String  checkdate,int iArchieveStatus,
    		int startvalue,int endvalue,int sortvalue) throws DAOException
    {
    	
    	String strProcedure = "%"+strpro+"%";
    	
    	Criteria queueItems = allQueueItems().add(Expression.or(
    			Expression.eq("statusId", QueueItemStatus.NEW.ordinal()),
    			Expression.eq("statusId", QueueItemStatus.IN_PROGRESS.ordinal())
    			));
    	
    	
    	if(strpro !=null && !strpro.equalsIgnoreCase("SHOW ALL") ){  
    		Criterion procedure= Restrictions.like("procedureTitle",
    				strProcedure);//retriving  rows with only selected Procedure string.  
    		queueItems.add(procedure);
    	
    	}
    	if(checkdate !=null && !checkdate.equalsIgnoreCase("SHOW ALL") )
    	{
    		Calendar current=Calendar.getInstance();
            current.get(Calendar.DATE);
            Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            Criterion dateCriterion= Restrictions.between("dateCreated",
            		fromDate.getTime(),current.getTime());//adding another criteria based on Date specified, with from and to Date specification.
            queueItems.add(dateCriterion);
            
    	}
    	
    	if(iArchieveStatus==1)
    	{
    		queueItems.add(Expression.eq("archived",0));//retreving only unarchived rows.
    	}
    	else if(iArchieveStatus==2)
    	{
    		queueItems.add(Expression.eq("archived",1));//retreving only archived rows based of "archived" column being '1'
    	}
    	else if(iArchieveStatus == 3)
    	{
    		queueItems.add(Expression.eq("archived",0));
    		Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
    		Criterion dateCriterion = Restrictions.le("dateCreated",
    				fromDate.getTime());
            queueItems.add(dateCriterion);
    		
    	}
    	//List<QueueItem> liscount = queueItems.list();
    	//System.out.println("total count:"+liscount.size());
    	if(sortvalue == 1)
    	{
    		queueItems.addOrder(Order.asc("dateCreated"));
    	}
    	else
    	{	
    		queueItems.addOrder(Order.desc("dateCreated"));
    	}	
    	queueItems.setFirstResult(startvalue);
    	queueItems.setMaxResults(endvalue);
        List<QueueItem> lis = queueItems.list();//excuting criteria and returning a list of rows(Queueitems).
        return lis;
    }
    
    @SuppressWarnings("unchecked")
    public int getProDateRowsCount(String strpro , int days , String  checkpro,
    		String  checkdate,int iArchieveStatus,int startvalue,int endvalue,
    		int sortvalue) throws DAOException
    {
    	//List<QueueItem> items = queueService.getProDateRows(null,days,null, null, 3,startvalue,endvalue,sortvalue);

    	//List<QueueItem> items = queueService.getProDateRows(null,defcount,"SHOW ALL", "true", 1,queuelistcount);
    	//if(checkpro !=null)
    	//returns a Queueitem list of rows based on criteria. 
    	String strProcedure = "%"+strpro+"%";
    	//System.out.println("getting Days:"+days);
    	
    	//adding a criteria based on  StatuID with NEW and INPROGRESS fields.
    	Criteria queueItems = allQueueItems().add(Expression.or(
    			Expression.eq("statusId", QueueItemStatus.NEW.ordinal()),
    			Expression.eq("statusId", QueueItemStatus.IN_PROGRESS.ordinal())
    			));
    	
    	/*if(checkpro !=null && !checkpro.equalsIgnoreCase("SHOW ALL") ){  
    		//System.out.println(" NOT EQ SHOW ALL"+strProcedure);
    		Criterion procedure= Restrictions.like("procedureTitle",strProcedure);//retriving  rows with only selected Procedure string.  
    		queueItems.add(procedure);*/
    	if(strpro !=null && !strpro.equalsIgnoreCase("SHOW ALL") ){  
    		//System.out.println(" NOT EQ SHOW ALL"+strProcedure);
    		Criterion procedure= Restrictions.like("procedureTitle",
    				strProcedure);//retriving  rows with only selected Procedure string.  
    		queueItems.add(procedure);
    	
    	}
    	if(checkdate !=null && !checkdate.equalsIgnoreCase("SHOW ALL") )
    	{
    		//Calender startDate=Calender.getInstance();
    		//Date date=Calender.DATE
    		//System.out.println("IN CHECKDATE NOT SHOW ALL"+ checkdate);
    		Calendar current=Calendar.getInstance();
            current.get(Calendar.DATE);
            
            Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            //System.out.println("DATE VALUE CHECK"+ days);
            
            //System.out.println("from and current Dates:"+fromDate.getTime()+":"+current.getTime());
    		
            Criterion dateCriterion= Restrictions.between("dateCreated",
            		fromDate.getTime(),current.getTime());//adding another criteria based on Date specified, with from and to Date specification.
            queueItems.add(dateCriterion);
            
    	}
    	
    	//System.out.println("Checking for Archieve Status");
    	if(iArchieveStatus==1)
    	{
    		//System.out.println("Archieve Status is 1");
    		queueItems.add(Expression.eq("archived",0));//retreving only unarchived rows.
    	}
    	else if(iArchieveStatus==2)
    	{
    		//System.out.println("Archieve Status is 0");
    		queueItems.add(Expression.eq("archived",1));//retreving only archived rows based of "archived" column being '1'
    	}
    	else if(iArchieveStatus == 3)
    	{
    		//System.out.println("Archieve Status is 3");
    		queueItems.add(Expression.eq("archived",0));
    		
    		Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            
            //System.out.println("in ProDate Rows | Days beyond : " + fromDate.getTime() );
            
    		Criterion dateCriterion = Restrictions.le("dateCreated",fromDate.getTime());
            queueItems.add(dateCriterion);
    		
    	}
    		
    	List<QueueItem> liscount = queueItems.list();
    	//System.out.println("Total Count:"+liscount.size());
    	return liscount.size();
    }
    
    @SuppressWarnings("unchecked")
    public int getProDateRowsClosedCount(String strpro , int days , 
    		String  checkpro, String  checkdate,int iArchieveStatus,
    		int startvalue,int endvalue,int sortvalue) throws DAOException
    {
    	//List<QueueItem> items = queueService.getProDateRows(null,days,null, null, 3,startvalue,endvalue,sortvalue);

    	//List<QueueItem> items = queueService.getProDateRows(null,defcount,"SHOW ALL", "true", 1,queuelistcount);
    	//if(checkpro !=null)
    	//returns a Queueitem list of rows based on criteria. 
    	String strProcedure = "%"+strpro+"%";
    	//System.out.println("getting Days:"+days);
    	
    	//adding a criteria based on  StatuID with NEW and INPROGRESS fields.
    	Criteria queueItems = allQueueItems().add(Expression.or(
    			Expression.eq("statusId", QueueItemStatus.CLOSED.ordinal()),
    			Expression.eq("statusId", QueueItemStatus.IN_PROGRESS.ordinal())
    			));
    	
    	/*if(checkpro !=null && !checkpro.equalsIgnoreCase("SHOW ALL") ){  
    		//System.out.println(" NOT EQ SHOW ALL"+strProcedure);
    		Criterion procedure= Restrictions.like("procedureTitle",strProcedure);//retriving  rows with only selected Procedure string.  
    		queueItems.add(procedure);*/
    	if(strpro !=null && !strpro.equalsIgnoreCase("SHOW ALL") ){  
    		//System.out.println(" NOT EQ SHOW ALL"+strProcedure);
    		Criterion procedure= Restrictions.like("procedureTitle",
    				strProcedure);//retriving  rows with only selected Procedure string.  
    		queueItems.add(procedure);
    	
    	}
    	if(checkdate !=null && !checkdate.equalsIgnoreCase("SHOW ALL") )
    	{
    		//Calender startDate=Calender.getInstance();
    		//Date date=Calender.DATE
    		//System.out.println("IN CHECKDATE NOT SHOW ALL"+ checkdate);
    		Calendar current=Calendar.getInstance();
            current.get(Calendar.DATE);
            
            Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            //System.out.println("DATE VALUE CHECK"+ days);
            
            //System.out.println("from and current Dates:"+fromDate.getTime()+":"+current.getTime());
    		
            Criterion dateCriterion= Restrictions.between("dateCreated",
            		fromDate.getTime(),current.getTime());//adding another criteria based on Date specified, with from and to Date specification.
            queueItems.add(dateCriterion);
            
    	}
    	
    	//System.out.println("Checking for Archieve Status");
    	if(iArchieveStatus==1)
    	{
    		//System.out.println("Archieve Status is 1");
    		queueItems.add(Expression.eq("archived",0));//retreving only unarchived rows.
    	}
    	else if(iArchieveStatus==2)
    	{
    		//System.out.println("Archieve Status is 0");
    		queueItems.add(Expression.eq("archived",1));//retreving only archived rows based of "archived" column being '1'
    	}
    	else if(iArchieveStatus == 3)
    	{
    		//System.out.println("Archieve Status is 3");
    		queueItems.add(Expression.eq("archived",0));
    		
    		Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            
            //System.out.println("in ProDate Rows | Days beyond : " + fromDate.getTime() );
            
    		Criterion dateCriterion = Restrictions.le("dateCreated",fromDate.getTime());
            queueItems.add(dateCriterion);
    		
    	}
    		
    	List<QueueItem> liscount = queueItems.list();
    	System.out.println("Total Count:"+liscount.size());
    	return liscount.size();
    }
    
    
    @SuppressWarnings("unchecked")
    public int getProDateRowsDeferredCount(String strpro , int days , 
    		String  checkpro, String  checkdate,int iArchieveStatus,
    		int startvalue,int endvalue,int sortvalue) throws DAOException
    {
    	//List<QueueItem> items = queueService.getProDateRows(null,days,null, null, 3,startvalue,endvalue,sortvalue);

    	//List<QueueItem> items = queueService.getProDateRows(null,defcount,"SHOW ALL", "true", 1,queuelistcount);
    	//if(checkpro !=null)
    	//returns a Queueitem list of rows based on criteria. 
    	String strProcedure = "%"+strpro+"%";
    	//System.out.println("getting Days:"+days);
    	
    	//adding a criteria based on  StatuID with NEW and INPROGRESS fields.
    	Criteria queueItems = allQueueItems().add(Expression.or(
    			Expression.eq("statusId", QueueItemStatus.DEFERRED.ordinal()),
    			Expression.eq("statusId", QueueItemStatus.IN_PROGRESS.ordinal())
    			));
    	
    	/*if(checkpro !=null && !checkpro.equalsIgnoreCase("SHOW ALL") ){  
    		//System.out.println(" NOT EQ SHOW ALL"+strProcedure);
    		Criterion procedure= Restrictions.like("procedureTitle",strProcedure);//retriving  rows with only selected Procedure string.  
    		queueItems.add(procedure);*/
    	if(strpro !=null && !strpro.equalsIgnoreCase("SHOW ALL") ){  
    		//System.out.println(" NOT EQ SHOW ALL"+strProcedure);
    		Criterion procedure= Restrictions.like("procedureTitle",
    				strProcedure);//retriving  rows with only selected Procedure string.  
    		queueItems.add(procedure);
    	
    	}
    	if(checkdate !=null && !checkdate.equalsIgnoreCase("SHOW ALL") )
    	{
    		//Calender startDate=Calender.getInstance();
    		//Date date=Calender.DATE
    		//System.out.println("IN CHECKDATE NOT SHOW ALL"+ checkdate);
    		Calendar current=Calendar.getInstance();
            current.get(Calendar.DATE);
            
            Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            //System.out.println("DATE VALUE CHECK"+ days);
            
            //System.out.println("from and current Dates:"+fromDate.getTime()+":"+current.getTime());
    		
            Criterion dateCriterion= Restrictions.between("dateCreated",
            		fromDate.getTime(),current.getTime());//adding another criteria based on Date specified, with from and to Date specification.
            queueItems.add(dateCriterion);
            
    	}
    	
    	//System.out.println("Checking for Archieve Status");
    	if(iArchieveStatus==1)
    	{
    		//System.out.println("Archieve Status is 1");
    		queueItems.add(Expression.eq("archived",0));//retreving only unarchived rows.
    	}
    	else if(iArchieveStatus==2)
    	{
    		//System.out.println("Archieve Status is 0");
    		queueItems.add(Expression.eq("archived",1));//retreving only archived rows based of "archived" column being '1'
    	}
    	else if(iArchieveStatus == 3)
    	{
    		//System.out.println("Archieve Status is 3");
    		queueItems.add(Expression.eq("archived",0));
    		
    		Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            
            //System.out.println("in ProDate Rows | Days beyond : " + fromDate.getTime() );
            
    		Criterion dateCriterion = Restrictions.le("dateCreated",
    				fromDate.getTime());
            queueItems.add(dateCriterion);
    		
    	}
    		
    	List<QueueItem> liscount = queueItems.list();
    	System.out.println("Total Count:"+liscount.size());
    	return liscount.size();
    }
    
    
    
    
    
    @SuppressWarnings("unchecked")
    public List<QueueItem> getProDateRowsClosed(String strpro , int days , 
    		String  checkpro, String  checkdate, int iArchieveStatus,
    		int startvalue,int endvalue,int sortvalue) throws DAOException
    {
    	// same as above function but the filter applied in criteria is on CLOSED Queueitems.
    	String strProcedure = "%"+strpro+"%";
    	
    	Criteria queueItems = allQueueItems().add(Expression.eq("statusId", 
    			QueueItemStatus.CLOSED.ordinal()));
    	
    	if(checkpro!=null && !checkpro.equalsIgnoreCase("SHOW ALL")){    		
    		Criterion procedure= Restrictions.like("procedureTitle",strProcedure);
    		queueItems.add(procedure);
    	}
    	if(checkdate!=null && !checkdate.equalsIgnoreCase("SHOW ALL"))
    	{
    		//Calender startDate=Calender.getInstance();
    		//Date date=Calender.DATE
    		Calendar current=Calendar.getInstance();
            current.get(Calendar.DATE);
            
            Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
    		
            Criterion dateCriterion= Restrictions.between("dateCreated",
            		fromDate.getTime(),current.getTime());
            queueItems.add(dateCriterion);
            
    	}
    	
    	//System.out.println("Checking closed for Archieve Status");
    	if(iArchieveStatus==1)
    	{
    		//System.out.println("Archieve Status is 1");
    		queueItems.add(Expression.eq("archived",0));
    	}
    	else if(iArchieveStatus==2)
    	{
    		//System.out.println("Archieve Status is 0");
    		queueItems.add(Expression.eq("archived",1));
    	}
    	else if(iArchieveStatus == 3)
    	{
    		//System.out.println("Archieve Status is 3");
    		queueItems.add(Expression.eq("archived",0));
    		
    		Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            
            //System.out.println("in ProDate Rows | Days beyond : " + fromDate.getTime() );
            
    		Criterion dateCriterion = Restrictions.le("dateCreated",
    				fromDate.getTime());
            queueItems.add(dateCriterion);
    		
    	}
    	
    	//Criterion ob = allQueueItems().add(Expression.or(Expression.eq("statusId", QueueItemStatus.NEW.ordinal()),Expression.eq("statusId", QueueItemStatus.IN_PROGRESS.ordinal()));
    	
    	//queueItems = allQueueItems().add(Expression.and(queueItems,Restrictions.like("procedureTitle",str)));
    	
       /*queueItems = defaultSort(queueItems);
       return queueItems.list();*/
    	System.out.println("##sort value in DAO:"+sortvalue);
    	if(sortvalue == 1)
    	{
    		System.out.println("ascending");
    	queueItems.addOrder(Order.asc("dateCreated"));
    	}
    	else
    	{	
    		System.out.println("descending");
    		queueItems.addOrder(Order.desc("dateCreated"));
    	}	
    	System.out.println("In DAO :"+ startvalue+":"+endvalue);
    	/*if(startvalue == 1)
    	{
    		startvalue--;
    		System.out.println("start-----");
    	}*/
        System.out.println(startvalue);
    	queueItems.setFirstResult(startvalue);
    	queueItems.setMaxResults(endvalue);
    	
    	//System.out.println("## Size of QueueItem :"+queueItems.list().size());
        List<QueueItem> lis = queueItems.list();//excuting criteria and returning a list of rows(Queueitems).
        System.out.println("size of queue:"+lis.size());	
        return lis;
    		
    }
    
    public List<QueueItem> getProDateRowsDeferred(String strpro , int days , 
    		String  checkpro, String  checkdate, int iArchieveStatus,
    		int startvalue,int endvalue,int sortvalue) throws DAOException
    {
    	
    	//Same as above function with filter applied to on DEFERRED criteria.
    	
    	String strProcedure = "%"+strpro+"%";
    	
    	Criteria queueItems = allQueueItems().add(Expression.eq("statusId", QueueItemStatus.DEFERRED.ordinal()));
    	
    	if(checkpro!=null && !checkpro.equalsIgnoreCase("SHOW ALL")){    		
    		Criterion procedure= Restrictions.like("procedureTitle",strProcedure);
    		queueItems.add(procedure);
    	}
    	if(checkdate!=null && !checkdate.equalsIgnoreCase("SHOW ALL"))
    	{
    		//Calender startDate=Calender.getInstance();
    		//Date date=Calender.DATE
    		Calendar current=Calendar.getInstance();
            current.get(Calendar.DATE);
            
            Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
    		
            Criterion dateCriterion= Restrictions.between("dateCreated",
            		fromDate.getTime(),current.getTime());
            queueItems.add(dateCriterion);
            
    	}
    	//System.out.println("Checking Deferred for Archieve Status");
    	if(iArchieveStatus==1)
    	{
    		//System.out.println("Archieve Status is 1");
    		queueItems.add(Expression.eq("archived",0));
    	}
    	else if(iArchieveStatus==2)
    	{
    		//System.out.println("Archieve Status is 0");
    		queueItems.add(Expression.eq("archived",1));
    	}
    	else if(iArchieveStatus == 3)
    	{
    		//System.out.println("Archieve Status is 3");
    		queueItems.add(Expression.eq("archived",0));

    		Calendar fromDate=Calendar.getInstance();            
            fromDate.add(Calendar.DATE, -days);
            
            //System.out.println("in ProDate Rows | Days beyond : " + fromDate.getTime() );
            
    		Criterion dateCriterion = Restrictions.le("dateCreated",
    				fromDate.getTime());
            queueItems.add(dateCriterion);
    	}
    
    	System.out.println("##sort value in DAO:"+sortvalue);
    	if(sortvalue == 1)
    	{
    		System.out.println("ascending");
    	queueItems.addOrder(Order.asc("dateCreated"));
    	}
    	else
    	{	
    		System.out.println("descending");
    		queueItems.addOrder(Order.desc("dateCreated"));
    	}	
    	System.out.println("In DAO :"+ startvalue+":"+endvalue);
    	/*if(startvalue == 1)
    	{
    		startvalue--;
    		System.out.println("start-----");
    	}*/
        System.out.println(startvalue);
    	queueItems.setFirstResult(startvalue);
    	queueItems.setMaxResults(endvalue);
    	
    	//System.out.println("## Size of QueueItem :"+queueItems.list().size());
        List<QueueItem> lis = queueItems.list();//excuting criteria and returning a list of rows(Queueitems).
        System.out.println("size of queue:"+lis.size());	
        return lis;
    }
    public List getProcedureAllRows(){
		 
    	//returning only a ProcedureTitle distinct values as array of Strings.
    	return this.sessionFactory.getCurrentSession().createQuery(
    			"select distinct procedureTitle  from QueueItem").list();
    	
   	
	}
    public List<DateItems> getDateMonths()
    {
    	//returning all the rows from DateItem table.
    	return this.sessionFactory.getCurrentSession().createQuery(
    			"from DateItems").list();
    }
    
    public List<QueueItem> getArchivedRows()
    {
    	
    	//returning all the "UnArchieved rows" based on '0'
    	Criteria crit = sessionFactory.getCurrentSession().createCriteria(
    			QueueItem.class);
    	crit.add(Expression.eq("archived",0));
    	return crit.list();
    	
    	
    }
    
    public List<QueueItem> getClosedArchivedRows()
    {
    	
    	//same as above function with a criteria added as CLOSED. 
    	Criteria crit = sessionFactory.getCurrentSession().createCriteria(
    			QueueItem.class);
    	crit.add(Expression.or(Expression.eq("statusId", 
    			QueueItemStatus.CLOSED.ordinal()),Expression.eq("archived",0)));
    	return crit.list();
    	
    	
    }
    
    public List<QueueItem> getDeferredArchivedRows()
    {
    	
    	//same as above function with a criteria added as DEFERRED.
    	Criteria crit = sessionFactory.getCurrentSession().createCriteria(
    			QueueItem.class);
    	crit.add(Expression.or(Expression.eq("statusId", 
    			QueueItemStatus.DEFERRED.ordinal()),Expression.eq("archived",0))
    			);
    	return crit.list();
    	
    	
    }
    
    public void getUnArchivedRows(int arr[])
    {
    	//Setting the selected rows with '0' for column Archived.
    	//System.out.println("unarchived row | HibernateDao");
		Session ses=this.sessionFactory.getCurrentSession();
		User strUserName = Context.getAuthenticatedUser();
    	for(int i=0; i<arr.length;i++)
    	{
    		Query qry=ses.createQuery("update QueueItem set archived=:arch, "+
    				"archivedDate=now(),archivedBy=:archUser where id="+arr[i]);
    		qry.setParameter("arch",1);
    		qry.setParameter("archUser",strUserName.getUsername());
    		//qry.setParameter("arhby",Context.getAuthenticatedUser());
    		//System.out.println("Query = update QueueItem set archived=0 where id="+arr[i]);
    		int j=qry.executeUpdate();
    	}
    }
}
