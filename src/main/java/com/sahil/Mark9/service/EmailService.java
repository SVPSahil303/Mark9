package com.sahil.Mark9.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender=mailSender;
    }

    public void sendSimpleEmail(String to,String subject,String text){
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setFrom("g24.sahil.patil@gnkhalsa.edu.in");
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}
