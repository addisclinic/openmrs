package org.openmrs.module.sana.queue.web.controller;

import org.openmrs.module.sana.ModuleConstants.Module;
import org.openmrs.module.sana.queue.web.resource.v1.QueueItemResource;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/rest/"+Module.ID+"/queue/queueitem")
public class QueueItemController extends BaseRestController{
	
	@Override
    public String getNamespace() {
        return "v1/" +Module.ID+"/queue/queueitem" ;
    }
	
}
