package com.pixelium.levelup.config;

import com.pixelium.levelup.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Imports adicionales para la configuración CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    // Eliminamos 'userDetailsService' de aquí

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // 1. CONFIGURACIÓN CORS: Permite peticiones desde el frontend (localhost:5173)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (Login, Register, Productos, Noticias)
                        .requestMatchers("/api/v1/usuarios/login", "/api/v1/usuarios/register").permitAll()
                        .requestMatchers("/api/v1/productos/**", "/api/v1/noticias/**").permitAll()
                        // Rutas estáticas para imágenes y avatares
                        .requestMatchers("/images/**", "/avatars/**").permitAll()

                        // Rutas protegidas
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
                        // Protegemos el resto de las rutas de usuarios (incluyendo PUT)
                        .requestMatchers("/api/v1/usuarios/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 2. CORRECCIÓN MENOR: authenticationProvider se llama sin argumentos
                // ya que Spring lo encuentra por tipo de retorno
                .authenticationProvider(authenticationProvider(null, passwordEncoder()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- CONFIGURACIÓN DEL BEAN CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir el origen de tu frontend (React)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Permitir todos los métodos HTTP que usamos, incluyendo PUT y OPTIONS
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permitir headers esenciales (Content-Type para FormData, Authorization para JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración CORS a TODAS las rutas (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // 3. CORRECCIÓN PRINCIPAL: Inyectamos UserDetailsService y PasswordEncoder en el método Bean
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        // Inicialización con constructor que requiere UserDetailsService (Spring Security 6+)
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        // El setPasswordEncoder se mantiene
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}