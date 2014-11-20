package com.bzanni.parisaccessible.indexer.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {
	@Value("#{'${parisaccessible_mail_to}'.split(',')}")
	private List<String> to;

	@Value("${parisaccessible_mail_from}")
	private String from;

	@Value("${parisaccessible_mail_host}")
	private String host;

	@Value("${parisaccessible_mail_port}")
	private String port;

	@Value("${parisaccessible_mail_username}")
	private String username;

	@Value("${parisaccessible_mail_password}")
	private String password;

	public void send(String subject, String body) throws AddressException,
			MessagingException, IOException {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		// Get system properties
		// Properties properties = System.getProperties();

		// Setup mail server
		// properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		// Session session = Session.getDefaultInstance(properties);

		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(from));

		// Set To: header field of the header.
		for (String t : to) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					t));
		}

		// Set Subject: header field
		message.setSubject(subject);

		// Now set the actual message
		message.setText(body);

		// Send message
		Transport.send(message);
	}
}
