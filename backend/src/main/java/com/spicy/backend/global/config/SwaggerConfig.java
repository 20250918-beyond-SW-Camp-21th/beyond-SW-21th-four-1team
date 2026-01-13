package com.spicy.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("Auth API")
                .pathsToMatch(
                        "/api/v1/auth/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi userGroup() {
        return GroupedOpenApi.builder()
                .group("User API")
                .pathsToMatch(
                        "/api/v1/users/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi orderGroup() {
        return GroupedOpenApi.builder()
                .group("Order API")
                .pathsToMatch(
                        "/api/v1/orders/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi cartItemGroup() {
        return GroupedOpenApi.builder()
                .group("Cart Item API")
                .pathsToMatch(
                        "/api/v1/cart-items/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi inventoryGroup() {
        return GroupedOpenApi.builder()
                .group("Inventory API")
                .pathsToMatch(
                        "/api/v1/inventory/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi settlementGroup() {
        return GroupedOpenApi.builder()
                .group("Settlement API")
                .pathsToMatch(
                        "/api/v1/settlements/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi allGroup() {
        return GroupedOpenApi.builder()
                .group("전체 API")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "bearerAuth";

        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)   // HTTP 인증
                .scheme("bearer")                 // Bearer 토큰
                .bearerFormat("JWT")              // 표시용
                .in(SecurityScheme.In.HEADER);    // Authorization 헤더

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("AccountBookForMoms API")
                        .version("v1"));
    }
}