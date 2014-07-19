package org.moca.queue.web.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moca.queue.web.MDSResponse;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.ConceptWord;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;

import com.google.gson.Gson;

public class LexiconServlet extends HttpServlet {

    private static final long serialVersionUID = 4847327702197L;
    private Log log = LogFactory.getLog(this.getClass());
    
    @SuppressWarnings({ "unchecked", "deprecation" })
    private List<FileItem> getUploadedFiles(HttpServletRequest request) throws FileUploadException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        if(!request.getHeader("content-type").contains("application/x-www-form-urlencoded"))        
            return (List<FileItem>)upload.parseRequest(request);
        return new ArrayList<FileItem>();
    }
    
    private void fail(PrintWriter output,String message) {
    	output.println("\nERROR: " + message + "\n");
    	log.error(message);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {
        log.debug("doPost");
        PrintWriter output = response.getWriter();
        
        //Get parameters
        String action = "";
        FileItem csvFile = null;
        FileItem retiredCsvFile = null;
        FileItem newCsvFile = null;
        String conceptSourceName = "";
        String conceptSourceDescription = "";
        String colConceptName = "";
        String colConceptClass = ""; 
        String colIdNum = "";
        
        List<FileItem> files;
        try {
            files = getUploadedFiles(request);
        } catch(FileUploadException e) {
            fail(output, "Parsing uploaded files error " + e.toString());
            return;
        }
        
        log.info("Got " + files.size() + " files");
        
        for(FileItem f : files) {
            log.info("Got file fieldname:" + f.getFieldName() 
            		+ " content type: " + f.getContentType() 
            		+ " name:" + f.getName() + " size:" + f.getSize());
        }
        
        // If we can't find it in the parameters, then we need to search for it in the files list.
    	for (int i = 0; i < files.size(); ++i) {
    		FileItem f = files.get(i);
    		if (f == null)
    			continue;
    		String name = f.getFieldName();
    		if (name.equals("action"))
    			action = f.getString();
    		else if (name.equals("csvFile"))
    			csvFile = f;
    		else if (name.equals("newCsvFile"))
    			newCsvFile = f;
    		else if (name.equals("retiredCsvFile"))
    			retiredCsvFile = f;
     		else if (name.equals("conceptSourceName"))
     			conceptSourceName = f.getString();
     		else if (name.equals("conceptSourceDescription"))
     			conceptSourceDescription = f.getString();
     		else if (name.equals("columnConceptName"))
     			colConceptName = f.getString();
     		else if (name.equals("columnConceptClass"))
     			colConceptClass = f.getString();
     		else if (name.equals("columnIdNum"))
     			colIdNum = f.getString();
    	}
    	
        //Check that concept source name is not null
        if(conceptSourceName == null || conceptSourceName.equals("")){
        	fail(output,"Concept source name can't be null or empty string");
        	return;
        }	
                
        //Columns according to the input csv file
        int columnIDNum = 0;
        int columnName = 0;
        int columnType = 0;
        if(colIdNum != null && !colIdNum.equals(""))
        	columnIDNum = Integer.parseInt(colIdNum);
        if(colConceptName != null && !colConceptName.equals(""))
        	columnName = Integer.parseInt(colConceptName);
        if(colConceptClass != null && !colConceptClass.equals(""))
        	columnType = Integer.parseInt(colConceptClass);
        
        
        //Get uploaded file name
        //Source: "Parse CSV File using String Tokenizer example"
        //http://www.java-examples.com/parse-csv-file-using-stringtokenizer-example
        int lineNumber = 0, tokenNumber = 1;
        
        try{
        	if(action.equals("add")){
		        if(csvFile != null){
		        	
		        	List<ConceptSource> sources = Context.getConceptService().getAllConceptSources();
		        	
		        	//Check that one doesn't already exist with that name
		            Iterator<ConceptSource> iteratorSources = sources.iterator();
		    		ConceptSource currentSource = null;

		            while(iteratorSources.hasNext()){
		            	currentSource = iteratorSources.next();
		            	if(currentSource.getName().equals(conceptSourceName)){
		            		//Concept source already exists with that name
		            		fail(output,"Cannot use existing concept source name");
		            		return;
		            	}
		            }
		        
		            //Set concept source fields 
		        	ConceptSource cSource = new ConceptSource();
		        	cSource.setName(conceptSourceName);
		        	cSource.setDescription(conceptSourceDescription);
		        	cSource.setCreator(Context.getAuthenticatedUser());
		        	cSource.setDateCreated(new Date());
		        	cSource.setHl7Code("");
		        	cSource = Context.getConceptService().saveConceptSource(cSource);
		        	
		        	output.println("FOR CONCEPT SOURCE: " + conceptSourceName.toUpperCase() + "\n");

			        BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
			        String nextLine = "";
			        StringBuffer sb = new StringBuffer();
			        boolean result = true;
			        
		        	output.println("ADDING MEDICAL VOCABULARY " + "\n");
	            	while ((nextLine = br.readLine()) != null) {
	            		
	            		//skip the header of the csv file
	            		if(lineNumber == 0)
	            			output.println("Strip out header line of text: " + nextLine);
	            		
			        	//process the rest of the lines
	            		else{
				        	output.println("Processing line " + lineNumber + " of text: " + nextLine);
			        		
			        		//Add a concept for this line of the csv file
			        		result = addConcept(output, nextLine, cSource, columnIDNum, columnName, columnType, tokenNumber);
			        		
			        		//If result is false, then stop execution because there is an error
			        		if(!result)
			        			return;
			        	}
			        	//reset variables for next line of csv file
			        	tokenNumber = 1;
			        	lineNumber++;
			        }
		        }
		       
        	}
        	else if(action.equals("update"))
        	{
        		
        		List<ConceptSource> sources = Context.getConceptService().getAllConceptSources();
	        	
	        	//Check that one doesn't already exist with that name
	            Iterator<ConceptSource> iteratorSources = sources.iterator();
	    		ConceptSource currentSource = null;
	    		ConceptSource cSource = null;
	    		
	            while(iteratorSources.hasNext()){
	            	currentSource = iteratorSources.next();
	            	if(currentSource.getName().equals(conceptSourceName)){
	            		cSource = currentSource;
	            	}
	            }
	            
	            if(cSource == null){
	            	fail(output,"Couldn't find concept source with the name " + conceptSourceName);
	            	return;
	            }
	            
	        	output.println("FOR CONCEPT SOURCE: " + conceptSourceName.toUpperCase() + "\n");
	        	
	            String nextLine = "";
		        StringBuffer sb = new StringBuffer();
		        boolean result = true;
		        
        		if(retiredCsvFile != null){
            		output.println("UPDATING MEDICAL VOCABULARY - RETIRING CONCEPTS"+"\n");
			        BufferedReader br = new BufferedReader(new InputStreamReader(retiredCsvFile.getInputStream()));
	            	while ((nextLine = br.readLine()) != null) {
	            		
	            		//skip the header of the csv file
	            		if(lineNumber == 0)
	            			output.println("Strip out header line of text: " + nextLine);
	            		
			        	//process the rest of the lines
	            		else{
				        	output.println("Processing line " + lineNumber + " of text: " + nextLine);
			        		
			        		//Retire a concept for this line of the csv file
			        		result = retireConcept(output, nextLine, cSource, columnIDNum, columnName, columnType, tokenNumber);
			        				
			        		//If result is false, then stop execution because there is an error
			        		if(!result)
			        			return;
			        	}
			        	
			        	//reset variables for next line of csv file
			        	tokenNumber = 1;
			        	lineNumber++;
			        }
		        }
        		
        		lineNumber = 0;
        		tokenNumber = 1;
        		if(newCsvFile != null){
            		output.println("UPDATING MEDICAL VOCABULARY - ADDING CONCEPTS"+"\n");
 			        BufferedReader br = new BufferedReader(new InputStreamReader(newCsvFile.getInputStream()));
 	            	while ((nextLine = br.readLine()) != null) {
 	            		
 	            		//skip the header of the csv file
	            		if(lineNumber == 0)
	            			output.println("Strip out header line of text: " + nextLine);
	            		
			        	//process the rest of the lines
	            		else{
				        	output.println("Processing line " + lineNumber + " of text: " + nextLine);
			        		
 			        		//Add a concept for this line of the csv file
 			        		result = addConcept(output, nextLine, cSource, columnIDNum, columnName, columnType, tokenNumber);
		        			
 			        		//If result is false, then stop execution because there is an error
			        		if(!result)
			        			return;
 			        	}
 			        	
 			        	//reset variables for next line of csv file
 			        	tokenNumber = 1;
 			        	lineNumber++;
 			        }
 		        }
        	}
        	else if(action.equals("delete")){
        		
        		output.println("FOR CONCEPT SOURCE: " + conceptSourceName.toUpperCase() + "\n");
        		
        		List<ConceptSource> sources = Context.getConceptService().getAllConceptSources();
	        	
	        	//Check that one doesn't already exist with that name
	            Iterator<ConceptSource> iteratorSources = sources.iterator();
	    		ConceptSource currentSource = null;
	    		ConceptSource cSource = null;
	    		
	            while(iteratorSources.hasNext()){
	            	currentSource = iteratorSources.next();
	            	if(currentSource.getName().equals(conceptSourceName)){
	            		cSource = currentSource;
	            	}
	            }
	            
	            if(cSource == null){
	            	fail(output,"Couldn't find concept source with the name " + conceptSourceName);
	            	return;
	            }
	            
	            cSource.setVoided(true);
	            cSource.setVoidedBy(Context.getAuthenticatedUser());
	            cSource.setDateVoided(new Date());
	            cSource = Context.getConceptService().saveConceptSource(cSource);
	            
	            output.println("Just voided concept source " + conceptSourceName + "\n");
        	}
        	else{
        		fail(output, "Invalid action to perform on vocabulary terms (needs to be add, update, or delete)");
        		return;
        	}
        } catch(Exception e){
         	fail(output, "Can't read csv file " + e.toString());
         	return;
        }
        
        
        output.println("Done");
        
    }
    
    /*
     *  Adds concept to dictionary
     *  
     *  @return True if successful, False if error
     */
    private boolean addConcept(PrintWriter output, String currentLine, ConceptSource cSource, int columnIDNum, int columnName, int columnType, int tokenNumber) throws ServletException{
    	//Separate line by commas
    	StringTokenizer stokenizer = new StringTokenizer(currentLine,",");
    	String token = "";

        //Initialize variables for concept creation
        String conceptName = "";
        String idNum = "";
        String conceptClassName = "Misc";
        
    	//Iterate through each comma separated string of the line
    	while(stokenizer.hasMoreTokens())
    	{
    		token = stokenizer.nextToken();
    		
    		//If this is the element in the column for ID numbers, save the ID
    		if(tokenNumber == columnIDNum){
    			idNum = token;
    			output.println("Concept Source Mapping ID number: " + idNum);
    		}
    		//If this is the element in the column for concept names, save the name
    		else if(tokenNumber == columnName){
    			conceptName = token.toUpperCase();
    			output.println("Concept name: " + conceptName);
    		}
    		else if(tokenNumber == columnType)
    		{
    			if(token.toLowerCase().indexOf("disorder") != -1)
    				conceptClassName = "Diagnosis";
    			else if(token.toLowerCase().indexOf("finding") != -1)
    				conceptClassName = "Finding";
    			output.println("Concept class: " + conceptClassName);
    		}
    		tokenNumber++;
    	}
    	
    	//Create the concept
    	//Make sure there is a concept name and ID number
    	if(!conceptName.equals("") && !idNum.equals("")){
    		Concept c = null;
    		
    		//Create new concept
        	c = new Concept();
        	
        	//Set concept type to be "text"
        	ConceptDatatype conceptType = Context.getConceptService().getConceptDatatypeByName("Text");
        	c.setDatatype(conceptType);  
        	
        	//Set concept class 
        	ConceptClass conceptClass = Context.getConceptService().getConceptClassByName(conceptClassName);
            c.setConceptClass(conceptClass);
            c.setCreator(Context.getAuthenticatedUser());
            c.setDateCreated(new Date());
            
            //Set concept name
            ConceptName name = new ConceptName();
            name.setName(conceptName);
            name.setCreator(Context.getAuthenticatedUser());
            name.setDateCreated(new Date());
            name.setLocale(Context.getLocale());
            c.addName(name);
            
            //Add mapping to SNOMED ID
            ConceptMap mapping = new ConceptMap();
            mapping.setSource(cSource);
            mapping.setSourceCode(idNum);
            c.addConceptMapping(mapping);
            
            try{
            	Context.getConceptService().saveConcept(c);
            	output.println("Successfully added concept: " + c.getDisplayString()+"\n");
            	return true;
            } catch(Exception e){
            	fail(output, "Can't save concept " + e.toString());
            	return false;
            }
              
    	}
    	fail(output, "No concept name or No ID number for line: " + currentLine);
    	return false;
    }
    
    /*
     *  Retires concept from dictionary
     *  
     *  @return True if successful, False if error
     */
    private boolean retireConcept(PrintWriter output, String currentLine, ConceptSource cSource, int columnIDNum, int columnName, int columnType, int tokenNumber) throws ServletException{
    	//Separate line by commas
    	StringTokenizer stokenizer = new StringTokenizer(currentLine,",");
    	String token = "";

        //Initialize variables for concept creation
        String conceptName = "";
        String idNum = "";
        
    	//Iterate through each comma separated string of the line
    	while(stokenizer.hasMoreTokens())
    	{
    		token = stokenizer.nextToken();
    		
    		//If this is the element in the column for ID numbers, save the ID
    		if(tokenNumber == columnIDNum){
    			idNum = token;
    		}
    		//If this is the element in the column for concept names, save the name
    		else if(tokenNumber == columnName){
    			conceptName = token.toUpperCase();
    		}
    		tokenNumber++;
    	}
    	
    	//Retire the concept
    	//Make sure there is a concept name and ID number
    	if(!conceptName.equals("") && !idNum.equals("")){

            try{
				//Mapping to search for
	            ConceptMap mapping = new ConceptMap();
	            mapping.setSource(cSource);
	            mapping.setSourceCode(idNum);
	            log.info("csource " + cSource.toString());
	            
	    		//Find concept 
	    		List<ConceptWord> cwords = Context.getConceptService().getConceptWords(conceptName,Context.getLocale());
	    		log.info("cwords found " + cwords.toString());
	    		
	    		Concept c = null; 
	    		for (ConceptWord cword : cwords) {
	    			if(cword.getConcept().hasName(conceptName,Context.getLocale())){
	    				Concept currentConcept = cword.getConcept();	
	    				Iterator<ConceptMap> i = currentConcept.getConceptMappings().iterator();
	    				while(i.hasNext()){
	    					ConceptMap m = i.next();
	    					if(m.getSource().equals(cSource) && m.getSourceCode().equals(idNum))
	    					{
	    						c = currentConcept;
		    					break;
	    					}
	    				}
	    			}
	    		}
	    		
	    		output.println("Found Concept in " + cSource.getName() + ": " + c);
	    		
	    		if(c == null){
	    			fail(output, "Can't find concept " + conceptName + " with concept source " + cSource.getName() + " in dictionary");
	    			return false;
	    		}
	    		
	    		c.setRetired(true);
	    		c.setRetiredBy(Context.getAuthenticatedUser());
	    		c.setDateRetired(new Date());
	    		
	            Context.getConceptService().saveConcept(c);
	            output.println("Successfully retired concept: " + c.getDisplayString()+"\n");
	            return true;
            } 
            catch(Exception e){
            	fail(output, "Can't retire concept " + e.toString());
            	return false;
            }
    	}
    	fail(output, "No concept name or No ID number for line: " + currentLine);
		return false;
    }
}




