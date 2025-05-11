package ru.marilka.swotbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailService {

    public void sendPasswordToUser(String email, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("neudachnic888@gmail.com");
        mailSender.setPassword("wlqa bhln xshp sudi"); // или обычный, если нет 2FA

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.debug", "true");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("janware@yandex.ru");
        message.setSubject("Ваш временный пароль");
        message.setText("Здравствуйте!\n\nВаш временный пароль для входа: " + password + "\n\nПожалуйста, смените его после входа.");
        mailSender.send(message);
    }
}

