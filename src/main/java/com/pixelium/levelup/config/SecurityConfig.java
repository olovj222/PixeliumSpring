package com.pixelium.levelup.config;

import com.pixelium.levelup.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 🚨 Importar para especificar métodos HTTP
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

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth
                        // RUTAS PÚBLICAS Y DE SWAGGER
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**", "/doc/**").permitAll()
                        .requestMatchers("/api/v1/usuarios/login", "/api/v1/usuarios/register").permitAll()
                        .requestMatchers("/images/**", "/avatars/**").permitAll()

                        // 🟡 PRODUCTOS: Permitir solo GET públicamente
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
                        // 🔴 PRODUCTOS: POST y DELETE REQUIEREN ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasAuthority("ADMIN")

                        // 🟡 NOTICIAS: Permitir solo GET públicamente
                        .requestMatchers(HttpMethod.GET, "/api/v1/noticias/**").permitAll()
                        // 🔴 NOTICIAS: POST y DELETE REQUIEREN ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/noticias/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/noticias/**").hasAuthority("ADMIN")

                        // RUTAS PROTEGIDAS GENERALES
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
                        // 👤 Rutas de Usuarios (requieren cualquier usuario autenticado)
                        .requestMatchers("/api/v1/usuarios/**").authenticated()

                        // Cualquier otra ruta REQUIERE autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider(null, passwordEncoder()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- CONFIGURACIÓN DEL BEAN CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // 3. CONFIGURACIÓN DEL AUTHENTICATION PROVIDER
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}