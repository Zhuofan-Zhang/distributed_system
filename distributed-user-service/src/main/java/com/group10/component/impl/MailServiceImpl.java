package com.group10.component.impl;

import com.group10.component.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    public void sendMail(String to, String subject, String content) {
        //Create SimpleMailMessage object
        SimpleMailMessage message = new SimpleMailMessage();
        //Email sender
        message.setFrom(from);
        //Email recipient
        message.setTo(to);
        //email subject
        message.setSubject(subject);
        //content
        message.setText(content);
        //send the email
        mailSender.send(message);
        log.info("Email sent successfully:{}",message);
    }


}
