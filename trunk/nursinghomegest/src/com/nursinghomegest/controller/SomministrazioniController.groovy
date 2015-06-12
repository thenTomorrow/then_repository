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
class SomministrazioniController {
	
	@Resource(name="ds")
	private DataSource dataSource
	
	@Resource(name="sqlService")
	private SqlService sqlService
			
	@RequestMapping(value="/somministrazioni/{id}", method = RequestMethod.GET)
	public @ResponseBody Object getSomministrazione(@PathVariable("id") Integer id) {
		sqlService.withSql { sql ->
			return sql.firstRow("""select id, farmaco_id, paziente_id, quantita, data_inserimento, data_inizio 
								   from somministrazione 
								   where id = ${id}""")
		}
	}
	
	@RequestMapping(value="/somministrazioni", method = RequestMethod.GET)
	public @ResponseBody Object getSomministrazioni(){
		sqlService.withSql { sql ->
			return sql.rows("""select somministrazione.id, 
									  farmaco.id as farmaco_id,
									  farmaco.descrizione as farmaco,
									  paziente.id as paziente_id,
									  concat(paziente.nome,' ',paziente.cognome) as paziente,
									  somministrazione.quantita, 
									  DATE_FORMAT(somministrazione.data_inserimento,'%d/%m/%Y') as data_inserimento_string,
									  somministrazione.data_inserimento,
									  DATE_FORMAT(somministrazione.data_inizio,'%d/%m/%Y') as data_inizio_string,
									  somministrazione.data_inizio
							   from somministrazione
							   inner join paziente on paziente.id = somministrazione.paziente_id
							   inner join farmaco on farmaco.id = somministrazione.farmaco_id
							   where paziente.disabilitato = 0
							   and farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita`-DATEDIFF(NOW(),somministrazione.`data_inizio`)>=0
							   order by somministrazione.id desc""")
		}
	}
	
	@RequestMapping(value="/somministrazioni.csv", method = RequestMethod.GET)
	public void getFarmaciCsv(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportCsv(getSomministrazioni()), "somministrazioni."+ControllerUtil.EXT_CSV, ControllerUtil.MIME_CSV)
	}
	
	@RequestMapping(value="/somministrazioni.xls", method = RequestMethod.GET)
	public void getFarmaciXls(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportExcel(getSomministrazioni()), "somministrazioni."+ControllerUtil.EXT_XLS, ControllerUtil.MIME_XLS)
	}
	
	@RequestMapping(value="/somministrazioni.pdf", method = RequestMethod.GET)
	public void getFarmaciPdf(HttpServletRequest request, HttpServletResponse response){
		
		ControllerUtil.sendFile(response, ExportUtil.exportPdf(getSomministrazioni()), "somministrazioni."+ControllerUtil.EXT_PDF, ControllerUtil.MIME_PDF)
	}
	
	@RequestMapping(value="/somministrazioni",  method = RequestMethod.PUT)
	public @ResponseBody Object insert(@RequestBody Object somministrazione) throws Exception {
		sqlService.withSql { sql ->
			
			for(int i=0; i<Integer.parseInt(""+somministrazione.quantita_pacchi); i++) {
				def res = sql.executeInsert("""INSERT INTO somministrazione(farmaco_id,paziente_id,quantita,data_inserimento)
									       VALUES(${somministrazione.farmaco_id},${somministrazione.paziente_id},${somministrazione.quantita},${somministrazione.data_inserimento})""")
				def id = (res[0][0]).intValue()
				
				if(id!=null) {
					// aggiorno la data_inizio
					def dataInizio = somministrazione.data_inserimento
					dataInizio = sql.firstRow("""select 
												 ifnull(GREATEST(somministrazione.`data_inserimento`,
												 (
												 select DATE_ADD(`somministrazione`.`data_inizio`,INTERVAL round(farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita`,0) DAY) as data_fine
												 from somministrazione 
												 inner join farmaco on farmaco.id = somministrazione.`farmaco_id`
												 where somministrazione.farmaco_id = ${somministrazione.farmaco_id}
												 and somministrazione.paziente_id = ${somministrazione.paziente_id}
												 and farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita`-DATEDIFF(NOW(),somministrazione.`data_inizio`)>=0
												 order by somministrazione.data_inizio DESC
												 limit 1
												 )
												 ),somministrazione.data_inserimento) as data_inizio
												 from somministrazione
												 where somministrazione.id = ${id}""").data_inizio
					
					sql.executeUpdate("""UPDATE somministrazione
										SET data_inizio = ${dataInizio}
										WHERE id = ${id} """)
				}
			}
			return somministrazione.quantita_pacchi
		}
	}
	
	@RequestMapping(value="/somministrazioni/{id}",  method = RequestMethod.DELETE)
	public @ResponseBody Object delete(@PathVariable("id") Integer id) throws Exception {
		def somministrazione = getSomministrazione(id)
		sqlService.withSql { sql ->
			sql.execute("""DELETE FROM somministrazione WHERE id = ${id}""")
			sql.execute("""CALL update_data_inizio_params(${somministrazione.paziente_id},${somministrazione.farmaco_id})""")
			return somministrazione
		}
	}
}
