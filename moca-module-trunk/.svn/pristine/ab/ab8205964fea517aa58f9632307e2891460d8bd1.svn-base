package org.moca.queue.web;

/**
 * A response returned from an entity of the Sana dispatch layer. This form 
 * does not provide a code field
 *  
 * @author Sana Development Team
 *
 */
public class MDSRequestResponse {
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	/** A status string */
	public String status;
	/** A message String */
	public String data;
	
	/** 
	 * Evaluates to True if this object's status equals "SUCCESS"
	 * @return
	 */
	public boolean succeeded() {
		return SUCCESS.equals(status);
	}

	/** 
	 * Evaluates to True if this object's status equals "FAILED
	 * @return
	 */
	public boolean failed() {
		return FAILURE.equals(status);
	}
}
