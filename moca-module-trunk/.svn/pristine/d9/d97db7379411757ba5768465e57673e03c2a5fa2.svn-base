package org.moca.handler;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileLock;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.obs.ComplexData;
import org.openmrs.obs.ComplexObsHandler;
import org.openmrs.obs.handler.ImageHandler;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Handler for storing images as apart of a complex obs which also generates a 
 * thumbnail.
 * 
 * @author Sana Development Team
 *
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ThumbnailingImageHandler extends ImageHandler implements 
	ComplexObsHandler 
{
    
    private static final int THUMBNAIL_DEFAULT_SIZE = 100;
    public static final String HTML_THUMBNAIL_VIEW = "HTML_THUMBNAIL_VIEW";
    public static final String THUMBNAIL_VIEW = "VIEW_THUMBNAIL";
    public static final String IMAGE_VIEW = "VIEW_IMAGE";
    public static final String THUMBNAIL_DIR = "thumbnails";

    public static final Log log = LogFactory.getLog(ThumbnailingImageHandler.class);
    
    /** Scales an image to a specified width and height
     * 
     * @param image Image to scale
     * @param thumbWidth thumbnail height
     * @param thumbHeight thumbnail width
     * @return
     * @throws InterruptedException
     */
    private static BufferedImage scaleImg(Image image, int thumbWidth, 
    		int thumbHeight) throws InterruptedException 
    {
        // load image from INFILE
        //Image image = Toolkit.getDefaultToolkit().getImage(args[0]);
        //MediaTracker mediaTracker = new MediaTracker(new Container());
        //mediaTracker.addImage(image, 0);
        //mediaTracker.waitForID(0);
        // determine thumbnail size from WIDTH and HEIGHT
        double thumbRatio = (double)thumbWidth / (double)thumbHeight;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double)imageWidth / (double)imageHeight;
        if (thumbRatio < imageRatio) {
            thumbHeight = (int)(thumbWidth / imageRatio);
        } else {
            thumbWidth = (int)(thumbHeight * imageRatio);
        }
        
        // draw original image to thumbnail image object and
        // scale it to the new size on-the-fly
        BufferedImage thumbImage = new BufferedImage(thumbWidth, 
        		thumbHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
        		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
        graphics2D.dispose();
        //mediaTracker.removeImage(image);
        return thumbImage;
    }
    
    private synchronized void scale(File in, File out) 
    		throws InterruptedException, IOException
    {
    	String thumbnailSizeProperty = Context.getAdministrationService()
    	.getGlobalProperty("moca.thumbnail_size");

    	int thumbnailSize = THUMBNAIL_DEFAULT_SIZE;
    	try {
    		thumbnailSize = Integer.parseInt(thumbnailSizeProperty);
    	} catch(NumberFormatException e) {

    	}      
    	int thumbWidth = thumbnailSize;
    	int thumbHeight = thumbnailSize;
    	double thumbRatio = (double)thumbWidth / (double)thumbHeight;
    	
    	BufferedImage image = ImageIO.read(in);
    		
    	int imageWidth = image.getWidth(null);
    	int imageHeight = image.getHeight(null);
    	double imageRatio = (double)imageWidth / (double)imageHeight;
    	if (thumbRatio < imageRatio) {
    		thumbHeight = (int)(thumbWidth / imageRatio);
    	} else {
    		thumbWidth = (int)(thumbHeight * imageRatio);
    	}

    	// draw original image to thumbnail image object and
    	// scale it to the new size on-the-fly
    	BufferedImage thumbImage = new BufferedImage(thumbWidth, 
    			thumbHeight, BufferedImage.TYPE_INT_RGB);
    	Graphics2D graphics2D = thumbImage.createGraphics();
    	graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
    			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    	graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
    	graphics2D.dispose();
    	image.flush();
    	ImageIO.write(thumbImage, "JPG", out);
    	notify();
    }

    /** Returns a thumbnail for an observation and image */
    private BufferedImage getThumbnailImage(Obs obs, BufferedImage img) 
    	throws InterruptedException 
    {
        String thumbnailSizeProperty = Context.getAdministrationService()
        							.getGlobalProperty("moca.thumbnail_size");
        
        int thumbnail_size = THUMBNAIL_DEFAULT_SIZE;
        try {
            thumbnail_size = Integer.parseInt(thumbnailSizeProperty);
        } catch(NumberFormatException e) {
            
        }      
        return scaleImg(img, thumbnail_size, thumbnail_size); 
    }
    
    /** generates a new File in the file system to store the thumb */
    private File getThumbnailFileFromImageFile(File output) {
        File thumbDir = new File(output.getParentFile(), THUMBNAIL_DIR);
        if(!thumbDir.exists())
            thumbDir.mkdir();
        return new File(thumbDir, output.getName());
    }
    
    /** generates a new File to store the thumb for an obs with an image */
    private File getThumbnailFileFromObs(Obs obs) {
        File imageFile = getComplexDataFile(obs);
        return getThumbnailFileFromImageFile(imageFile);
    }
    
    /** {@inheritDoc} */
    @Override
    public Obs saveObs(Obs obs) throws APIException {
        // Get the buffered image from the ComplexData.
        Object data = obs.getComplexData().getData();
        String extension = getExtension(obs.getComplexData().getTitle());
        File output = null;
        FileInputStream in = null; 
        OutputStream out = null;
        try {
        	in = (FileInputStream) obs.getComplexData().getData();
        	output = getOutputFileToWrite(obs);
        	out = new FileOutputStream(output);
        	OpenmrsUtil.copyFile(in, out);
        	if (in != null)
        		in.close();
        	if (out != null)
        		out.close();
        } catch (IOException e) {
        	throw new APIException("Unable to convert complex data to a " 
        		+"valid input stream and then read it into a buffered image");
        }

        // create thumb
        try{
        	if(output != null && output.exists()){
        		File thumb = getThumbnailFileFromImageFile(output);
        		thumb.createNewFile();
        		if(thumb.exists()){
        			scale(output, thumb);
        		}
        	}
        } catch (IOException e) {
        	throw new APIException("Unable to convert complex data to a " 
        			+"valid input stream and then read it into a buffered image");
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
    	// Set the Title and URI for the valueComplex
    	obs.setValueComplex(extension + " image |" + output.getName());
    	// Remove the ComlexData from the Obs
    	obs.setComplexData(null);
        return obs;
    }
    
    private Obs getOrCreateThumbObs(Obs obs){
        log.error("getOrCreateThumbObs for " + obs.getObsId());
		System.out.println("Sana.ThumbNailingImageHandler.getOrCreateThumbObs()."
    			+"156: obs.complexValue -> " + obs.getValueComplex());
		BufferedImage thumb = null;
		File thumbFile = null;
    	try{
            log.error("getOrCreateThumbObs(): " + obs.getObsId() +" -> ");
    		System.out.println("Sana.ThumbNailingImageHandler.getOrCreateThumbObs().161");
    		String extension = getExtension(obs.getComplexData().getTitle());
            log.error("getOrCreateThumbObs(): " + obs.getObsId() +" -> " + extension);
    		File original = getOutputFileToWrite(obs);
            log.error("getOrCreateThumbObs(): " + obs.getObsId() +" original -> " + original.getName());
    		System.out.println("Sana.ThumbNailingImageHandler.getOrCreateThumbObs()."
        			+"164: original: " + original.getAbsolutePath());
            thumbFile = getThumbnailFileFromObs(obs);

            log.error("getOrCreateThumbObs(): " + obs.getObsId() +" thumb -> " + thumbFile.getName());
    			System.out.println("Sana.ThumbNailingImageHandler.getOrCreateThumbObs()."
        			+"167: thumb: " + thumbFile.getAbsolutePath());
    		if(thumbFile.exists()){
    			System.out.println("Sana.ThumbNailingImageHandler.getOrCreateThumbObs()."
        			+"169: image exists: " + thumbFile.getAbsolutePath());
    			// get existing
    			return getObsThumbnail(obs);
    		} 
    		// create thumb object
    		System.out.println("Sana.ThumbNailingImageHandler.getOrCreateThumbObs()."
        			+"176: creating new: " + thumbFile.getAbsolutePath());
    		BufferedImage image = ImageIO.read(original);
            log.error("getOrCreateThumbObs(): " + obs.getObsId() +" original -> opened");
    		thumb = getThumbnailImage(obs, image);
            log.error("getOrCreateThumbObs(): " + obs.getObsId() +" thumb -> image created");
    		ImageIO.write(thumb, extension, thumbFile);
            log.error("getOrCreateThumbObs(): " + obs.getObsId() +" thumb -> file created");
    		System.out.println("Sana.ThumbNailingImageHandler.getOrCreateThumbObs()."
        			+"181: new File created: " + thumbFile.getName());
    		ComplexData complexData = new ComplexData(thumbFile.getName(),thumb);
    		obs.setComplexData(complexData);
    	} catch (Exception e){
            log.error("getOrCreateThumbObs for " + obs.getObsId() +": "
            		+e);
			System.out.println("Sana.ThumbNailingImageHandler.getOrCreateThumbObs()."
    			+"184: Error: " + e.getMessage());
			e.printStackTrace();
    	}
        return obs;
    }
    
    /**
     * Gets the thumbnail for an observation */
    private Obs getObsThumbnail(Obs obs) {
        File thumbFile = getThumbnailFileFromObs(obs);
    	System.out.println("Sana.ThumbNailingImageHandler.getObsThumbnail()."
    		+".. thumb file name = " 
    		+ ((thumbFile != null)? thumbFile.getName(): "No thumbnail file"));
        BufferedImage img = getThumbImage(thumbFile);
        ComplexData complexData = new ComplexData(thumbFile.getName(), img);
        obs.setComplexData(complexData);
        return obs;
    }
    
    private synchronized BufferedImage getThumbImage(File f){
        BufferedImage img = null;
        try {
            img = ImageIO.read(f);
        }
        catch (IOException e) {
            log.error("Unable to open file: " + f.getName(), e);
        }
        return img;
    }
    
    /**
     * Gets the hyperlink for an image for viewing */
    private String getHyperlink(Obs obs, String view) {
        return "/" + WebConstants.WEBAPP_NAME + "/complexObsServlet?obsId=" 
        		+ obs.getObsId() +"&view=" + view;
    }
    
    /** {@inheritdoc} */
    @Override
    public Obs getObs(Obs obs, String view) {
        log.error("getObs for " + obs.getObsId() + " view:" + view);
        if(HTML_THUMBNAIL_VIEW.equals(view)) {
        	System.out.println("Sana.ThumbNailingImageHandler.getObs()."
        			+"HTML_THUMBNAIL_VIEW");
            log.error("Rendering HTML thumb view for " + obs.getObsId());
            String imgtag = "<img src='" + getHyperlink(obs, THUMBNAIL_VIEW) 
            					+ "'/>";
            ComplexData cd = new ComplexData(obs.getValueAsString(null),imgtag);
            obs.setComplexData(cd);
            return obs;
        } else if(WebConstants.HTML_VIEW.equals(view)) {
        	System.out.println("Sana.ThumbNailingImageHandler.getObs()."
        			+"HTML_VIEW");
            log.error("Rendering HTML view for " + obs.getObsId());
            String imgtag = "<img src='" + getHyperlink(obs, IMAGE_VIEW) + "'/>";
            ComplexData cd = new ComplexData(obs.getValueAsString(null), imgtag);
            obs.setComplexData(cd);
            return obs;
        } else if(WebConstants.HYPERLINK_VIEW.equals(view)) {
            log.error("Rendering hyperlink for " + obs.getObsId());
        	System.out.println("Sana.ThumbNailingImageHandler.getObs()."
        			+"HYPERLINK_VIEW");
            ComplexData cd = new ComplexData(obs.getValueAsString(null), 
            		getHyperlink(obs, IMAGE_VIEW));
            obs.setComplexData(cd);
            return obs;
        } else if(THUMBNAIL_VIEW.equals(view)) {
            log.error("Rendering THUMBNAIL_VIEW view for " + obs.getObsId());
        	System.out.println("Sana.ThumbNailingImageHandler.getObs()."
        			+"THUMBNAIL_VIEW");
            return getObsThumbnail(obs);
        }
        return super.getObs(obs, view);
    }
    

	/** {@inheritDoc} */
    @Override
    public boolean purgeComplexData(Obs obs) {
        File thumbFile = getThumbnailFileFromObs(obs);
        if(thumbFile.exists()) {
            thumbFile.delete();
        }
        return super.purgeComplexData(obs);
    }
}
