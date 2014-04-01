package org.openmrs.module.sana.queue.web.controller;

import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.ModuleConstants;
import org.openmrs.module.sana.ModuleConstants.Property;
import org.openmrs.module.sana.queue.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
/**
 * This controller handles the logic which backs the /web/module/queueClosed.jsp page
 * and is tied to that jsp page by the RequestMapping annotation.
 *
 * @author Sana Development Team
 *
 */
public class ClosedQueueController  {

    protected final Log log = LogFactory.getLog(getClass());
    
    /** Success form view name */
    private final String HTML_SUCCESS_VIEW = ModuleConstants.FORM_PATH + "queueClosed";
    private final String JSON_SUCCESS_VIEW =ModuleConstants.FORM_PATH + "jsonformat";
    private final String XML_SUCCESS_VIEW =ModuleConstants.FORM_PATH + "xmlformat";

    //@RequestMapping(value = "module/sana/queue/v1/queueClosed.form",method=RequestMethod.GET)
    public ModelAndView handleRequestInternal(HttpServletRequest request, 
    		HttpServletResponse response) 
    {
    	
    	String formpage=HTML_SUCCESS_VIEW;
    	HttpSession httpsession=request.getSession();
        
    	//reading the global property value  for queueitems per page
    	int defcount = Integer.parseInt(Context.getAdministrationService()
    			.getGlobalProperty(Property.DISPLAY_COUNT));
    	int queuelistcount = Integer.parseInt(Context.getAdministrationService()
    			.getGlobalProperty(Property.MAX_QUEUE_ITEMS));
    	//---------------------------------------------------
    	int sortvalue=0;
    	User strUserName = Context.getAuthenticatedUser();
		
    	//getting the request parameters from Jsp
		String strprocedure = request.getParameter("proname");
        String  checkPro = request.getParameter("comboPro");
        String  checkDate = request.getParameter("comboDate");
        String strArchieve = request.getParameter("optionarchive");
    	String queueLimit = request.getParameter("queueLimit");
    	String sortstring = request.getParameter("hidsortname");
    	//---------------------------------------------------
    	//logic to place the sort value 0 for lifo 1 for fifo
    	
    	if(sortstring != null && sortstring != "")
    	{
    		sortvalue = Integer.parseInt(sortstring);
    	}
    	else
    	{
    		sortvalue = 1;
    	}	
    	//--------------------------------------------------
    	String checkProo = "SHOW ALL";
    	int iArchieveState = 1;
    	
    	int endvalue = queuelistcount;
    	if( request.getParameter("queuelimitname") != null &&  
    			request.getParameter("queuelimitname") != "")
    		endvalue = Integer.parseInt( request.getParameter("queuelimitname"));
    	
    	int startvalue = 0; 
    	
    	
    	//logic to select the which page to display, it checks gotopage text 
    	// field if is any value renders resonse based on the page no or it 
    	// takes the value from the hidprev hidden field to display date 
    	
    	if(request.getParameter("gotopagename") != "" && 
    			request.getParameter("gotopagename") != null )
    	{   
    		int substract = Integer.parseInt(
    				request.getParameter("gotopagename"));
    		if( request.getParameter("queuelimitname") != null &&  
    				request.getParameter("queuelimitname") != "")
    		{
        		endvalue = Integer.parseInt(
        				request.getParameter("queuelimitname"));
    		    startvalue = Integer.parseInt(
    		    		request.getParameter("gotopagename"))*endvalue;
    		    startvalue =startvalue-endvalue-(substract-1);
    		}
    		else
    		{
    		    startvalue = Integer.parseInt(
    		    		request.getParameter("gotopagename"))*queuelistcount;
    		    startvalue =startvalue-endvalue-(substract-1);
    		}
    	}	
    	else
	    	if( request.getParameter("hidprevname") != null &&  
	    			request.getParameter("hidprevname") != "")
	    	{  
	    		startvalue = Integer.parseInt(
	    				request.getParameter("hidprevname"));
	    	}	
    	
    	//----------------------------------------------------------
    	
		QueueItemService queueService = 
			Context.getService(QueueItemService.class);
		StringBuffer sbr = null;
		
		//checking archived button is submited or not.
		if(request.getParameter("subarchivename")!=null) 
    	{
			// chklist is Hidden Text field, contains checked ids from 
			// queue (appended all IDs). 
			String checkedids = request.getParameter("chklist");
	    	if(checkedids != null && checkedids!="")
	    	{
	        	String strcheckedlist = checkedids;
	        	String[] temp;
	        	//spliting the checked ids string into array[] values.
	        	temp =strcheckedlist.split(";");
	        	
	        	int intarr[] = new int[temp.length];
	        	for(int i=0;i<temp.length;i++)
	        	{	
	        		intarr[i]=Integer.parseInt(temp[i]);
	        	}
	        	//function for "Archiving" intarr[] values.
	        	queueService.getUnArchivedRows(intarr);
	    	}
    	}	
		//logic to display active,inactive or all
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
    	//--------------------------------------
    	int strdate = 365;
        int strmonth = 0;
        try
        {
        	//logic to display queueitems before the selected date
	        if(request.getParameter("daysname") != null && 
	        		request.getParameter("daysname") != "")
	        {
	        	
	        	String strdaysname = request.getParameter("daysname");
	        	if(strdaysname != null && strdaysname != ""){
	        		strdate=Integer.parseInt(strdaysname);
	        		
	            }	
	        }
	        //------------------------------------------------
	        //logic to display queueitems beyond the selected date
	        if(request.getParameter("daysarcname") != "" && 
	        		request.getParameter("daysarcname") != null)// && request.getParameter("monthname") != "")
	        {       
	        	    
		    		String strdaysname=request.getParameter("daysarcname");
		        	if(strdaysname!=null && strdaysname != "")
		        		strdate=Integer.parseInt(strdaysname);
		        	checkDate = null;
		        	iArchieveState = 3;
	        }
	        //-----------------------------------------------------
        }
        catch(Exception e) { e.printStackTrace();}
        int days = strdate;
        //if(days != 365)
        defcount = days;
        String procedure = null;
		if(strprocedure != null && strprocedure != "" )
			procedure = strprocedure;
		if(checkPro != null && checkPro != "")
			 checkProo = checkPro;
		//getting size of queueuitemtable
		int totalrows = queueService.getProDateRowsClosedCount(
				procedure,defcount,checkProo, checkDate, 
				iArchieveState,startvalue,endvalue,sortvalue);
		//getting rows of queueitemtable depends on selected options
    	List<QueueItem> items = queueService.getProDateRowsClosed(procedure,
    			defcount,checkProo, checkDate, iArchieveState,startvalue,
    			endvalue,sortvalue);
    	//getting procedure list for dropdown selection.
    	List<QueueItem> procedurelist = queueService.getProcedureAllRows();
    	List<DateItems> dateitems = queueService.getDateMonths();
    	Integer queuelistcountObj = new Integer(queuelistcount);
    	
    	//setting up pageno depends on queuelimitvalue
    	int pageno = 1;
    	if(startvalue > 0 && startvalue < endvalue)
    		pageno = 2;
    	if(startvalue > endvalue)
    	{	
    		pageno = startvalue / (endvalue-1);
    		pageno = pageno+1;
    	}	
    	System.out.println("pageno:"+pageno);
    	
    	int count=1;
    	while(totalrows/(endvalue)>0)
    	{
    		totalrows = totalrows-endvalue+1;
    		//System.out.println("In While"+totalrows);
    		count++;
    	}
        System.out.println("Total pages:"+count);
    	
    	Map map=new HashMap();
    	map.put("queueItems", items);
    	map.put("procedurerows",procedurelist);
    	map.put("dateItems", dateitems);
    	map.put("queuelistcount",queuelistcountObj);
    	map.put("queuesize",items.size());
    	map.put("totocount",count);
    	map.put("pageno",pageno);
    	
    	request.setAttribute("proname", "SHOW ALL");
        log.info("Returning " + items.size() + "axx deferred queue items" );
        
        //it show the response as selected formates html,xml,json
        if(request.getParameter("disformate") == null || 
        		request.getParameter("disformate").equalsIgnoreCase("htmlformat"))
        {
            return new ModelAndView(HTML_SUCCESS_VIEW, "map",map);
        }
        //--------------------------------------------------
        else // is selected formate is Json it displays the Jsonformate
            if(request.getParameter("disformate").equalsIgnoreCase("Jsonformat"))
            {
            	
	            QueueItemJson qij = new QueueItemJson();//Logic to convert data to Jsonformate
	            sbr = qij.encode(items);
	            
	            Map map1=new HashMap();
	            map1.put("sbr",sbr.toString());
	            return new ModelAndView(JSON_SUCCESS_VIEW,"sbr", map1);
            }
       //-----------------------------------------------
        else //if selected formate is Xml it displys the Xmlformate
        	if(request.getParameter("disformate").equalsIgnoreCase("Xmlformat"))
        	{
        		//logic to convert data to Xmlformate
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
	            return new ModelAndView(XML_SUCCESS_VIEW,"sbr", map1);
        	}
        //------------------------------------------------------
        return null;
	}
}