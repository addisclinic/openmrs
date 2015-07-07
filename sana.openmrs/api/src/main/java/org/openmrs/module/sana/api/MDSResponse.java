package org.openmrs.module.sana.api;

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
	 * Returns the JSON String representation of the object
	 */
	public String toJSON(){
    	Gson g = new Gson();
    	return g.toJson(this);
	}
	
    /**
     * Called when a transaction is successful. The response code is set to 
     * unspecified.
     * @param message The message body of the response
     * @return
     */
    public static MDSResponse succeed(String message){
    	return succeed(message, "unspecified");
    }
    
    /**
     * Call when a transaction is successful to generate a response message.
     * 
     * @param message The message body of the response
     * @param code A response code.
     * @return
     */
    public static MDSResponse succeed(String message, String code){
    	MDSResponse mdsresponse = new MDSResponse();
    	mdsresponse.status = MDSResponse.SUCCESS;
    	mdsresponse.code = code;
    	mdsresponse.message = message;
    	return mdsresponse;
    }
    
    /**
     * Called when a transaction fails. The response code is set to 
     * unspecified.
     * @param message The message body of the response
     * @return
     */
    public static MDSResponse fail(String message){
    	return fail(message, "unspecified");
    }
    
    /**
     * Called when a transaction fails.
     * @param message The message body of the response
     * @param code
     * @return
     */
    public static MDSResponse fail(String message, String code){
    	MDSResponse mdsresponse = new MDSResponse();
    	mdsresponse.status = MDSResponse.FAILURE;
    	mdsresponse.code = "unspecified";
    	mdsresponse.message = message;
    	return mdsresponse;
    }
}
