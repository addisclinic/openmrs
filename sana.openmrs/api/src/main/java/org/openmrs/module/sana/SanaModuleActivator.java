package org.openmrs.module.sana;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleException;
import org.openmrs.module.sana.ModuleConstants.Module;
import org.openmrs.module.sana.ModuleConstants.Property;

/**
 * Activates the Sana module. 
 * 
 * @author Sana Development Team
 *
 */
public class SanaModuleActivator extends BaseModuleActivator {
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * @see org.openmrs.module.Activator#startup()
     */
    public void startup() {
        log.info("Starting " + Module.ID + "module");
        
        AdministrationService as = Context.getAdministrationService();

        // set up requirements
        String pstr = Module.ID +Property.MDS_URI;
        String gp = as.getGlobalProperty(pstr, ""); 
        if ("".equals(gp)) {
            throw new ModuleException("Global property '"+pstr+"' must be defined");
        }
        
    }
    
    /**
     *  @see org.openmrs.module.Activator#shutdown()
     */
    public void shutdown() {
        log.info("Shutting down "+Module.ID+" module");
    }
    
}
