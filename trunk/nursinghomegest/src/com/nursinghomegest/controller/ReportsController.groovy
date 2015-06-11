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
	
	@RequestMapping(value="/reports/scadenze", method = RequestMethod.GET)
	public @ResponseBody Object getScadenze() {
		
		sqlService.withSql { sql ->
			return sql.rows("""select t.farmaco_id,
									  t.farmaco,
									  t.paziente_id, 
								      t.paziente,
								      round(sum(if(t.giorni_durata-t.giorni_passati<0,0,t.giorni_durata-t.giorni_passati)),0) as giorni_rimanenti
								from
								(
								select farmaco.id as farmaco_id, 
									   farmaco.`descrizione` as farmaco,
								       paziente.id as paziente_id,	
								       concat(paziente.nome,' ',paziente.cognome) as paziente,
									   farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita` as giorni_durata,
								       DATEDIFF(NOW(),somministrazione.`data_inserimento`) as giorni_passati
								from 
								`somministrazione`
								inner join farmaco on farmaco.id = somministrazione.`farmaco_id`
								inner join paziente on paziente.id = somministrazione.`paziente_id`
							    where paziente.disabilitato = 0
								) t
								group by t.farmaco_id, t.paziente_id
								order by giorni_rimanenti""")
		}
	}
	
	@RequestMapping(value="/reports/scadenze.csv", method = RequestMethod.GET)
	public void getScadenzeCsv(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getScadenze()), "scadenze."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/reports/scadenze.xls", method = RequestMethod.GET)
	public void getScadenzeXls(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getScadenze()), "scadenze."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/reports/scadenze.pdf", method = RequestMethod.GET)
	public void getScadenzePdf(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getScadenze()), "scadenze."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
	
	@RequestMapping(value="/reports/scadenze/{pazienteId}", method = RequestMethod.GET)
	public @ResponseBody Object getScadenze(@PathVariable("pazienteId") Integer pazienteId) {
		
		sqlService.withSql { sql ->
		return sql.rows("""select  t.farmaco_id,
								   t.farmaco,
								   t.paziente_id, 
								   t.paziente,
								   round(sum(if(t.giorni_durata-t.giorni_passati<0,0,t.giorni_durata-t.giorni_passati)),0) as giorni_rimanenti
						   from
						   (
						   select farmaco.id as farmaco_id, 
						   farmaco.`descrizione` as farmaco,
						   paziente.id as paziente_id,	
						   concat(paziente.nome,' ',paziente.cognome) as paziente,
						   farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita` as giorni_durata,
						   DATEDIFF(NOW(),somministrazione.`data_inserimento`) as giorni_passati
						   from 
						   `somministrazione`
						   inner join farmaco on farmaco.id = somministrazione.`farmaco_id`
						   inner join paziente on paziente.id = somministrazione.`paziente_id`
						   where paziente.id = ${pazienteId}
						   ) t
						   group by t.farmaco_id, t.paziente_id
						   order by giorni_rimanenti""")
		}
	}
	
	@RequestMapping(value="/reports/{pazienteId}/scadenze.csv", method = RequestMethod.GET)
	public void getScadenzeCsv(@PathVariable("pazienteId") Integer pazienteId, 
							   HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getScadenze(pazienteId)), "scadenze."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/reports/{pazienteId}/scadenze.xls", method = RequestMethod.GET)
	public void getScadenzeXls(@PathVariable("pazienteId") Integer pazienteId, 
							   HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getScadenze(pazienteId)), "scadenze."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/reports/{pazienteId}/scadenze.pdf", method = RequestMethod.GET)
	public void getScadenzePdf(@PathVariable("pazienteId") Integer pazienteId, 
							   HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getScadenze(pazienteId)), "scadenze."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
							   
	@RequestMapping(value="/reports/scadenze/byfarmaco/{farmacoId}", method = RequestMethod.GET)
	public @ResponseBody Object getScadenzeByFarmaco(@PathVariable("farmacoId") Integer farmacoId) {
		
		sqlService.withSql { sql ->
		return sql.rows("""select  t.farmaco_id,
									t.farmaco,
									t.paziente_id, 
									t.paziente,
									round(sum(if(t.giorni_durata-t.giorni_passati<0,0,t.giorni_durata-t.giorni_passati)),0) as giorni_rimanenti
							from
							(
							select farmaco.id as farmaco_id, 
							farmaco.`descrizione` as farmaco,
							paziente.id as paziente_id,	
							concat(paziente.nome,' ',paziente.cognome) as paziente,
							farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita` as giorni_durata,
							DATEDIFF(NOW(),somministrazione.`data_inserimento`) as giorni_passati
							from 
							`somministrazione`
							inner join farmaco on farmaco.id = somministrazione.`farmaco_id`
							inner join paziente on paziente.id = somministrazione.`paziente_id`
							where farmaco.id = ${farmacoId}
							) t
							group by t.farmaco_id, t.paziente_id
							order by giorni_rimanenti""")
		}
	}
	
	@RequestMapping(value="/reports/byfarmaco/{farmacoId}/scadenze.csv", method = RequestMethod.GET)
	public void getScadenzeByFarmacoCsv(@PathVariable("farmacoId") Integer farmacoId, 
							  			HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getScadenzeByFarmaco(farmacoId)), "scadenze."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/reports/byfarmaco/{farmacoId}/scadenze.xls", method = RequestMethod.GET)
	public void getScadenzeByFarmacoXls(@PathVariable("farmacoId") Integer farmacoId, 
										HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getScadenzeByFarmaco(farmacoId)), "scadenze."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/reports/byfarmaco/{farmacoId}/scadenze.pdf", method = RequestMethod.GET)
	public void getScadenzeByFarmacoPdf(@PathVariable("farmacoId") Integer farmacoId, 
										HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getScadenzeByFarmaco(farmacoId)), "scadenze."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
}
