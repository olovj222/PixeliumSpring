package com.pixelium.levelup.service;

import com.pixelium.levelup.model.Producto;
import com.pixelium.levelup.repository.ProductoRepository;
import com.pixelium.levelup.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll(){return productoRepository.findAll();}

    public Optional<Producto> findById(int id){return productoRepository.findById(id);}
    public  Producto save(Producto p){return productoRepository.save(p);}
    public void deleteById(int id){productoRepository.deleteById(id);}

}
