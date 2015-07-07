package org.openmrs.module.sana;

/**
 * Collection of global strings. 
 * <p/>
 * Privileges are accessed through:
 * </code>
 *     String privilege = Constants.PRIV_name;
 *     if(Context.getAuthenticatedUser()hasPrivilege(managePrivi)){ ... }
 * </code>
 * <p/>
 * Properties are accessed through:
 * <code>
 *     String prop = Constants.MODULE_ID + Constants.PROP_name
 *     Context.getAdministrationService().getGlobalProperty(prop));
 * </code>
 * 
 * @author Sana Development Team
 */
public class ModuleConstants {

	public static final String MODULE_ID = "sana";
	public static final String MODULE_PKG = "org.openmrs.module." + MODULE_ID;
	public static final String MODULE_TITLE = "Encounter Queue";
	
	
	// Privileges
	public static final String PRIV_MANAGE_QUEUE = "Manage Encounter Queue";
	public static final String PRIV_VIEW_QUEUE = "View Encounter Queue";
	
	// Properties 
	public static final String PROP_MDS_URI = ".mdsUri";
	public static final String PROP_THUMB_SIZE = ".thumbnail_size";
	public static final String PROP_NOTIFICATION_URL = ".notification_server_url";
	public static final String PROP_EMAIL_URL = ".email_notification_server_url";
	public static final String PROP_DISPLAY_COUNT = ".DefaultDisplayCount";
	public static final String PROP_MAX_QUEUE_ITEMS = ".maxQueueItemListSize";
	public static final String PROP_ALLOW_CONCEPT_CREATE = ".allowUserConceptCreation";
	
	// Path
	public static final String FORM_PATH = "/module/sana/";
	
	/**
    *
    * @author Sana Development Team
    */
   public static final class Module {
       
       public static final String ID = "sana";
       public static final String PKG = "org.openmrs.module." + ID;
       
       private Module(){}
   }
   
   /**
   *
   * @author Sana Development Team
   */
  public static final class Privilege {
      
      // Privileges
      public static final String MANAGE_QUEUE = "Manage Encounter Queue";
      public static final String VIEW_QUEUE = "View Encounter Queue";
      
      private Privilege(){}
  }
   
	/**
	 * Global properties.
	 * 
	 * @author Sana Development Team
	 */
	public static final class Property {
	    
	    public static final String MDS_URI = Module.ID +".mdsUri";
	    public static final String THUMB_SIZE = Module.ID +".thumbnail_size";
	    public static final String NOTIFICATION_URL = Module.ID +".notification_server_url";
	    public static final String EMAIL_URL = Module.ID +".email_notification_server_url";
	    public static final String DISPLAY_COUNT = Module.ID +".DefaultDisplayCount";
	    public static final String MAX_QUEUE_ITEMS = Module.ID +".maxQueueItemListSize";
	    public static final String ALLOW_CONCEPT_CREATE = Module.ID +".allowUserConceptCreation";
	    public static final String DATE_FORMAT = Module.ID + ".date_format";
	    private Property(){}
	}
}
