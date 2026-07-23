package edu.ite.libraryapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libraryManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management API")
                        .version("1.0.0")
                        .description("""
                                Interactive documentation for managing books, students, admins, and borrowing.

                                Click an endpoint, choose **Try it out**, enter the request data, and click **Execute**.
                                No authentication token is required by this project.
                                """)
                        .contact(new Contact().name("Library Management Team"))
                        .license(new License().name("Educational project")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Current server")
                ));
    }
}
