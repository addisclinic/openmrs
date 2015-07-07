package org.openmrs.module.sana.queue.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller backs the /web/module/lexicon.jsp page. This controller is 
 * tied to that jsp page by the RequestMapping annotation.
 *
 * @author Sana Development Team
 *
 */
@Controller
@RequestMapping(value = "module/sana/lexicon/")
public class LexiconController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
    

	/** Success form view name */
	private final String SUCCESS_VIEW = "/module/sana/lexicon/lexicon";
	
	//TODO
	/**
	 * Returns a view of all concept sources associated with ...?
	 */

	@RequestMapping(value = "lexicon.form", method = RequestMethod.GET)
    public String handleRequest(ModelMap map) {
    	List<ConceptSource> sources = Context.getConceptService()
    		.getAllConceptSources();
        log.info("Returning " + sources.size() + " sources");
        map.put("sources",sources);
        return SUCCESS_VIEW;
    }
	
	@RequestMapping(value = "lexicon.form", method = RequestMethod.POST)
	public void onSubmit(HttpServletRequest request, 
			HttpServletResponse response)
	{
		//TODO Redirect
	}
}
