package com.project.Obur.us.config;

import com.project.Obur.us.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@EnableAsync // Kritik: ReviewService'deki @Async metodunun çalışmasını sağlar.
public class ApplicationConfig {

    private final UserRepository userRepository;

    // JWT/Login için gerekli: Kullanıcıyı e-posta ile DB'den çeker.
    // User Entity'nin UserDetails arayüzünü uyguladığı varsayılmıştır.
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    // Doğrulama Sağlayıcısı: UserDetailsService ve PasswordEncoder'ı birleştirir.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // AuthenticationManager: Giriş işlemlerini yönetir. SecurityConfig'in ihtiyacıdır.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // PasswordEncoder: Şifreleri hashlemek için BCrypt kullanır.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // WebClient Bean: AI Servisi ile iletişimi yönetir.
    // Base URL'nin application.properties'den çekildiği varsayılmıştır.
    @Bean
    public WebClient webClient(@Value("${ai.service.url}") String aiServiceUrl) {
        return WebClient.builder()
                .baseUrl(aiServiceUrl)
                .build();
    }
}
