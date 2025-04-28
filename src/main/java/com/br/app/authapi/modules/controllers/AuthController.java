package com.br.app.authapi.modules.controllers;

import com.br.app.authapi.modules.dto.LoginRequest;
import com.br.app.authapi.modules.dto.RegisterRequest;
import com.br.app.authapi.modules.dto.VerifyOtpRequest;
import com.br.app.authapi.modules.entity.User;
import com.br.app.authapi.modules.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo usuário com os dados fornecidos")
    @ApiResponse(responseCode = "200", description = "Usuário cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de usuário inválidos")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setProfile(request.getProfile());
            user.setAuthorizedIp(request.getAuthorizedIp());
            authService.register(user);
            return ResponseEntity.ok("Usuário cadastrado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Autentica o usuário com nome de usuário, senha e IP")
    @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida")
    @ApiResponse(responseCode = "401", description = "Falha na autenticação")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            String clientIp = httpRequest.getRemoteAddr();
            User user = authService.authenticate(request.getUsername(), request.getPassword(), clientIp);
            return ResponseEntity.ok("Autenticação bem-sucedida. OTP enviado para o email.");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Falha na autenticação: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verificar OTP", description = "Verifica o OTP enviado para o e-mail do usuário")
    @ApiResponse(responseCode = "200", description = "OTP verificado com sucesso")
    @ApiResponse(responseCode = "401", description = "OTP inválido")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean verified = authService.verifyOtp(request.getUsername(), request.getOtp());
        if (verified) {
            return ResponseEntity.ok("OTP verificado com sucesso");
        }
        return ResponseEntity.status(401).body("OTP inválido");
    }
}