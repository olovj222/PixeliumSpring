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

    private final Path rootLocation = Paths.get("uploads");

    // ... (Métodos find, delete, save(Producto p) se mantienen) ...

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(int id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto p) {
        return productoRepository.save(p);
    }

    // El método save anterior se puede renombrar o mantener, pero si el Controller usa el nuevo,
    // este es el método que hay que usar si solo se envía un archivo:
    public Producto save(Producto producto, MultipartFile file) throws IOException {
        // Lógica de guardado de un solo archivo (se mantiene)
        if (file != null && !file.isEmpty()) {
            String fileName = storeFile(file);
            producto.setImageSrc(fileName);
        }
        return productoRepository.save(producto);
    }

    // --- NUEVOS MÉTODOS DE SOPORTE ---

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

    // --- NUEVO MÉTODO PRINCIPAL PARA MÚLTIPLES ARCHIVOS ---
    public Producto saveWithMultipleFiles(Producto producto, MultipartFile[] files) throws IOException {

        // El array files tiene 4 posiciones: [Principal, Detalle2, Detalle3, Detalle4]
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            // Verificamos si el archivo existe (solo el principal es obligatorio, el resto pueden ser null)
            if (file != null && !file.isEmpty()) {
                String fileName = storeFile(file); // Guardamos el archivo

                // Asignamos el nombre de archivo a la propiedad correcta del Producto
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

        // Guardamos el producto en la BD con todos los nombres de archivo asignados
        return productoRepository.save(producto);
    }

    public void deleteById(int id) {
        productoRepository.deleteById(id);
    }
}