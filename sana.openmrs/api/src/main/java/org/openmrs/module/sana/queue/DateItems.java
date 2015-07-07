package org.openmrs.module.sana.queue;

/**
 * POJO class for date_items table used for displaying date combobox:
 * 
 * @author Sana Development Team
 *
 */
public class DateItems
{
	 int id;
	 public String  textName;
	 int days;
	 int months;
	 
	 /**
	  * Sets an object id
	  * @param id the new id
	  */
	 public void setId(int id)
	 {
		 this.id=id;
	 }
	
	 /**
	  * Gets the object id
	  * @return
	  */
	 public int getId()
	 {
		 return id;
	 }
	 
	 /**
	  * Sets the text name for the object
	  * @param textname
	  */
	 public void setTextName(String textname)
	 {
		 this.textName = textname;
	 }
	 
	 /**
	  * Gets the object text name 
	  * @return
	  */
	 public String getTextName()
	 {
		 return textName;
	 }
	 
	 /**
	  * Gets the number of days represented by this object
	  * @return
	  */
	 public int getDays()
	 {
		 return days;
	 }
	 
	 /**
	  * Sets the number of days
	  * @param days the new number of days
	  */
	 public void setDays(int days)
	 {
		 this.days = days;
	 }
	 
	 /**
	  * Sets the number of months represented by this object
	  * @param months the new number of months
	  */
	 public void setMonths(int months)
	 {
		 this.months = months;
	 }
	 
	 /**
	  * Gets the number of months represented by this object
	  * @return a number of months
	  */
	 public int getMonths()
	 {
		 return months;
	 }
}