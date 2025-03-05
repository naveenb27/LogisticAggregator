package com.example.LogisticAggregator.Service;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    EmailSenderService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendMain(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("lazyman046@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);

        System.out.println("Message sent Successfully!");
    }
}
