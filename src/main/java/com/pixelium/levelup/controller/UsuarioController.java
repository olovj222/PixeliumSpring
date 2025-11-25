package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Usuario;
import com.pixelium.levelup.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // DTO para Login
    static class LoginRequest {
        public String correo; // Coincide con el JSON { "correo": "...", "password": "..." }
        public String password;
    }

    @GetMapping
    public List<Usuario> getAll() { return usuarioService.findAll(); }

    // --- REGISTRO COMPLETO ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            // Validar si el correo ya existe (opcional pero recomendado)
            // if(usuarioService.exists(usuario.getCorreo())) ...

            Usuario newUsuario = usuarioService.save(usuario);
            return ResponseEntity.ok(newUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.validateLogin(loginRequest.correo, loginRequest.password);

        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }
}