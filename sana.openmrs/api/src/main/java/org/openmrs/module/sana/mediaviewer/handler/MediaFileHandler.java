package org.openmrs.module.sana.mediaviewer.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;
import org.openmrs.obs.ComplexObsHandler;
import org.openmrs.obs.handler.AbstractHandler;
import org.openmrs.util.OpenmrsUtil;

public class MediaFileHandler extends AbstractHandler implements
	ComplexObsHandler
{

    private Log log = LogFactory.getLog(MediaFileHandler.class);

    /**
     * @see org.openmrs.obs.ComplexObsHandler#getComplexData(org.openmrs.Obs,
     *      java.lang.String)
     */
    public ComplexData getComplexData(Obs obs, String view) {
        return this.getObs(obs).getComplexData();
    }

    /**
     * @see org.openmrs.obs.ComplexObsHandler#getObs(org.openmrs.Obs)
     */
    public Obs getObs(Obs obs) {
        File genericFile = getComplexObjectFile(obs);
        log.info("getObs: " + obs.getValueComplex());
        log.info("getObs: " + genericFile.getAbsolutePath());
        
        /*InputStream is = null;
        try{
        	is = new FileInputStream(genericFile);
        }catch(Exception e)
        {
        	log.info("getObs ERROR: " + e);
        }
        
        // Get the size of the file
        long length = genericFile.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        try{
	        while ((offset < bytes.length)
	               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
        } catch(Exception e)
        {
        	log.info("getObs ERROR: " + e);
        }
        
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            log.info("getObs ERROR: Could not completely read file "+genericFile.getName());
        }
    
        // Close the input stream and return bytes
        try{
        	is.close();
        } catch(Exception e)
        {
        	log.info("getObs ERROR: " +e);
        }
        */
        ComplexData complexData = new ComplexData(genericFile.getName(), genericFile);

        obs.setComplexData(complexData);

        return obs;
    }

    /**
     * @see org.openmrs.obs.ComplexObsHandler#getObs(org.openmrs.Obs,
     *      java.lang.String)
     */
    public Obs getObs(Obs obs, String view) {
        return this.getObs(obs);
    }

    /**
     * @see org.openmrs.obs.ComplexObsHandler#purgeComplexData(org.openmrs.Obs)
     */
    public boolean purgeComplexData(Obs obs) {
        File genericFile = getComplexObjectFile(obs);
        File metadaFile = getComplexObjectFile(obs);
        if (metadaFile.exists() && metadaFile.delete() && genericFile.exists()
                && genericFile.delete()) {
            obs.setComplexData(null);
            // obs.setValueComplex(null);
            return true;
        }
        log.debug("Could not delete complex data object for obsId="
                + obs.getObsId() + " located at " + genericFile.getAbsolutePath()
                + " (.xml)");
        return false;
    }

    /**
     * @see org.openmrs.obs.ComplexDataHandler#saveObs()
     */
//    public Obs saveObs(Obs obs) {
//        AnnotatedImage annoimg = obs.getComplexData().getData();
//        //something like this
//        
//        super.saveObs(


    public Obs saveObs(Obs obs) throws APIException {
    	InputStream genericInputStream = null;
    	
    	//NEEDS TO BE FIXED
    	log.info("Saving Obs where complex data is " + obs.getComplexData());
    	if(obs.getComplexData().getTitle().equals("noData"))
    		return obs;
    	
    	InputStream data = (InputStream)(obs.getComplexData().getData());
	
		if (data == null) {
			throw new APIException("Cannot save complex obs where obsId=" + obs.getObsId()
			        + " because its ComplexData.getData() is null and data class is " + data.getClass());
		}
		
		try {
			File outFile = getOutputFileToWrite(obs);
			
			String extension = getExtension(obs.getComplexData().getTitle());
			
			// Write the file to the file system
			InputStream in = data;
			OutputStream out = null;      
			try {
			      in = data;
			      out = new FileOutputStream(outFile);       
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

	        
			// Set the Title and URI for the valueComplex
			obs.setValueComplex(extension + " genericObject |" + outFile.getName());
			
			// Remove the ComlexData from the Obs
			obs.setComplexData(null);
			
		}
		catch (IOException ioe) {
			throw new APIException("Trying to write complex obs to the file system. ", ioe);
		}
		
		return obs;
    }
    
    /**
     * Parses the XML metadata file (if it exists) loads the metadata into the
     * given AnnotatedImage and returns it.
     * 
     * @param obs
     */
   /* public AnnotatedObject loadMetadata(Obs obs, AnnotatedObject genericObject) {

        File metadataFile = getComplexMetadataFile(obs);
        
        genericObject.setHandler(this);

        ArrayList<ObjectAnnotation> annotations = new ArrayList<ObjectAnnotation>();
        if (metadataFile.exists() && metadataFile.canRead()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document xmldoc = builder.parse(metadataFile);

                NodeList annotationNodeList = xmldoc
                        .getElementsByTagName("Annotation");

                for (int i = 0; i < annotationNodeList.getLength(); i++) {
                    try {
                        Node node = annotationNodeList.item(i);
                        NamedNodeMap attributes = node.getAttributes();
                        String text = node.getTextContent();
                        String idString = attributes.getNamedItem("id")
                                .getNodeValue();
                        String date = attributes.getNamedItem("date")
                                .getNodeValue();
                        String userid = attributes.getNamedItem("userid")
                                .getNodeValue();

                        int annotationid = Integer.parseInt(idString);
                        User user = Context.getUserService().getUser(
                                Integer.parseInt(userid));
                        annotations.add(new ObjectAnnotation(annotationid,
                                text, new Date(Long.parseLong(date)),
                                user));
                    } catch (NumberFormatException e) {
                        // Skip that annotation
                    }
                }

            } catch (Exception e) {
                //Likely ParserConfigurationException, SAXException or IOException.
                //Fail silently, log the error and return the image with no annotations.
                log.error("Error loading annotations", e);
            }
        }
        genericObject.setAnnotations(annotations.toArray(new ObjectAnnotation[0]));

        return genericObject;
    }*/

    /*public void saveAnnotation(Obs obs, ObjectAnnotation annotation,
            boolean delete) {
        try {
            log.info("gmapsimageviewer: Saving annotation for obs "
                    + obs.getObsId());

            File metadataFile = getComplexMetadataFile(obs);
            log.info("gmapsimageviewer: Using file "
                    + metadataFile.getCanonicalPath());

            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmldoc;
            Element annotationsParent;
            int newId = 0;

            if (metadataFile.exists()) {
                xmldoc = builder.parse(metadataFile);
                annotationsParent = (Element) xmldoc.getElementsByTagName(
                        "Annotations").item(0);

                NodeList annotationNodeList = xmldoc
                        .getElementsByTagName("Annotation");

                for (int i = 0; i < annotationNodeList.getLength(); i++) {
                    NamedNodeMap attributes = annotationNodeList.item(i)
                            .getAttributes();
                    String idString = attributes.getNamedItem("id")
                            .getNodeValue();
                    int existingId = Integer.parseInt(idString);
                    if (existingId == annotation.getId()) {
                        annotationsParent.removeChild(annotationNodeList
                                .item(i));
                        break;
                    }
                    if (existingId >= newId)
                        newId = existingId + 1;
                }

            } else {
                metadataFile.createNewFile();
                DOMImplementation domImpl = builder.getDOMImplementation();
                xmldoc = domImpl.createDocument(null, "GenericObjectMetadata", null);
                Element root = xmldoc.getDocumentElement();
                annotationsParent = xmldoc.createElementNS(null, "Annotations");
                root.appendChild(annotationsParent);

            }

            if (!delete) {
                if (annotation.getId() >= 0)
                    newId = annotation.getId();

                Element e = xmldoc.createElementNS(null, "Annotation");
                Node n = xmldoc.createTextNode(annotation.getText());
                e.setAttributeNS(null, "id", newId + "");
                e.setAttributeNS(null, "userid", annotation.getUser()
                        .getUserId()
                        + "");
                e.setAttributeNS(null, "date", annotation.getDate().getTime()
                        + "");
                e.appendChild(n);
                annotationsParent.appendChild(e);
            }

            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(xmldoc), new StreamResult(
                    metadataFile));

            log.info("gmapsimageviewer: Saving annotation complete");

        } catch (Exception e) {
            log.error("gmapsimageviewer: Error saving generic object metadata: "
                    + e.getClass() + " " + e.getMessage());
        }

    }*/

    /**
     * Convenience method to create and return a file for the stored image
     * 
     * @param obs
     * @return
     */
    public static File getComplexObjectFile(Obs obs) {
    	return getComplexDataFile(obs);
    }

    /**
     * Convenience method to create and return a file for the stored metadata
     * file
     * 
     * @param obs
     * @return
     */
    public static File getComplexMetadataFile(Obs obs) {
    	if(obs.getAccessionNumber().equals("noData"))
    	{
    		return new File("/var/www/emptyfile");
    	}
    	//From ImageHandler's getComplexDataFile method in OpenMRS 
    	String[] names = obs.getValueComplex().split("\\|"); 
    	String filename = names.length < 2 ? names[0] : names[names.length - 1]; 
        File dir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(Context.getAdministrationService().getGlobalProperty("obs.complex_obs_dir")); 
        File genericFile = new File(dir, filename);
        
        try {
            return new File(genericFile.getCanonicalPath() + ".xml");
        } catch (IOException e) {
            return new File(genericFile.getAbsolutePath() + ".xml");
        }
    }
}
