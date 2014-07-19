package org.moca;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.module.ModuleException;

/**
 * Activates the Sana module. 
 * 
 * Note: This module still retatins the Moca name and will eventually be changed
 * to the updated project name, Sana
 * 
 * @author Sana Development Team
 *
 */
public class MocaActivator implements Activator {
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * @see org.openmrs.module.Activator#startup()
     */
    public void startup() {
        log.info("Starting Moca module");
        
        AdministrationService as = Context.getAdministrationService();

        // set up requirements
        String gp = as.getGlobalProperty("moca.mdsUri", ""); 
        if ("".equals(gp)) {
            throw new ModuleException("Global property 'moca.mdsUri' must be defined");
        }
        
    }
    
    /**
     *  @see org.openmrs.module.Activator#shutdown()
     */
    public void shutdown() {
        log.info("Shutting down Moca module");
    }
    
}
