package com.nursinghomegest.util;

import com.nursinghomegest.service.MailService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

class ClientMail implements MailService {
	
	private static Logger logger = Logger.getLogger(ClientMail.class)

	private String mailHost = ""
	private String mailPort = ""

	public void send(MimeMessage msg) throws MessagingException, IOException {
		
		try{
			String from = ""
			for(Address addr: msg.getFrom()) {
				from += " "+addr
			}
			String to = ""
			for(Address addr: msg.getAllRecipients()) {
				to += " "+addr.getType()+":"+addr.toString()
			}
			Properties props = new Properties()
			props.put("mail.smtp.host", mailHost)
			props.put("mail.smtp.port", mailPort)
			props.put("mail.smtp.auth", true)
//			props.put("mail.smtp.starttls.enable", true)
//			props.put("mail.smtp.socketFactory.port",  mailPort);
//			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			Session.getDefaultInstance(props, null)
			Transport.send(msg, "g.poidomani@e-tna.it", "gp310881")
			logger.info("Mail inviata con oggetto \""+msg.getSubject()+"\", from"+from+", to"+to)
		}catch(Exception e) {
			logger.error("Errore nell'invio della mail", e)
		}
	}
	
	public String getMailHost() {
		return mailHost;
	}
	
	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public void setMailPort(String mailPort) {
		this.mailPort = mailPort;
	}
	
	public String getMailPort() {
		return mailPort;
	}
}
