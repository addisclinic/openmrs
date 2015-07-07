package org.openmrs.module.sana.queue.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Integer;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.Encounter;

public class EncounterServlet extends HttpServlet {
    private static final long serialVersionUID = -56973436229838856L;
    private Log log = LogFactory.getLog(this.getClass());
    
    @Override 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("doGet");
        
        String encounterID = request.getParameter("encounterID");
        Encounter e = Context.getEncounterService().getEncounter(new Integer(encounterID));
        String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        s += "<encounter patientID=\"" + e.getPatientId().toString() + "\" patientFirst=\"" + e.getPatient().getGivenName() + "\" patientLast=\"" + e.getPatient().getFamilyName() + "\">";
        log.debug("Encounter: %s, " + encounterID);
        for (Obs o : e.getAllObs())
        {
        	if(o.isComplex()){

            	String type = o.getValueComplex();
        		log.debug("type " + o.getValueComplex() + ", MIME: " + type);
        		//Audio file
        		//if(o.getValueText().equals("SOUND")){
        		if(MIMEUtil.isAudio(o)){	
        			s += "<genericFile type=\"audio\" obsID=\"" + o.getObsId().toString() + "\" summary=\"\"/>";        			
            		log.info("audio obs " + o.toString() + " " + o.isComplex());
        		}
        		//Video file
        		//else if(o.getValueText().equals("VIDEO")){
        		else if(MIMEUtil.isVideo(o)){	
        			s += "<genericFile type=\"video\" obsID=\"" + o.getObsId().toString() + "\" summary=\"\"/>";        			
            		log.info("video obs " + o.toString() + " " + o.isComplex());
        		}
        		//Image
        		//else{
        		else if(MIMEUtil.isImage(o)){	
        			s += "<genericFile type=\"image\" obsID=\"" + o.getObsId().toString() + "\" summary=\"\"/>";        			
            		log.info("image obs " + o.toString() + " " + o.isComplex());
        		}
        		//ConceptComplex c = (ConceptComplex)o.getConcept();
        		//if (c.getHandler().equals("ImageHandler")) {
        		//	
        		//}
        	}
        }
        
        s += "</encounter>";
        log.info("s " + s);
        PrintWriter writer = response.getWriter();
        writer.print(s);
  
    }
    

    
    public static class MIMEUtil{
    	final static String[] AUDIO = {"wav", "mp3" };
    	final static String[] IMAGE = {"jpg", "bmp"};
    	final static String[] VIDEO = {"mpg", "flv"};
    	
        public static String getMimeType(Obs o){
        	MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        	String uri = o.getValueComplex().split("|")[1].trim();
        	String mimeType = mimeTypesMap.getContentType(uri);
    		return mimeType;
        }
        
        public static boolean isAudio(Obs o){
        	String type = o.getValueComplex();
        	if(type.indexOf("audio") > -1){
        		return true;
        	} else 
        		return false;
        }
    	
        public static boolean isImage(Obs o){
        	String type = o.getValueComplex();
        	if(type.indexOf("image") > -1){
        		return true;
        	} else 
        		return false;
        	
        }
        
        public static boolean isVideo(Obs o){
        	String type = o.getValueComplex();
        	if(type.indexOf("video") > -1){
        		return true;
        	} else 
        		return false;
        	
        }
        
    }
}
