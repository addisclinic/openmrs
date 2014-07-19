package org.moca.queue.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moca.queue.web.MDSResponse;
import org.openmrs.api.context.Context;
import org.openmrs.User;

import com.google.gson.Gson;

/**
 * Handles Requests to verify if a user has permission to interact with Sana 
 * queue
 * 
 * @author Sana Development Team
 *
 */
public class PermissionsServlet extends HttpServlet {

    private static final long serialVersionUID = 4847187771370210197L;
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * Called when a transaction is successful.
     * @param request The originating request
     * @param response The servlet response object
     * @param message The message to return
     */
    private void succeed(HttpServletRequest request, 
    		HttpServletResponse response, String message) 
    {
        try {
        	MDSResponse mdsresponse = new MDSResponse();
        	mdsresponse.status = MDSResponse.SUCCESS;
        	mdsresponse.code = "unspecified";
        	mdsresponse.message = message;
        	Gson g = new Gson();
        	String response_json = g.toJson(mdsresponse);
        	
        	//get the PrintWriter object to write the html page
        	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
        	out.write(response_json);
        	out.flush();
        	out.close();
        } catch(IOException e){
        	log.error("problem while writing succeed():" + e.toString());
        }
    }

    /**
     * Called when a transaction fails.
     * @param request The originating request
     * @param response The servlet response object
     * @param message The message to return
     */
    private void fail(HttpServletRequest request, HttpServletResponse response,
    		String message) 
    {
        try {
        	MDSResponse mdsresponse = new MDSResponse();
        	mdsresponse.status = MDSResponse.FAILURE;
        	mdsresponse.code = "unspecified";
        	mdsresponse.message = message;
        	Gson g = new Gson();
        	String response_json = g.toJson(mdsresponse);
        	
        	//get the PrintWriter object to write the html page
        	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
        	out.write(response_json);
        	out.flush();
        	out.close();
        } catch(IOException e){
        	log.error("problem while writing fail(): " + e.toString());
        }
    }

	/**
	 * Check if current user has permissions to manage the queue in order to 
	 * upload a procedure	 
	 */
    @Override
    protected void doGet(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {    
    	User currentUser = Context.getAuthenticatedUser();
    	String managePrivilege = "Manage Moca Queue";
    	
    	if(currentUser.hasPrivilege(managePrivilege)){
    		log.info("User " + currentUser.getGivenName() + " " 
    				+ currentUser.getFamilyName() + "does have privilege");
    		succeed(request, response, "User has Manage Sana Queue privileges");
    	}
    	else{
    		log.info("User " + currentUser.getGivenName() + " " 
    				+ currentUser.getFamilyName() + "does NOT have privilege");
    		fail(request, response, "User " + currentUser.getGivenName() + " " 
    				+ currentUser.getFamilyName() 
    				+ "does not have Manage Sana Queue Privileges");
    	}
    }
}


