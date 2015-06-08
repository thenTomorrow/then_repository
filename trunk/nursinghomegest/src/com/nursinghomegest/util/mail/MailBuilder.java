package com.nursinghomegest.util.mail;

import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class MailBuilder {

	private static final String PLAIN_TEXT = "text/plain";
	private static final String HTML_TEXT = "text/html";
	
	private MimeMessage message;
	private LinkedList<MimeBodyPart> parts = new LinkedList<MimeBodyPart>();
	
	public MailBuilder() {
		message = new MimeMessage((Session) null);
	}
	
	public MailBuilder from(String from) throws AddressException, MessagingException {
		message.setFrom(new InternetAddress(from));
		return this;
	}
	
	public MailBuilder subject(String subject) throws AddressException, MessagingException {
		message.setSubject(subject);
		return this;
	}

	public MailBuilder addTo(String ...addresses) throws AddressException, MessagingException {
		for(String a: addresses)
			addRecipient(new RecipientTo(a));
		return this;
	}
	
	public MailBuilder addTo(Collection<String> addresses) throws AddressException, MessagingException {
		addTo(addresses.toArray(new String[addresses.size()]));
		return this;
	}
	
	public MailBuilder addBcc(String ...addresses) throws AddressException, MessagingException {
		for(String a: addresses)
			addRecipient(new RecipientBcc(a));
		return this;
	}

	public MailBuilder addBcc(Collection<String> addresses) throws AddressException, MessagingException {
		addBcc(addresses.toArray(new String[addresses.size()]));
		return this;
	}

	public MailBuilder addCc(String ...addresses) throws AddressException, MessagingException {
		for(String a: addresses)
			addRecipient(new RecipientCc(a));
		return this;
	}
	
	public MailBuilder addCc(Collection<String> addresses) throws AddressException, MessagingException {
		addCc(addresses.toArray(new String[addresses.size()]));
		return this;
	}
	
	public MailBuilder addRecipient(Recipient ...recipients) throws AddressException, MessagingException {
		for(Recipient r: recipients) {
			message.addRecipients(r.getRecipientType(), new InternetAddress[] { new InternetAddress(r.getAddress()) });
		}
		return this;
	}
	
	public MailBuilder addPart(String text, String contentType) throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(text, contentType);
		parts.add(bodyPart);
		return this;
	}
	
	public MailBuilder addPart(String cid, URL url) throws MessagingException {
		DataSource fds = new URLDataSource(url);
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setDataHandler(new DataHandler(fds));
		bodyPart.setHeader("Content-ID","<"+cid+">");
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mimeType = fileNameMap.getContentTypeFor(url.toString());		
		bodyPart.setHeader("Content-Type", mimeType);
		parts.add(bodyPart);
		return this;
	}
	
	public MailBuilder addPart(String cid, byte[] content, String mimeType) throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();
	    DataSource ds = new ByteArrayDataSource(content, mimeType);
		bodyPart.setDataHandler(new DataHandler(ds));
		bodyPart.setHeader("Content-ID","<"+cid+">");
		bodyPart.setFileName(cid);
		bodyPart.setHeader("Content-Type", mimeType);
		parts.add(bodyPart);
		return this;
	}
	
	public MailBuilder text(String textBody) throws MessagingException {
		addPart(textBody, PLAIN_TEXT);
		return this;
	}
	
	public MailBuilder html(String htmlBody) throws MessagingException {
		addPart(htmlBody, HTML_TEXT);
		return this;
	}
	
	public MimeMessage message() throws MessagingException {
		if(parts.size()==0) 
			throw new IllegalStateException("needed almost 1 MimeBodyPart");
		if(message.getAllRecipients().length==0)
			throw new IllegalStateException("needed almost 1 recipients");
		
		MimeMultipart multipart = new MimeMultipart("related");
		for(BodyPart b: parts) {
			multipart.addBodyPart(b);
		}		
		message.setContent(multipart);
		message.setSentDate(new Date());
		return message;
	}

}
