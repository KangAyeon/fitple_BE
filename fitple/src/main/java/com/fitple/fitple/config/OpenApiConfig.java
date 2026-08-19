package com.fitple.fitple.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "JSESSIONID",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.COOKIE)
                                                .name("JSESSIONID")
                                                .description("로그인 후 발급되는 JSESSIONID S.S. 내가 맹근 쿠키")
                                )
                );
    }
}