package com.pixelium.levelup.service;

import com.pixelium.levelup.model.Noticias;
import com.pixelium.levelup.repository.NoticiasRepository;
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
public class NoticiasService {

    @Autowired
    private NoticiasRepository noticiasRepository;

    // Definimos la ruta donde se guardan las imagenes
    private final Path rootLocation = Paths.get("uploads");

    public Noticias save(Noticias noticia, MultipartFile file) throws IOException {
        // 1. Si viene un archivo, lo guardamos
        if (file != null && !file.isEmpty()) {
            // Generamos un nombre único para evitar duplicados (ej: asd3-324s-foto.jpg)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Creamos la ruta completa
            Path destinationFile = this.rootLocation.resolve(Paths.get(fileName))
                    .normalize().toAbsolutePath();

            // Copiamos el archivo (si no existe la carpeta, hay que crearla manualmente o por código)
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // 2. Seteamos el nombre en el objeto
            noticia.setImagen(fileName);
        }

        // 3. Guardamos en BD
        return noticiasRepository.save(noticia);
    }

    // ... (Mantén tus métodos findAll, findById, deleteById iguales) ...

    public List<Noticias> findAll() { return noticiasRepository.findAll(); }
    public Optional<Noticias> findById(int id) { return noticiasRepository.findById(id); }
    public void deleteById(int id) { noticiasRepository.deleteById(id); }
}