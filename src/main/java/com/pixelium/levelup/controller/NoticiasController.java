package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Noticias;
import com.pixelium.levelup.service.NoticiasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/noticias")
public class NoticiasController {

    @Autowired
    private NoticiasService noticiasService;

    @GetMapping
    public List<Noticias> getAll() {
        return noticiasService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Noticias> getById(@PathVariable int id) {
        return noticiasService.findById(id);
    }

    @PostMapping
    public Noticias save(@RequestBody Noticias noticias) {
        return noticiasService.save(noticias);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        noticiasService.deleteById(id);
    }
}