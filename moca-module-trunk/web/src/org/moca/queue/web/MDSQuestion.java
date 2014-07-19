package org.moca.queue.web;

/**
 * An point of data collection as it is represented on the Sana platform. 
 * Translated into Observations on OpenMRS
 * 
 * @author Sana Development Team
 *
 */
public class MDSQuestion {
    
	/**
	 * Default constructor
	 */
    public MDSQuestion() {}
    
    /** The id attribute within a procedure */
    public String id;
    /** The type attribute within a procedure */
    public String type;
    /** The concept attribute within a procedure */
    public String concept;
    /** The question attribute within a procedure */
    public String question;
    /** The answer attribute within a procedure */
    public String answer;
    
    /**
     * Returns the object formatted as:
     *   id, type, concept, question, answer
     */
    @Override
    public String toString(){
    	return String.format("id:%s, type: %s, concept: %s, question: %s, "
    			+ " answer: %s", id,type,concept,question, answer);
    }
}
