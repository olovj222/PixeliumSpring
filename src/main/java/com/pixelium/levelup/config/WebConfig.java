package com.pixelium.levelup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 1. Mapea la URL "/images/**" a la carpeta física "uploads/" (Imágenes de Productos)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");

        // 2. Mapea la URL "/avatars/**" a la carpeta física "avatars/" (Fotos de Perfil)
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:avatars/");

        // 3. 🟢 NUEVA RUTA: Mapea la URL "/uploads/**" a la carpeta física "uploads/" (Imágenes de Noticias)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}