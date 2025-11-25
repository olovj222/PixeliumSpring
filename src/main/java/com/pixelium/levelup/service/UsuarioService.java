package com.pixelium.levelup.service;

import com.pixelium.levelup.model.Usuario;
import com.pixelium.levelup.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAll() { return usuarioRepository.findAll(); }
    public Optional<Usuario> findById(int id) { return usuarioRepository.findById(id); }

    // --- REGISTRO ---
    public Usuario save(Usuario usuario) {
        if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
            usuario.setRole("USER"); // Rol por defecto
        }
        return usuarioRepository.save(usuario);
    }

    // --- LOGIN ---
    public Usuario validateLogin(String correo, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // AQUÍ: Comparación simple. En producción usa BCryptPasswordEncoder
            if (usuario.getPassword().equals(password)) {
                return usuario;
            }
        }
        return null;
    }

    public void deleteById(int id) { usuarioRepository.deleteById(id); }
}