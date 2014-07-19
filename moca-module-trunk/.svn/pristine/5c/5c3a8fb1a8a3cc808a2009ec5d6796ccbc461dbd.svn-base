package org.moca.extension;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * Creates the entries for the module on the Administration page
 * 
 * @author Sana Development Team
 */
public class AdminList extends AdministrationSectionExt {

    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }
    
    /**
     * The String title which will be placed in the Adminstration section
     */
    public String getTitle() {
        return "moca.title";
    }
    
    /**
     * Links to sub entries in the admin section
     */
    public Map<String, String> getLinks() {
        
        Map<String, String> map = new HashMap<String, String>();
        
        map.put("module/moca/queue.form", "moca.view");
        map.put("module/moca/lexicon.form", "moca.lexicon");
        
        return map;
    }
}
