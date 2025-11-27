package com.pixelium.levelup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Usuario implements UserDetails { // <--- 1. AGREGAR "implements UserDetails"
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String nombre;

    @Column(length = 20, unique = true)
    private String rut;

    @Column(length = 20)
    private String telefono;

    private String fechaNacimiento;

    @Column(unique = true, length = 100)
    private String correo;

    private String password;

    private String comuna;

    private String correoReferido;

    private String role;

    private String avatarSrc;

    // --- 2. MÉTODOS OBLIGATORIOS DE SPRING SECURITY (UserDetails) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> this.role); // role = "USER" o "ADMIN"
    }

    @Override
    public String getUsername() {
        return this.correo;   // MUY IMPORTANTE
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // Nota: getPassword() ya es generado por Lombok (@Data) al tener el campo 'password',
    // pero si no usaras Lombok, tendrías que implementarlo manualmente así:
    // @Override public String getPassword() { return password; }

    // Estos métodos indican si la cuenta está activa.
    // Devolvemos 'true' para decir que el usuario siempre está habilitado.
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}