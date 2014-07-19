package org.moca.queue.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import com.google.gson.Gson;

/**
 * A response returned from an entity of the Sana dispatch layer.
 *  
 * @author Sana Development Team
 *
 */
public class MDSResponse {
	public static final String FAILURE = "FAIL";
	public static final String SUCCESS = "OK";
	
	public MDSResponse() {}
	
	/** A status string */
	public String status;
	/** A status code string for providing more granular response */
	public String code;
	/** A message body */
	public String message;
	

    /**
     * Called when a transaction is successful.
     * @param request The originating request
     * @param response The servlet response object
     * @param message The message to return
     * @throws IOException 
     */
    public static void succeed(HttpServletRequest request, 
    		HttpServletResponse response, String message, Log log)
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
        	log.error("problem while writing fail(): " + e.toString());
        }
    }
    
    /**
     * Called when a transaction fails.
     * @param request The originating request
     * @param response The servlet response object
     * @param message The message to return
     * @throws IOException 
     */
    public static final void fail(HttpServletRequest request, 
    		HttpServletResponse response, String message, Log log) 
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
}
