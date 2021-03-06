package com.nursinghomegest.controller

import com.nursinghomegest.service.SqlService;
import com.nursinghomegest.util.ControllerUtil;
import com.nursinghomegest.util.ExportUtil;
import com.nursinghomegest.util.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.ResultSet;

import groovy.json.JsonSlurper;
import groovy.sql.DataSet;
import groovy.sql.GroovyRowResult;
import groovy.sql.Sql
import groovy.util.logging.Log4j;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import au.com.bytecode.opencsv.CSVWriter;

@Log4j
@Controller
class EsamiController {
	
	@Resource(name="ds")
	private DataSource dataSource
	
	@Resource(name="sqlService")
	private SqlService sqlService
			
	@RequestMapping(value="/esami/{id}", method = RequestMethod.GET)
	public @ResponseBody Object getEsame(HttpServletRequest request, @PathVariable("id") Integer id) {
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.firstRow("""select esami.* 
								   from esami
								   inner join paziente on paziente.id=esami.paziente_id
						           where esami.id = ${id} and paziente.cliente_id=${cliente_id}""")
		}
	}
	
	@RequestMapping(value="/esami", method = RequestMethod.GET)
	public @ResponseBody Object getEsami(HttpServletRequest request){
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.rows("""select esami.id, esami.paziente_id, CONCAT(paziente.nome,' ',paziente.cognome) as paziente,
									  esami.descrizione, if(esami.mime_type is null,'non caricata','caricata') as img,
									  if(esami.file_mime_type is null,'non caricato','caricato') as documento,
							          esami.data, DATE_FORMAT(esami.data,'%d/%m/%Y') as data_esame 
							   from esami inner join paziente on paziente.id=esami.paziente_id
							   where paziente.cliente_id=${cliente_id}
							   order by esami.data desc""")
		}
	}
	
	@RequestMapping(value="/esami.csv", method = RequestMethod.GET)
	public void getEsamiCsv(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getEsami(request)), "esami."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/esami.xls", method = RequestMethod.GET)
	public void getEsamiXls(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getEsami(request)), "esami."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/esami.pdf", method = RequestMethod.GET)
	public void getEsamiPdf(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getEsami(request)), "esami."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
	
	@RequestMapping(value="/esami",  method = RequestMethod.PUT)
	public @ResponseBody Object insert(HttpServletRequest request, @RequestBody Object esame) throws Exception {
		sqlService.withSql { sql ->
			
			def res = sql.executeInsert("""INSERT INTO esami(paziente_id,descrizione,data)
									        VALUES(${esame.paziente_id},${esame.descrizione},${esame.data})""")
			def id = (res[0][0]).intValue()
			return getEsame(request, id)
		}
	}
	
	@RequestMapping(value="/esami/{id}",  method = RequestMethod.POST)
	public @ResponseBody Object edit(HttpServletRequest request, @PathVariable("id") Integer id, @RequestBody Object esame) throws Exception {
		sqlService.withSql { sql ->
		
			def res = sql.executeUpdate("""UPDATE esami 
									       SET paziente_id = ${esame.paziente_id},
									   	       descrizione = ${esame.descrizione},
											   data = ${esame.data}
										   WHERE id = ${id} """)			
		}
		return getEsame(request, id)
	}
	
	@RequestMapping(value="/esami/{id}/add",  method = RequestMethod.POST)
	public @ResponseBody Object addImg(HttpServletRequest request,
									   @PathVariable("id") Integer id, 
									   @RequestParam("file") MultipartFile multipartFile) throws Exception {
		sqlService.withSql { sql ->
			
			def fileIS = multipartFile.getInputStream()
			def fileType = multipartFile.getContentType()
			
			if(fileType.indexOf("image/")!=-1)
			{	sql.executeUpdate("""
								UPDATE esami 
								SET mime_type = ${fileType}, 
								    img = ${fileIS}
								WHERE id = ${id} """)
			}
		}
		return getEsame(request, id)
	}
	
	@RequestMapping(value="/esami/{id}",  method = RequestMethod.DELETE)
	public @ResponseBody Object delete(HttpServletRequest request, @PathVariable("id") Integer id) throws Exception {
		def esame = getEsame(request, id)
		sqlService.withSql { sql ->
			sql.execute("""DELETE FROM esami WHERE id = ${id}""")
			return esame
		}
	}
	
	@RequestMapping(value="/esami/{id}/img", method = RequestMethod.GET)
	public downloadImg(@PathVariable("id") Integer id,
						@RequestParam(value="thumb", required=false) String thumb,
						@RequestParam(value="r", required=false) String random,
						HttpServletResponse response) {
		sqlService.withSql { sql ->
			try{
				sql.query("""SELECT mime_type, img FROM esami WHERE id = ${id} LIMIT 1""") {
					ResultSet res ->
					if(res.next()){
						def mimeType =  res.getString(1)
						if(mimeType!=null) {
							byte[] data = res.getBytes(2)
							
							if(thumb!=null) //resize thumbnail
								data = ImageUtil.resizeImage(data)
							
							response.setContentType(mimeType)
							response.setContentLength(data.size())
							response.setHeader("Content-Disposition", "attachment;")
							FileCopyUtils.copy(data, response.getOutputStream())
						}
					}
				}
			}catch(Exception e){
				log.error("ERRORE DOWNLOAD FILE ID = "+id,e)
			}
		}
	}
						
	@RequestMapping(value="/esami/{id}/addFile",  method = RequestMethod.POST)
	public @ResponseBody Object addFile(HttpServletRequest request,
										@PathVariable("id") Integer id,
										@RequestParam("file") MultipartFile multipartFile) throws Exception {
		  sqlService.withSql { sql ->
			 
			 def fileIS = multipartFile.getInputStream()
			 def fileType = multipartFile.getContentType()
			 def fileName = multipartFile.getOriginalFilename()
			 
			  sql.executeUpdate("""
				  				UPDATE esami 
				  				SET file_mime_type = ${fileType},
								  	file = ${fileIS}
								WHERE id = ${id} """)
		 }
		 return getEsame(request, id)
	 }
								
															
	 @RequestMapping(value="/esami/{id}/file", method = RequestMethod.GET)
	 public downloadFile(@PathVariable("id") Integer id,
						 HttpServletResponse response){
		sqlService.withSql { sql ->
			try{
					sql.query("""SELECT file_mime_type, file FROM esami WHERE id = ${id} LIMIT 1""") {
					ResultSet res ->
					if(res.next()){
						def mimeType =  res.getString(1)
						def data = res.getBytes(2)				
						response.setContentType(mimeType)
						response.setContentLength(data.size())
						response.setHeader("Content-Disposition", "attachment;")
						
						FileCopyUtils.copy(data, response.getOutputStream())
					}
				}
			}catch(Exception e){
				log.error("ERRORE DOWNLOAD FILE ID = "+id,e)
			}
		}
	}
}
