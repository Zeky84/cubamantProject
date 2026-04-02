package com.company.cubamant.b_service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	public EmailServiceImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void sendWorkerSetup(String to, String link) {
		try {
			MimeMessage message = mailSender.createMimeMessage();

			// 🔥 IMPORTANT: specify encoding to avoid charset issues
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			// ✅ Set FROM (must match authenticated Gmail or alias)
			helper.setFrom("duqueezequiel90@gmail.com", "Cubamant");

			helper.setTo(to);
			helper.setSubject("Welcome to Cubamant - Setup Your Account");

			String content = buildHtmlContent(link);

			helper.setText(
					"Visit this link to setup your account: " + link,
					content
			);

			mailSender.send(message);

		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new RuntimeException("Error sending email", e);
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
