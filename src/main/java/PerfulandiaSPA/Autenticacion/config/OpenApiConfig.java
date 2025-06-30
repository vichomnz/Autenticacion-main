package PerfulandiaSPA.Autenticacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Autenticación - PerfulandiaSPA")
                        .version("1.0")
                        .description("API para gestión de autenticación y roles de usuarios")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@perfulandiaspa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}