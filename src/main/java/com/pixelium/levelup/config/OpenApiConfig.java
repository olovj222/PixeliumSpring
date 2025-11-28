package com.pixelium.levelup.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Level-UP Gamer API", version = "1.0", description = "Documentación de la API del Back-End de Level-UP Gamer."))
@SecurityScheme(
        name = "Bearer Authentication", // Nombre que aparecerá en el botón de autorización
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer", // Tipo de esquema HTTP (Bearer)
        description = "Token JWT de un usuario autenticado (ADMIN o USER)."
)
public class OpenApiConfig {
    // Esta clase solo contiene anotaciones de configuración.
    // No necesita código dentro, pero puedes añadir @Beans si fuera necesario.
}