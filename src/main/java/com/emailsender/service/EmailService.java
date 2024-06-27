package com.emailsender.service;

public interface EmailService {
    void sendSimpleMailMessage(String name, String to, String token);

    void sendMessageWithAttachment(String name, String to, String token);


    void sendMessageWithEmbeddedFiles(String name, String to, String token);

    void sendHtmlEmail(String name, String to, String token);

    void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token);


}
