package org.openmrs.module.sana.queue.web.resource;

import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.module.sana.queue.QueueItemList;
import org.openmrs.module.sana.queue.web.resource.v1.QueueItemResource;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * @author Sana Development
 *
 */
public class QueueResource {

	public DelegatingResourceDescription getRepresentationDescription(
			Representation arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public QueueItemList newDelegate() {
		return new QueueItemList();
	}

	public PageableResult getAll(RequestContext context) {
		QueueItemResource qr = new QueueItemResource();
		return qr.doGetAll(context);
	}
	
	public QueueItemList save(QueueItemList arg0) {
		QueueItemResource qr = new QueueItemResource();
		QueueItemList list = new QueueItemList(arg0.size());
		for(QueueItem q: arg0){
			list.add(qr.save(q));
		}
		return list;
	}

	protected void delete(QueueItemList arg0, String arg1, RequestContext arg2)
			throws ResponseException {

		QueueItemResource qr = new QueueItemResource();
		for(QueueItem q: arg0){
			qr.purge(q, arg2);
		}
	}

	public void purge(QueueItemList arg0, RequestContext arg1)
			throws ResponseException {
		QueueItemResource qr = new QueueItemResource();
		for(QueueItem q: arg0){
			qr.purge(q, arg1);
		}		
	}

}
