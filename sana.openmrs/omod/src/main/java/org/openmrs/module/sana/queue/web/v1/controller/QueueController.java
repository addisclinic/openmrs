package org.openmrs.module.sana.queue.web.v1.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.print.DocFlavor.STRING;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.openmrs.module.sana.queue.QueueItemXml;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.ModuleConstants;
import org.openmrs.module.sana.ModuleConstants.Module;
import org.openmrs.module.sana.ModuleConstants.Property;
import org.openmrs.module.sana.queue.*;
import org.openmrs.module.sana.queue.web.resource.v1.QueueItemResource;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.obs.ComplexData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

/**
 * This controller handles the logic which backs the /web/module/queue.jsp page
 * and is tied to that jsp page by the RequestMapping annotation.
 *
 * @author Sana Development Team
 *
 */
@Controller
@RequestMapping(value = "module/"+Module.ID+"/queue/v1/")
public class QueueController {

    
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    /** Success form view name */
    private final String HTML_SUCCESS_VIEW = "/module/sana/queue/v1/queue";
    private final String JSON_SUCCESS_VIEW = "/module/sana/queue/v1/jsonformat";
    private final String XML_SUCCESS_VIEW = "/module/sana/queue/v1/xmlformat";

    public int totalhits = 0;

    protected QueueItemResource getResource() {
        QueueItemResource q = new QueueItemResource();
        return q;
    }

    @RequestMapping(value = "queue.form")
    public ModelAndView onRequest(HttpServletRequest request, 
        HttpServletResponse response) 
    {
        log.warn("In Method: onRequest()");
        return onRequestInternal(request, response, QueueItemStatus.NEW);
    }

    @RequestMapping(value = "queueClosed.form")
    public ModelAndView onClosedRequest(HttpServletRequest request, 
        HttpServletResponse response) 
    {
        log.warn("In Method: onClosedRequest()");
        return onRequestInternal(request, response, QueueItemStatus.CLOSED);
    }

    @RequestMapping(value = "queueDeferred.form")
    public ModelAndView onDeferredRequest(HttpServletRequest request, 
        HttpServletResponse response) 
    {
        log.warn("In Method: onDeferredRequest()");
        return onRequestInternal(request, response, QueueItemStatus.DEFERRED);
    }
    
    public static final String ORDER_BY = "hidsortname";
    public static final String LIMIT = "limit";
    public static final String START = "gotopagename";
    public static final String PREV = "hidprevname";
    public static final String NEXT = "gotopagename";
    public static final String ARCHIVED = "optionarchive";
    public static final String PROCEDURE = "comboPro";
    public static final String PROCEDURE_SEARCH = "proname";
    public static final String FORMAT = "disformate";

    public static final String OBJECTS = "queueItems";
    public static final String PROCEDURES = "procedures";
    public static final String DATES = "dates";
    public static final String SIZE = "queuesize";
    public static final String MAX_SIZE = "maxsize";
    public static final String PAGES = "count";
    public static final String PAGE = "start";
    public static final String QUEUE_STATUS = "queuestatus";

    public ModelAndView onRequestInternal(HttpServletRequest request,
        HttpServletResponse response, QueueItemStatus status) 
    {
        log.debug("In Method: onRequestInternal()");
        int defcount = Integer.parseInt(Context.getAdministrationService()
                        .getGlobalProperty(Property.DISPLAY_COUNT));

        log.warn("...Queue status" + status);
        // ------------------------------------------------------------
        // Set max queue size and sort order Lifo or fifo
        int queuelistcount = Integer.parseInt(Context
                .getAdministrationService().getGlobalProperty(
                        Property.MAX_QUEUE_ITEMS));
        Integer queuelistcountObj = new Integer(queuelistcount);
        //String queueSize = request.getParameter(SIZE);
        int sortvalue = 0;

        String sortstring = getParameter(request, ORDER_BY);
        if (!sortstring.isEmpty()) {
            sortvalue = Integer.parseInt(sortstring);
        } else {
            sortvalue = 1;
        }
        String checkProo = "SHOW ALL";
        int iArchieveState = 1;
        int endvalue = queuelistcount;

        // the number per page
        String limit = getParameter(request, LIMIT);
        if (!limit.isEmpty())
            endvalue = Integer.parseInt(limit);
        else
            limit = String.valueOf(queuelistcount);
        int startvalue = 0;

        // the starting page
        String start = getParameter(request, START);
        if (!start.isEmpty()) {
            int substract = Integer.parseInt(limit);
            if (limit.length() > 0) {
                endvalue = Integer.parseInt(limit);
                startvalue = Integer.parseInt(start) * endvalue;
                startvalue = startvalue - endvalue - (substract - 1);
            } else {
                startvalue = Integer.parseInt(start) * queuelistcount;
                startvalue = startvalue - endvalue - (substract - 1);
            }
        } else if (request.getParameter("hidprevname") != null
                && request.getParameter("hidprevname") != "") {
            startvalue = Integer.parseInt(request.getParameter("hidprevname"));
        }
        StringBuffer sbr = null;
        // TODO This should go into a POST call
        if (request.getParameter("subarchivename") != null) // checking archived
                                                            // button is
                                                            // submitted or not.
        {
            this.handleArchiveRequest(request, response);
        }

        String strprocedure = getParameter(request, "proname");
        String checkPro = getParameter(request, "comboPro");
        String checkDate = getParameter(request, "comboDate");
        String strArchieve = getParameter(request, "archive");
        if (!strArchieve.isEmpty()) {
            iArchieveState = 1;
            if (strArchieve.equalsIgnoreCase("Show ALL"))
                iArchieveState = 0;
            else if (strArchieve.equalsIgnoreCase("Show InActive"))
                iArchieveState = 2;
            else if (strArchieve.equalsIgnoreCase("Show Active"))
                iArchieveState = 1;
        }
        // System.out.println("##iArchieveState :"+iArchieveState);
        int strdate = 365;

        int strmonth = 0;

        int days = strdate;
        defcount = days;
        String procedure = null;
        if (strprocedure != null && strprocedure != "")
            procedure = strprocedure;

        if (!checkPro.isEmpty())
            checkProo = checkPro;

        int totalrows = 0;
        QueueItemResource resource = getResource();
        List<QueueItem> items = null;
        switch (status) {
        case CLOSED:
            log.info("...getting CLOSED items");
            items = resource.closed(procedure, defcount, checkProo, checkDate,
                    iArchieveState, startvalue, endvalue, sortvalue);
            totalrows = resource.getProDateRowsClosedCount(procedure, defcount,
                    checkProo, checkDate, iArchieveState, startvalue, endvalue,
                    sortvalue);
            break;
        case DEFERRED:
            log.info("...getting DEFERRED items");
            items = resource.deferred(procedure, defcount, checkProo,
                    checkDate, iArchieveState, startvalue, endvalue, sortvalue);
            totalrows = resource.getProDateRowsDeferredCount(procedure,
                    defcount, checkProo, checkDate, iArchieveState, startvalue,
                    endvalue, sortvalue);
            break;
        default:
            log.info("...getting ALL items");
            items = resource.all(procedure, defcount, checkProo, checkDate,
                    iArchieveState, startvalue, endvalue, sortvalue);
            totalrows = resource.getProDateRowsCount(procedure, defcount,
                    checkProo, checkDate, iArchieveState, startvalue, endvalue,
                    sortvalue);
            break;
        }
        Map<String,Object> map = new HashMap<String,Object>();
        int pageno = 1;
        if (startvalue > 0 && startvalue < endvalue)
            pageno = 2;
        if (startvalue > endvalue) {
            pageno = startvalue / (endvalue - 1);
            pageno = pageno + 1;
        }

        int count = 1;
        while (totalrows / (endvalue) > 0) {
            totalrows = totalrows - endvalue + 1;
            count++;
        }
        
        List<QueueItem> procedurelist = resource.all();
        List<DateItems> dateitems = resource.dateItems();
        
        map.put(OBJECTS, items);
        map.put(PROCEDURES, procedurelist);
        map.put(DATES, dateitems);
        map.put(LIMIT, Integer.parseInt(limit));
        map.put(SIZE, items.size());
        map.put(MAX_SIZE, queuelistcountObj);
        map.put(PAGES, count);
        map.put(PAGE, pageno);
        
        // set the status
        map.put(QUEUE_STATUS, status);
        
        request.setAttribute("proname", "SHOW ALL");
        request.setAttribute("proname", "SHOW ALL");
        log.debug("Returning " + items.size() + " " +status + " queue items");

        if (request.getParameter(FORMAT) == null
                || request.getParameter(FORMAT).equalsIgnoreCase("htmlformat")) {
            // System.out.println("formate:"+request.getParameter("disformate"));

            return new ModelAndView(HTML_SUCCESS_VIEW, "map", map);
        } else if (request.getParameter(FORMAT).equalsIgnoreCase("Jsonformat")) {
            sbr = QueueItemJson.encode(items);
            Map<String,Object> map1 = new HashMap<String,Object>();
            map1.put("sbr", sbr.toString());
            return new ModelAndView(JSON_SUCCESS_VIEW, "sbr", map1);
        }

        else if (request.getParameter(FORMAT).equalsIgnoreCase("Xmlformat")) {

            sbr = QueueItemXml.encode(items);
            PatientResource pr = new PatientResource();
            try {

                PrintWriter pw = response.getWriter();
                pr.printPatientList(pw, sbr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<String,Object> map1 = new HashMap<String,Object>();

            map1.put("sbr", sbr);
            return new ModelAndView(XML_SUCCESS_VIEW, "sbr", map1);
        }
        //System.gc();
        return null;

    }
    
    public ModelAndView handleArchiveRequest(HttpServletRequest request,
            HttpServletResponse response) {

        String checkedids = request.getParameter("chklist");
        if (checkedids != null && checkedids != "") {
            String strcheckedlist = checkedids;
            String[] temp;
            temp = strcheckedlist.split(";");
            int intarr[] = new int[temp.length];
            for (int i = 0; i < temp.length; i++) {
                intarr[i] = Integer.parseInt(temp[i]);
            }
            QueueItemService queueService = Context
                    .getService(QueueItemService.class);
            queueService.getUnArchivedRows(intarr);// function for "Archiving"
                                                    // intarr[] values.
        }
        return null;
    }
    

    
    /**
     * Returns a request parameter or default value if the result of 
     * HttpServletRequest.getParameter(String) returns null or an empty String
     * 
     * @param request the source of the parameter
     * @param name the name of the parameter
     * @param defaultValue the value to return if the request parameter is null
     * @return a request parameter or default value
     */
    String getParamOrDefault(HttpServletRequest request, String name, 
            String defaultValue)
    {
        String value = request.getParameter(name);
        value = (value == null || value == "")? defaultValue: value;
        return value;
    }
    
    /**
     * Returns a request parameter or empty string "". Will not return null.
     * 
     * @param request the source of the parameter
     * @param name the parameter name
     * @return the parameter value or ""
     */
    String getParameter(HttpServletRequest request, String name){
        return getParamOrDefault(request, name, "");
    }
    
    //----------------------- Test methods ------------------------------------
    private Patient pickRandomPatient() {
        PatientService pservice = (PatientService)Context.getService(
                PatientService.class);
        //List<PatientIdentifier> identifiers = pservice.getPatientIdentifiers(null, new Vector<PatientIdentifierType>(), null, null, null);
        /*
        if(identifiers.size() > 0) {
            int random = (int)Math.round(Math.random()*identifiers.size());
            PatientIdentifier id = identifiers.get(random);
            Set<Patient> patients = pservice.getPatientsByIdentifier(id.getIdentifier(), false);
            if(patients.size() > 0)
                return (Patient)patients.toArray()[0];
        } */

        return pservice.getPatient(2); //TODO
    }
    
    private Obs createFakeObservation(Patient p) {
        ObsService oservice = (ObsService)Context.getService(ObsService.class);
        
        LocationService lservice = (LocationService)Context.getService(
                LocationService.class);
        ConceptService cservice = Context.getConceptService();
        
        Obs o = new Obs();
        o.setPerson(p);
        o.setLocation(lservice.getDefaultLocation());
        o.setConcept(cservice.getConcept(1119));
        o.setDateCreated(new Date());
        o.setObsDatetime(new Date());
        o.setCreator(Context.getAuthenticatedUser());
        
        return o;
    }
    
    private Obs createFakeComplexObservation(Patient p) throws IOException {
        ObsService oservice = (ObsService)Context.getService(ObsService.class);
        
        LocationService lservice = (LocationService)Context.getService(
                LocationService.class);
        ConceptService cservice = Context.getConceptService();
        
        Obs o = new Obs();
        o.setPerson(p);
        o.setLocation(lservice.getDefaultLocation());
        o.setConcept(cservice.getConcept(6099));
        o.setDateCreated(new Date());
        o.setObsDatetime(new Date());
        o.setCreator(Context.getAuthenticatedUser());

        BufferedImage img = ImageIO.read(new File("/home/rryan/Pictures/rryan-head.jpg"));
        ComplexData complexData = new ComplexData("test-image.jpg", img);
        o.setComplexData(complexData);
        
        
        return o;
    }
    private void createFakeQueueItem() throws IOException {
        QueueItemService qservice = (QueueItemService)Context.getService(
                QueueItemService.class);
        
        Patient p = pickRandomPatient();
        Encounter e = new Encounter();
        e.setEncounterDatetime(new Date());
        e.setDateCreated(new Date());
        e.setLocation(((LocationService)Context.getService(
                LocationService.class)).getDefaultLocation());
        e.setPatient(p);
        e.setCreator(Context.getAuthenticatedUser());
        e.setForm(Context.getFormService().getAllForms().get(0));
        e.setEncounterType(Context.getEncounterService()
                .getAllEncounterTypes().get(0));
        e.setProvider(Context.getAuthenticatedUser());
        
        Context.getEncounterService().saveEncounter(e);
        
        Obs o = createFakeObservation(p);
        Context.getObsService().saveObs(o, "");

        Obs o2 = createFakeComplexObservation(p);
        Context.getObsService().saveObs(o2, "");

        e.addObs(o);
        e.addObs(o2);
        Context.getEncounterService().saveEncounter(e);
        
        if(p != null) {
            QueueItem q = new QueueItem();
            q.setVisible(true);
            q.setEncounter(e);
            q.setDateCreated(new Date());
            qservice.createQueueItem(q);
        }
        
    }
}



