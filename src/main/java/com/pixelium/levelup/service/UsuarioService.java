package com.pixelium.levelup.service;

import com.pixelium.levelup.model.Usuario;
import com.pixelium.levelup.repository.UsuarioRepository;
import com.pixelium.levelup.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // Importación necesaria

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    @Lazy // Lazy para evitar dependencias circulares
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Directorio donde se guardarán los avatares
    private final Path rootLocation = Paths.get("avatars");

    // Método obligatorio de Spring Security para cargar usuario por email
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // --- REGISTRO CON ENCRIPTACIÓN ---
    public Usuario save(Usuario usuario) {
        if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
            usuario.setRole("USER");
        }
        // Encriptamos la contraseña antes de guardar en la base de datos
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // --- LOGIN QUE RETORNA TOKEN ---
    public String login(String correo, String password) {
        Usuario user = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificamos que la contraseña ingresada coincida con la encriptada
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Si todo es correcto, generamos el Token JWT
        return jwtService.generateToken(user);
    }

    // --- MÉTODO PRINCIPAL: ACTUALIZAR PERFIL CON/SIN AVATAR ---
    public Usuario updateProfile(Usuario updatedUser, MultipartFile avatarFile) throws IOException {

        Usuario existingUser = usuarioRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar"));

        // 1. Actualizar campos de texto
        existingUser.setNombre(updatedUser.getNombre());
        existingUser.setRut(updatedUser.getRut());
        existingUser.setTelefono(updatedUser.getTelefono());
        existingUser.setFechaNacimiento(updatedUser.getFechaNacimiento());
        existingUser.setComuna(updatedUser.getComuna());

        // 🆕 ACTUALIZAR ROL SI SE PROVEE (solo para admins)
        if (updatedUser.getRole() != null && !updatedUser.getRole().isEmpty()) {
            existingUser.setRole(updatedUser.getRole());
        }

        // 2. Manejo del Avatar
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Si ya existe un avatar, lo eliminamos primero (limpieza)
            if (existingUser.getAvatarSrc() != null && !existingUser.getAvatarSrc().isEmpty()) {
                deleteFile(existingUser.getAvatarSrc());
            }

            // Guardamos el nuevo archivo y asignamos el nombre al usuario
            String fileName = storeFile(avatarFile);
            existingUser.setAvatarSrc(fileName);
        }

        // 3. Guardamos los cambios en la BD
        return usuarioRepository.save(existingUser);
    }

    // --- MÉTODOS HELPER PARA ARCHIVOS ---

    private String storeFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path destinationFile = this.rootLocation.resolve(Paths.get(fileName))
                .normalize().toAbsolutePath();

        // Aseguramos que la carpeta exista
        if (!Files.exists(this.rootLocation)) {
            Files.createDirectories(this.rootLocation);
        }

        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void deleteFile(String fileName) {
        try {
            Path fileToDelete = this.rootLocation.resolve(fileName).normalize().toAbsolutePath();
            // Solo si el archivo existe y es un archivo regular
            if (Files.exists(fileToDelete) && Files.isRegularFile(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
            // Manejar error de eliminación (ej: archivo no encontrado, permisos)
            System.err.println("Error al eliminar archivo: " + fileName + ". Error: " + e.getMessage());
        }
    }



    public List<Usuario> findAll() { return usuarioRepository.findAll(); }
    public Optional<Usuario> findById(int id) { return usuarioRepository.findById(id); }
    public void deleteById(int id) { usuarioRepository.deleteById(id); }
    public Optional<Usuario> findByCorreo(String correo) { return usuarioRepository.findByCorreo(correo); }
}