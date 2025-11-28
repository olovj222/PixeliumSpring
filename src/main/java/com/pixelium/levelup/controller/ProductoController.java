package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Producto;
import com.pixelium.levelup.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/productos")
@Tag(name = "Productos", description = "Operaciones sobre productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // ... (Métodos GET, DELETE se mantienen igual) ...

    @GetMapping
    @Operation(summary = "Obtener todos los productos",description = "Obtiene una lista de todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Productos no encontrados")
    })
    public List<Producto> getAll() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto",description = "Obtiene producto por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Optional<Producto>> getById(@PathVariable int id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    // --- METODO SAVE CORREGIDO PARA MULTIPLES ARCHIVOS ---
    @PostMapping
    public Producto save(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("price") int price,
            // Imagen Principal (El nombre del campo debe coincidir con el 'formData.append' del frontend)
            @RequestParam("filePrincipal") MultipartFile filePrincipal,
            // Imágenes de Detalle (Opcionales - Usamos required = false)
            @RequestParam(value = "fileDetalle2", required = false) MultipartFile fileDetalle2,
            @RequestParam(value = "fileDetalle3", required = false) MultipartFile fileDetalle3,
            @RequestParam(value = "fileDetalle4", required = false) MultipartFile fileDetalle4
    ) throws IOException {

        // 1. Creamos el objeto Producto
        Producto producto = new Producto();
        producto.setTitle(title);
        producto.setDescription(description);
        producto.setCategory(category);
        producto.setPrice(price);

        // 2. Creamos un array de archivos para pasarlos al servicio
        MultipartFile[] files = {filePrincipal, fileDetalle2, fileDetalle3, fileDetalle4};

        // 3. Llamamos al servicio que guardará el producto y todos los archivos
        return productoService.saveWithMultipleFiles(producto, files);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        productoService.deleteById(id);
    }
}