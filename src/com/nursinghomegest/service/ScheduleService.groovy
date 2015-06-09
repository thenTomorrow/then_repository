package com.nursinghomegest.service

import com.nursinghomegest.service.MailService;

import groovy.sql.Sql

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
	
//	@Scheduled(cron="0 31 15 * * ?")
//	public void scadenze() {
//		
//		String giorniPreavviso = impostazioniService.getImpostazione("numero_giorni")
//		String mailTo = impostazioniService.getImpostazione("email_scadenze")	
//		
//		Object list = null 
//		sqlService.withSql { sql ->
//			list = sql.rows("""select  t.farmaco,
//								       t.paziente,
//								       sum(if(t.giorni_durata-t.giorni_passati<0,0,t.giorni_durata-t.giorni_passati)) as giorni_rimanenti
//								from
//								(
//								select farmaco.id as farmaco_id, 
//									   farmaco.`descrizione` as farmaco,
//								       paziente.id as paziente_id,	
//								       concat(paziente.nome,' ',paziente.cognome) as paziente,
//									   farmaco.`quantita_per_pezzo`/`somministrazione`.`quantita` as giorni_durata,
//								       DATEDIFF(NOW(),somministrazione.`data_inserimento`) as giorni_passati
//								from 
//								`somministrazione`
//								inner join farmaco on farmaco.id = somministrazione.`farmaco_id`
//								inner join paziente on paziente.id = somministrazione.`paziente_id`
//							    where paziente.disabilitato = 0
//								) t
//								group by t.farmaco_id, t.paziente_id
//								having giorni_rimanenti<=${giorniPreavviso}
//								order by giorni_rimanenti""")
//		}
//		
//		if(list!=null) {
//		
//			String subject = "Scadenze Farmaci"
//			String messaggio = "<table>"	
//			
//			list.each { element ->
//				messaggio+="<tr><td>"element.farmaco+"</td><td>"+element.paziente+"<td></td>"+element.giorni_rimanenti+"</td></tr>"
//			}
//			messaggio+="</table>"
//			
//			// Invio l'email
//			try {
//				mailService.send(
//						new MailBuilder()
//						.subject(subject)
//						.html(messaggio)
//						.addTo(mailTo)
//						.from("then.poidomani@gmail.com")
//						.message()
//						)
//				logger.info("Inviata richiesta cliente")
//				
//			}catch(Exception e) {
//				logger.error("Errore invio richiesta cliente")
//			}
//		}
//	}
}
