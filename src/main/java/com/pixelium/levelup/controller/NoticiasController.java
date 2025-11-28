package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Noticias;
import com.pixelium.levelup.service.NoticiasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/noticias")
@Tag(name = "Noticias", description = "Operaciones sobre noticias")
@CrossOrigin(origins = "*") // Importante para que React pueda subir archivos
public class NoticiasController {

    @Autowired
    private NoticiasService noticiasService;

    @GetMapping
    public List<Noticias> getAll() { return noticiasService.findAll(); }

    @GetMapping("/{id}")
    public Optional<Noticias> getById(@PathVariable int id) { return noticiasService.findById(id); }

    // Modificado para recibir archivo + datos
    @PostMapping
    public Noticias save(
            @RequestParam("titulo") String titulo,
            @RequestParam("detalle") String detalle,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        Noticias noticias = new Noticias();
        noticias.setTitulo(titulo);
        noticias.setDetalle(detalle);

        return noticiasService.save(noticias, file);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { noticiasService.deleteById(id); }
}