package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Producto;
import com.pixelium.levelup.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/productos")
@CrossOrigin(origins = "*") // Importante para permitir peticiones desde React
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> getAll() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Producto> getById(@PathVariable int id) {
        return productoService.findById(id);
    }

    // --- AQUÍ ESTÁ LA SOLUCIÓN ---
    // NO uses @RequestBody Producto producto.
    // Debes desglosar los campos uno a uno para aceptar 'multipart/form-data'
    @PostMapping
    public Producto save(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("price") int price,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // 1. Creamos el objeto manualmente con los datos que llegan
        Producto producto = new Producto();
        producto.setTitle(title);
        producto.setDescription(description);
        producto.setCategory(category);
        producto.setPrice(price);

        // 2. Llamamos al servicio que sabe guardar la imagen
        return productoService.save(producto, file);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        productoService.deleteById(id);
    }
}