package org.openmrs.module.sana.queue.handler;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;                                                                            
import org.openmrs.annotation.Handler; 
import org.openmrs.api.context.Context;
import org.openmrs.api.handler.SaveHandler;
import org.openmrs.module.sana.queue.QueueItem;
import org.springframework.stereotype.Component;

/**
 * Pre-interceptor for cascading saves to the encounter.
 * 
 * @author Sana Development
 *
 */
@Component
@Handler(supports = QueueItem.class)
public class QueueItemSaveHandler implements SaveHandler<QueueItem>{

	private static final Log log = LogFactory.getLog(QueueItemSaveHandler.class);

	public void handle(QueueItem object, User creator, Date dateCreated,
			String other) {
			log.debug("Saving queue item.");
			Context.getEncounterService().saveEncounter(object.getEncounter());
	}

}
