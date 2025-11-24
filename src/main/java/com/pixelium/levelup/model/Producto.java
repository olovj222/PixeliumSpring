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
    @Column(length = 30)
    private String title;
    @Column(length = 30)
    private String description;
    @Column(length = 30)
    private String category;
    @Column(length = 30)
    private int price;
    @Column(name = "image_src")
    private String imageSrc;

    @Column(name = "image_src_2")
    private String imageSrc2;

    @Column(name = "image_src_3")
    private String imageSrc3;

    @Column(name = "image_src_4")
    private String imageSrc4;
}
