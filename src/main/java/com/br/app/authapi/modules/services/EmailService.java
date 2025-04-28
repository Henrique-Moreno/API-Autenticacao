package com.br.app.authapi.modules.services;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendOtp(String email, String otp) {
        System.out.println("Enviando OTP " + otp + " \n" +
                "para " + email);
    }
}
