package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Usuario;
import com.pixelium.levelup.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // 🚨 NUEVO IMPORT
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones sobre usuarios")
// 🚨 APLICAR SEGURIDAD JWT A NIVEL DE CONTROLADOR (afecta a todos los métodos por defecto)
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Clase interna (DTO) para recibir los datos del login de forma limpia
    static class LoginRequest {
        public String correo;
        public String password;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios",description = "Obtiene una lista de todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "403", description = "No autorizado"), // Añadido 403 para rutas protegidas
            @ApiResponse(responseCode = "404", description = "Usuarios no encontrados")
    })
    public ResponseEntity<List<Usuario>> getAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario",description = "Obtiene un usuario mediante su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Optional<Usuario>> getById(@PathVariable int id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping("/register")
    @Operation(summary = "Crea un nuevo usuario",description = "Registra un usuario mediante sus datos",
            security = @SecurityRequirement(name = "")) // 🚨 EXCLUIR SEGURIDAD JWT
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Registro no exitoso")
    })
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            Usuario newUsuario = usuarioService.save(usuario);
            return ResponseEntity.ok(newUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión con usuario",description = "Inicia sesión de usuario registrado",
            security = @SecurityRequirement(name = "")) // 🚨 EXCLUIR SEGURIDAD JWT
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
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

    // --- ENDPOINT: ACTUALIZAR PERFIL (PUT) ---
    @PutMapping("/{id}")
    @Operation(summary = "Actualiza perfil de usuario", description = "Modifica datos de usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "403", description = "No autorizado o ID no coincide"),
            @ApiResponse(responseCode = "400", description = "Modificación no exitosa")
    })
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("rut") String rut,
            @RequestParam("telefono") String telefono,
            @RequestParam("fechaNacimiento") String fechaNacimiento,
            @RequestParam("comuna") String comuna,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "role", required = false) String role // 🆕 NUEVO PARÁMETRO PARA ROL
    ) {
        try {
            // Lógica de validación de autenticación de Spring Security
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correoAuth = auth.getName();

            Usuario usuarioAuth = usuarioService.findByCorreo(correoAuth)
                    .orElseThrow(() -> new RuntimeException("No existe el usuario autenticado"));

            // 🆕 PERMITIR QUE ADMINS MODIFIQUEN CUALQUIER USUARIO
            boolean isAdmin = usuarioAuth.getRole().equals("ADMIN");
            boolean isOwnProfile = usuarioAuth.getId().equals(id);

            // Si NO es admin y NO es su propio perfil, denegar
            if (!isAdmin && !isOwnProfile) {
                return ResponseEntity.status(403).body("No puedes editar el perfil de otro usuario");
            }

            // Crear objeto User con los cambios
            Usuario updatedUser = new Usuario();
            updatedUser.setId(id);
            updatedUser.setNombre(nombre);
            updatedUser.setRut(rut);
            updatedUser.setTelefono(telefono);
            updatedUser.setFechaNacimiento(fechaNacimiento);
            updatedUser.setComuna(comuna);

            // 🆕 SOLO ADMINS PUEDEN CAMBIAR ROLES
            if (role != null && isAdmin) {
                updatedUser.setRole(role);
            }

            // Guardar cambios
            Usuario result = usuarioService.updateProfile(updatedUser, avatar);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar perfil: " + e.getMessage());
        }
    }
    // --- FIN ENDPOINT ACTUALIZAR PERFIL ---

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina usuario",description = "Elimina usuario mediante su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Eliminado sin exito")
    })
    public void delete(@PathVariable int id) {
        usuarioService.deleteById(id);
    }
}

