package com.nursinghomegest.service;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


public interface MailService {
	public void send(MimeMessage msg) throws MessagingException, IOException;
}
