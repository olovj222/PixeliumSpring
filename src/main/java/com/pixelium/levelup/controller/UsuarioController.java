package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Usuario;
import com.pixelium.levelup.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // ¡IMPORTANTE! Necesario para manejar archivos

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Clase interna (DTO) para recibir los datos del login de forma limpia
    static class LoginRequest {
        public String correo;
        public String password;
    }

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Usuario> getById(@PathVariable int id) {
        return usuarioService.findById(id);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            Usuario newUsuario = usuarioService.save(usuario);
            return ResponseEntity.ok(newUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = usuarioService.login(loginRequest.correo, loginRequest.password);

            Usuario user = usuarioService.findByCorreo(loginRequest.correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    // --- NUEVO ENDPOINT: ACTUALIZAR PERFIL (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("rut") String rut,
            @RequestParam("telefono") String telefono,
            @RequestParam("fechaNacimiento") String fechaNacimiento,
            @RequestParam("comuna") String comuna,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        try {
            // 1️⃣ Obtener usuario autenticado desde el JWT
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correoAuth = auth.getName();

            Usuario usuarioAuth = usuarioService.findByCorreo(correoAuth)
                    .orElseThrow(() -> new RuntimeException("No existe el usuario autenticado"));

            // Validar que el usuario solo edita su propio ID
            if (!usuarioAuth.getId().equals(id)) {
                return ResponseEntity.status(403).body("No puedes editar el perfil de otro usuario");
            }

            // 3️⃣ Crear objeto User con los cambios
            Usuario updatedUser = new Usuario();
            updatedUser.setId(id);
            updatedUser.setNombre(nombre);
            updatedUser.setRut(rut);
            updatedUser.setTelefono(telefono);
            updatedUser.setFechaNacimiento(fechaNacimiento);
            updatedUser.setComuna(comuna);

            // 4️⃣ Guardar cambios
            Usuario result = usuarioService.updateProfile(updatedUser, avatar);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar perfil: " + e.getMessage());
        }
    }
    // --- FIN ENDPOINT ACTUALIZAR PERFIL ---

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        usuarioService.deleteById(id);
    }
}