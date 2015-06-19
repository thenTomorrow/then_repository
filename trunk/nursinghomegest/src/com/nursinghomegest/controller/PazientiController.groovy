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
class PazientiController {
	
	@Resource(name="ds")
	private DataSource dataSource
	
	@Resource(name="sqlService")
	private SqlService sqlService
			
	@RequestMapping(value="/pazienti/{id}", method = RequestMethod.GET)
	public @ResponseBody Object getPaziente(HttpServletRequest request, @PathVariable("id") Integer id) {
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.firstRow("""select id, 
										  nome, 
										  cognome,
										  cf, 
										  comune_nascita, 
										  data_nascita,
										  comune_residenza,
										  indirizzo_residenza,
										  medico_curante,
										  esenzione_ticket,
										  disabilitato,
										  if(disabilitato=0,'PRESENTE','ASSENTE') stato 
								   from paziente 
								   where cliente_id = ${cliente_id} and id = ${id}""")
		}
	}
	
	@RequestMapping(value="/pazienti", method = RequestMethod.GET)
	public @ResponseBody Object getPazienti(HttpServletRequest request){
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.rows("""select id, 
									  nome, 
									  cognome,
									  cf, 
									  comune_nascita, 
									  DATE_FORMAT(data_nascita,'%d/%m/%Y') data_nascita,
									  comune_residenza,
									  indirizzo_residenza,
									  medico_curante,
									  esenzione_ticket,
									  disabilitato,
									  if(disabilitato=0,'PRESENTE','ASSENTE') stato
							   from paziente 
							   where disabilitato = 0 and cliente_id = ${cliente_id}
							   order by nome, cognome""")
		}
	}
	
	@RequestMapping(value="/pazientiAll", method = RequestMethod.GET)
	public @ResponseBody Object getPazientiAll(HttpServletRequest request){
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			return sql.rows("""select id, 
									  nome, 
									  cognome,
									  cf, 
									  comune_nascita, 
									  DATE_FORMAT(data_nascita,'%d/%m/%Y') data_nascita,
									  comune_residenza,
									  indirizzo_residenza,
									  medico_curante,
									  esenzione_ticket,
									  disabilitato,
									  if(disabilitato=0,'PRESENTE','ASSENTE') stato
							   from paziente 
							   where cliente_id = ${cliente_id}
							   order by nome, cognome""")
		}
	}
	
	@RequestMapping(value="/pazienti.csv", method = RequestMethod.GET)
	public void getPazientiCsv(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getPazienti(request)), "pazienti."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/pazienti.xls", method = RequestMethod.GET)
	public void getPazientiXls(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getPazienti(request)), "pazienti."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/pazienti.pdf", method = RequestMethod.GET)
	public void getPazientiPdf(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getPazienti(request)), "pazienti."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
	
	@RequestMapping(value="/pazienti",  method = RequestMethod.PUT)
	public @ResponseBody Object insert(HttpServletRequest request, @RequestBody Object paziente) throws Exception {
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			def res = sql.executeInsert("""INSERT INTO paziente(nome,cognome,cf,comune_nascita,data_nascita,comune_residenza,indirizzo_residenza,medico_curante,esenzione_ticket,cliente_id)
									       VALUES(${paziente.nome},${paziente.cognome},${paziente.cf},${paziente.comune_nascita},${paziente.data_nascita},${paziente.comune_residenza},${paziente.indirizzo_residenza},${paziente.medico_curante},${paziente.esenzione_ticket},${cliente_id})""")
			def id = (res[0][0]).intValue()
			return getPaziente(request, id)
		}
	}
	
	@RequestMapping(value="/pazienti/{id}",  method = RequestMethod.POST)
	public @ResponseBody Object edit(HttpServletRequest request, @PathVariable("id") Integer id, @RequestBody Object paziente) throws Exception {
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			def res = sql.executeUpdate("""UPDATE paziente 
									       SET nome = ${paziente.nome},
										   	   cognome = ${paziente.cognome},
											   cf = ${paziente.cf}, 
											   comune_nascita = ${paziente.comune_nascita}, 
											   data_nascita = ${paziente.data_nascita},
											   comune_residenza = ${paziente.comune_residenza},
											   indirizzo_residenza = ${paziente.indirizzo_residenza},
											   medico_curante = ${paziente.medico_curante},
											   esenzione_ticket = ${paziente.esenzione_ticket}
										   WHERE id = ${id} and cliente_id = ${cliente_id}""")			
		}
		return getPaziente(request, id)
	}
	
	@RequestMapping(value="/pazienti/{id}/disabilita",  method = RequestMethod.DELETE)
	public @ResponseBody Object disabilita(HttpServletRequest request, @PathVariable("id") Integer id) throws Exception {
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			def res = sql.executeUpdate("""UPDATE paziente 
									       SET disabilitato = 1
										   WHERE id = ${id} and cliente_id = ${cliente_id}""")			
		}
		return getPaziente(request, id)
	}
	
	@RequestMapping(value="/pazienti/{id}/riabilita",  method = RequestMethod.DELETE)
	public @ResponseBody Object riabilita(HttpServletRequest request, @PathVariable("id") Integer id) throws Exception {
		sqlService.withSql { sql ->
			Integer cliente_id = request.getSession().getAttribute("cliente_id")
			def res = sql.executeUpdate("""UPDATE paziente 
									   	   SET disabilitato = 0
									       WHERE id = ${id} and cliente_id = ${cliente_id}""")			
		}
		return getPaziente(request, id)
	}
}
