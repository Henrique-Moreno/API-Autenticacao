package com.br.app.authapi.modules.services;

import com.br.app.authapi.modules.entity.User;
import com.br.app.authapi.modules.repositories.UserRepository;
import com.br.app.authapi.modules.utils.SanitizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LogService logService;

    public void register(User user) throws Exception {
        // Verificar se o username já existe
        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent()) {
            throw new Exception("Usuário já cadastrado com este username");
        }

        // Sanitizar todos os campos antes de processar
        user.setUsername(SanitizationUtil.sanitize(user.getUsername()));
        user.setName(SanitizationUtil.sanitize(user.getName()));
        user.setEmail(SanitizationUtil.sanitizeEmail(user.getEmail()));
        user.setProfile(SanitizationUtil.sanitize(user.getProfile()));
        user.setAuthorizedIp(SanitizationUtil.sanitize(user.getAuthorizedIp()));

        String sanitizedPassword = SanitizationUtil.sanitize(user.getPassword());
        String hashedPassword = hashPassword(sanitizedPassword);
        user.setPassword(hashedPassword);

        userRepository.save(user);
        logService.logAction("Usuário cadastrado: " + user.getUsername());
    }

    public User authenticate(String username, String password, String clientIp) throws Exception {
        String sanitizedUsername = SanitizationUtil.sanitize(username);
        String sanitizedPassword = SanitizationUtil.sanitize(password);
        String sanitizedClientIp = SanitizationUtil.sanitize(clientIp);

        User user = userRepository.findByUsername(sanitizedUsername)
                .orElseThrow(() -> new Exception("Usuário não encontrado"));

        String hashedPassword = hashPassword(sanitizedPassword);
        if (!user.getPassword().equals(hashedPassword)) {
            String captcha = generateCaptcha();
            logService.logAction("Tentativa de login com falha para nome de usuário: " + sanitizedUsername);
            throw new Exception("Senha inválida. Captcha: " + captcha);
        }

        if (!user.getAuthorizedIp().equals(sanitizedClientIp)) {
            logService.logAction("Incompatibilidade de IP para nome de usuário: " + sanitizedUsername);
            throw new Exception("IP não autorizado");
        }

        String otp = generateOtp();
        emailService.sendOtp(user.getEmail(), otp);
        logService.logAction("OTP enviado para " + user.getEmail() + " para nome de usuário: " + sanitizedUsername);
        return user;
    }

    public boolean verifyOtp(String username, String otp) {
        String sanitizedUsername = SanitizationUtil.sanitize(username);
        String sanitizedOtp = SanitizationUtil.sanitize(otp);
        logService.logAction("Verificação OTP para nome de usuário: " + sanitizedUsername);
        return true;
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String generateCaptcha() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}