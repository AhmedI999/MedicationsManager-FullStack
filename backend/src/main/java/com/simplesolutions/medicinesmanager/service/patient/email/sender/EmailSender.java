package com.simplesolutions.medicinesmanager.service.patient.email.sender;

public interface EmailSender {
    void sendVerification(String to, String email);
}
