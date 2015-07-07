package org.openmrs.module.sana.mediaviewer.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;


public class UploadToEncounterServlet extends HttpServlet {

    private static final long serialVersionUID = 4847187771370210197L;
    private Log log = LogFactory.getLog(this.getClass());
    
    private void fail(HttpServletRequest request, HttpServletResponse response,
    		String message) 
    {

        //response.setContentType("text/html"); 
        try{
        	//get the PrintWriter object to write the html page
        	response.setContentType("text/html");
        	PrintWriter out = response.getWriter();
            // write the HTML header
            log.error("Upload to encounter failed " + message);
            out.print("Upload to encounter failed " + message);
            // response.sendRedirect("queue.form");
        } catch(IOException e){
        	log.error(e.toString());
        	return;
        }
       
    }
    
    private Encounter getEncounter(String encounterID) {
    	
        if(encounterID == null || "".equals(encounterID)) 
            return null;
        try{
        	User u = Context.getAuthenticatedUser();
        	Encounter e = Context.getEncounterService().getEncounter(new Integer(encounterID));
            return e;
        } catch(Exception er){
        	log.error("error: " + er.toString());
        }
        return null;
        
    }
    
    @SuppressWarnings({ "unchecked", "deprecation" })
    private List<FileItem> getUploadedFiles(HttpServletRequest request) 
    		throws FileUploadException 
    {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        if(!request.getHeader("content-type").contains("application/x-www-form-urlencoded"))        
            return (List<FileItem>)upload.parseRequest(request);
        return new ArrayList<FileItem>();
    }

    @Override
    protected void doPost(HttpServletRequest request, 
    		HttpServletResponse response) throws ServletException, IOException 
    {

        String encounterID = request.getParameter("encounterId");
        
        if(encounterID == null) { 
            fail(request,response, "Can't have null encounter ID number");
            return;
        }
        
        Encounter enc = getEncounter(encounterID);
            
        if(enc == null) {
            fail(request, response, "Couldn't find encounter with identifier " + encounterID);
            return;
        } else{
        	log.error("encounter found");
        }
           
        //Get uploaded files
        List<FileItem> files;
        try {
            files = getUploadedFiles(request);
        } catch(FileUploadException e) {
            fail(request, response, "Error in parsing uploaded files: " + e.toString());
            return;
        }
        
        // Process the uploaded items
        Iterator iter = files.iterator();
        FileItem textItem = null;
        
        while (iter.hasNext()) {

        	FileItem item = (FileItem) iter.next();

            if (item.isFormField()) {
            	
            	String name = item.getFieldName();
                String value = item.getString();
            
            }
        }
        
        for(FileItem f : files) {
            log.info("Got file fieldname:" + f.getFieldName() 
            		+ " content type: " + f.getContentType() 
            		+ " name:" + f.getName() + " size:" 
            		+ f.getSize());
        }
       
        //Process upload
        try{
        	processUpload(enc.getPatient(), enc, files);
        }
        catch(Exception err){
        	fail(request, response, err.getMessage());
        	return;
        }
        response.sendRedirect(request.getContextPath() 
        		+ "/admin/encounters/encounter.form?encounterId=" +encounterID);
                   
    }
    
    void processUpload(Patient patient, Encounter e, List<FileItem> files) throws Exception {
        
    	for(FileItem f:files){
    		//Filter out the blank field items
    		if(f.getSize() > 0){
    			// Get Concept for Type/ID
    	        Concept c = getConcept(f.getContentType());
    	        
    	        if(c == null){
    	        	throw new Exception("Concepts PICTURE, AUDIO, and VIDEO must be added to dictionary.");
    	        }
    	        
    	        if(c.isComplex()) {
    	        	Obs o = makeObs(patient, c, "Media File: " + f.getName(), f);
    	        	
                    // Add obs to encounter
    	        	e.addObs(o);
    	            Context.getObsService().saveObs(o, "");
    	        }
    		}
    	}
    	Context.getEncounterService().saveEncounter(e);
    }
    
    private Concept getConcept(String fileType){
    	
    	log.info("file type: " + fileType);
    	
    	if(fileType.equals("image/png") || fileType.equals("image/gif") || fileType.equals("image/jpeg")) {
    		return Context.getConceptService().getConcept("PICTURE");
    	}
    	else if(fileType.equals("video/x-flv") || fileType.equals("video/mp4")){
    		return Context.getConceptService().getConcept("VIDEO");
    	}
        else if(fileType.equals("audio/mpeg") || fileType.equals("audio/3gpp") 
                || fileType.equals("audio/mp3")){ // audio/mpeg
        	return Context.getConceptService().getConcept("SOUND");
        } else{
            return Context.getConceptService().getConcept("CSV");
    }
    }
    
    private Obs makeObs(Patient p, Concept c, String value, FileItem f) throws IOException {
        Obs o = new Obs();
        
        o.setCreator(Context.getAuthenticatedUser());
        o.setDateCreated(new Date());
        o.setObsDatetime(new Date());
        o.setPerson(p);
        o.setConcept(c);
        
        //Set content type: image, audio, video
        String fileType = f.getContentType();
        String contentType = "";
        
        if(fileType.equals("image/png") || fileType.equals("image/gif") || fileType.equals("image/jpeg")) {
        	contentType = "image";
    	}
    	else if(fileType.equals("audio/mpeg") || fileType.equals("audio/3gpp")){
    		contentType = "audio";
    	}else if(fileType.equals("video/x-flv") || fileType.equals("video/mp4")){
    		contentType = "video";
    	}
        
        o.setValueText(contentType);
        
        Location location = Context.getLocationService().getLocation("unknown location");
        if (location == null) {
            location = Context.getLocationService().getLocation(1);
        }

        o.setLocation(location);
        
        if(f != null) {
            ComplexData cd = new ComplexData(f.getName(), f.getInputStream());
            o.setComplexData(cd);
        }
        
        return o;
    }
   
}


