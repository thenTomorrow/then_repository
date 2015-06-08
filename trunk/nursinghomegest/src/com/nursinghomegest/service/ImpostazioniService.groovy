package com.nursinghomegest.service

import groovy.sql.Sql

import javax.sql.DataSource;
import javax.annotation.Resource

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Service("impostazioniService")
class ImpostazioniService {
	
	@Resource(name="sqlService")
	private SqlService sqlService
	
	public @ResponseBody Object getImpostazione(String nome) {
		Object impostazione
		sqlService.withSql { sql ->
			impostazione = sql.firstRow("select nome, valore from impostazioni where nome = ${nome}")
		}
		if(impostazione!=null)
			return impostazione.valore
		else
			return null
	}
}
