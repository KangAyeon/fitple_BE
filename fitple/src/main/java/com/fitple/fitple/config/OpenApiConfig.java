package com.fitple.fitple.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${turnelKagamine}")
    private String turnelKagamine;

    @Bean
    public OpenAPI fitpleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fitple API")
                        .version("1.0")
                        .description("Fitple Backend API"))
                .addServersItem(
                        new Server()
                                .url(turnelKagamine)
                                .description("Cloudflare Tunnel")
                );
    }
}