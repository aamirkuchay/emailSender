package com.emailsender.service.userServiceImpl;

import com.emailsender.service.EmailService;
import com.emailsender.utils.EmailUtils;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

import static com.emailsender.utils.EmailUtils.getVerificationUrl;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final String UTF_8_ENCODING = "UTF-8";
    @Value("${spring.mail.username}")
    private String fromEmail;


    private final String host = "http://localhost:8085";
    private  final TemplateEngine templateEngine;

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("New User Account Verification");
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(EmailUtils.getEmailMessage(name, host, token));
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMessageWithAttachment(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, host, token));
//            ----------Add Attachment ---------
            FileSystemResource fort = new FileSystemResource(new File(System.getProperty("user.home") +
                    "/Downloads/abbasSI.jpg"));
            FileSystemResource pdf = new FileSystemResource(new File(System.getProperty("user.home") +
                    "/Downloads/abbas.pdf"));
            FileSystemResource image = new FileSystemResource(new File(System.getProperty("user.home") +
                    "/Downloads/arks.jpg"));
            helper.addAttachment(fort.getFilename(), fort);
            helper.addAttachment(pdf.getFilename(), pdf);
            helper.addAttachment(image.getFilename(), image);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }


    @Override
    @Async
    public void sendMessageWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(EmailUtils.getEmailMessage(name, host, token));
//            ----------Add Attachment ---------
            FileSystemResource fort = new FileSystemResource(new File(System.getProperty("user.home") +
                    "/Downloads/abbasSI.jpg"));
            FileSystemResource pdf = new FileSystemResource(new File(System.getProperty("user.home") +
                    "/Downloads/abbas.pdf"));
            FileSystemResource image = new FileSystemResource(new File(System.getProperty("user.home") +
                    "/Downloads/arks.jpg"));
            helper.addInline(getContentId(fort.getFilename() ), fort);
            helper.addInline(getContentId(pdf.getFilename()), pdf);
            helper.addInline(getContentId(image.getFilename()), image);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    @Async
    public void sendHtmlEmail(String name, String to, String token) {
        try {
            Context context = new Context();
//            context.setVariable("name", name);
//            context.setVariable("url", getVerificationUrl(host,token));
            context.setVariables(Map.of("name", name , "url", getVerificationUrl(host,token)));
            String text = templateEngine.process("emailtemplate", context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token) {
        try {
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject("New User Account Verification");
            helper.setFrom(fromEmail);
            helper.setTo(to);
//            helper.setText(text, true);
            Context context = new Context();
            context.setVariables(Map.of("name", name , "url", getVerificationUrl(host,token)));
            String text = templateEngine.process("emailtemplate", context);

//            Add Attachment ----------------

            // ADD HTML EMAIL BODY
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, "text/html");
            mimeMultipart.addBodyPart(messageBodyPart);

            // ADD IMAGES TO EMAIL BODY ---------
            BodyPart imageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(System.getProperty("user.home") +
                    "/Downloads/abbasSI.jpg");
            imageBodyPart.setDataHandler(new DataHandler(dataSource));
            imageBodyPart.setHeader("Content-ID" , "image");
            mimeMultipart.addBodyPart(imageBodyPart);


           message.setContent(mimeMultipart);

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }
    private String getContentId(String filename) {
        return "<" + filename + ">";
    }
}
