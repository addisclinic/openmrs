package org.moca.queue.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

import org.openmrs.ConceptSource;
import org.openmrs.ConceptWord;
import org.openmrs.ConceptMap;
import org.openmrs.api.ConceptService;

/**
 * A servlet for searching Concepts
 * 
 * @author Sana Development Team
 *
 */
public class ConceptSearchServlet extends HttpServlet {
    private static final long serialVersionUID = -5697343627728598856L;
    private Log log = LogFactory.getLog(this.getClass());
    
    @Override 
    protected void doGet(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
        log.debug("doGet");
        PrintWriter writer = response.getWriter();
        
        //Get search phrase
        String searchPhrase = request.getParameter("phrase");
        String conceptSourceName = request.getParameter("conceptSourceName");

        if(conceptSourceName == null || conceptSourceName.equals(""))
        {
        	log.error("Invalid concept source name, can't perform search");
        	return;
        }
        
        //Find Concept Source
        ConceptSource cSource = null;
        boolean filterConcepts = false;
        
        //If concept source specified, filter concepts
        if(!conceptSourceName.equals("Default")){
        	filterConcepts = true;
        	
    		//Get reference to source
    		List<ConceptSource> sources = 
    			Context.getConceptService().getAllConceptSources();
    	
    		Iterator<ConceptSource> iteratorSources = sources.iterator();
    		
    		ConceptSource next = null;
    	        
            while(iteratorSources.hasNext() && cSource == null){
            	next = iteratorSources.next();
            	if(next.getName().equals(conceptSourceName)){
            		cSource = next;
            		break;
            	}
            }
            
            if(cSource == null)
            {
            	log.error("Invalid concept source object, can't perform search");
            	return;
            }
            
            log.info("source found:" + cSource.toString() + "\n");
        }
        
        //Get concept service
		ConceptService cs = Context.getConceptService();
		List<ConceptWord> conceptWords = new Vector<ConceptWord>();
		String words = "";
		
		//Get locale
		Locale defaultLocale = Context.getLocale();
		
		//Perform the search
		conceptWords.addAll(cs.getConceptWords(searchPhrase, defaultLocale));
		log.error("cwords:" + conceptWords.toString());
       
		//Create a list of the word strings
		Iterator<ConceptMap> listMappings;
		ConceptMap mapping;
		String idNum = "";
		
		for (ConceptWord cword : conceptWords) {
			//Only use openmrs concepts that have matching concept source
			if(filterConcepts){
				log.info("word:" + cword.toString());
				listMappings = cword.getConcept().getConceptMappings().iterator();
				if(listMappings.hasNext()){
					log.error("a mapping");
			        
					mapping = listMappings.next();
					log.error("mapping" + mapping.toString());
			        
					if(mapping.getSource().equals(cSource)){
						//Get SNOMED ID associated with this concept
						idNum = mapping.getSourceCode();
						
						//Add it as a possible word
						words += cword.getConceptName().getName() + "|" 
									+ idNum + ",";
					}     
				}
			}
			//Use all openmrs concepts
			else{
				log.info("word:" + cword.toString());
				words += cword.getConceptName().getName() + ",";
			}
			
		}
		log.error("words" + words);
		writer.print(words);
    }
    
}
