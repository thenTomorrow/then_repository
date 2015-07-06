package com.nursinghomegest.service

import com.nursinghomegest.service.MailService;

import groovy.sql.Sql

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.annotation.Resource

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.nursinghomegest.service.SqlService;
import com.nursinghomegest.util.mail.MailBuilder

@Service("scheduleService")
class ScheduleService {
	
	private static Logger logger = Logger.getLogger(ScheduleService.class)
	
	@Resource(name="sqlService")
	private SqlService sqlService
	@Resource(name="impostazioniService")
	private ImpostazioniService impostazioniService
	@Resource
	private MailService mailService
	
	@Scheduled(cron="0 30 8 * * ?")
	public void scadenze() {
		
		def clienti = []
		sqlService.withSql { sql ->
			clienti = sql.rows("select * from cliente")
		}
		
		clienti.each { cliente ->
			String giorniPreavviso = impostazioniService.getImpostazione("numero_giorni", cliente.id)
			String mailToList = impostazioniService.getImpostazione("email_scadenze", cliente.id)
			String[] mailTo = mailToList!=null?mailToList.split(","):null	
			
			def list = []
			if(giorniPreavviso!=null && mailTo!=null) {
				sqlService.withSql { sql ->
					list = sql.rows(getScadenzeQuery(null, null, cliente.id, giorniPreavviso))
				}
			}
			
			if(list) {
			
				String subject = "Scadenze Farmaci - "+cliente.denominazione;
				String messaggio = """<style type='text/css'> table.altrowstable { font-family: verdana,arial,sans-serif; font-size:11px; color:#333333;  
								   	  border-width: 1px; border-color: #a9c6c9;  border-collapse: collapse;}  
								   	  table.altrowstable th { border-width: 1px; padding: 8px; border-style: solid; border-color: #a9c6c9;  text-align: left;} 
								   	  table.altrowstable td { border-width: 1px; padding: 8px; border-style: solid; border-color: #a9c6c9; } 
								   	  .oddrowcolor{ background-color:#d4e3e5; }.evenrowcolor{background-color:#c3dde0; } 
								      </style>  
								      <html> 
	  							      <head> 
								      <title>Scadenze Farmaci</title>
								      </head> 
								      <body>
									  <h4>I seguenti farmaci sono in scadenza:</h4>
								      <table class='altrowstable'>
								      <tr><th>Farmaco</th><th>Paziente</th><th>Giorni Rimanenti</th></tr>"""
				
				list.each { element ->
					Integer giorniRim = element.giorni_rimanenti
					messaggio+="""<tr><td>${element.farmaco}</td><td>${element.paziente}</td><td>${giorniRim} giorni rimanenti</td></tr>"""
				}
				
				messaggio+="""</table>
						   	  </body>
						   	  </html>"""
				
				// Invio l'email
				try {
					mailService.send(
							new MailBuilder()
							.subject(subject)
							.html(messaggio)
							.addTo(mailTo)
							.from("then.poidomani@gmail.com")
							.message()
							)
					logger.info("Inviata mail scadenze farmaci cliente "+cliente.id)
					
				}catch(Exception e) {
					logger.error("Errore invio mail scadenze farmaci cliente "+cliente.id)
				}
			}
		}
	}
	
	@Scheduled(cron="0 45 8 * * ?")
	public void compleanni() {
		
		def clienti = []
		sqlService.withSql { sql ->
			clienti = sql.rows("select * from cliente")
		}
		
		clienti.each { cliente ->
			String mailToList = impostazioniService.getImpostazione("email_scadenze", cliente.id)
			String[] mailTo = mailToList!=null?mailToList.split(","):null	
					
			def list = []
			if(mailTo!=null) {
				sqlService.withSql { sql ->
					list = sql.rows(getCompleanniQuery(cliente.id, "2"))
				}
			}
			
			if(list) {
				list.each { element ->
					String subject = "Notifica Compleanno - "+cliente.denominazione;
					String messaggio = """<html> 
										  <head> 
										  <title>Notifica Compleanno</title>
										  </head> 
										  <body>
										  <h4>Tra 2 giorni il paziente <span style='color:blue'>${element.paziente}</span> spegner&agrave; le sue <span style='color:blue'>${element.eta+1}</span> candeline!</h4>
										  <h3 style='color:green'>AUGURI!!!!</h3>
										  <i>PS: Ricordati di conservare una fetta di torta per il programmatore :-P</i>
								 		  </body>
										  </html>"""
							
							// Invio l'email
							try {
								mailService.send(
										new MailBuilder()
										.subject(subject)
										.html(messaggio)
										.addTo(mailTo)
										.from("then.poidomani@gmail.com")
										.message()
										)
								logger.info("Inviata mail compleanno cliente "+cliente.id)
								
							}catch(Exception e) {
								logger.error("Errore invio mail compleanno cliente "+cliente.id)
							}
					}
			}
		}
	}
	
	public String getScadenzeQuery(Integer pazienteId, Integer farmacoId, Integer cliente_id, String giorniPreavviso) {
		
		String query = """select t.farmaco_id,
								  t.farmaco,
								  t.paziente_id,
							      t.paziente,
								  cast(sum(if(t.giorni_durata-t.giorni_passati<0,0,t.giorni_durata-t.giorni_passati)) as signed integer) as giorni_rimanenti
							from
							(
							select farmaco.id as farmaco_id, 
							       farmaco.`descrizione` as farmaco,
							       paziente.id as paziente_id,	
							       concat(paziente.nome,' ',paziente.cognome) as paziente,
								   round(farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita`,0) as giorni_durata,
							       if(DATEDIFF(NOW(),somministrazione.`data_inizio`)<0,0,DATEDIFF(NOW(),somministrazione.`data_inizio`)) as giorni_passati
							from 
							`somministrazione`
							inner join farmaco on farmaco.id = somministrazione.`farmaco_id`
							inner join paziente on paziente.id = somministrazione.`paziente_id` """
		
		if(pazienteId==null && farmacoId==null)
			query+="""where paziente.disabilitato = 0 """
		else if(pazienteId!=null && farmacoId==null)
			query+="""where paziente.id = ${pazienteId} """
		else if(pazienteId==null && farmacoId!=null)
			query+="""where farmaco.id = ${farmacoId} """
		else 
			query+="""where paziente.id = ${pazienteId} and farmaco.id = ${farmacoId} """		
		
		query+="""and paziente.cliente_id = ${cliente_id} and farmaco.cliente_id = ${cliente_id}
				  and farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita`-DATEDIFF(NOW(),somministrazione.`data_inizio`)>=-3
				  ) t
				  group by t.farmaco_id, t.paziente_id """
		
	    if(giorniPreavviso!=null)
			query+="""having giorni_rimanenti<=${giorniPreavviso} """
		
		query+="""order by giorni_rimanenti """
		
		return query
	}
	
	public String getCompleanniQuery(Integer cliente_id, String giorniPreavviso) {
		
		String query = """select paziente.id as paziente_id,
							       concat(paziente.nome,' ',paziente.cognome) as paziente,  
								   paziente.data_nascita,
							       DATE_FORMAT(paziente.data_nascita,'%d/%m/%Y') data_nascita_string,
							       if(DATEDIFF(CONCAT(YEAR(CURDATE()),'-',LPAD(MONTH(paziente.`data_nascita`),2,'00'),'-',LPAD(DAY(paziente.`data_nascita`),2,'00')), CURDATE())<0,
							          DATEDIFF(CONCAT(YEAR(CURDATE())+1,'-',LPAD(MONTH(paziente.`data_nascita`),2,'00'),'-',LPAD(DAY(paziente.`data_nascita`),2,'00')), CURDATE()),
							          DATEDIFF(CONCAT(YEAR(CURDATE()),'-',LPAD(MONTH(paziente.`data_nascita`),2,'00'),'-',LPAD(DAY(paziente.`data_nascita`),2,'00')), CURDATE())
							         )as num_giorni_compleanno,
								   if(DATEDIFF(CONCAT(YEAR(CURDATE()),'-',LPAD(MONTH(paziente.`data_nascita`),2,'00'),'-',LPAD(DAY(paziente.`data_nascita`),2,'00')), CURDATE())<0,
							          CONCAT(LPAD(DAY(paziente.`data_nascita`),2,'00'),'/',LPAD(MONTH(paziente.`data_nascita`),2,'00'),'/',YEAR(CURDATE())+1),
							          CONCAT(LPAD(DAY(paziente.`data_nascita`),2,'00'),'/',LPAD(MONTH(paziente.`data_nascita`),2,'00'),'/',YEAR(CURDATE()))
							         )as giorno_compleanno,
								   TIMESTAMPDIFF(YEAR, paziente.`data_nascita`, CURDATE()) as eta
							from `paziente`
							where paziente.`cliente_id` = ${cliente_id}
							and paziente.disabilitato = 0
							and paziente.data_nascita is not null
						"""
		
		if(giorniPreavviso!=null)
			query+="""having num_giorni_compleanno<=${giorniPreavviso} """
		query+="""order by paziente.`data_nascita`"""				
						
		return query
	}
}
