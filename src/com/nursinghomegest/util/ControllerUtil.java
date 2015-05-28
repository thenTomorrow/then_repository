package com.nursinghomegest.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;


public class ControllerUtil {
	
	public static final int BUFFER_SIZE = 4096;
	
	public static final String MIME_CSV = "text/comma-separated-values";
	public static final String MIME_TXT = "text/plain";
	public static final String MIME_PDF = "application/pdf";
	public static final String MIME_XLS = "application/vnd.ms-excel";
	public static final String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String MIME_DOC = "application/msword";
	public static final String MIME_ZIP = "application/zip";
	public static final String MIME_RAR = "application/x-rar-compressed";
	public static final String MIME_PNG = "image/png";
	public static final String MIME_P12 = "application/pkcs-12";
	public static final String MIME_XML = "application/xml";
	
	public static final String EXT_CSV = "csv";
	public static final String EXT_TXT = "txt";
	public static final String EXT_PDF = "pdf";
	public static final String EXT_XLS = "xls";
	public static final String EXT_XML = "xml";
	public static final String EXT_XLSX = "xlsx";
	public static final String EXT_DOC = "doc";
	public static final String EXT_ZIP = "zip";
	public static final String EXT_RAR = "rar";
	public static final String EXT_DOCX = "docx";
	public static final String EXT_JPG = "jpg";
	public static final String EXT_JPEG = "jpeg";
	public static final String EXT_GIF = "gif";
	public static final String EXT_PNG = "png";
	public static final String EXT_P12 = "p12";
	
	//tipi di file
	public static final String FILE_IMMAGINI = "immagini";
	public static final String FILE_DOC = "doc";
	
	//filtro ricerca letture (accounting_x)
	public static final String LETTURE_ALL="all";
	public static final String LETTURE_GIORNALIERE="daily";
	public static final String LETTURE_MENSILI="montly";
	public static final String LETTURE_ANNUALI="yearly";
	
    public static String getMimeTypeFromFilename(String filename) {
    	String mimeType = URLConnection.getFileNameMap().getContentTypeFor(filename);
    	if(mimeType==null){
			if(filename.endsWith("doc")){
				mimeType = MIME_DOC;
			}else if(filename.endsWith("xls")){
				mimeType = MIME_XLS;
			}else if(filename.endsWith("csv")){
				mimeType = MIME_CSV;
			}else if(filename.endsWith("zip")){
				mimeType = MIME_ZIP;
			}else if(filename.endsWith("rar")){
				mimeType = MIME_RAR;
			}
		}
		return mimeType;
    }
    
    public static String getExtensionFromMimeType(String contentType){
    	if(contentType!=null){
	    	if(contentType.equals(MIME_DOC))
	    		return EXT_DOC;
	    	else if(contentType.equals(MIME_XLS))
	    		return EXT_XLS;
	    	else if(contentType.equals(MIME_PDF))
	    		return EXT_PDF;
	    	else if(contentType.equals(MIME_TXT))
	    		return EXT_TXT;
	    	else if(contentType.equals(MIME_CSV))
	    		return EXT_CSV;
	    	else if(contentType.equals(MIME_ZIP))
	    		return EXT_ZIP;
	    	else if(contentType.equals(MIME_RAR))
	    		return EXT_RAR;
    	}
    	return null;
    }
    
    public static void sendFile(
    		HttpServletResponse response, FileOverHttpWriter src,
    		String filename, String contentType)
    throws IOException
    {
    	response.setContentType(contentType);
    	response.addHeader("Content-Disposition","attachment; filename=\"" +filename+"\"");
    	response.setHeader("Pragma", "e-tna");
    	response.setHeader("Cache-Control","max-age=0");
       	OutputStream os = response.getOutputStream();       	
       	try {
	    	os.flush();
	    	src.put(os);
	    	os.flush();
       	}
       	finally {
       		os.close();
       	}
    }
    
	/** Invia un file (inteso come array di byte) sulla connessione HTTP */
    public static void sendFile(HttpServletResponse response, byte[] file, String filename, String contentType) throws IOException
    {
    	sendFile(response, getFileOverHttpWriter(file), filename, contentType);
    }
    
    /** Invia un file sulla connessione HTTP  */
    public static void sendFile(HttpServletResponse response, InputStream inputStream, String filename, String contentType) throws IOException
    {
    	sendFile(response, getFileOverHttpWriter(inputStream), filename, contentType);
    }

    /** Invia un'immagine sulla connessione HTTP  */
    public static void sendFile(HttpServletResponse response, BufferedImage bufferedImage, String filename, String contentType) throws IOException
    {
    	String ext=filename.substring(filename.indexOf(".")+1);
    	response.setContentType(contentType);
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, ext, bytearrayoutputstream);
        bytearrayoutputstream.flush();
        byte abyte0[] = bytearrayoutputstream.toByteArray();
        response.setContentLength(abyte0.length);
        ServletOutputStream servletoutputstream = response.getOutputStream();
        servletoutputstream.write(abyte0);
        servletoutputstream.close();
    	
    }
    
	private static FileOverHttpWriter getFileOverHttpWriter(final byte[] file) {
		return new FileOverHttpWriter() {			
			@Override
			public void put(OutputStream os) throws IOException {
				os.write(file);
			}
		};
	}
	
	private static FileOverHttpWriter getFileOverHttpWriter(final InputStream inputStream) {
		return new FileOverHttpWriter() {			
			@Override
			public void put(OutputStream os) throws IOException {
				byte[] buff = new byte[BUFFER_SIZE];
				int r = 0;
				while( (r = inputStream.read(buff))!=-1 ) {
					os.write(buff, 0, r);
				}
			}
		};
	}
	
	public static String getExtensionFromFileName(String filename){
		String[] partOfFilename =filename.split("\\.");
		return partOfFilename[partOfFilename.length-1];
    }
	
	public static boolean verificaExtFile(String tipoFile, String filename){
		String ext = getExtensionFromFileName(filename);
		if(tipoFile.equals(FILE_IMMAGINI) && (ControllerUtil.EXT_JPG.equalsIgnoreCase(ext) || ControllerUtil.EXT_JPEG.equalsIgnoreCase(ext) || ControllerUtil.EXT_GIF.equalsIgnoreCase(ext) || ControllerUtil.EXT_PNG.equalsIgnoreCase(ext))){
			return true;
		}
		else if(tipoFile.equals(FILE_DOC) && (ControllerUtil.EXT_TXT.equalsIgnoreCase(ext) || ControllerUtil.EXT_PDF.equalsIgnoreCase(ext) || ControllerUtil.EXT_DOC.equalsIgnoreCase(ext) || ControllerUtil.EXT_DOCX.equalsIgnoreCase(ext) || ControllerUtil.EXT_XLS.equalsIgnoreCase(ext) || ControllerUtil.EXT_XLSX.equalsIgnoreCase(ext))){
			return true;
		}
		else
			return false;
	}
	
	public static String collectionToString(Collection<String> values, String separator) {
		String result = "";
		if(values!=null)
		{	for(String value: values) 
				result += value+",";
			return result.substring(0, result.length()-1);
		}
		else
			return null;
	}
	
	public static Collection<String> stringToCollection(String value, String separator) {
		try {
			Collection<String> collection = new ArrayList<String>();
			String[] stringhe = value.split(separator);
			for(int i = 0; i<stringhe.length; i++)
				collection.add(stringhe[i]);
			return collection;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String readTempFile(File temp) throws IOException {
		FileInputStream fileinput = new FileInputStream(temp);
		DataInputStream datainput = new DataInputStream(fileinput);
		BufferedReader mybuffer = new BufferedReader(new InputStreamReader(datainput));
		String html = "";
		String strLine = "";
		while ((strLine = mybuffer.readLine()) != null) {
			html+=strLine;
		}
		mybuffer.close();
		datainput.close();
		temp.delete();
		return html;
	}
}
