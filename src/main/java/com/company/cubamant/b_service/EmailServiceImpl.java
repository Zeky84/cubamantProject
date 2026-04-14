package com.company.cubamant.b_service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;


import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(EmailServiceImpl.class);

	@Value("${company.email}")
	private String companyEmail;

	public EmailServiceImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Async
	@Override
	public void sendWorkerSetup(String to, String link) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(companyEmail, "Cubamant");
			helper.setTo(to);
			helper.setSubject("Welcome to Cubamant - Setup Your Account");
			helper.setText("Visit this link to setup your account: " + link, buildHtmlContent(link));
			mailSender.send(message);

		} catch (MessagingException | UnsupportedEncodingException e) {
			// ✅ Must log, not throw — @Async runs in a separate thread,
			// any exception thrown here is silently swallowed and never reaches the caller
			logger.error("Failed to send setup email to {}: {}", to, e.getMessage());
		}
	}

	private String buildHtmlContent(String link) {
		return """
				    <html>
				        <body style="font-family: Arial, sans-serif;">
				            <h2>Welcome to Cubamant</h2>
				            <p>Hello,</p>
				        <p>Your Cubamant account has been created.</p>
				        <p>Please click the button below to securely set your password:</p>
				            
				            <a href="%s" 
				               style="display:inline-block; padding:10px 20px; background:#2d89ef; color:white; text-decoration:none; border-radius:5px;">
				               Setup Account
				            </a>
				            
				            <p>This link will expire in 24 hours.</p>
				        </body>
				    </html>
				""".formatted(link);
	}
}
