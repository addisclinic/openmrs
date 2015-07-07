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
package org.openmrs.module.sana.queue.web.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;

public class ComplexObsServlet extends HttpServlet {
	
	private static final long serialVersionUID = -5935229838856L;
    private Log log = LogFactory.getLog(this.getClass());
    
    @Override 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	log.info("doGet");
        
        String obsId = request.getParameter("obsId");
        String type = request.getParameter("obsType");
        String view = request.getParameter("view");
		String viewType = request.getParameter("viewType");
		
		HttpSession session = request.getSession();
		
		if (obsId == null || obsId.length() == 0) {
			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.null");
			return;
		}
		if (!Context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_OBS)) {
			session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Privilege required: " + OpenmrsConstants.PRIV_VIEW_OBS);
			session.setAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR, request.getRequestURI() + "?"
			        + request.getQueryString());
			response.sendRedirect(request.getContextPath() + "/login.htm");
			return;
		}
		
		Obs complexObs = Context.getObsService().getComplexObs(Integer.valueOf(obsId), view);
		ComplexData cd = complexObs.getComplexData();
		Object data = cd.getData();
		
		if ("DOWNLOAD".equals(viewType)) {
			response.setHeader("Content-Disposition", "attachment; filename=" + cd.getTitle());
			response.setHeader("Pragma", "no-cache");
		}
		
		if (data instanceof byte[]) {
			ByteArrayInputStream stream = new ByteArrayInputStream((byte[]) data);
			OpenmrsUtil.copyFile(stream, response.getOutputStream());
		} else if (BufferedImage.class.isAssignableFrom(data.getClass())) {
			BufferedImage img = (BufferedImage) data;
			String[] parts = cd.getTitle().split(".");
			String extension = "jpg";
			if (parts.length > 0) {
				extension = parts[parts.length - 1];
			}
			
			ImageIO.write(img, extension, response.getOutputStream());
		} else if (InputStream.class.isAssignableFrom(data.getClass())) {
			InputStream stream = (InputStream) data;
			OpenmrsUtil.copyFile(stream, response.getOutputStream());
			stream.close();
		} else {
			//throw new ServletException("Couldn't serialize complex obs data for obsId=" + obsId + " of type "
			//        + data.getClass());
			String title = complexObs.getComplexData().getTitle();
            log.error("name type: " + title);
			
			
            // FIXME: This is a very hacky way to deal with mime types
            Hashtable<String,String> mimes = new Hashtable<String,String>();
            //support for audio/video files
            mimes.put("3gp","audio/3gpp"); 
            mimes.put("mp3","audio/mpeg"); 
            mimes.put("mp4","video/mp4");  
            mimes.put("mpg","video/mpeg");
            mimes.put("flv","video/x-flv");
            
            //support for text files
            mimes.put("txt","text/plain"); 
            mimes.put("doc","application/msword"); 
            mimes.put(".docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");

            // FIXME: This is a very hacky way to deal with mime types
            for(String mime : mimes.keySet()){
                if (title.contains("." + mime)){
                    response.setContentType(mimes.get(mime));
                }
            }
            
            // Write the file to response
			FileInputStream f = new FileInputStream((File)data);
			
            InputStream in = null;
			OutputStream out = null;      
			try {
			      in = f;
			      out = response.getOutputStream();      
			      while (true) {
			         int dataFromStream = in.read();
			         if (dataFromStream == -1) {
			            break;
			         }
			         out.write(dataFromStream);
			      }
			      in.close();
			      out.close();
		    } finally {
			      if (in != null) {
			         in.close();
			      }
			      if (out != null) {
			         out.close();
			      }
			}
		}
        
        
    }
	
}
