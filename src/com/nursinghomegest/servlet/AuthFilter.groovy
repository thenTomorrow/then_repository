package com.nursinghomegest.servlet;

import groovy.util.logging.Log4j;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Log4j
public class AuthFilter implements Filter {
	
	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		def ctxPath = request.getServletContext().getContextPath()
		def ctrlPath = request.getRequestURI().substring(ctxPath.length())
		def wrapper = new ReqWrapper((HttpServletRequest) request)
		
		if(wrapper?.userPrincipal?.name==null && 
			ctrlPath!='/login' && 
			!ctrlPath.startsWith('/js/') && 
			!ctrlPath.startsWith('/css/') && 
			!ctrlPath.startsWith('/img/')) {			
			response.sendRedirect("${ctxPath}/login")
		}		
		else {		
			chain.doFilter(wrapper, response)
		}
	}
}

class ReqWrapper extends HttpServletRequestWrapper {

	private Principal principal
	
	public ReqWrapper(HttpServletRequest request) {
		super(request)
	}
	
	@Override
	public Principal getUserPrincipal() {
		return ((HttpServletRequest) request).getSession()?.getAttribute("principal")
	}
	
	public void setUserPrincipal(final Principal principal) {
		((HttpServletRequest) request).getSession()?.setAttribute("principal", principal)
	}
	
	public void setUserPrincipal(final String username) {
		setUserPrincipal(new Principal() {
			@Override
			public String getName() {
				return username
			}			
			public boolean implies(Subject subject) {
				if (subject == null)
					return false
				return subject.getPrincipals().contains(this)
			}
		})		
	}	
}
