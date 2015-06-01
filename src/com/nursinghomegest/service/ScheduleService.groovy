package com.nursinghomegest.service

import groovy.sql.Sql

import javax.sql.DataSource;
import javax.annotation.Resource

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service("scheduleService")
class ScheduleService {
	
	@Scheduled(cron="0 31 15 * * ?")
	public void scadenze() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>ENTRATOOOOOO>>>>>>>>>>>>>>");
	}
}
