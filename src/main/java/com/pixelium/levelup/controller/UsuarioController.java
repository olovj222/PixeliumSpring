package com.pixelium.levelup.controller;

import com.pixelium.levelup.model.Usuario;
import com.pixelium.levelup.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAll() {return usuarioService.findAll(); }

    @GetMapping("id")
    public Optional<Usuario> getById(@PathVariable int id){ return usuarioService.findById(id);}

    @PostMapping
    public Usuario save(@RequestBody Usuario usuario) {
        Usuario newUsuario = usuarioService.save(usuario);
        return newUsuario;
    }

    @DeleteMapping({"id"})
    public void delete(@PathVariable int id) {usuarioService.deleteById(id);}
}
