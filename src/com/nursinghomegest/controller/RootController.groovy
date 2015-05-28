package com.nursinghomegest.controller

import com.nursinghomegest.service.SqlService
import com.nursinghomegest.util.ControllerUtil;
import com.nursinghomegest.util.ExportUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import groovy.sql.GroovyRowResult;
import groovy.sql.Sql
import groovy.util.logging.Log4j;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpRequest;
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

@Log4j
@Controller
class RootController {

	@Resource(name="sqlService")
	private SqlService sqlService
	
	@RequestMapping(value="/", method = RequestMethod.GET)
	public String index(HttpServletRequest request) {
		return 'index'	
	}

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login(HttpSession session) {
		return 'login'
	}

	@RequestMapping(value="/whoami", method = RequestMethod.GET)
	public @ResponseBody String whoami(HttpServletRequest request) {
		return request?.userPrincipal?.name
	}
	
	@RequestMapping(value="/login", method = RequestMethod.POST)
	public @ResponseBody Object doLogin(@RequestBody Object user, HttpServletRequest request) {
		def utente=false
		
		try{
			def isUser;
			sqlService.withSql { sql ->
				isUser = sql.firstRow("select username from utenti where username = ${user.username} and password = ${user.password}")
			}
			if(isUser){
				request.setUserPrincipal(user.username)
				utente=true
				log.info "LOGIN SUCCESS ${user.username}"				
			}
			else  {
				log.warn "LOGIN ERROR ${user.username}"
				//invalidate session
			}
		}catch(Exception e){
			log.error "LOGIN EXCEPTION ${user.username}", e
		}
		return utente
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		session?.invalidate()
		return 'login'
	}
}
