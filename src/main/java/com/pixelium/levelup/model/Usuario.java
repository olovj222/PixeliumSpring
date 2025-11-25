package com.pixelium.levelup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Campos básicos
    @Column(length = 100)
    private String nombre;

    @Column(length = 20, unique = true)
    private String rut; // Nuevo

    @Column(length = 20)
    private String telefono; // Nuevo

    private String fechaNacimiento; // Nuevo (Lo guardamos como String "YYYY-MM-DD" para simplificar)

    @Column(unique = true, length = 100)
    private String correo; // Usamos 'correo' para coincidir con tu frontend

    private String password;

    private String comuna; // Nuevo

    private String correoReferido; // Nuevo (Tu 'correo3')

    private String role; // "ADMIN" o "USER"
}