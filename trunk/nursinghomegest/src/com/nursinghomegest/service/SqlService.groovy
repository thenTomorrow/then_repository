package com.nursinghomegest.service

import groovy.sql.Sql
import javax.sql.DataSource;
import javax.annotation.Resource
import org.springframework.stereotype.Service;

@Service("sqlService")
class SqlService {
	
	@Resource(name="ds")
	private DataSource dataSource
	
	public withSql(Closure c) {
		def gsql = new Sql(dataSource)
		try {
			c.call(gsql)
		}
		finally {
			gsql?.close()
		}
	}
}
