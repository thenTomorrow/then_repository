package com.nursinghomegest.controller

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Date;

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
import com.nursinghomegest.service.SqlService;
import com.nursinghomegest.util.ControllerUtil;
import com.nursinghomegest.util.ExportUtil;

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
class FarmaciController {
	
	@Resource(name="ds")
	private DataSource dataSource
	
	@Resource(name="sqlService")
	private SqlService sqlService
			
	@RequestMapping(value="/farmaci/{id}", method = RequestMethod.GET)
	public @ResponseBody Object getFarmaco(@PathVariable("id") Integer id) {
		sqlService.withSql { sql ->
			return sql.firstRow("select id, descrizione, quantita_per_pezzo from farmaco where id = ${id}")
		}
	}
	
	@RequestMapping(value="/farmaci", method = RequestMethod.GET)
	public @ResponseBody Object getFarmaci(){
		sqlService.withSql { sql ->
			return sql.rows("select id, descrizione, quantita_per_pezzo from farmaco")
		}
	}
	
	@RequestMapping(value="/farmaci.csv", method = RequestMethod.GET)
	public void getFarmaciCsv(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getFarmaci()), "farmaci."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/farmaci.xls", method = RequestMethod.GET)
	public void getFarmaciXls(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getFarmaci()), "farmaci."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/farmaci.pdf", method = RequestMethod.GET)
	public void getFarmaciPdf(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getFarmaci()), "farmaci."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
	
	@RequestMapping(value="/farmaci",  method = RequestMethod.PUT)
	public @ResponseBody Object insert(@RequestBody Object farmaco) throws Exception {
		sqlService.withSql { sql ->
			
			def res = sql.executeInsert("""INSERT INTO farmaco(descrizione,quantita_per_pezzo)
									       VALUES(${farmaco.descrizione},${farmaco.quantita_per_pezzo})""")
			def id = (res[0][0]).intValue()
			return getFarmaco(id)
		}
	}
	
	@RequestMapping(value="/farmaci/{id}",  method = RequestMethod.POST)
	public @ResponseBody Object edit(@PathVariable("id") Integer id, @RequestBody Object farmaco) throws Exception {
		sqlService.withSql { sql ->
		
			def res = sql.executeUpdate("""UPDATE farmaco 
									       SET descrizione = ${farmaco.descrizione},
										   	   quantita_per_pezzo = ${farmaco.quantita_per_pezzo}
										   WHERE id = ${id} """)			
		}
		return getFarmaco(id)
	}
	
	@RequestMapping(value="/farmaci/{id}",  method = RequestMethod.DELETE)
	public @ResponseBody Object delete(@PathVariable("id") Integer id) throws Exception {
		def farmaco = getFarmaco(id)
		sqlService.withSql { sql ->
			sql.execute("""DELETE FROM farmaco WHERE id = ${id}""")
			return farmaco
		}
	}
}
