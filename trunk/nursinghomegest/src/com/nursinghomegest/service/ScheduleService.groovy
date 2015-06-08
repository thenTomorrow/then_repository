package com.nursinghomegest.service

import groovy.sql.Sql

import javax.sql.DataSource;
import javax.annotation.Resource

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.nursinghomegest.service.SqlService;

@Service("scheduleService")
class ScheduleService {
	
	@Resource(name="sqlService")
	private SqlService sqlService
	
	@Scheduled(cron="0 31 15 * * ?")
	public void scadenze() {
		
		String giorniPreavviso = null
		Object impostazione
		sqlService.withSql { sql ->
			impostazione = sql.firstRow("select nome, valore from impostazioni where nome = 'numero_giorni'")
		}
		if(impostazione!=null)
			giorniPreavviso = impostazione.valore
		else
			giorniPreavviso = null
		
		Object list = null 
		sqlService.withSql { sql ->
			list = sql.rows("""select  t.farmaco,
								       t.paziente,
								       sum(t.giorni_durata-t.giorni_passati) as giorni_rimanenti
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
								having giorni_rimanenti<=${giorniPreavviso}
								order by giorni_rimanenti""")
		}
		
		
	}
}
