package com.simplesolutions.medicinesmanager.service.patient.email.sender;

import com.simplesolutions.medicinesmanager.exception.VerificationSenderException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmailService implements EmailSender {
    private final JavaMailSender mailSender;
    private static final String MAIL_SUBJECT = "Confirm your email";
    private static final String SENDER_EMAIL = "MedicationManager@gmail.com";

    @Override
    @Async
    public void sendVerification(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(MAIL_SUBJECT);
            helper.setFrom(SENDER_EMAIL);
            mailSender.send(mimeMessage);
            log.info("Email sent!. Mime message: {}", helper.getMimeMessage());
        } catch (MessagingException e){
            log.error("Failed to send email", e);
            throw new VerificationSenderException("Error While sending message to the user");
        }
    }
}
