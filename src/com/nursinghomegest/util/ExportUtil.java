package com.nursinghomegest.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

public class ExportUtil {

	private static Logger logger = Logger.getLogger(ExportUtil.class);
	
	/********************************************************************************************
	 * export excel
	 * @param nomeFile	nome del file da generare
	 * @param content	lista di array di oggetti contenenti i valori, in ordine rispetto a campiMap, della tabella
	 * @return
	 * @throws Exception
	 *
	 */
	public static byte[] exportExcel(List<Map<String,Object>> content) throws Exception {
		return exportExcel(content, null);
	}
	/********************************************************************************************
	 * export excel
	 * @param nomeFile	nome del file da generare
	 * @param campiMap	mappa dei campi della tabella (map(key=campo, value=align{left,center,right})
	 * @param content	lista di array di oggetti contenenti i valori, in ordine rispetto a campiMap, della tabella
	 * @return
	 * @throws Exception
	 *
	 */
	public static byte[] exportExcel(List<Map<String,Object>> content,List<String> campi) throws Exception {

		byte[] fileContent = null;
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		// se il nome contiene gli /, allora prendiamo solo l'ultima parte...
		String sheetName = "foglio";

		FastReportBuilder drb = new FastReportBuilder();

		drb.setPrintColumnNames(true);

		Style titleStyle = new Style();
		titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));
		Style oddRowStyle = new Style();
		oddRowStyle.setBorder(Border.NO_BORDER());
		oddRowStyle.setBackgroundColor(new Color(0.9f, 0.9f, 0.9f));
		oddRowStyle.setTransparency(Transparency.OPAQUE);

		Style columnStyle = new Style();
		columnStyle.setBorder(Border.THIN());
		columnStyle.setBackgroundColor(new Color(0.2f, 0.2f, 0.3f));
		columnStyle.setTransparency(Transparency.OPAQUE);
		columnStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		columnStyle.setTextColor(Color.white);
		columnStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		columnStyle.setFont(new Font(10, Font._FONT_ARIAL, true));
		columnStyle.setPaddingLeft(20);
		columnStyle.setPaddingRight(20);
		columnStyle.setPaddingRight(3);
		columnStyle.setPaddingRight(3);

		Style columDetail = new Style();
		columDetail.setBorder(Border.NO_BORDER());
		columDetail.setBorderLeft(Border.THIN());
		columDetail.setBorderRight(Border.THIN());
		columDetail.setBackgroundColor(Color.white);
		columDetail.setTransparency(Transparency.OPAQUE);
		columDetail.setFont(new Font(9, Font._FONT_ARIAL, true));

		drb.setPrintBackgroundOnOddRows(true).setPrintColumnNames(true).setHeaderHeight(20);

		drb.setDefaultStyles(titleStyle, titleStyle, columnStyle, columDetail);
		drb.setOddRowBackgroundStyle(oddRowStyle);

		try {
			HSSFWorkbook workBook = new HSSFWorkbook();

			HSSFSheet sheet = workBook.createSheet(sheetName);

			HSSFCellStyle headerStyle = workBook.createCellStyle();
			headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headerStyle.setFillBackgroundColor(new HSSFColor.DARK_BLUE().getIndex());
			headerStyle.setFillForegroundColor(new HSSFColor.DARK_BLUE().getIndex());
			headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			HSSFFont font = workBook.createFont();
			font.setFontHeightInPoints((short)10);
			font.setFontName(Font._FONT_ARIAL);
			font.setColor(new HSSFColor.WHITE().getIndex());
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			headerStyle.setFont(font);

			HSSFCellStyle footerStyle = workBook.createCellStyle();
			footerStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			footerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			footerStyle.setFont(font);

			font = workBook.createFont();
			font.setFontHeightInPoints((short)9);
			font.setFontName(Font._FONT_ARIAL);

			HSSFCellStyle cellStyleCA = workBook.createCellStyle();
			cellStyleCA.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			cellStyleCA.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyleCA.setFont(font);

			HSSFCellStyle cellStyleLA = workBook.createCellStyle();
			cellStyleLA.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			cellStyleLA.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyleLA.setFont(font);
			
			HSSFCellStyle cellStyleRA = workBook.createCellStyle();
			cellStyleRA.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cellStyleRA.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyleRA.setFont(font);
			HSSFDataFormat dataFormat = workBook.createDataFormat();
			cellStyleRA.setDataFormat(dataFormat.getFormat("#,##0.00"));
			
			HSSFCellStyle cellStyleRAInt = workBook.createCellStyle();
			cellStyleRAInt.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cellStyleRAInt.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			cellStyleRAInt.setFont(font);

			List<HSSFCellStyle> stili = new Vector<HSSFCellStyle>();
			if(campi==null && content.get(0)!=null){
				campi=new ArrayList<String>(content.get(0).keySet());
			}
			Iterator<String> it = campi.iterator();
			int i = 0;
			HSSFRow title = sheet.createRow((short)0);
			title.setHeight((short) 300);
			while (it.hasNext()) {
				stili.add(cellStyleLA);
				String fieldName = it.next();

				HSSFCell t_cell = title.createCell(i++);

				t_cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				t_cell.setCellValue(new HSSFRichTextString(fieldName));
				t_cell.setCellStyle(headerStyle);
			}

			int rows = 1;
			
			Iterator<Map<String,Object>> obj_it = content.iterator();
			int columnCounter = 1;
			while (obj_it.hasNext()) {
				Map<String,Object> line = obj_it.next();
				HSSFRow val_row = sheet.createRow(rows++);
				val_row.setHeight((short) 300);
				int columnIndex=0;
				for (Object val:line.values()) {
					HSSFCell t_cell = val_row.createCell(columnIndex);
					
					if(val instanceof Number) {
						t_cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						double valore = 0;
						if (val instanceof BigDecimal) {
							BigDecimal d = (BigDecimal) (val);
							valore = d.doubleValue();
						} else if (val instanceof Double) {
							Double d = (Double) (val);
							valore = d.doubleValue();
						} else if (val instanceof Float) {
							Float d = (Float) (val);
							valore = d.floatValue();
						} else if (val instanceof BigInteger) {
							BigInteger d = (BigInteger) (val);
							valore = d.longValue();
						} else if (val instanceof Long) {
							Long d = (Long) (val);
							valore = d.longValue();
						} else if (val instanceof Integer) {
							Integer d = (Integer) (val);
							valore = d.longValue();
						}
						
				        DecimalFormat df = new DecimalFormat("#.##");
				        valore = df.parse(df.format(valore)).doubleValue();

					    t_cell.setCellValue(valore);
						
					} else if (val instanceof String) {
						t_cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						t_cell.setCellValue(new HSSFRichTextString(val.toString()));

					}else if (val instanceof Character) {
						t_cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						t_cell.setCellValue(new HSSFRichTextString(val.toString()));
					}
					else if (val instanceof java.sql.Date) {
						t_cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						Date d = (Date) (val);
						t_cell.setCellValue(new HSSFRichTextString(formatter.format(d)));

					} else if (val instanceof java.sql.Timestamp) {
						t_cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						Date d = (Date) (val);
						t_cell.setCellValue(new HSSFRichTextString(formatter.format(d)));
					}
					else {
						if(val == null)
							val = new String("");
						t_cell.setCellValue(new HSSFRichTextString(val.toString()));
						
					}
					
					HSSFCellStyle defaultStyle = cellStyleLA;
					HSSFCellStyle style = defaultStyle;
					try {
						style = stili.get(columnIndex);
					} catch(Exception e) {	}

					t_cell.setCellStyle(style);
					
					columnIndex++;
				}
				columnCounter=columnCounter+1;
			}

			/**
			 * a questo punto, per ogni colonna, gli dico di gestire
			 * automaticamente le larghezze.....
			 */
			int max = campi.size();
			while (max-- >= 0)
				sheet.autoSizeColumn((short) max);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workBook.write(baos);
			fileContent = baos.toByteArray();
			baos.close();
			
		} catch (Exception e) {
			logger.error("",e);
		}
		
		return fileContent;
	}
	
	public static byte[] exportCsv(List<Map<String,Object>> content){
		return exportCsv(content, null);
	}
	
	public static byte[] exportCsv(List<Map<String,Object>> content,List<String> campi){
        
		try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            Object r = null;
            
            if(campi==null && content.get(0)!=null){
				campi=new ArrayList<String>(content.get(0).keySet());
			}
            
            String intestazione = "";
            for(String colonna: campi)
                intestazione +="\""+colonna+"\";";
            intestazione = intestazione.substring(0, intestazione.length()-1);
            baos.write((intestazione+"\n").getBytes());
            
            for(Map<String,Object> valori: content){	
                String riga = "";
                //for(int i=0; i<campi.size(); i++) {
                for (Object val:valori.values()) {	
                	try{
	                	//Object val = valori[i];
						
		                if (val instanceof BigInteger) {
							BigInteger d = (BigInteger) (val);
							r = "\""+d.longValue()+"\";";
						} else if (val instanceof Long) {
							Long d = (Long) (val);
							r = "\""+d.longValue()+"\";";
						} else if (val instanceof Integer) {
							Integer d = (Integer) (val);
							r = "\""+d.longValue()+"\";";
						} else if(val instanceof Number) {
							double valore = 0;
							if (val instanceof BigDecimal) {
								BigDecimal d = (BigDecimal) (val);
								valore = d.doubleValue();
							} else if (val instanceof Double) {
								Double d = (Double) (val);
								valore = d.doubleValue();
							} else if (val instanceof Float) {
								Float d = (Float) (val);
								valore = d.floatValue();
							}
							NumberFormat nf = new DecimalFormat("##############0.00"); 
					        valore = nf.parse(nf.format(valore)).doubleValue();
					        r = "\""+valore+"\";"; 
						} else if (val instanceof String) {
							r = "\""+val.toString()+"\";";
						} else if (val instanceof Character) {
							r = "\""+val.toString()+"\";";
						} else if (val instanceof java.sql.Date) {
							Date d = (Date) (val);
							r = "\""+formatter.format(d)+"\";";
						} else if (val instanceof java.sql.Timestamp) {
							Date d = (Date) (val);
							r = "\""+formatter.format(d)+"\";";
						} else {
							if(val == null)
								val = new String("");
							r = "\""+val.toString()+"\";";
						}
                	}catch(NullPointerException e){
                		r="";
                	}
	                riga += r;
                    
            	}
                riga = riga.substring(0, riga.length()-1);
                
                baos.write((riga+"\n").getBytes());
            }
            byte[] result = baos.toByteArray();
            baos.close();
            return result;
           
        }catch (Exception e) {
        	logger.error("",e);
        	return null;
        }
    } 

	/**
	 * @param page
	 * @param contentStream
	 * @param y the y-coordinate of the first row
	 * @param margin the padding on left and right of table
	 * @param content a 2d array containing the table data
	 * @throws IOException
	 */
	public static void drawTable(PDPage page, PDPageContentStream contentStream, 
	                            float y, float margin, 
	                            String[][] content) throws IOException {
	    final int rows = content.length;
	    final int cols = content[0].length;
	    final float rowHeight = 20f;
	    final float tableWidth = page.findMediaBox().getWidth() - margin - margin;
	    final float tableHeight = rowHeight * rows;
	    final float colWidth = tableWidth/(float)cols;
	    final float cellMargin=5f;

	    //draw the rows
	    float nexty = y ;
	    for (int i = 0; i <= rows; i++) {
	        contentStream.drawLine(margin, nexty, margin+tableWidth, nexty);
	        nexty-= rowHeight;
	    }

	    //draw the columns
	    float nextx = margin;
	    for (int i = 0; i <= cols; i++) {
	        contentStream.drawLine(nextx, y, nextx, y-tableHeight);
	        nextx += colWidth;
	    }

	    //now add the text        
	    contentStream.setFont( PDType1Font.HELVETICA_BOLD , 12 );        

	    float textx = margin+cellMargin;
	    float texty = y-15;        
	    for(int i = 0; i < content.length; i++){
	        for(int j = 0 ; j < content[i].length; j++){
	            String text = content[i][j];
	            contentStream.beginText();
	            contentStream.moveTextPositionByAmount(textx,texty);
	            contentStream.drawString(text);
	            contentStream.endText();
	            textx += colWidth;
	        }
	        texty-=rowHeight;
	        textx = margin+cellMargin;
	    }
	}
	
	public static byte[] exportPdf(List<Map<String,Object>> content) throws DocumentException, IOException{
		return exportPdf(content, null);
	}
	
	public static byte[] exportPdf(List<Map<String,Object>> content,List<String> campi) throws DocumentException, IOException{
		Document documento = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
		documento.addCreationDate();
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		PdfWriter.getInstance(documento, baos);
		documento.open();
		//documento.setHeader(createHeader(writer));
		//documento.setFooter(createFooter(writer));
		
		if(campi==null && content.get(0)!=null){
			campi=new ArrayList<String>(content.get(0).keySet());
		}
				
		Table tablePDF=new Table(campi.size());
		tablePDF.setAlignment(Element.ALIGN_TOP);
		tablePDF.setCellsFitPage(true);
		tablePDF.setWidth(100);
		tablePDF.setPadding(2);
		tablePDF.setSpacing(0);
		
		for(String header:campi){
			Cell cell = getCell(header.toUpperCase());
			cell.setGrayFill(0.9f);
			cell.setHeader(true);
			tablePDF.addCell(cell);
		}
		
		for(Map<String,Object> row: content){
			//for(int i=0;i<campi.size();i++){
			for (Object body:row.values()){
				if(body!=null)
					tablePDF.addCell(getCell(body+""));
				else
					tablePDF.addCell(getCell(""));
			}
		}
		documento.add(tablePDF);
		documento.close();		
		return baos.toByteArray();
	}
	
	private static Cell getCell(String value) throws BadElementException {
		com.lowagie.text.Font smallFont = 
				FontFactory.getFont(FontFactory.HELVETICA, 7, com.lowagie.text.Font.NORMAL, new Color(0, 0, 0));
		Cell cell = new Cell(new Chunk(StringUtils.trimToEmpty(value), smallFont));
		cell.setVerticalAlignment(Element.ALIGN_TOP);
		cell.setLeading(8);
		return cell;
	}
	
}
