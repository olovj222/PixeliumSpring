package com.pixelium.levelup.service;

import com.pixelium.levelup.model.Producto;
import com.pixelium.levelup.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 🟡 IMPORT FALTANTE

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

    private final Path rootLocation = Paths.get("uploads");

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(int id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto p) {
        return productoRepository.save(p);
    }

    // Método para guardar con un solo archivo
    public Producto save(Producto producto, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = storeFile(file);
            producto.setImageSrc(fileName);
        }
        return productoRepository.save(producto);
    }

    // --- MÉTODOS DE SOPORTE ---

    // Método helper para guardar un archivo y retornar el nombre único
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

    // 🟡 MÉTODO DELETE FILE FALTANTE
    private void deleteFile(String fileName) {
        try {
            Path fileToDelete = this.rootLocation.resolve(fileName).normalize().toAbsolutePath();
            if (Files.exists(fileToDelete) && Files.isRegularFile(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
            System.err.println("Error al eliminar archivo: " + fileName + ". Error: " + e.getMessage());
        }
    }

    // Método para múltiples archivos (si lo necesitas)
    public Producto saveWithMultipleFiles(Producto producto, MultipartFile[] files) throws IOException {
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            if (file != null && !file.isEmpty()) {
                String fileName = storeFile(file);

                switch (i) {
                    case 0: // Imagen Principal
                        producto.setImageSrc(fileName);
                        break;
                    case 1: // Imagen 2
                        producto.setImageSrc2(fileName);
                        break;
                    case 2: // Imagen 3
                        producto.setImageSrc3(fileName);
                        break;
                    case 3: // Imagen 4
                        producto.setImageSrc4(fileName);
                        break;
                }
            }
        }

        return productoRepository.save(producto);
    }

    // Método update corregido
    public Producto update(Producto producto, MultipartFile filePrincipal, MultipartFile fileDetalle2,
                           MultipartFile fileDetalle3, MultipartFile fileDetalle4) throws IOException {

        // Actualizar imagen principal si se proporciona
        if (filePrincipal != null && !filePrincipal.isEmpty()) {
            if (producto.getImageSrc() != null && !producto.getImageSrc().isEmpty()) {
                deleteFile(producto.getImageSrc());
            }
            producto.setImageSrc(storeFile(filePrincipal));
        }

        // Actualizar imágenes de detalle si se proporcionan
        if (fileDetalle2 != null && !fileDetalle2.isEmpty()) {
            if (producto.getImageSrc2() != null && !producto.getImageSrc2().isEmpty()) {
                deleteFile(producto.getImageSrc2());
            }
            producto.setImageSrc2(storeFile(fileDetalle2));
        }

        if (fileDetalle3 != null && !fileDetalle3.isEmpty()) {
            if (producto.getImageSrc3() != null && !producto.getImageSrc3().isEmpty()) {
                deleteFile(producto.getImageSrc3());
            }
            producto.setImageSrc3(storeFile(fileDetalle3));
        }

        if (fileDetalle4 != null && !fileDetalle4.isEmpty()) {
            if (producto.getImageSrc4() != null && !producto.getImageSrc4().isEmpty()) {
                deleteFile(producto.getImageSrc4());
            }
            producto.setImageSrc4(storeFile(fileDetalle4));
        }

        return productoRepository.save(producto);
    }

    public void deleteById(int id) {
        productoRepository.deleteById(id);
    }
}