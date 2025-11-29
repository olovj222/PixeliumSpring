package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Noticias;
import com.pixelium.levelup.service.NoticiasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/noticias")
@Tag(name = "Noticias", description = "Operaciones sobre noticias")
@CrossOrigin(origins = "*")
public class NoticiasController {

    @Autowired
    private NoticiasService noticiasService;

    @GetMapping
    @Operation(summary = "Obtener todas las noticias", description = "Obtiene una lista de todas las noticias disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "No se encontraron noticias")
    })
    public ResponseEntity<List<Noticias>> getAll() {
        List<Noticias> noticias = noticiasService.findAll();
        if (noticias.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(noticias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener noticia por ID", description = "Obtiene una noticia específica usando su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Noticia no encontrada")
    })
    public ResponseEntity<Noticias> getById(@PathVariable int id) {
        Optional<Noticias> noticia = noticiasService.findById(id);
        if (noticia.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(noticia.get());
    }

    @PostMapping // Esta es una ruta administrativa, aunque esté en permitAll()
    @Operation(summary = "Crear nueva noticia", description = "Crea y guarda una nueva noticia con una imagen asociada.", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Noticia creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error de archivo")
            // Si estuviera protegida por JWT, deberíamos añadir 403
    })
    public ResponseEntity<Noticias> save(
            @RequestParam("titulo") String titulo,
            @RequestParam("detalle") String detalle,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {

        Noticias noticias = new Noticias();
        noticias.setTitulo(titulo);
        noticias.setDetalle(detalle);

        try {
            Noticias savedNews = noticiasService.save(noticias, file);
            // 201 Created es ideal para POST
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);
        } catch (IOException e) {
            // Error en la lectura/escritura del archivo
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            // Otros errores de servicio
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar noticia", description = "Elimina una noticia por su identificador.", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Noticia eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Noticia no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            noticiasService.deleteById(id);
            // 204 No Content para eliminación exitosa sin cuerpo de respuesta
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Asumiendo que si falla, es porque no existe el recurso
            return ResponseEntity.notFound().build(); // Devuelve 404
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar noticia", description = "Actualiza una noticia existente por su ID.", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Noticia actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error de archivo"),
            @ApiResponse(responseCode = "404", description = "Noticia no encontrada")
    })
    public ResponseEntity<Noticias> update(
            @PathVariable int id,
            @RequestParam("titulo") String titulo,
            @RequestParam("detalle") String detalle,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            // Verificar que la noticia existe
            Optional<Noticias> existingNoticia = noticiasService.findById(id);
            if (existingNoticia.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Noticias noticia = existingNoticia.get();
            noticia.setTitulo(titulo);
            noticia.setDetalle(detalle);

            Noticias updatedNews = noticiasService.update(noticia, file);
            return ResponseEntity.ok(updatedNews);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}