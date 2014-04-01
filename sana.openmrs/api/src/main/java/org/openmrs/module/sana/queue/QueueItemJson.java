package org.openmrs.module.sana.queue;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PersonName;

import org.openmrs.module.sana.queue.QueueItem;
import org.openmrs.web.WebUtil;

/**
 * Representation of an item in the Queue as a JSON encoded string.
 * 
 * @author Sana Development Team
 *
 */
public class QueueItemJson {
    /**
     * Encodes a list of queue items into JSON text
     * @param items
     * @return
     */
    public static StringBuffer encode(List items) {
            StringBuffer json = new StringBuffer();
            Iterator itr = items.iterator();
            Calendar current=Calendar.getInstance();
            current.get(Calendar.DATE);
            if(items.size() <= 0)
                    json.append("{}");
            else {
                    while(itr.hasNext()) {
                            QueueItem it=(QueueItem)itr.next();
                            PersonName name=it.patient.getPersonName();
                            json.append("{");

                            json.append("\"status\":\"").append(it.status).append("\",");
                            json.append("\"patientIdentifier\":\"").append(it.patient.getPatientIdentifier()).append("\",");
                            json.append("\"Procedure\":\"").append(it.getProcedureTitle()).append("\",");
                            json.append("\"age\":\"").append(it.patient.getAge(null)).append("\",");
                            json.append("\"sex\":\"").append(it.patient.getGender()).append("\",");
                            json.append("\"dateCreated\":\"").append(it.dateCreated).append("\",");
                            json.append("\"name\":{");
                            boolean hasContent = addOptionalElement(json, "prefix", name.getPrefix());
                            hasContent |= addOptionalElement(json, "givenName", name.getGivenName());
                            hasContent |= addOptionalElement(json, "familyName", name.getFamilyName());
                            if (hasContent)
                                    json.deleteCharAt(json.length()-1); // delete last comma if at least something was added
                            json.append("}");
                            json.append(",\"patientId\":\"").append(it.patient.getPatientId()).append("\",");
                            json.append("\"encounterId\":\"").append(it.encounter.getEncounterId()).append("\"");
                            json.append("},");
                    }
                    json.deleteCharAt(json.length()-1);
            }
            return json;
    }


    private static boolean addOptionalElement(StringBuffer json, 
                    String attrName, String value) 
    {
            if (value == null || "".equals(value))
                    return false;
            json.append("\"");
            json.append(attrName);
            json.append("\":\"");
            json.append(WebUtil.escapeQuotes(value));
            json.append("\",");
            return true;
    }
}



