package com.pixelium.levelup.service;

import com.pixelium.levelup.model.Noticias;
import com.pixelium.levelup.repository.NoticiasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoticiasService {
    @Autowired
    private NoticiasRepository noticiasRepository;

    public List<Noticias> findAll() {
        return noticiasRepository.findAll();
    }

    public Optional<Noticias> findById(int id) {
        return noticiasRepository.findById(id);
    }

    public Noticias save(Noticias noticia) {
        return noticiasRepository.save(noticia);
    }

    public void deleteById(int id) {
        noticiasRepository.deleteById(id);
    }
}