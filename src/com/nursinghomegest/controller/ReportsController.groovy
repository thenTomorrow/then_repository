package com.nursinghomegest.controller

import it.etna.common.MapBuilder;

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
import com.nursinghomegest.service.ScheduleService;
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
class ReportsController {
	
	@Resource(name="ds")
	private DataSource dataSource
	
	@Resource(name="sqlService")
	private SqlService sqlService
	
	@Resource(name="scheduleService")
	private ScheduleService scheduleService
	
	
	@RequestMapping(value="/reports/scadenze", method = RequestMethod.GET)
	public @ResponseBody Object getScadenze(HttpServletRequest request) {
		
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.rows(scheduleService.getScadenzeQuery(null, null, cliente_id, null))
		}
	}
	
	@RequestMapping(value="/reports/scadenze.csv", method = RequestMethod.GET)
	public void getScadenzeCsv(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getScadenze(request)), "scadenze."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/reports/scadenze.xls", method = RequestMethod.GET)
	public void getScadenzeXls(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getScadenze(request)), "scadenze."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/reports/scadenze.pdf", method = RequestMethod.GET)
	public void getScadenzePdf(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getScadenze(request)), "scadenze."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
	
	@RequestMapping(value="/reports/scadenze/{pazienteId}", method = RequestMethod.GET)
	public @ResponseBody Object getScadenze(HttpServletRequest request, @PathVariable("pazienteId") Integer pazienteId) {
		
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.rows(scheduleService.getScadenzeQuery(pazienteId, null, cliente_id, null))
		}
	}
	
	@RequestMapping(value="/reports/{pazienteId}/scadenze.csv", method = RequestMethod.GET)
	public void getScadenzeCsv(@PathVariable("pazienteId") Integer pazienteId, 
							   HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getScadenze(request, pazienteId)), "scadenze."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/reports/{pazienteId}/scadenze.xls", method = RequestMethod.GET)
	public void getScadenzeXls(@PathVariable("pazienteId") Integer pazienteId, 
							   HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getScadenze(request, pazienteId)), "scadenze."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/reports/{pazienteId}/scadenze.pdf", method = RequestMethod.GET)
	public void getScadenzePdf(@PathVariable("pazienteId") Integer pazienteId, 
							   HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getScadenze(request, pazienteId)), "scadenze."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
							   
	@RequestMapping(value="/reports/scadenze/byfarmaco/{farmacoId}", method = RequestMethod.GET)
	public @ResponseBody Object getScadenzeByFarmaco(HttpServletRequest request, @PathVariable("farmacoId") Integer farmacoId) {
		
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.rows(scheduleService.getScadenzeQuery(null, farmacoId, cliente_id, null))
		}
	}
	
	@RequestMapping(value="/reports/byfarmaco/{farmacoId}/scadenze.csv", method = RequestMethod.GET)
	public void getScadenzeByFarmacoCsv(@PathVariable("farmacoId") Integer farmacoId, 
							  			HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getScadenzeByFarmaco(request, farmacoId)), "scadenze."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/reports/byfarmaco/{farmacoId}/scadenze.xls", method = RequestMethod.GET)
	public void getScadenzeByFarmacoXls(@PathVariable("farmacoId") Integer farmacoId, 
										HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getScadenzeByFarmaco(request, farmacoId)), "scadenze."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/reports/byfarmaco/{farmacoId}/scadenze.pdf", method = RequestMethod.GET)
	public void getScadenzeByFarmacoPdf(@PathVariable("farmacoId") Integer farmacoId, 
										HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getScadenzeByFarmaco(request, farmacoId)), "scadenze."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
										
	@RequestMapping(value="/reports/statistiche", method = RequestMethod.GET)
	public @ResponseBody Object getStatistiche(HttpServletRequest request) {
		
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.rows("""
							select paziente.id as paziente_id,
							       concat(paziente.nome,' ',paziente.cognome) as paziente,  
								   TIMESTAMPDIFF(YEAR, paziente.`data_nascita`, CURDATE()) as eta
							from `paziente`
							where paziente.`cliente_id` = ${cliente_id}
							and paziente.disabilitato = 0
							and paziente.data_nascita is not null
							order by paziente.`data_nascita`
							""")
		}
	}
	
	@RequestMapping(value="/reports/statistiche/etamedia", method = RequestMethod.GET)
	public @ResponseBody Object getStatisticheEtaMedia(HttpServletRequest request) {
		
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.firstRow("""
								select round(avg(TIMESTAMPDIFF(YEAR, paziente.`data_nascita`, CURDATE())),0) as eta_media,
									   min(TIMESTAMPDIFF(YEAR, paziente.`data_nascita`, CURDATE())) as eta_minima,
									   max(TIMESTAMPDIFF(YEAR, paziente.`data_nascita`, CURDATE())) as eta_massima
								from `paziente`
								where paziente.`cliente_id` = ${cliente_id}
								and paziente.disabilitato = 0
								and paziente.data_nascita is not null
								""")
		}
	}
}
