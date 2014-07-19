package org.moca.queue;

import java.util.Calendar;

import java.util.Iterator;
import java.util.List;
import org.moca.queue.QueueItem;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.openmrs.PersonName;

/**
 * XML representation of an encounter in the Sana Queue
 * 
 * @author Sana Development Team
 *
 */
public class QueueItemXml {
	
	/**
	 * Takes a list of encounters in the queue and returns them as an xml
	 * encoded string
	 * @param items the list of encounters
	 * @return
	 */
	public static StringBuffer encode(List items)
	{
		StringBuffer xml = new StringBuffer();
    	Iterator itr = items.iterator();
    	Calendar current=Calendar.getInstance();
        current.get(Calendar.DATE);
        xml.append("<QueueItemList version=\"1.0\">");
    	while(itr.hasNext())
    	{
    		//TODO: clean this up and validate 
    		QueueItem it=(QueueItem)itr.next();
    		xml.append("<queueitem>");
    		xml.append("<age>");
    		//xml.append((it.patient.getBirthdate() != null ? dateFormatter.format(it.patient.getBirthdate()) : ""));
    		xml.append(it.patient.getAge(null));
    		xml.append("</age>");
    		/*xml.append("\" birthdateEstimated=\"");
			xml.append(patient.getBirthdateEstimated());*/
    		xml.append("<gender>");
    		xml.append(it.patient.getGender());
    		xml.append("</gender>");

    		/*xml.append("<identifierList>");
			for (PatientIdentifier pid : it.patient.getIdentifiers()) {
				xml.append("<identifier");
				if (pid.isPreferred())
					xml.append(" preferred=\"1\"");
				xml.append(" type=\"");
				xml.append(pid.getIdentifierType().getName());
				// TODO: should encode invalid chars in name
				xml.append("\">");
				xml.append(pid.getIdentifier());
				xml.append("</identifier>");
		}
			xml.append("</identifierList>");*/
    		xml.append("<patientidentifier>");
    		xml.append(it.patient.getPatientIdentifier());
    		xml.append("</patientidentifier>");

    		xml.append("<name>");
    		PersonName name = it.patient.getPersonName();
    		//addOptionalElement(xml, "prefix", name.getPrefix());
    		addOptionalElement(xml, "givenName", name.getGivenName());
    		//addOptionalElement(xml, "middleName", name.getMiddleName());
    		addOptionalElement(xml, "familyName", name.getFamilyName());
    		//addOptionalElement(xml, "familyName2", name.getFamilyName2());
    		addOptionalElement(xml, "degree", name.getDegree());
    		xml.append("</name>");
    		xml.append("<procedure>");
    		xml.append(it.getProcedureTitle());
    		xml.append("</procedure>");
    		xml.append("<datecreated>");
    		xml.append(it.dateCreated);
    		xml.append("</datecreated>");
    		xml.append("<patientid>");
    		xml.append(it.patient.getPatientId());
    		xml.append("</patientid>");
    		xml.append("<encounterId>");
    		xml.append(it.encounter.getEncounterId());
    		xml.append("</encounterId>");
    		xml.append("</queueitem>");
    		/*xml.append("<addressList>");
			for (PersonAddress address : patient.getAddresses()) {
				xml.append("<address");
				if (address.getPreferred())
					xml.append(" preferred=\"1\">");
				else
					xml.append(">");
				addOptionalElement(xml, "address1", address.getAddress1());
				addOptionalElement(xml, "address2", address.getAddress2());
				addOptionalElement(xml, "cityVillage", address.getCityVillage());
					addOptionalElement(xml, "countyDistrict", address
					.getCountyDistrict());
				addOptionalElement(xml, "stateProvince", address.getStateProvince());
				addOptionalElement(xml, "country", address.getCountry());
				xml.append("</address>");
			}
			xml.append("</addressList>");*/

    	}
    	xml.append("</QueueItemList>");
    	return xml;
	}
	
	private static void addOptionalElement(StringBuffer xml, String tag,
			String value) {
		if (value == null)
			return;
		xml.append("<");
		xml.append(tag);
		xml.append(">");
		xml.append(value);
		xml.append("</");
		xml.append(tag);
		xml.append(">");
	}
}


