/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.sana.queue.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.sana.ModuleConstants.Module;
import org.openmrs.module.sana.ModuleConstants.Privilege;
import org.openmrs.module.web.extension.LinkExt;

/**
 * Extends the OpenMRS menu bar with direct links to the Sana Queue.
 * 
 * This extension is enabled by defining it in the /metadata/config.xml file.
 * 
 * @author Sana Development Team
 */
public class GutterExtension extends LinkExt {

    /**
     * @see org.openmrs.module.extension.Extension#getMediaType()
     */
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }

    /**
     * Th visible label
     * 
     * @return The message code of the label of this link
     */
    public String getLabel(){
    	 return Module.ID+".title";
     }
	
     /** 
      * The hyperlink connected to the label.
      * 
      * @return The url that this link should go to 
      */
     public String getUrl(){
    	 return "module/"+Module.ID+"/queue/v1/queue.form";
     }

    /**
     * The privilege the user must have to see this link
     * 
     * @return The String value of the privilege
     */
    public String getRequiredPrivilege(){
    	return Privilege.VIEW_QUEUE;
    }



}
