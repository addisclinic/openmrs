package org.moca.queue.web.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.print.DocFlavor.STRING;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moca.queue.QueueItem;
//import org.moca.queue.QueueItemXml;
import org.moca.queue.QueueItemService;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.moca.queue.*;

/**
 * This controller handles the logic which backs the /web/module/queue.jsp page
 * and is tied to that jsp page in the /metadata/moduleApplicationContext.xml 
 * file
 *
 * @author Sana Development Team
 *
 */
public class QueueController extends ParameterizableViewController {

    protected final Log log = LogFactory.getLog(getClass());
    public int totalhits = 0;
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, 
    		HttpServletResponse response) 
    {
    	
    	System.out.println("totalhits:"+(++totalhits));
    	HttpSession httpsession=request.getSession();
    	//System.out.println("checkDate:"+request.getParameter("comboDate"));
    	int defcount = Integer.parseInt(Context.getAdministrationService()
    			.getGlobalProperty("moca.DefaultDisplayCount"));
    	String formpage="/module/moca/queue";
    	 
    	//------------------------------------------------------------
    	//Set max queue size and sort order Lifo or fifo
    	int queuelistcount = Integer.parseInt(Context.getAdministrationService()
    			.getGlobalProperty("moca.maxQueueItemListSize"));
    	Integer queuelistcountObj = new Integer(queuelistcount);
    	String queueLimit = request.getParameter("queueLimit");
    	int sortvalue=0;
    	String sortstring = request.getParameter("hidsortname");
    	//System.out.println("##Sorting Values: "+sortstring+"sortvalue:"+sortvalue);
    	if(sortstring != null && sortstring != "")
    	{
    		//System.out.println("## in if :"+sortvalue);
    		sortvalue = Integer.parseInt(sortstring);
    		
    	}
    	else
    	{
    		sortvalue = 1;
    		//System.out.println("in else:");
    	}	
    	String checkProo = "SHOW ALL";
    	int iArchieveState = 1;
    	

    	/*if(request.getParameter("queuelimitname") != null && request.getParameter("queuelimitname") != "")
    	{
    		if(request.getParameter("queueLimit")!=null)
    		{
    			System.out.println("queuelistcount :"+queueLimit);
    			queuelistcount = Integer.parseInt(queueLimit);
    		    
    		}
    	}	*/
    	int endvalue = queuelistcount;
    	//System.out.println("##maxQueueItemListSize " +queuelistcount);
    	if( request.getParameter("queuelimitname") != null &&  
    			request.getParameter("queuelimitname") != "")
    		endvalue = Integer.parseInt( request.getParameter("queuelimitname"));
    	int startvalue = 0; 
    	
    	//System.out.println("gotopagename :"+request.getParameter("gotopagename"));
    	if(request.getParameter("gotopagename") != "" && 
    			request.getParameter("gotopagename") != null )
    	{   
    		int substract = Integer.parseInt(request.getParameter("gotopagename"));
    		if( request.getParameter("queuelimitname") != null &&  
    				request.getParameter("queuelimitname") != "")
    		{
        		endvalue = Integer.parseInt( 
        				request.getParameter("queuelimitname"));
    		    startvalue = Integer.parseInt(
    		    		request.getParameter("gotopagename"))*endvalue;
    		    //System.out.println("start and end:"+startvalue+":"+endvalue+":"+substract);
    		    startvalue =startvalue-endvalue-(substract-1);
    		   // System.out.println("start value1:"+startvalue);
    		}
    		else
    		{
    			
    		    startvalue = Integer.parseInt(
    		    		request.getParameter("gotopagename"))*queuelistcount;
    		    //System.out.println("start and end:"+startvalue+":"+endvalue+":"+substract);
    		    startvalue =startvalue-endvalue-(substract-1);
    			//System.out.println("start value2:"+startvalue);
    		}
    	}	
    	else
	    	if( request.getParameter("hidprevname") != null &&  
	    			request.getParameter("hidprevname") != "")
	    	{  
	    		//System.out.println("in else :");
	    	
	    		startvalue = Integer.parseInt(request.getParameter("hidprevname"));
	    		//System.out.println("in else :"+startvalue);
	    	}	
    	//System.out.println("startvalue:"+startvalue+"endvalue:"+endvalue);
    	
    	
    		//System.out.println(" IN ELSE COMPLETE ");
		QueueItemService queueService = Context.getService(QueueItemService.class);
    	//List<QueueItem> items = queueService.getVisibleQueueItems();//.getDeferredQueueItems();
		
		StringBuffer sbr = null;
		if(request.getParameter("subarchivename")!=null) //checking archived button is submited or not.
    	{
    		
			//QueueItemService queueService = Context.getService(QueueItemService.class);
			
			//System.out.println("On handleRequestInternal : split | subarchivename");
			String checkedids = request.getParameter("chklist");// chklist is Hidden Text field, contains checked ids from Moca_queue (appended all IDs). 
			//System.out.println("Request Parameter : " + checkedids);
	    	if(checkedids != null && checkedids!="")
	    	{
	        	String strcheckedlist = checkedids;
	        	//System.out.println("IN STRCHECKEDLIST | " + strcheckedlist);
	        	String[] temp;
	        	temp =strcheckedlist.split(";");//spliting the checked ids string into array[] values.
	        	
	        	//System.out.println("ONSPLIT"+temp);
	        	int intarr[] = new int[temp.length];
	        	for(int i=0;i<temp.length;i++)
	        	{	
	        		//System.out.println(temp[i]+" ");
	        		intarr[i]=Integer.parseInt(temp[i]);
	        	}
	        	queueService.getUnArchivedRows(intarr);//function for "Archiving" intarr[] values.
	    	}
    	}	
		
		
		User strUserName = Context.getAuthenticatedUser();
		//------------------------------------------------------------------------------------------------
		//System.out.println("## in else :");
		String strprocedure = request.getParameter("proname");
        String  checkPro = request.getParameter("comboPro");
        String  checkDate = request.getParameter("comboDate");
        String strArchieve = request.getParameter("optionarchive");
        //System.out.println("ON FILTER | COMBOPRO " +checkPro+" "+checkDate);
        //System.out.println("## All Values :"+strprocedure+" :"+checkPro+" :"+checkDate+": "+strArchieve);
        
    	//System.out.println("strarchive: Value =" + strArchieve);
    	if(strArchieve != null)
    	{
    	iArchieveState = 1;
    	if(strArchieve.equalsIgnoreCase("Show ALL"))
    			iArchieveState = 0;
    	else if(strArchieve.equalsIgnoreCase("Show InActive"))
    		iArchieveState = 2;
    	else if(strArchieve.equalsIgnoreCase("Show Active"))
    		iArchieveState = 1;
    	}
       // System.out.println("##iArchieveState :"+iArchieveState);
    	int strdate = 365;
        
        int strmonth = 0;
        //System.out.println("Near Daysname ");
        //System.out.println("## daysname :"+request.getParameter("daysname"));
        try{
        if(request.getParameter("daysname") != null && 
        		request.getParameter("daysname") != "")
        {
        	
        	String strdaysname = request.getParameter("daysname");
        	if(strdaysname != null && strdaysname != ""){
        		//System.out.println("strdate=Integer.parseInt(strdaysname)"+strdaysname);
        		strdate=Integer.parseInt(strdaysname);
        		
        	}	
        	/*String strmonthname=request.getParameter("monthname");
        	if(strmonthname!=null && strmonthname != "")
        		strmonth=Integer.parseInt(strmonthname);*/
        	//System.out.println("days"+strdate+"iArc:"+iArchieveState+"checkDate:"+checkDate);
        }
        if(request.getParameter("daysarcname") != "" && 
        		request.getParameter("daysarcname") != null)// && request.getParameter("monthname") != "")
        {       
        	    
	    		String strdaysname=request.getParameter("daysarcname");
	        	if(strdaysname!=null && strdaysname != "")
	        		strdate=Integer.parseInt(strdaysname);
	        	//String strmonthname=request.getParameter("monthname");
	        	//if(strmonthname!=null && strmonthname != "")
	        		//strmonth=Integer.parseInt(strmonthname);
	        	checkDate = null;
	        	iArchieveState = 3;
	        	//System.out.println("days"+strdate+"iArc:"+iArchieveState+"checkDate:"+checkDate);
        }
        }catch(Exception e) { e.printStackTrace();}
        int days = strdate;
        //if(days != 365)
        defcount = days;
        String procedure = null;
		if(strprocedure != null && strprocedure != "" )
			procedure = strprocedure;
		
		if(checkPro != null && checkPro != "")
			 checkProo = checkPro;
		//System.out.println("checkPro:"+checkProo);
		
        //----------------------------------------------------------------------
		
		//System.out.println("Days2:"+defcount);
		
		
		
		//System.out.println("## Before Method call:"+procedure);
		System.out.println("checkDate"+checkDate);
		int totalrows = queueService.getProDateRowsCount(
				procedure,defcount,checkProo, checkDate, iArchieveState,
				startvalue,endvalue,sortvalue);
		
		//System.out.println("Total Row count:"+totalrows);
    	List<QueueItem> items = queueService.getProDateRows(procedure,defcount,
    			checkProo, checkDate, iArchieveState,startvalue,endvalue,
    			sortvalue);
    	//strprocedure,days,checkPro, checkDate, iArchieveState,queuelistcount,sortvalue
		//List<QueueItem> items = queueService.getArchivedRows();
    	Map map=new HashMap();
    	//SmsTypeService smsserv=Context.getService(SmsTypeService.class);
    	//List<SmsType> smslist=smsserv.getSmsAllRows();
    	int pageno = 1;
    	if(startvalue > 0 && startvalue < endvalue)
    		pageno = 2;
    	if(startvalue > endvalue)
    	{	
    		pageno = startvalue / (endvalue-1);
    		pageno = pageno+1;
    	}	
    	//System.out.println("pageno:"+pageno);
    	
    	int count=1;
    	while(totalrows/(endvalue)>0)
    	{
    		totalrows = totalrows-endvalue+1;
    		//System.out.println("In While"+totalrows);
    		count++;
    	}
        //System.out.println("Total pages:"+count);
    	List<QueueItem> procedurelist = queueService.getProcedureAllRows();
    	List<DateItems> dateitems = queueService.getDateMonths();
    	//System.out.println("on else : ");
    	//String strArchieve =request.getParameter("optionarchive");
    	//System.out.println("On Else strarchive: " + strArchieve);
    	map.put("queueItems", items);
    	map.put("procedurerows",procedurelist);
    	map.put("dateItems", dateitems);
    	map.put("queuelistcount",queuelistcountObj);
    	map.put("queuesize",items.size());
    	map.put("totocount",count);
    	map.put("pageno",pageno);
    	
    	request.setAttribute("proname", "SHOW ALL");
    	//response.setContentType("text/xml");
        log.info("Returning " + items.size() + "axx deferred queue items" );
        
        if(request.getParameter("disformate") == null || request.getParameter("disformate").equalsIgnoreCase("htmlformat"))
        {
        	//System.out.println("formate:"+request.getParameter("disformate"));
            setViewName("/module/moca/queue");
            return new ModelAndView(getViewName(), "map",map);
        }
        else
        
            if(request.getParameter("disformate").equalsIgnoreCase("Jsonformat"))
            {
            	//System.out.println("formate:"+request.getParameter("disformate"));	
            setViewName("/module/moca/jsonformat");
            QueueItemJson qij = new QueueItemJson();
            sbr = qij.encode(items);
            
            Map map1=new HashMap();
            map1.put("sbr",sbr.toString());
            return new ModelAndView(getViewName(),"sbr", map1);
            }
       
        else
        	if(request.getParameter("disformate").equalsIgnoreCase("Xmlformat"))
        	{
        		//System.out.println("formate:"+request.getParameter("disformate"));
        		setViewName("/module/moca/xmlformat");
        		QueueItemXml qix=new QueueItemXml();
        		
	            sbr = qix.encode(items);
	            PatientResource pr = new PatientResource();
	            try{
	            	
	            
	            PrintWriter pw=response.getWriter();
	            pr.printPatientList(pw, sbr);
	            }
	            catch(Exception e)
	            {
	            	e.printStackTrace();
	            }
	           
	            Map map1=new HashMap();
	           
	            map1.put("sbr",sbr);
	            return new ModelAndView(getViewName(),"sbr", map1);
        	}
        System.gc();
        return null;
        
	}
	
    	
    		
    	/*QueueItemService queueService = Context.getService(QueueItemService.class);
    	List<QueueItem> items = queueService.getVisibleQueueItems();
        log.info("Returning " + items.size() + " queue items");*/
    	//return null;
    /*}*/
    
    
    
    private Patient pickRandomPatient() {
        PatientService pservice = (PatientService)Context.getService(
        		PatientService.class);
        //List<PatientIdentifier> identifiers = pservice.getPatientIdentifiers(null, new Vector<PatientIdentifierType>(), null, null, null);
        /*
        if(identifiers.size() > 0) {
            int random = (int)Math.round(Math.random()*identifiers.size());
            PatientIdentifier id = identifiers.get(random);
            Set<Patient> patients = pservice.getPatientsByIdentifier(id.getIdentifier(), false);
            if(patients.size() > 0)
                return (Patient)patients.toArray()[0];
        } */

        return pservice.getPatient(2); //TODO
    }
    
    private Obs createFakeObservation(Patient p) {
        ObsService oservice = (ObsService)Context.getService(ObsService.class);
        
        LocationService lservice = (LocationService)Context.getService(
        		LocationService.class);
        ConceptService cservice = Context.getConceptService();
        
        Obs o = new Obs();
        o.setPerson(p);
        o.setLocation(lservice.getDefaultLocation());
        o.setConcept(cservice.getConcept(1119));
        o.setDateCreated(new Date());
        o.setObsDatetime(new Date());
        o.setCreator(Context.getAuthenticatedUser());
        
        return o;
    }
    
    private Obs createFakeComplexObservation(Patient p) throws IOException {
        ObsService oservice = (ObsService)Context.getService(ObsService.class);
        
        LocationService lservice = (LocationService)Context.getService(
        		LocationService.class);
        ConceptService cservice = Context.getConceptService();
        
        Obs o = new Obs();
        o.setPerson(p);
        o.setLocation(lservice.getDefaultLocation());
        o.setConcept(cservice.getConcept(6099));
        o.setDateCreated(new Date());
        o.setObsDatetime(new Date());
        o.setCreator(Context.getAuthenticatedUser());

        BufferedImage img = ImageIO.read(new File("/home/rryan/Pictures/rryan-head.jpg"));
        ComplexData complexData = new ComplexData("test-image.jpg", img);
        o.setComplexData(complexData);
        
        
        return o;
    }
    private void createFakeQueueItem() throws IOException {
        QueueItemService qservice = (QueueItemService)Context.getService(
        		QueueItemService.class);
        
        Patient p = pickRandomPatient();
        Encounter e = new Encounter();
        e.setEncounterDatetime(new Date());
        e.setDateCreated(new Date());
        e.setLocation(((LocationService)Context.getService(
        		LocationService.class)).getDefaultLocation());
        e.setPatient(p);
        e.setCreator(Context.getAuthenticatedUser());
        e.setForm(Context.getFormService().getAllForms().get(0));
        e.setEncounterType(Context.getEncounterService()
        		.getAllEncounterTypes().get(0));
        e.setProvider(Context.getAuthenticatedUser());
        
        Context.getEncounterService().saveEncounter(e);
        
        Obs o = createFakeObservation(p);
        Context.getObsService().saveObs(o, "");

        Obs o2 = createFakeComplexObservation(p);
        Context.getObsService().saveObs(o2, "");

        e.addObs(o);
        e.addObs(o2);
        Context.getEncounterService().saveEncounter(e);
        
        if(p != null) {
            QueueItem q = new QueueItem();
            q.setVisible(true);
            q.setPatient(p);
            q.setEncounter(e);
            q.setDateUploaded(new Date());
            qservice.createQueueItem(q);
        }
        
    }
    
}
/*if( request.getParameter("hidnextname") != null &&  request.getParameter("hidnextname") != "")
{	
	
		if(request.getParameter("queueLimit")!=null)
		{
			System.out.println("queuelistcount :"+queueLimit);
			queuelistcount = Integer.parseInt(queueLimit);
		    
			endvalue = queuelistcount+Integer.parseInt( request.getParameter("hidprevname"));

		}
		else
     		endvalue = Integer.parseInt( request.getParameter("hidnextname"));
	

}*/
//System.out.println("EndValue :"+endvalue);
//-----------------------------------------------------------------
//System.out.println("On HandleReqeuestInternal");

//System.out.println("UserName "+strUserName.getUsername());
/*if(request.getParameter("subarchivename")!=null) //checking archived button is submited or not.
{
	
	QueueItemService queueService = Context.getService(QueueItemService.class);
	
	//System.out.println("On handleRequestInternal : split | subarchivename");
	String checkedids = request.getParameter("chklist");// chklist is Hidden Text field, contains checked ids from Moca_queue (appended all IDs). 
	//System.out.println("Request Parameter : " + checkedids);
	if(checkedids != null && checkedids!="")
	{
    	String strcheckedlist = checkedids;
    	//System.out.println("IN STRCHECKEDLIST | " + strcheckedlist);
    	String[] temp;
    	temp =strcheckedlist.split(";");//spliting the checked ids string into array[] values.
    	
    	//System.out.println("ONSPLIT"+temp);
    	int intarr[] = new int[temp.length];
    	for(int i=0;i<temp.length;i++)
    	{	
    		//System.out.println(temp[i]+" ");
    		intarr[i]=Integer.parseInt(temp[i]);
    		
    		
    	}
    	queueService.getUnArchivedRows(intarr);//function for "Archiving" intarr[] values.
	
	}
	
	//System.out.println("On HandleReqeuestInternal | if");
	String strprocedure=request.getParameter("proname");//Retriving Procedure Text Field String .
	
    String  checkPro = request.getParameter("comboPro");
    String  checkDate = request.getParameter("comboDate");
    
    
    String strArchieve =request.getParameter("optionarchive");
	//System.out.println("Archived Button | strarchive: Value =" + strArchieve);
	//Selecting archived Options like Show All / Show Archived / Show UNArchived into iArchiveState flag.
	
	 iArchieveState=0;
	if(strArchieve.equalsIgnoreCase("Show ALL"))
			iArchieveState=0;
	else if(strArchieve.equalsIgnoreCase("Show InActive"))
		iArchieveState=2;
	else if(strArchieve.equalsIgnoreCase("Show Active"))
		iArchieveState=1;
    
    int strdate=0;
    
    int strmonth=0;
    //Checking for date checkbox
    try {
    if(!checkDate.equalsIgnoreCase("SHOW ALL"))
    {
    	
    	String strdaysname=request.getParameter("daysname");
    	if(strdaysname!=null && strdaysname != "")
    		strdate=Integer.parseInt(strdaysname);
    	
    }
    }catch(Exception e){ e.printStackTrace();}
   
   // System.out.println("On if : "+ checkPro + checkDate);
    Map map = new HashMap();
    
    int days = strdate;
    
	//System.out.println("On handleRequestInternal : "+request.getParameter("proname"));
	
	//QueueItemService queueService = Context.getService(QueueItemService.class);
	
	List<QueueItem> procedurelist = queueService.getProcedureAllRows();
	List<QueueItem> items = queueService.getProDateRows(strprocedure,days,checkPro, checkDate, iArchieveState,startvalue,endvalue,sortvalue);
	List<DateItems> dateitems = queueService.getDateMonths();
	map.put("queueItems", items);
	map.put("procedurerows",procedurelist);
	map.put("dateItems", dateitems);
	map.put("queuelistcount",queuelistcountObj);
	
	log.info("Returning " + items.size() + " queue items");
    return new ModelAndView(getViewName(), "map", map);//returns map to Jsp where these values are Displayed in respected Places.
    
}
else*/
/*if(request.getParameter("subdatearchivename") != null)
{
	
	//System.out.println("On SubDateArchiveName : ");
	Map map = new HashMap();
	QueueItemService queueService = Context.getService(QueueItemService.class);
	int strdate = 1;
    int strmonth = 0;
    if(request.getParameter("daysarcname") != "")// && request.getParameter("monthname") != "")
    {
    		String strdaysname=request.getParameter("daysname");
        	if(strdaysname!=null && strdaysname != "")
        		strdate=Integer.parseInt(strdaysname);
        	//String strmonthname=request.getParameter("monthname");
        	//if(strmonthname!=null && strmonthname != "")
        		//strmonth=Integer.parseInt(strmonthname);
    }
 
    int days = strdate;
    System.out.println("Days1:"+days);

  //  System.out.println("Date after converting : " + days);
	List<QueueItem> items = queueService.getProDateRows(null,days,null, null, 3,startvalue,endvalue,sortvalue);

	        
	List<QueueItem> procedurelist=queueService.getProcedureAllRows();
	List<DateItems> dateitems = queueService.getDateMonths();
	
	//System.out.println("on else : ");
	String strArchieve =request.getParameter("optionarchive");
	//System.out.println("On Else strarchive: " + strArchieve);
	map.put("queueItems", items);
	map.put("procedurerows",procedurelist);
	map.put("dateItems", dateitems);
	map.put("queuelistcount",queuelistcountObj);
	map.put("queuesize",items.size());
    log.info("Returning " + items.size() + "axx deferred queue items" );
    System.out.println("##viewName:"+getViewName());
    setViewName("/module/moca/queue");
    System.out.println("##viewName:"+getViewName());
    return new ModelAndView(getViewName(), "map", map);
	
	
	        	
}*/

//System.out.println("Between and After other else");
/*else if(request.getParameter("subproname") != null)
{//Checking for procedure or Date or Option Arichived combobox selection without Archived Submit button.
	//if(request.getParameter("comboPro") !=null   || request.getParameter("comboDate")!=null  || request.getParameter("optionarchive")!=null)
	
		
		//System.out.println("On HandleReqeuestInternal | on comboPro And comboDate" + request.getParameter("comboPro"));
		String strprocedure = request.getParameter("proname");
    	
        String  checkPro = request.getParameter("comboPro");
        String  checkDate = request.getParameter("comboDate");
        //System.out.println("ON FILTER | COMBOPRO " +checkPro+" "+checkDate);
        
        String strArchieve = request.getParameter("optionarchive");
    	//System.out.println("strarchive: Value =" + strArchieve);
    	
        iArchieveState = 0;
    	if(strArchieve.equalsIgnoreCase("Show ALL"))
    			iArchieveState = 0;
    	else if(strArchieve.equalsIgnoreCase("Show InActive"))
    		iArchieveState = 2;
    	else if(strArchieve.equalsIgnoreCase("Show Active"))
    		iArchieveState = 1;
        
        int strdate = 365;
        
        int strmonth = 0;
        //System.out.println("Near Daysname ");
        try{
        if(request.getParameter("daysname") != null && request.getParameter("daysname") != "")
        {
        	
        	String strdaysname = request.getParameter("daysname");
        	if(strdaysname != null && strdaysname != "")
        		strdate=Integer.parseInt(strdaysname);
        	String strmonthname=request.getParameter("monthname");
        	if(strmonthname!=null && strmonthname != "")
        		strmonth=Integer.parseInt(strmonthname);
        }
        }catch(Exception e) { e.printStackTrace();}
       // System.out.println("On if : "+ checkPro + strdate);
        Map map = new HashMap();
        int days = strdate;
        
    	//System.out.println("On handleRequestInternal Proname value : "+request.getParameter("proname"));
    	
    	

    	
		QueueItemService queueService = Context.getService(QueueItemService.class);
		
		List<QueueItem> procedurelist=queueService.getProcedureAllRows();
    	List<QueueItem> items = queueService.getProDateRows(strprocedure,days,checkPro, checkDate, iArchieveState,startvalue,endvalue,sortvalue);
    	List<DateItems> dateitems = queueService.getDateMonths();
    	//Storing all the queueItems, procedurerows, dateItems in map for retrivin into jsp.
    	map.put("queueItems", items);
    	map.put("procedurerows",procedurelist);
    	map.put("dateItems", dateitems);
    	map.put("queuelistcount",queuelistcountObj);
    	
    	log.info("Returning " + items.size() + " queue items");
        return new ModelAndView(getViewName(), "map", map);
	
}*/

/*else
{*/


