package org.openmrs.module.sana.queue.web.servlet;

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
import java.util.Locale;
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
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.ConceptWord;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.api.MDSResponse;
import org.openmrs.obs.ComplexData;

import com.google.gson.Gson;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

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
    
    private void fail(PrintWriter output,String message, Throwable e) {
    	e.printStackTrace(output);
    	output.println("\nERROR: " + message + "\n");
    	log.error(message);
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
        String conceptSourceCode = "";
        String colConceptName = "";
        String colConceptClass = ""; 
        String colIdNum = "";
        String colConceptDescription = "";
        
        List<FileItem> files;
        try {
            files = getUploadedFiles(request);
        } catch(FileUploadException e) {
        	e.printStackTrace(output);
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
    		if (name.equals(Form.ACTION))
    			action = f.getString();
    		else if (name.equals(Form.CSV_FILE))
    			csvFile = f;
    		else if (name.equals(Form.CSV_FILE_NEW))
    			newCsvFile = f;
    		else if (name.equals(Form.CSV_FILE_RETIRED))
    			retiredCsvFile = f;
     		else if (name.equals(Form.CONCEPT_SOURCE_NAME))
     			conceptSourceName = f.getString();
     		else if (name.equals(Form.CONCEPT_SOURCE_CODE))
     			conceptSourceCode = f.getString();
     		else if (name.equals(Form.CONCEPT_SOURCE_DESCRIPTION))
     			conceptSourceDescription = f.getString();
     		else if (name.equals(Form.COLUMN_CONCEPT_NAME))
     			colConceptName = f.getString();
     		else if (name.equals(Form.COLUMN_CONCEPT_NAME))
     			colConceptClass = f.getString();
     		else if (name.equals(Form.COLUMN_CONCEPT_CLASS))
     			colIdNum = f.getString();     		
     		else if (name.equals(Form.COLUMN_CONCEPT_DESCRIPTION))
         		colConceptDescription = f.getString();
    	}
    	
        //Check that concept source name is not null
        if(!StringUtils.hasText(conceptSourceName)){
        	log.error("Problem with concept source name");
        	fail(output,"Concept source name can't be null or empty string");
        	return;
        }	
                
        //Columns according to the input csv file
        int columnIDNum = 0;
        int columnName = 0;
        int columnType = 0;
        int columnDescription = 0;
        if(StringUtils.hasText(colIdNum))
        	columnIDNum = Integer.parseInt(colIdNum);
        if(StringUtils.hasText(colConceptName)) 
        	columnName = Integer.parseInt(colConceptName);
        if(StringUtils.hasText(colConceptClass))
        	columnType = Integer.parseInt(colConceptClass);
        if(StringUtils.hasText(colConceptDescription))
        	columnDescription = Integer.parseInt(colConceptDescription);
        
        //Get uploaded file name
        //Source: "Parse CSV File using String Tokenizer example"
        //http://www.java-examples.com/parse-csv-file-using-stringtokenizer-example
        int lineNumber = 0, tokenNumber = 1;
        
        // Check for conceptSource

    	
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
        
        FormAction formAction = FormAction.valueOf(action.toUpperCase(Locale.ENGLISH));
        try{
        	switch(formAction){
        	case ADD:
		        if(csvFile != null){

	            	//Concept source already exists with that name
		            if(cSource != null){
		            	fail(output,"Cannot use existing concept source name");
		            	return;
		            }
		            
		            //Set concept source fields 
		        	cSource = new ConceptSource();
		        	cSource.setName(conceptSourceName);
		        	cSource.setDescription(conceptSourceDescription);
		        	cSource.setCreator(Context.getAuthenticatedUser());
		        	cSource.setDateCreated(new Date());
		        	cSource.setHl7Code(conceptSourceCode);
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
				        	output.println("\nProcessing line " + lineNumber + " of text: " + nextLine);
			        		
			        		//Add a concept for this line of the csv file
			        		result = addConcept(output, nextLine, cSource, columnIDNum, columnName, columnType, tokenNumber, columnDescription);
			        		
			        		//If result is false, then stop execution because there is an error
			        		if(!result)
			        			return;
			        	}
			        	//reset variables for next line of csv file
			        	tokenNumber = 1;
			        	lineNumber++;
			        }
		        }
		        break;
        	case UPDATE:
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
				        	output.println("\nProcessing line " + lineNumber + " of text: " + nextLine);
			        		
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
				        	output.println("\nProcessing line " + lineNumber + " of text: " + nextLine);
			        		
 			        		//Add a concept for this line of the csv file
 			        		result = addConcept(output, nextLine, cSource, columnIDNum, columnName, columnType, tokenNumber,columnDescription);
		        			
 			        		//If result is false, then stop execution because there is an error
			        		if(!result)
			        			output.println("Skipping Concept\n");
			        			return;
 			        	}
 			        	
 			        	//reset variables for next line of csv file
 			        	tokenNumber = 1;
 			        	lineNumber++;
 			        }
 		        }
        		break;
        	case DELETE:
        		output.println("\nDELETE FOR CONCEPT SOURCE: " + conceptSourceName.toUpperCase() + "\n");
	    		
	            if(cSource == null){
	            	fail(output,"Couldn't find concept source with the name " + conceptSourceName);
	            	return;
	            }
	            cSource = Context.getConceptService().purgeConceptSource(cSource);
	            output.println("Just voided concept source " + conceptSourceName + "\n");
	            //}
	            break;
	        default:
        		fail(output, "Invalid action to perform on vocabulary terms (needs to be add, update, or delete)");
        		//return;
        		
        	}
        } catch(Exception e){
         	fail(output, "Can't read csv file " + e.toString());
         	return;
        }
        
        
        output.println("Done");
        
    }
    
    private boolean addConcept(PrintWriter output, String currentLine, 
    		ConceptSource cSource, int columnIDNum, int columnName, 
    		int columnType, int tokenNumber) throws ServletException
    {
    		return this.addConcept(output, currentLine, cSource, columnIDNum, 
    				columnName, columnType, tokenNumber, -1);
    }
    
    /*
     *  Adds concept to dictionary
     *  
     *  @return True if successful, False if error
     */
    private boolean addConcept(PrintWriter output, String currentLine, 
    		ConceptSource cSource, int columnIDNum, int columnName, 
    		int columnType, int tokenNumber, int columnDescription) throws ServletException
    {
    	//Separate line by commas
    	StringTokenizer stokenizer = new StringTokenizer(currentLine,",");
    	String token = "";

        //Initialize variables for concept creation
        String conceptName = "";
        String idNum = "";
        String conceptClassName = "Misc";
        String conceptDescription = "NA";
        
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
    			conceptName = token.toUpperCase().trim();
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
    		else if(tokenNumber == columnDescription)
    		{
    			conceptDescription = token.trim();
    			output.println("Concept description: " + conceptDescription);
    		}
    		tokenNumber++;
    	}
    	// Check for duplicate
    	
    	//Create the concept
    	//Make sure there is a concept name and ID number
    	
    	if(StringUtils.hasText(conceptName)){//.equals("") && !idNum.equals("")){
    		ConceptService cs = Context.getConceptService();
        	Concept c = cs.getConcept(conceptName);
        	if (c != null){
        		log.warn("concept exists: " + conceptName);
        		output.println("Concept " + conceptName + " Exists. UPDATE");
        		// return true to skip existing concepts
        		return true;
        	} else 
        		//Create new concept
        		c = new Concept();
        	
        	
        	//Set concept type to be "text"
        	ConceptDatatype conceptType = cs.getConceptDatatypeByName("Text");
        	c.setDatatype(conceptType);  
        	
        	//Set concept class 
        	ConceptClass conceptClass = cs.getConceptClassByName(conceptClassName);
            c.setConceptClass(conceptClass);
            c.setCreator(Context.getAuthenticatedUser());
            c.setDateCreated(new Date());
            
            // Set the name and description for each locale
            for(Locale locale: Context.getAdministrationService().getAllowedLocales()){
            	//Set concept name
            	ConceptName name = new ConceptName(conceptName,locale);
            	name.setCreator(Context.getAuthenticatedUser());
            	//name.setDateCreated(new Date());
            	//name.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
            	c.setFullySpecifiedName(name);
                c.setPreferredName(name);
            
            	// set the description
            	ConceptDescription description = new ConceptDescription(
            			conceptDescription, locale);
            	description.setCreator(Context.getAuthenticatedUser());
            	description.setDateCreated(new Date());
            	c.addDescription(description);
            }

            // 
            ConceptReferenceTerm conceptTerm = new ConceptReferenceTerm();
            conceptTerm.setConceptSource(cSource);
            conceptTerm.setCode(idNum.toString());
            conceptTerm.setName(conceptName);
            
            //Add mapping to source ID
            ConceptMapType mapType = cs.getConceptMapTypeByName("SAME-AS");
            ConceptMap mapping = new ConceptMap(conceptTerm, mapType);
            c.addConceptMapping(mapping);
            
            try{
            	log.debug("Atempting Save.... " + c.getDisplayString());
            	cs.saveConcept(c);
            	log.debug("Successfully added concept: " + c.getDisplayString()+"\n");
            	//Concept ck = cs.getConcept(conceptName);
            	//.debug("Found " + ck.getName() + ":" + ck.getId()+ ":"+ck.getFullySpecifiedName(locale));
            	return true;
            } catch(Exception e){
            	fail(output, "Can't save concept " + e.getMessage(),e);
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
    
    public static final class Form{
    	private Form(){}

		public static final String ACTION = "action";
		public static final String ACTION_ADD = "add";
		public static final String ACTION_UPDATE = "update";
		public static final String ACTION_DELETE = "delete";
		public static final String CSV_FILE = "csvFile";
		public static final String CSV_FILE_NEW = "newCsvFile";
		public static final String CSV_FILE_RETIRED = "retiredCsvFile";
		public static final String CONCEPT_SOURCE_NAME = "conceptSourceName";
		public static final String CONCEPT_SOURCE_CODE = "conceptSourceCode";
		public static final String CONCEPT_SOURCE_DESCRIPTION = "conceptSourceDescription";
		public static final String COLUMN_CONCEPT_NAME = "columnConceptName";
		public static final String COLUMN_CONCEPT_CLASS = "columnConceptClass";
		public static final String COLUMN_ID_NUM = "columnIdNum";
		public static final String COLUMN_CONCEPT_DESCRIPTION = "columnConceptDescription";
		

		public static final String CONCEPT_DIAGNOSIS = "Diagnosis";
		public static final String CONCEPT_DISORDER = "disorder";
		public static final String CONCEPT_FINDING = "finding";
		public static final String CONCEPT_CLASS_MISC = "Misc";
    	
    }
    
    public static enum FormAction{
    	ADD,UPDATE,DELETE;
    }
}




