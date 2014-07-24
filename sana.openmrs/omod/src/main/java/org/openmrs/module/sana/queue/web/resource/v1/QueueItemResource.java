package org.openmrs.module.sana.queue.web.resource.v1;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.sana.queue.DateItems;
import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = "queueitem", supportedClass = QueueItem.class, supportedOpenmrsVersions = { "1.9" })
@Handler(supports = { QueueItem.class }, order=0)
public class QueueItemResource extends DataDelegatingCrudResource<QueueItem>{

    
    public enum VisibleState{
        
        SHOW_ALL,
        SHOW_INACTIVE,
        SHOW_ACTIVE;
        
        public VisibleState fromString(String string){
            String s = string.trim().toUpperCase().replaceAll(" ", "_");
            return VisibleState.valueOf(s);
        }
        
    }
    
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    public int totalhits = 0;

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     * @should create an encounter type
     */
    //@Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        
        description.addRequiredProperty("encounter");
        description.addProperty("observer");
        description.addProperty("status");
        return description;
    }   
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
     */
    public DelegatingResourceDescription getRepresentationDescription(
            Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            
            description.addProperty("voided");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("voided");
            description.addProperty("auditInfo", findMethod("getAuditInfo"));
            description.addSelfLink();
            return description;
        }
        return null;
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
     */
    public QueueItem newDelegate() {
        QueueItem queueItem = new QueueItem();
        // TODO Any post create processing if needed
        return queueItem;
    }
    
    /**
     * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(org.openmrs.Encounter)
     */
    public QueueItem save(QueueItem queueItem) {
        return getService().saveQueueItem(queueItem);
    }

    @Override
    protected void delete(QueueItem queueItem, String reason, RequestContext arg2)
            throws ResponseException {
        if (queueItem.isVoided()) {
            // DELETE is idempotent, so we return success here
            return;
        }
        getService().purgeQueueItem(queueItem);
    }

    
    @Override
    public QueueItem getByUniqueId(String uuid) {
        return getService().getQueueItemByUuid(uuid);
    }

    
    @Override
    public void purge(QueueItem queueItem, RequestContext arg1)
            throws ResponseException {
        if (queueItem == null) {
            // DELETE is idempotent, so we return success here
            return;
        }
        getService().purgeQueueItem(queueItem);
    }

    private QueueItemService getService(){
        return Context.getService(QueueItemService.class);
    }

    public List<QueueItem> closed(String procedure,int defcount,
            String checkProo, String checkDate, int iArchieveState,
            int startvalue, int endvalue, int sortvalue){
        QueueItemService queueService = Context.getService(QueueItemService.class);
        int totalrows = queueService.getProDateRowsClosedCount(
                procedure,defcount,checkProo, checkDate, iArchieveState,
                startvalue,endvalue,sortvalue);
        
        //System.out.println("Total Row count:"+totalrows);
        List<QueueItem> items = queueService.getProDateRowsClosed(procedure,defcount,
                checkProo, checkDate, iArchieveState,startvalue,endvalue,
                sortvalue);
        return items;
    }
    
    public List<QueueItem> deferred(String procedure,int defcount,
            String checkProo, String checkDate, int iArchieveState,
            int startvalue, int endvalue, int sortvalue){
        QueueItemService queueService = Context.getService(QueueItemService.class);
        int totalrows = queueService.getProDateRowsDeferredCount(
                procedure,defcount,checkProo, checkDate, iArchieveState,
                startvalue,endvalue,sortvalue);
        log.debug("Total rows:" + totalrows);
        //System.out.println("Total Row count:"+totalrows);
        List<QueueItem> items = queueService.getProDateRowsDeferred(procedure,defcount,
                checkProo, checkDate, iArchieveState,startvalue,endvalue,
                sortvalue);
        return items;
    }
    
    public List<QueueItem> all(){
        return getService().getProcedureAllRows();
    }
    
    public List<QueueItem> all(String procedure,int defcount,
            String checkProo, String checkDate, int iArchieveState,
            int startvalue, int endvalue, int sortvalue){
        QueueItemService queueService = Context.getService(QueueItemService.class);
        int totalrows = queueService.getProDateRowsCount(
                procedure,defcount,checkProo, checkDate, iArchieveState,
                startvalue,endvalue,sortvalue);
        log.debug("Total rows:" + totalrows);
        List<QueueItem> items = queueService.getProDateRows(procedure,defcount,
                checkProo, checkDate, iArchieveState,startvalue,endvalue,
                sortvalue);
        log.debug("Returning queue items, count: " + items);
        return items;
    }
    
    public PageableResult doGetAll(RequestContext context){
        return null;
    }
    
    public List<DateItems> dateItems(){
        return getService().getDateMonths();
    }
    
    public int getProDateRowsDeferredCount(String procedure,int defcount,
            String checkProo, String checkDate, int iArchieveState,
            int startvalue, int endvalue, int sortvalue){
        return getService().getProDateRowsDeferredCount(
                procedure,defcount,checkProo, checkDate, iArchieveState,
                startvalue,endvalue,sortvalue);
    
    }
    public int getProDateRowsClosedCount(String procedure,int defcount,
            String checkProo, String checkDate, int iArchieveState,
            int startvalue, int endvalue, int sortvalue){
        return getService().getProDateRowsClosedCount(
                procedure,defcount,checkProo, checkDate, iArchieveState,
                startvalue,endvalue,sortvalue);
    
    }

    public int getProDateRowsCount(String procedure,int defcount,
            String checkProo, String checkDate, int iArchieveState,
            int startvalue, int endvalue, int sortvalue){
        return getService().getProDateRowsCount(
                procedure,defcount,checkProo, checkDate, iArchieveState,
                startvalue,endvalue,sortvalue);
    
    }
}
