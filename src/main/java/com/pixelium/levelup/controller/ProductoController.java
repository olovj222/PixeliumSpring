package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Producto;
import com.pixelium.levelup.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/products")

public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> findAll(){return productoService.findAll();}

    @GetMapping("{id}")
    public Optional<Producto> findById(@PathVariable int id){return productoService.findById(id);}

    @PostMapping
    public Producto save(@RequestBody Producto p){
        Producto newProducto = productoService.save(p);
        return newProducto;
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable int id){productoService.deleteById(id);}
}
