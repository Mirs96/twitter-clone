package com.twitterclone.backend.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Massa Miryam",
                        email = "miryammassa@gmail.com"
                ),
                title = "Y - Twitter Clone",
                description = "Spring Boot 3+ Spring Security 6+",
                version = "0.0.1-SNAPSHOT"
        ),
        servers = {
                @Server(
                        description = "Development",
                        url = "http://localhost:8080"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfiguration {
//    @Bean
//    public OpenApiCustomizer schemaCustomizer() {
//        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
//                .resolveAsResolvedSchema(new AnnotatedType(com.twitterclone.backend.dto.ErrorResponse.class));
//        return openApi -> {
//            if (resolvedSchema.schema != null && resolvedSchema.schema.getName() != null) {
//                openApi.getComponents().addSchemas(resolvedSchema.schema.getName(), resolvedSchema.schema);
//            }
//        };
//    }
}