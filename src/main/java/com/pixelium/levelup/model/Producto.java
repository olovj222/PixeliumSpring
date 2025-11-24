package com.pixelium.levelup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(length = 1000) // Aumentamos el tamaño para descripciones largas
    private String description;

    private String category;
    private int price;

    // Imágenes (Usamos camelCase en Java)
    private String imageSrc;
    private String imageSrc2;
    private String imageSrc3;
    private String imageSrc4;
}