package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Producto;
import com.pixelium.levelup.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Import necesario si aplicaras seguridad
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Nuevo import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/productos")
@Tag(name = "Productos", description = "Operaciones sobre productos")
// 🚨 NOTA: NO aplicamos @SecurityRequirement(name = "Bearer Authentication") aquí
// porque estas rutas están en permitAll() en SecurityConfig.
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Obtener todos los productos",description = "Obtiene una lista de todos los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Productos no encontrados")
    })
    public ResponseEntity<List<Producto>> getAll() {
        // Usamos ResponseEntity para controlar el estado 200 OK
        List<Producto> productos = productoService.findAll();
        if (productos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto",description = "Obtiene producto por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Optional<Producto>> getById(@PathVariable int id) {
        Optional<Producto> producto = productoService.findById(id);
        if (producto.isEmpty()) {
            return ResponseEntity.notFound().build(); // Devuelve 404
        }
        return ResponseEntity.ok(producto);
    }

    // --- METODO SAVE CORREGIDO PARA MULTIPLES ARCHIVOS ---
    // NOTA: Asumimos que esta acción es de ADMIN, pero la ruta es permitAll()
    // Si quieres protegerla, debes mover /api/v1/productos a /api/v1/admin/productos
    @PostMapping
    @Operation(
            summary = "Crear nuevo producto",
            description = "Crea un nuevo producto con hasta 4 imágenes (Requiere multipart/form-data)",
            security = @SecurityRequirement(name = "Bearer Authentication")
            // 🚨 AQUÍ ELIMINAMOS requestBody Y LA PROPIEDAD CONSUMES INCOMPATIBLE
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error de I/O"),
            @ApiResponse(responseCode = "403", description = "No autorizado (Requiere ADMIN)")
    })
    public ResponseEntity<Producto> save(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("price") int price,
            @RequestParam(value = "filePrincipal", required = false) MultipartFile filePrincipal,
            @RequestParam(value = "fileDetalle2", required = false) MultipartFile fileDetalle2,
            @RequestParam(value = "fileDetalle3", required = false) MultipartFile fileDetalle3,
            @RequestParam(value = "fileDetalle4", required = false) MultipartFile fileDetalle4
    ) {
        try {
            Producto producto = new Producto();
            producto.setTitle(title);
            producto.setDescription(description);
            producto.setCategory(category);
            producto.setPrice(price);

            MultipartFile[] files = {filePrincipal, fileDetalle2, fileDetalle3, fileDetalle4};
            Producto savedProduct = productoService.saveWithMultipleFiles(producto, files);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto",description = "Elimina un producto por id", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable int id) {
        // En una implementación robusta, verificaríamos si el producto existe antes de eliminar.
        try {
            productoService.deleteById(id);
            // 204 No Content: Respuesta estándar para DELETE exitoso sin cuerpo de respuesta.
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Asumimos que cualquier fallo es porque no se encontró el recurso
            return ResponseEntity.notFound().build(); // Devuelve 404
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente por su ID.", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error de archivo"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Producto> update(
            @PathVariable int id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("price") int price,
            @RequestParam(value = "filePrincipal", required = false) MultipartFile filePrincipal,
            @RequestParam(value = "fileDetalle2", required = false) MultipartFile fileDetalle2,
            @RequestParam(value = "fileDetalle3", required = false) MultipartFile fileDetalle3,
            @RequestParam(value = "fileDetalle4", required = false) MultipartFile fileDetalle4
    ) {
        try {
            // Verificar que el producto existe
            Optional<Producto> existingProducto = productoService.findById(id);
            if (existingProducto.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Producto producto = existingProducto.get();
            producto.setTitle(title);
            producto.setDescription(description);
            producto.setCategory(category);
            producto.setPrice(price);

            Producto updatedProduct = productoService.update(producto, filePrincipal, fileDetalle2, fileDetalle3, fileDetalle4);
            return ResponseEntity.ok(updatedProduct);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}