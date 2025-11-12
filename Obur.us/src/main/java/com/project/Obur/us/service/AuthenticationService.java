package com.project.Obur.us.service;

import com.project.Obur.us.dto.AuthRequestDto;
import com.project.Obur.us.persistence.entity.User;
import com.project.Obur.us.repository.UserRepository;
import com.project.Obur.us.security.JwtService;
import com.project.Obur.us.dto.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Kayıt Olma İşlemi
    public AuthResponseDto register(AuthRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already registered.");
        }

        // KRİTİK: Şifre hashlenerek kaydedilir.
        var user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken, user.getId());
    }

    // Giriş Yapma İşlemi
    public AuthResponseDto authenticate(AuthRequestDto request) {
        // AuthenticationManager şifreleri doğrular. Başarısız olursa hata fırlatır.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Doğrulama başarılıysa kullanıcıyı çek ve JWT üret
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken, user.getId());
    }
}