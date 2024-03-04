package com.simplesolutions.medicinesmanager.service.patient.email.sender;

import com.simplesolutions.medicinesmanager.exception.VerificationSenderException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmailService implements EmailSender {
    private final JavaMailSender mailSender;
    @Value("#{'${service.email-sender.mail.subject}'}")
    private String MAIL_SUBJECT;
    @Value("#{'${service.email-sender.mail.sender-email}'}")
    private String SENDER_EMAIL;
    @Value("#{'${service.email-sender.mail.encoding}'}")
    private String EMAIL_ENCODE;
    @Value("#{'${service.email-sender.error.message}'}")
    private String EMAIL_SENDER_ERROR;

    @Override
    @Async
    public void sendVerification(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, EMAIL_ENCODE);
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(MAIL_SUBJECT);
            helper.setFrom(SENDER_EMAIL);
            mailSender.send(mimeMessage);
            log.info("Email sent!. Mime message: {}", helper.getMimeMessage());
        } catch (MessagingException e){
            log.error("Failed to send email", e);
            throw new VerificationSenderException(EMAIL_SENDER_ERROR);
        }
    }
}
