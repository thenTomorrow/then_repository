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
import com.nursinghomegest.service.ImpostazioniService;
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
class ImpostazioniController {
	
	@Resource(name="ds")
	private DataSource dataSource
	
	@Resource(name="sqlService")
	private SqlService sqlService
	
	@Resource(name="impostazioniService")
	private ImpostazioniService impostazioniService
	
	@RequestMapping(value="/impostazioni/{nome}", method = RequestMethod.GET)
	public @ResponseBody Object getImpostazione(@PathVariable("nome") String nome) {
		return impostazioniService.getImpostazione(nome)
	}
	
	@RequestMapping(value="/impostazioni",  method = RequestMethod.POST)
	public @ResponseBody Object insertOrUpdate(@RequestBody Object impostazione) throws Exception {
		
		 sqlService.withSql { sql ->
			sql.execute("""INSERT INTO impostazioni (nome, valore) VALUES(${impostazione.nome}, ${impostazione.valore}) ON DUPLICATE KEY UPDATE    
						   nome=${impostazione.nome}, valore=${impostazione.valore}""")			
		 }
		 return getImpostazione(impostazione.nome)
	}
}
