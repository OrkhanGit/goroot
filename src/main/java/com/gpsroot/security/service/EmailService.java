package com.gpsroot.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Şifrə bərpası kodu");
        message.setText("Şifrənizi bərpa etmək üçün kodunuz: " + code +
                "\nKod 10 dəqiqə ərzində etibarlıdır.");
        mailSender.send(message);
    }
}