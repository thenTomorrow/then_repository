package com.nursinghomegest.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;



public class ImageUtil {
	
	private static final int IMG_WIDTH = 256;
	
	public static byte[] resizeImage(byte[] originalImage) throws IOException{
		return imageToByteArray(resizeImage(byteArrayToImage(originalImage)));
	}
	
	public static byte[] resizeImageWithHint(byte[] originalImage) throws IOException{
		return imageToByteArray(resizeImageWithHint(byteArrayToImage(originalImage)));
	}
	
    public static BufferedImage resizeImage(BufferedImage originalImage){
    	
    	int originalWidth = originalImage.getWidth();
    	int originalHeight = originalImage.getHeight();
    	int height = IMG_WIDTH*originalHeight/originalWidth; 
    	
    	int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
    	BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, height, type);
    	Graphics2D g = resizedImage.createGraphics();
    	g.drawImage(originalImage, 0, 0, IMG_WIDTH, height, null);
    	g.dispose();
    	return resizedImage;
    }
 
    public static BufferedImage resizeImageWithHint(BufferedImage originalImage){
 
    	int originalWidth = originalImage.getWidth();
    	int originalHeight = originalImage.getHeight();
    	int height = IMG_WIDTH*originalHeight/originalWidth; 
    	
    	int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
    	BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, height, type);
    	Graphics2D g = resizedImage.createGraphics();
    	g.drawImage(originalImage, 0, 0, IMG_WIDTH, height, null);
    	g.dispose();	
		g.setComposite(AlphaComposite.Src);
 
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						   RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
						   RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						   RenderingHints.VALUE_ANTIALIAS_ON);
 
		return resizedImage;
    }	
    
    public static BufferedImage byteArrayToImage(byte[] bytes){  
        BufferedImage bufferedImage=null;
        try {
            InputStream inputStream = new ByteArrayInputStream(bytes);
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return bufferedImage;
    }
    
    public static byte[] imageToByteArray(BufferedImage originalImage) throws IOException{
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ImageIO.write(originalImage, "jpg", baos);
    	baos.flush();
    	byte[] imageInByte = baos.toByteArray();
    	baos.close();
    	return imageInByte;
    }
}
