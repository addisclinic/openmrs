package org.moca.queue.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.openmrs.api.context.Context;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * Utility class for sending notifications and returning a reply in the 
 * Sana dispatch layer
 * 
 * @author Sana Development Team
 *
 */
public class MDSNotificationReply {
	private static final long serialVersionUID = -569574324L;
	private static Log log = LogFactory.getLog(MDSNotificationReply.class);
   
	/**
	 * Sends an email message which will be dispatched by an upstream dispatch
	 * server.
	 * 
	 * @param recipientAddresses One or more recipients
	 * @param caseIdentifier An encounter related to the message 
	 * @param patientIdentifier A patient whonm the message is about
	 * @param message The message body
	 * @return true if successful
	 * @throws Exception
	 */
	public static boolean sendEmail(String recipientAddresses, 
			String caseIdentifier, String patientId, String subject, 
			String message) throws Exception
	{
		boolean result = false;
    	if(patientId != null && message != null && !message.equals("")) {
			String emailUrl = Context.getAdministrationService()
				.getGlobalProperty("moca.email_notification_server_url");
			if(emailUrl == null || emailUrl.equals("")) {
				throw new ServletException("FAIL: POST email: " 
						+ "moca.email_server_url  is not set");
			}
			HttpPost post = new HttpPost(emailUrl);
			List<NameValuePair> fields = new ArrayList<NameValuePair>();
			fields.add(new BasicNameValuePair("emailAddresses", 
					recipientAddresses));
    		fields.add(new BasicNameValuePair("caseIdentifier", 
    				caseIdentifier));
    		fields.add(new BasicNameValuePair("patientIdentifier", 
    				patientId));
    		fields.add(new BasicNameValuePair("subject", subject));
			fields.add(new BasicNameValuePair("notificationText", message));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(fields, 
					"UTF-8");
			post.setEntity(formEntity);

    		// Execute and read the response
    		HttpClient c = new DefaultHttpClient(); 
    		HttpResponse r = c.execute(post);
    		HttpEntity responseEntity = r.getEntity();
    		if (responseEntity != null) {
    			InputStream is = responseEntity.getContent();
    			StringBuffer sb = new StringBuffer();
    			try {
    				BufferedReader reader = new BufferedReader(
    						new InputStreamReader(is));
    				String line = null;
    				while((line = reader.readLine()) != null) {
    					sb.append(line);
    				}
    				reader.readLine();
    			} catch (IOException ex) {
    				
    			} catch (RuntimeException ex) {
    				
    			} finally {
    				is.close();
    			}
    			//Parse response
    			String response = sb.toString();
    			Gson g = new Gson();
    			try {
    				MDSRequestResponse mdsResponse = g.fromJson(response, 
    						MDSRequestResponse.class);
    				result = mdsResponse.succeeded();
    			} catch (JsonParseException e) {
    				
    			}
    		}
    		c.getConnectionManager().shutdown();
    	}
    	return result;
	}
	
	/**
	 * Sends an SMS message to an upstream dispatch server
	 * 
	 * @param phoneNumber the recipient
	 * @param caseIdentifier An encounter related to the message 
	 * @param patientIdentifier A patient whonm the message is about
	 * @param message The message body
	 * @return true if successful
	 * @throws Exception
	 */
	public static boolean sendSMS(String phoneNumber, String caseIdentifier, 
			String patientIdentifier, String message) throws Exception 
	{
		boolean result = false;
    	if(patientIdentifier != null && message != null && !message.equals("")) 
    	{
			String notificationUrl = Context.getAdministrationService()
							.getGlobalProperty("moca.notification_server_url");
			if(notificationUrl == null || notificationUrl.equals("")) {
				throw new ServletException("Failed to POST message because "
						+ "moca.notification_server_url is not set");
			}

			HttpPost post = new HttpPost(notificationUrl);
			List<NameValuePair> fields = new ArrayList<NameValuePair>();
			fields.add(new BasicNameValuePair("notificationText", 
					message));
			fields.add(new BasicNameValuePair("caseIdentifier", 
					caseIdentifier));
			fields.add(new BasicNameValuePair("patientIdentifier", 
					patientIdentifier));
			fields.add(new BasicNameValuePair("phoneIdentifier", phoneNumber));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(fields, 
					"UTF-8");
			post.setEntity(formEntity);

			// Execute and read the response
			HttpClient c = new DefaultHttpClient(); 
			HttpResponse r = c.execute(post);
			HttpEntity responseEntity = r.getEntity();

			if (responseEntity != null) {
				InputStream is = responseEntity.getContent();
				StringBuffer sb = new StringBuffer();
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));
					String line = null;
					while((line = reader.readLine()) != null) {
						sb.append(line);
					}
					reader.readLine();
				} catch (IOException ex) {
					log.error("IOException " + ex.toString());
				} catch (RuntimeException ex) {
					log.error("RuntimeException " + ex.toString());
				} finally {
					is.close();
				}
				// Parse the response
				String response = sb.toString();
				Gson g = new Gson();
				try {
					MDSRequestResponse mdsResponse = g.fromJson(response, 
							MDSRequestResponse.class);
					result = mdsResponse.succeeded();
				} catch (JsonParseException e) {
					log.error("Send SMS - Json Parse Exception");
				}
			}
			c.getConnectionManager().shutdown();
    	}
    	return result;
	}
}
