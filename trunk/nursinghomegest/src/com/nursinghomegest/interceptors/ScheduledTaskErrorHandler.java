package com.nursinghomegest.interceptors;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

@Service
public class ScheduledTaskErrorHandler implements ErrorHandler,ApplicationContextAware{
	
	private static Logger logger = Logger.getLogger(ScheduledTaskErrorHandler.class);
	
	private ApplicationContext ctx = null;
	
	
	@Override
	public void handleError(Throwable t) {
		 logger.error("Error occurred while executing scheduled task "+t.getMessage());		 
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}

	public ApplicationContext getApplicationContext() {
		return ctx;
	}

	
	
	
	
}
