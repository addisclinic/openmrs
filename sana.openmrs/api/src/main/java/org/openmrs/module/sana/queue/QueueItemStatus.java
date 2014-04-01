package org.openmrs.module.sana.queue;

import java.io.Serializable;

import org.openmrs.module.sana.ModuleConstants.Module;

/**
 * The valid status codes for items in the queue
 * 
 * @author Sana Development Team
 *
 */
public enum QueueItemStatus implements Serializable {
	/** An item that has not yet been viewed */
	NEW(Module.ID+".queueitem_status_new"),
	
	/** An item that is currently under review */
	IN_PROGRESS(Module.ID+".queueitem_status_inprogress"),
	
	/** An item that has been deferred for later handling */
	DEFERRED(Module.ID+".queueitem_status_deferred"),
	
	/** An item that is no longer active-reviewed and processed, or deactivated*/
	CLOSED(Module.ID+".queueitem_status_closed");
	
	private static final long serialVersionUID = -14980395290372323L;
	private String code;
	
	private QueueItemStatus(String code) {
		this.code = code;
	}
	
	/**
	 * The items string code
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}
	
	public Integer getId() {
		return ordinal();
	}
	
	/**
	 * String representation of the status codes
	 */
	public String toString(){
		if(code.equals(Module.ID+".queueitem_status_new")){
			return "New";
		}
		else if(code.equals(Module.ID+".queueitem_status_inprogress")){
			return "In Progress";
		}
		else if(code.equals(Module.ID+".queueitem_status_deferred")){
			return "Deferred";
		}
		else if(code.equals(Module.ID+".queueitem_status_closed")){
			return "Closed";
		}
		return "None";
	}
	
	/**
	 * Gets the QueueItemStatus from a string representation of the code 
	 * @param status the code string
	 * @return
	 */
	public static QueueItemStatus parseStatus(String status) {
		if (NEW.code.equals(status))
			return NEW; 
		else if(IN_PROGRESS.code.equals(status))
			return IN_PROGRESS;
		else if(DEFERRED.code.equals(status))
			return DEFERRED;
		else if(CLOSED.code.equals(status))
			return CLOSED;
		return NEW;
	}

}
