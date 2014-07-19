package org.moca.queue.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * This controller backs the /web/module/lexicon.jsp page. This controller is 
 * tied to that jsp page in the /metadata/moduleApplicationContext.xml file
 */
public class LexiconController extends ParameterizableViewController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
    
	//TODO
	/**
	 * Returns a view of all concept sources associated with ...?
	 */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, 
    		HttpServletResponse response) {
    	List<ConceptSource> sources = Context.getConceptService()
    		.getAllConceptSources();
        log.info("Returning " + sources.size() + " sources");
        return new ModelAndView(getViewName(), "sources", sources);
    }
	
}
