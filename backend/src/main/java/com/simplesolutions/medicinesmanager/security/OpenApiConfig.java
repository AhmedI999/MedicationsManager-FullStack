package com.simplesolutions.medicinesmanager.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Ahmed Ibrahim",
                        email = "ahmedibrahim5182@gmail.com",
                        url = "https://www.linkedin.com/in/ahmed-i99"
                ),
                description = "OpenApi documentation for Medications Manager",
                title = "OpenApi specification - MM",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local Development ENV",
                        url = "http://localhost:8080"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "BearerAuthentication"
                )
        }
)
@SecurityScheme(
        name = "BearerAuthentication",
        description = "JWT Bearer Token Required",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {}
