package com.pixelium.levelup.service;

import com.pixelium.levelup.model.Producto;
import com.pixelium.levelup.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    // Definimos la carpeta donde se guardarán los archivos
    private final Path rootLocation = Paths.get("uploads");

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(int id) {
        return productoRepository.findById(id);
    }

    // Método para guardar SIN archivo (útil si editas datos pero no la imagen)
    public Producto save(Producto p) {
        return productoRepository.save(p);
    }

    // --- NUEVO MÉTODO ---
    // Este es el que usa tu Controller para guardar la imagen física
    public Producto save(Producto producto, MultipartFile file) throws IOException {
        // 1. Verificamos si viene un archivo
        if (file != null && !file.isEmpty()) {
            // 2. Generamos nombre único (UUID + nombre original) para evitar conflictos
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 3. Resolvemos la ruta completa
            Path destinationFile = this.rootLocation.resolve(Paths.get(fileName))
                    .normalize().toAbsolutePath();

            // 4. Copiamos el archivo a la carpeta 'uploads'
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // 5. Guardamos SOLO el nombre del archivo en la base de datos
            producto.setImageSrc(fileName);
        }

        // 6. Guardamos el producto en la BD
        return productoRepository.save(producto);
    }

    public void deleteById(int id) {
        productoRepository.deleteById(id);
    }
}